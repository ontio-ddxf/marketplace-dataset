package com.ontology.service;

import com.ontology.bean.EsPage;
import com.ontology.controller.vo.JudgeResultVo;
import com.ontology.entity.Judger;

import java.util.List;

public interface JudgerService {
    List<Judger> getJudger();

    EsPage getTobeJudged(String action, String ontid, Integer pageNum, Integer pagesize);

    String judgeResult(String action, JudgeResultVo req);

}
