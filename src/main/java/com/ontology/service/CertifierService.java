package com.ontology.service;

import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageDto;
import com.ontology.entity.Certifier;

import java.util.List;
import java.util.Map;

public interface CertifierService {
    List<Certifier> getCertifier();

    Map<String, Object> getMessage(String id);

    Result callback(String action, MessageDto req);

    String certificateResult(String action, String id);

}
