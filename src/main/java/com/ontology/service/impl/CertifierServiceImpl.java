package com.ontology.service.impl;

import com.ontology.entity.Certifier;
import com.ontology.mapper.CertifierMapper;
import com.ontology.service.CertifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertifierServiceImpl implements CertifierService {
    @Autowired
    private CertifierMapper certifierMapper;

    @Override
    public List<Certifier> getCertifier() {
        return certifierMapper.selectAll();
    }
}
