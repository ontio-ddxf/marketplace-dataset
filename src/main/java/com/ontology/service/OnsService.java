package com.ontology.service;


import java.util.Map;

public interface OnsService {

    Map<String,Object> registerOns(String action, String ontid, String domain) throws Exception;

    String loginOns(String action, String ontid);
}
