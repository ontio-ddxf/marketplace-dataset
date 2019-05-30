package com.ontology.service;

import com.ontology.bean.EsPage;
import com.ontology.controller.vo.JudgeVo;
import com.ontology.entity.Judger;

import java.util.List;

public interface JudgerService {
    List<Judger> getJudger();

    EsPage getTobeJudged(String action, JudgeVo req);
}
