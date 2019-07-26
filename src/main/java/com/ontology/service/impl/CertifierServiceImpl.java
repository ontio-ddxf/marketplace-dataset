package com.ontology.service.impl;

import com.ontology.controller.vo.MessageDto;
import com.ontology.entity.Certifier;
import com.ontology.entity.Invoke;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.CertifierMapper;
import com.ontology.mapper.InvokeMapper;
import com.ontology.service.CertifierService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.ErrorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CertifierServiceImpl implements CertifierService {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private CertifierMapper certifierMapper;
    @Autowired
    private InvokeMapper invokeMapper;

    @Override
    public List<Certifier> getCertifier() {
        return certifierMapper.selectAll();
    }

    @Override
    public Map<String, Object> getMessage(String id) {
        String uuid = UUID.randomUUID().toString();
        String message = "certificate "+id;

        Invoke invoke = new Invoke();
        invoke.setId(uuid);
        invoke.setParams(message);
        invoke.setObject(id);
        invoke.setSuccess(0);
        invokeMapper.insert(invoke);

        String callback = String.format(configParam.CALLBACK_URL,"api/v1/certifier/callback");
        Map<String, Object> map = new HashMap<>();
        map.put("id", uuid);
        map.put("message", message);
        map.put("callback", callback);
        return map;
    }

    @Override
    public Map<String, Object> callback(String action, MessageDto req) {
        String uuid = req.getId();
        Invoke invoke = invokeMapper.selectByPrimaryKey(uuid);
        String id = invoke.getObject();

        Map<String, Object> map = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        if (map == null) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        map.put("isCertificated", 1);
        ElasticsearchUtil.updateDataById(map, Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id);

        invoke.setSuccess(1);
        invokeMapper.updateByPrimaryKeySelective(invoke);

        Map<String, Object> result = new HashMap<>();
        map.put("action", action);
        map.put("id", id);
        map.put("error", 0);
        map.put("desc", "SUCCESS");
        map.put("result", true);
        return result;
    }

    @Override
    public String certificateResult(String action, String id) {
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
}
