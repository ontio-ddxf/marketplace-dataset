package com.ontology.service.impl;

import com.ontology.bean.EsPage;
import com.ontology.entity.Judger;
import com.ontology.mapper.JudgerMapper;
import com.ontology.service.JudgerService;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JudgerServiceImpl implements JudgerService {
    @Autowired
    private JudgerMapper judgerMapper;

    @Override
    public List<Judger> getJudger() {
        return judgerMapper.selectAll();
    }

    @Override
    public EsPage getTobeJudged(String action, String ontid, Integer pageNum, Integer pageSize) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryJudger = QueryBuilders.matchQuery("judger", ontid);
        MatchQueryBuilder queryState = QueryBuilders.matchQuery("state", "4");
        boolQuery.must(queryJudger);
        boolQuery.must(queryState);
        EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, pageNum, pageSize, boolQuery, null, null, null);
        return esPage;
    }
}
