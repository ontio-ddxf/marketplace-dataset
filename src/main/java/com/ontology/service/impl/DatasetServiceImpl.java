package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.Invoke;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.ContractService;
import com.ontology.service.DatasetService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private ContractService contractService;


    @Override
    public Map<String, Object> registerDataIdAndPost(String action, AuthVo req) throws Exception {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, uuid.indexOf("-"));
        List<String> txHexList = new ArrayList<>();
        // 注册DataId
        DataIdVo dataIdVo = req.getDataIdVo();
        String dataId = dataIdVo.getDataId();
        String ontid = dataIdVo.getOntid();
        Integer pubKey = dataIdVo.getPubKey();
        String id = dataIdVo.getId();
        String dataIdTxHex = sdkUtil.makeRegIdWithController(dataId, ontid, pubKey);

        txHexList.add(dataIdTxHex);

        // 挂单
        OrderVo orderVo = req.getOrderVo();
        ContractVo contractVo = orderVo.getContractVo();
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("id", orderVo.getId());
        objMap.put("dataId", dataId);
        objMap.put("state", "2");
        objMap.put("token", orderVo.getToken());
        objMap.put("price", orderVo.getPrice());
        objMap.put("amount", orderVo.getAmount());
        objMap.put("judger", JSON.toJSONString(orderVo.getOjList()));
        String authOrderTxHex = contractService.makeTransaction(action, contractVo);

        txHexList.add(authOrderTxHex);

//        String callback = String.format(configParam.CALLBACK_URL, "api/v1/dataset/dataId/invoke");
        String callback = String.format(configParam.CALLBACK_URL, "back/dataId");

        Invoke invoke = new Invoke();
        invoke.setId(uuid);
        invoke.setSuccess(0);
        invoke.setParams(JSON.toJSONString(txHexList));
        invoke.setObject(JSON.toJSONString(objMap));
        invokeMapper.insert(invoke);

        Map<String, Object> map = new HashMap<>();
        map.put("id", uuid);
        map.put("callback", callback);
        map.put("message", txHexList);
        return map;
    }

    @Override
    public Result invokeResult(String action, MultiTransactionDto req) throws Exception {
        Invoke invoke = invokeMapper.selectByPrimaryKey(req.getId());
        if (invoke == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        List<Map<String, Object>> params = req.getParams();
        Map<String, Object> dataIdMap = params.get(0);
        String dataIdSignature = (String) dataIdMap.get("signature");
        String publickey = (String) dataIdMap.get("publickey");

        Map<String, Object> authOrderMap = params.get(1);
        String authOrderSignature = (String) authOrderMap.get("signature");

        String txHexListStr = invoke.getParams();
        JSONArray txHexList = JSONObject.parseArray(txHexListStr);

        try {
            // 注册DataId交易
            SigVo dataIdSigVo = new SigVo();
            dataIdSigVo.setPubKeys(publickey);
            dataIdSigVo.setSigData(dataIdSignature);
            dataIdSigVo.setTxHex(txHexList.getString(0));
            sdkUtil.sendSyncTransaction(dataIdSigVo);
            // 授权挂单交易
            SigVo authOrderSigVo = new SigVo();
            authOrderSigVo.setPubKeys(publickey);
            authOrderSigVo.setSigData(authOrderSignature);
            authOrderSigVo.setTxHex(txHexList.getString(1));
            sdkUtil.sendTransaction(authOrderSigVo);
            JSONObject map = JSONObject.parseObject(invoke.getObject());
            String id = map.getString("id");
            ElasticsearchUtil.updateDataById(map, Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id);

            invoke.setSuccess(1);
            invokeMapper.updateByPrimaryKeySelective(invoke);
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), "SUCCESS");
        } catch (Exception e) {
            log.error("catch error:",e);
            invoke.setSuccess(2);
            invokeMapper.updateByPrimaryKeySelective(invoke);
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }





    }
}
