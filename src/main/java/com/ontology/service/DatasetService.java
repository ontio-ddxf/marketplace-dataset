package com.ontology.service;

import com.alibaba.fastjson.JSONObject;
import com.ontology.controller.vo.DataIdVo;
import com.ontology.controller.vo.TokenIdVo;
import com.ontology.controller.vo.TransactionDto;

import java.io.IOException;
import java.util.Map;

public interface DatasetService {

    Map<String, Object> registerDataId(String action, DataIdVo req) throws Exception;

    JSONObject invokeResult(String action, TransactionDto req) throws Exception;

}
