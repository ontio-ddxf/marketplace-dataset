package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.core.transaction.Transaction;
import com.ontology.bean.Result;
import com.ontology.controller.vo.ContractVo;
import com.ontology.controller.vo.MessageDto;
import com.ontology.controller.vo.OnsLoginDto;
import com.ontology.controller.vo.TransactionDto;
import com.ontology.entity.Invoke;
import com.ontology.entity.Login;
import com.ontology.entity.Ons;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.mapper.LoginMapper;
import com.ontology.mapper.OnsMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.ContractService;
import com.ontology.service.OnsService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OnsServiceImpl implements OnsService {

    @Autowired
    private OnsMapper onsMapper;
    @Autowired
    private SDKUtil sdkUtil;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private LoginMapper loginMapper;


    @Override
    public Map<String,Object> registerOns(String action, String ons) throws Exception {

        String id = UUID.randomUUID().toString();
        Ons exist = new Ons();
        exist.setDomain(ons);
        exist.setSuccess(1);
        List<Ons> list = onsMapper.select(exist);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MarketplaceException(action, ErrorInfo.ALREADY_EXIST.descCN(), ErrorInfo.ALREADY_EXIST.descEN(), ErrorInfo.ALREADY_EXIST.code());
        }

        List<Map<String, Object>> args = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "fulldomain");
        arg0.put("value", "String:" + ons);
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("name", "registerdid");
        arg1.put("value", "String:%ontid");
        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("name", "idx");
        arg2.put("value", 1);
        Map<String, Object> arg3 = new HashMap<>();
        arg3.put("name", "validto");
        arg3.put("value", -1);
        args.add(arg0);
        args.add(arg1);
        args.add(arg2);
        args.add(arg3);

        String callback = String.format(configParam.CALLBACK_URL, "api/v1/ons/invoke");

        String params = Helper.getParams("signTransaction", id, configParam.CONTRACT_HASH_ONS,
                "registerDomain", args, "ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR", callback, true);

        Ons domain = new Ons();
        domain.setId(id);
        domain.setDomain(ons);
        domain.setParams(params);
        onsMapper.insertSelective(domain);


        String qrcodeUrl = String.format(configParam.CALLBACK_URL, "api/v1/ons/qrcode/%s");
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("callback", callback);
        map.put("qrcodeUrl", String.format(qrcodeUrl, id));

        return map;
    }

    @Override
    public JSONObject getParams(String action, String id) {
        Ons ons = onsMapper.selectByPrimaryKey(id);
        if (ons == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        String paramsStr = ons.getParams();
        JSONObject params = JSONObject.parseObject(paramsStr);
        return params;
    }

    @Override
    public Result invokeResult(String action, TransactionDto req) throws Exception {
        Ons ons = onsMapper.selectByPrimaryKey(req.getId());
        if (ons == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        String signedTx = (String) req.getParams().get("signedTx");
        Transaction transaction = Transaction.deserializeFrom(com.github.ontio.common.Helper.hexToBytes(signedTx));
        log.info("transaction:{},ons:{}",transaction,configParam.ONS_OWNER);
        try {
            String txHash = (String) sdkUtil.sendTransaction(transaction, configParam.ONS_OWNER, false);
            Executors.newCachedThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object event = null;
                        for (int i = 0; i < 5; i++) {
                            Thread.sleep(6 * 1000);
                            event = sdkUtil.checkEvent(txHash);
                            if (!Helper.isEmptyOrNull(event)) {
                                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(event));
                                JSONArray notify = jsonObject.getJSONArray("Notify");
                                String domain = notify.getJSONObject(0).getJSONArray("States").getString(1);
                                String ontid = notify.getJSONObject(0).getJSONArray("States").getString(2);
                                ons.setOntid(new String (com.github.ontio.common.Helper.hexToBytes(ontid)));
                                ons.setDomain(new String (com.github.ontio.common.Helper.hexToBytes(domain)));
                                ons.setSuccess(1);
                                break;
                            }
                            ons.setSuccess(0);
                        }
                        log.info("event:{}", event);
                        ons.setTxHash(txHash);
                        onsMapper.updateByPrimaryKeySelective(ons);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), "SUCCESS");
        } catch (Exception e) {
            log.error("catch exception",e);
            ons.setSuccess(0);
            onsMapper.updateByPrimaryKeySelective(ons);
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }

    }

    @Override
    public String registerResult(String action, String id) {
        Ons ons = onsMapper.selectByPrimaryKey(id);
        if (ons == null) {
            throw new MarketplaceException (action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        Integer success = ons.getSuccess();
        if (success != null && success.equals(1)) {
            return "1";
        } else if (success != null && success.equals(0)) {
            return "0";
        }
        return null;
    }

    @Override
    public List<String> getOnsList(String action, String ontid, String domain) {
        Ons ons = new Ons();
        ons.setOntid(ontid);
        ons.setSuccess(1);
        List<Ons> onsList = onsMapper.select(ons);
        List<String> list = new ArrayList<>();
        for (Ons one : onsList) {
            if (one.getDomain().endsWith(domain)) {
                list.add(one.getDomain());
            }
        }
        return list;
    }

    @Override
    public Map<String, Object> loginOns() {
        String id = UUID.randomUUID().toString();
        String message = "hello " + System.currentTimeMillis();

        String callback = String.format(configParam.CALLBACK_URL,"api/v1/ons/login/callback");
        String domainListUrl = String.format(configParam.CALLBACK_URL,"api/v1/ons/list");
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("message", message);
        map.put("callback", callback);
        map.put("domain_list_url", domainListUrl);
        return map;
    }

    @Override
    public Map<String, Object> callback(String action, MessageDto req) {
        String id = req.getId();
        String user = req.getParams().getUser();
        String domain = req.getParams().getDomain();

        Login msg = new Login();
        msg.setId(id);
        msg.setOntid(user);
        msg.setDomain(domain);
        msg.setMessage(req.getParams().getMessage());
        msg.setIsVerified(1);
        loginMapper.insert(msg);

        Map<String, Object> map = new HashMap<>();
        map.put("action", action);
        map.put("id", id);
        map.put("error", 0);
        map.put("desc", "SUCCESS");
        map.put("result", true);
        return map;
    }

    @Override
    public Map<String, Object> loginResult(String action, String id) {
        Login msg = loginMapper.selectByPrimaryKey(id);
        if (msg == null) {
            return null;
        }
        Integer isVerified = msg.getIsVerified();
        Map<String, Object> result = new HashMap<>();
        if (isVerified != null && isVerified.equals(1)) {
            String ontid = msg.getOntid();
            String domain = msg.getDomain();

            result.put("ontid", ontid);
            result.put("ons", domain);
            result.put("result", "1");

        } else if (isVerified != null && isVerified.equals(0)) {
            result.put("result", "0");
        }
        return result;
    }

}
