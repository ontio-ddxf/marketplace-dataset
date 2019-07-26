package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.core.transaction.Transaction;
import com.ontology.controller.vo.*;
import com.ontology.entity.Certifier;
import com.ontology.entity.Invoke;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.CertifierMapper;
import com.ontology.mapper.InvokeMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.DatasetService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class DatasetServiceImpl implements DatasetService {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private InvokeMapper invokeMapper;
    @Autowired
    private SDKUtil sdkUtil;
    @Autowired
    private SecureConfig secureConfig;


    @Override
    public Map<String, Object> registerDataId(String action, DataIdVo req) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String dataId = req.getDataId();
        String ontid = req.getOntid();
        Integer pubKey = req.getPubKey();
        String id = req.getId();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("id", id);
        dataMap.put("dataId", dataId);

        String callback = String.format(configParam.CALLBACK_URL, "api/v1/dataset/dataId/invoke");

        String txHex = sdkUtil.makeRegIdWithController(dataId, ontid, pubKey);

        Invoke invoke = new Invoke();
        invoke.setId(uuid);
        invoke.setSuccess(0);
        invoke.setParams(txHex);
        invoke.setObject(JSON.toJSONString(dataMap));
        invokeMapper.insert(invoke);

        Map<String, Object> map = new HashMap<>();
        map.put("id", uuid);
        map.put("callback", callback);
        map.put("message", txHex);
        return map;
    }

    @Override
    public JSONObject invokeResult(String action, TransactionDto req) throws Exception {
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
        sdkUtil.sendTransaction(sigVo);

        JSONObject dataMap = JSONObject.parseObject(invoke.getObject());
        String id = dataMap.getString("id");
        String dataId = dataMap.getString("dataId");

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("dataId", dataId);
        dataset.put("state", "1");
        ElasticsearchUtil.updateDataById(dataset, Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id);

        invoke.setSuccess(1);
        invokeMapper.updateByPrimaryKeySelective(invoke);
        return new JSONObject();
    }
}
