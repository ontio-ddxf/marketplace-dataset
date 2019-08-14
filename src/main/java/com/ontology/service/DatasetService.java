package com.ontology.service;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.AuthVo;
import com.ontology.controller.vo.MultiTransactionDto;

import java.util.Map;

public interface DatasetService {

    Map<String, Object> registerDataIdAndPost(String action, AuthVo req) throws Exception;

    Result invokeResult(String action, MultiTransactionDto req) throws Exception;

}
