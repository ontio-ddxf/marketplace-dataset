package com.ontology.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.Invoke;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.service.ContractService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;


@Slf4j
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private SDKUtil sdk;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private InvokeMapper invokeMapper;

    @Override
    public String makeTransaction(String action, ContractVo contractVo) throws Exception {
        String method = contractVo.getMethod();
        String contractHash = contractVo.getContractHash();
        List argsList = contractVo.getArgsList();
        String payerAddr = configParam.PAYER_ADDRESS;
        String params = Helper.getParams("", contractHash, method, argsList, payerAddr);
        String txHex = (String) sdk.makeTransaction(params);
        log.info("txHex:{}", txHex);
        return txHex;
    }

    @Override
    public String sendTransaction(String action, SigVo sigVo) throws Exception {
        String txHash = sdk.sendTransaction(sigVo);
        return txHash;
    }

    @Override
    public String registerDataId(String action, DataIdVo dataIdVo) throws Exception {
        String dataIdTxHex = sdk.makeRegIdWithController(dataIdVo.getDataId(), dataIdVo.getOntid(), dataIdVo.getPubKey());
        return dataIdTxHex;
    }

    @Override
    public String sendSyncTransaction(String action, SigVo sigDataVo) throws Exception {
        String txHash = sdk.sendSyncTransaction(sigDataVo);
        return txHash;
    }

    @Override
    public String invokeResult(String action, String id) {
        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        Integer success = invoke.getSuccess();
        if (success != null && success.equals(1)) {
            return "1";
        } else if (success != null && success.equals(0)) {
            return "0";
        } else if (success != null && success.equals(2)) {
            return "2";
        }
        return null;
    }

    @Override
    public Map makeTransactionAndCallback(String action, ContractVo contractVo) throws Exception {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, uuid.indexOf("-"));
        String txHex = makeTransaction(action, contractVo);
//        String callback = String.format(configParam.CALLBACK_URL, "api/v1/contract/invoke");
        String callback = String.format(configParam.CALLBACK_URL, "back/invoke");
        Invoke invoke = new Invoke();
        invoke.setId(uuid);
        invoke.setSuccess(0);
        invoke.setParams(txHex);
        invokeMapper.insert(invoke);

        Map<String, Object> map = new HashMap<>();
        map.put("id", uuid);
        map.put("callback", callback);
        map.put("message", txHex);
        return map;
    }

    @Override
    public Result invoke(String action, TransactionDto req) throws Exception {
        Invoke invoke = invokeMapper.selectByPrimaryKey(req.getId());
        if (invoke == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        try {
            String publickey = (String) req.getParams().get("publickey");
            String signature = (String) req.getParams().get("signature");
            String txHex = invoke.getParams();
            SigVo sigVo = new SigVo();
            sigVo.setPubKeys(publickey);
            sigVo.setSigData(signature);
            sigVo.setTxHex(txHex);
            sdk.sendTransaction(sigVo);

            invoke.setSuccess(1);
            invokeMapper.updateByPrimaryKeySelective(invoke);

            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), "SUCCESS");
        } catch (Exception e) {
            log.error("catch error:", e);
            invoke.setSuccess(2);
            invokeMapper.updateByPrimaryKeySelective(invoke);
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }

    }

    @Override
    public void postHonor(String action, HonorVo req) throws Exception {
        String ontid = req.getOntid();
        int value = req.getValue();

        Matcher matcher = ConstantParam.ONTID_PATTERN.matcher(ontid);
        matcher.matches();
        if (!matcher.matches()) {
            throw new MarketplaceException(action, ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), ErrorInfo.IDENTITY_VERIFY_FAILED.code());
        }

        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "from_acct");
        arg0.put("value", "Address:ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR");
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("name", "to_acct");
        arg1.put("value", "Address:" + ontid.substring(8));
        Map<String, Object> arg2 = new HashMap<>();
        arg2.put("name", "amount");
        arg2.put("value", value);
        List<Map<String, Object>> argList = new ArrayList<>();
        argList.add(arg0);
        argList.add(arg1);
        argList.add(arg2);
        String params = Helper.getParams(ontid, configParam.CONTRACT_HASH_OBP, "transfer", argList, "ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR");

        sdk.invokeContract(params, configParam.ONS_OWNER, false);
    }
}
