package com.ontology.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.ontology.controller.vo.ContractVo;
import com.ontology.controller.vo.DataIdVo;
import com.ontology.controller.vo.SigVo;
import com.ontology.controller.vo.TransactionDto;
import com.ontology.entity.Invoke;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.service.ContractService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


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
        log.info("txHex:{}",txHex);
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
        }
        return null;
    }

    @Override
    public Map makeTransactionAndCallback(String action, ContractVo contractVo) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String txHex = makeTransaction(action, contractVo);
        String callback = String.format(configParam.CALLBACK_URL, "api/v1/contract/invoke");
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
    public JSONObject invoke(String action, TransactionDto req) throws Exception {
        Invoke invoke = invokeMapper.selectByPrimaryKey(req.getId());
        if (invoke == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

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

        return new JSONObject();
    }

}
