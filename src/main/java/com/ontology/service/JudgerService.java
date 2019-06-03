package com.ontology.service;

import com.ontology.bean.EsPage;
import com.ontology.entity.Judger;

import java.util.List;

public interface JudgerService {
    List<Judger> getJudger();

    EsPage getTobeJudged(String action, String ontid, Integer pageNum, Integer pagesize);
}
