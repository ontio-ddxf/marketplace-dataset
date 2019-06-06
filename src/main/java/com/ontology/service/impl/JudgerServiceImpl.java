package com.ontology.service.impl;

import com.ontology.bean.EsPage;
import com.ontology.controller.vo.JudgeResultVo;
import com.ontology.controller.vo.SigVo;
import com.ontology.entity.Judger;
import com.ontology.mapper.JudgerMapper;
import com.ontology.service.ContractService;
import com.ontology.service.JudgerService;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JudgerServiceImpl implements JudgerService {
    @Autowired
    private JudgerMapper judgerMapper;
    @Autowired
    private ContractService contractService;

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
        boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_ORDER);
        if (!indexExist) {
            return null;
        }
        EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, pageNum, pageSize, boolQuery, null, null, null);
        List<Map<String, Object>> recordList = esPage.getRecordList();
        for (Map<String, Object> result : recordList) {
            ElasticsearchUtil.formatOrderResult(result);
        }
        return esPage;
    }

    @Override
    public String judgeResult(String action, JudgeResultVo req) {
        String id = req.getId();
        Boolean winOrLose = req.getWinOrLose();
        SigVo sigVo = req.getSigVo();

        try {
            // 发送交易
            String txHash = contractService.sendTransaction(action, sigVo);
            // 本地记录结果&状态
            Map<String,Object> map = new HashMap<>();
            map.put("state","5");
            if (winOrLose) {
                map.put("arbitrage","1");
            } else {
                map.put("arbitrage","0");
            }
            ElasticsearchUtil.updateDataById(map,Constant.ES_INDEX_ORDER,Constant.ES_TYPE_ORDER,id);
            return txHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
