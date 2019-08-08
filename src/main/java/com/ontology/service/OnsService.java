package com.ontology.service;


import com.alibaba.fastjson.JSONObject;
import com.ontology.controller.vo.MessageDto;
import com.ontology.controller.vo.OnsLoginDto;
import com.ontology.controller.vo.TransactionDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface OnsService {

    Map<String,Object> registerOns(String action, String ons) throws Exception;

    JSONObject getParams(String action, String id);

    JSONObject invokeResult(String action, TransactionDto req) throws Exception;

    String registerResult(String action, String id);

    List<String> getOnsList(String action, String ontid, String domain);

    Map<String, Object> loginOns();

    Map<String, Object> callback(String action, MessageDto req);

    Map<String, Object> loginResult(String action, String id);

}
