package com.ontology.service.impl;

import com.ontology.entity.Certifier;
import com.ontology.entity.Judger;
import com.ontology.mapper.CertifierMapper;
import com.ontology.mapper.JudgerMapper;
import com.ontology.service.CertifierService;
import com.ontology.service.JudgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JudgerServiceImpl implements JudgerService {
    @Autowired
    private JudgerMapper judgerMapper;

    @Override
    public List<Judger> getJudger() {
        return judgerMapper.selectAll();
    }
}
