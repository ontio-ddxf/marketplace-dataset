package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ontology.controller.vo.ContractVo;
import com.ontology.entity.Invoke;
import com.ontology.entity.Ons;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.mapper.OnsMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.ContractService;
import com.ontology.service.OnsService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private SecureConfig secureConfig;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private ContractService contractService;
    @Autowired
    private InvokeMapper invokeMapper;


    @Override
    public Map<String,Object> registerOns(String action, String ontid, String domain) throws Exception {
        String uuid = UUID.randomUUID().toString();
        Matcher matcher = ConstantParam.ONTID_PATTERN.matcher(ontid);
        matcher.matches();
        if (!matcher.matches()) {
            throw new MarketplaceException(action, ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), ErrorInfo.IDENTITY_VERIFY_FAILED.code());
        }
        Ons ons = onsMapper.findByOntid(ontid);
        if (ons == null) {
            ons = new Ons();
            ons.setOntid(ontid);
            ons.setDomain(domain);
            ons.setCreateTime(new Date());
            ons.setUpdateTime(new Date());
            ons.setState(0);

            Map<String,Object> arg0 = new HashMap<>();
            arg0.put("name","fulldomain");
            arg0.put("value","String:"+domain);
            Map<String,Object> arg1 = new HashMap<>();
            arg1.put("name","registerdid");
            arg1.put("value","String:"+ontid);
            Map<String,Object> arg2 = new HashMap<>();
            arg2.put("name","idx");
            arg2.put("value",1);
            Map<String,Object> arg3 = new HashMap<>();
            arg3.put("name","validto");
            arg3.put("value",-1);
            List<Map<String,Object>> argList = new ArrayList<>();
            argList.add(arg0);
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);

            ContractVo contractVo = new ContractVo();
            contractVo.setArgsList(argList);
            contractVo.setContractHash(configParam.CONTRACT_HASH_ONS);
            contractVo.setMethod("registerDomain");
            String txHex = contractService.makeTransaction(action, contractVo);

            String callback = String.format(configParam.CALLBACK_URL, "api/v1/ons/invoke");
            Invoke invoke = new Invoke();
            invoke.setId(uuid);
            invoke.setSuccess(0);
            invoke.setParams(txHex);
            invoke.setObject(JSON.toJSONString(ons));
            invokeMapper.insert(invoke);

            Map<String, Object> result = new HashMap<>();
            result.put("id", uuid);
            result.put("callback", callback);
            result.put("message", txHex);
            return result;

        }
        log.info("already exist");
        return null;
    }

    @Override
    public String loginOns(String action, String ontid) {
        Ons ons = onsMapper.findByOntid(ontid);
        if (ons == null) {
            return null;
        } else {
            String domain = ons.getDomain();
            log.info("domain:{}",domain);
            Map<String,Object> arg = new HashMap<>();
            arg.put("name","fulldomain");
            arg.put("value","String:"+domain);
            List<Map<String,Object>> argList = new ArrayList<>();
            argList.add(arg);
            String params = Helper.getParams(ontid, configParam.CONTRACT_HASH_ONS, "ownerOf", argList, configParam.PAYER_ADDRESS);
            String ownerOntid;
            try {
                JSONObject jsonObject = (JSONObject) sdkUtil.invokeContract(params,null, null, true);
                log.info("login result:{}",jsonObject);
                String owner = jsonObject.getString("Result");
                ownerOntid  = new String(com.github.ontio.common.Helper.hexToBytes(owner));
            } catch (Exception e) {
                log.error("catch exception:",e);
                throw new MarketplaceException(e.getMessage());
            }
            if (ontid.equals(ownerOntid)) {
                return domain;
            } else if (Helper.isEmptyOrNull(ownerOntid)) {
                // 未注册成功，删除，重新注册
                onsMapper.delete(ons);
            }
            return null;
        }
    }

}
