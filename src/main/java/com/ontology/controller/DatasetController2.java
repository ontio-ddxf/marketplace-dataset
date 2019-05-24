package com.ontology.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.utils.ElasticsearchUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dataset")
@CrossOrigin
public class DatasetController2 {

    private String indexName = "dataset_index";

    private String esType = "dataset";

    /**
     * 新增或更新数据
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "新增或修改数据", notes = "新增修改数据", httpMethod = "PUT")
    @PutMapping
    public Result updateData(@RequestBody AttributeVo req) {
        String id = req.getId();
        String ontid = req.getOntid();
        String certifier = req.getCertifier();
        DataVo data = req.getData();
        List<String> keywords = data.getKeywords();
        String dataSource = req.getDataSource();
        if (keywords == null) {
            return new Result(400, "PARAMS_ERROR", "");
        }


        if (StringUtils.isEmpty(id)) {
            // 自动分配id
            id = UUID.randomUUID().toString().replace("-", "");
        }

        Map<String, Object> obj = new LinkedHashMap<>();
        String date = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss").replace("\"", "");
        obj.put("id", id);
        obj.put("ontid", ontid);
        obj.put("createTime", date);
        obj.put("certifier", certifier);
        obj.put("isCertificated", 0);
        obj.put("data",JSON.toJSONString(data));
        obj.put("dataSource", dataSource);
        for (int i = 0; i < keywords.size(); i++) {
            obj.put("column" + i, keywords.get(i));
        }
        Map<String, Object> exist = null;
        try {
            // 根据id查询数据是否存在
            exist = ElasticsearchUtil.searchDataById(indexName, esType, id, null);
        } catch (IndexNotFoundException e) {
            ElasticsearchUtil.createIndex(indexName);
            log.info("索引不存在，创建索引");
            exist = ElasticsearchUtil.searchDataById(indexName, esType, id, null);
        }
        if (exist == null) {
            // 新增数据
            obj.put("state", 0);
            ElasticsearchUtil.addData(obj, indexName, esType, id);
        } else {
            int state = (int) exist.get("state");
            if (state == 1) {
                return new Result(500, "STATE_ERROR", "");
            }
            // 旧数据的tag数
            int j = 0;
            for (int i = 0; ; i++) {
                if (!exist.containsKey("column" + i)) {
                    j = i;
                    break;
                }
            }
            // 旧数据tag字段长于新数据，将旧数据多出的tag置为"";
            if (j > keywords.size()) {
                for (int k = keywords.size(); k < j; k++) {
                    obj.put("column" + k, "");
                }
            }
            // 修改后需要重新认证
            obj.put("isCertificated", 0);
            // 更新数据
            ElasticsearchUtil.updateDataById(obj, indexName, esType, id);
        }
        return new Result(0, "SUCCESS", id);
    }

    @ApiOperation(value = "分页查询数据", notes = "分页查询数据", httpMethod = "POST")
    @PostMapping
    public Result getPageData(@RequestBody PageQueryVo req) {
        List<QueryVo> queryParams = req.getQueryParams();
        int pageIndex = req.getPageIndex();
        int pageSize = req.getPageSize();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (QueryVo vo : queryParams) {
            if (StringUtils.isEmpty(vo.getText())) {
                continue;
            }
            if (vo.getPercent() > 100) {
                vo.setPercent(100);
            } else if (vo.getPercent() < 0) {
                vo.setPercent(0);
            }
            MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("column" + vo.getColumnIndex(), vo.getText()).minimumShouldMatch(vo.getPercent() + "%");
            boolQuery.must(queryBuilder);
        }
        MatchQueryBuilder queryState = QueryBuilders.matchQuery("state", 1);
        boolQuery.must(queryState);
        EsPage list = ElasticsearchUtil.searchDataPage(indexName, esType, pageIndex, pageSize, boolQuery, null, "createTime.keyword", null);

        List<Map<String, Object>> recordList = list.getRecordList();
        for (Map<String, Object> result : recordList) {
            ElasticsearchUtil.formatResult(result);
            result.remove("dataSource");
            JSONArray judger = JSONArray.parseArray((String) result.get("judger"));
            JSONArray challengePeriod = JSONArray.parseArray((String) result.get("challengePeriod"));
            result.put("judger", judger);
            result.put("challengePeriod", challengePeriod);
        }

        return new Result(0, "SUCCESS", list);
    }

    @ApiOperation(value = "根据id查询数据", notes = "根据id查询数据", httpMethod = "GET")
    @GetMapping("/{id}")
    public Result getData(@PathVariable String id) {
        Map<String, Object> result = ElasticsearchUtil.searchDataById(indexName, esType, id, null);
        ElasticsearchUtil.formatResult(result);
        result.remove("dataSource");
        return new Result(0, "SUCCESS", result);
    }


    @ApiOperation(value = "根据卖家ontid查询数据", notes = "根据卖家ontid查询数据", httpMethod = "GET")
    @GetMapping("/provider/{ontid}")
    public Result getDataByProvider(@PathVariable String ontid) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryProvider = QueryBuilders.matchQuery("ontid", ontid);
        boolQuery.must(queryProvider);
        List<Map<String, Object>> result = ElasticsearchUtil.searchListData(indexName, esType, boolQuery, null, null, null, null);
        for (Map<String, Object> map : result) {
            ElasticsearchUtil.formatResult(map);
        }
        return new Result(0, "SUCCESS", result);
    }

    @ApiOperation(value = "上架", notes = "上架", httpMethod = "POST")
    @PostMapping("/provide")
    public Result provideData(@RequestBody ProvideVo req) {
        String id = req.getId();
        String ontid = req.getOntid();
        Map<String, Object> map = ElasticsearchUtil.searchDataById(indexName, esType, id, null);
        if (map == null) {
            return new Result(500, "NOT_FOUND", "");
        }
        if ((int) map.get("isCertificated") != 1) {
            return new Result(500, "STATE_ERROR", "");
        }
        if (!ontid.equals(map.get("ontid"))) {
            return new Result(500, "NO_PERMISSION", "");
        }


        List<String> judgerList = req.getJudger();
        List<Integer> challengePeriod = req.getChallengePeriod();
        map.put("coin", req.getCoin());
        map.put("price", req.getPrice());
        map.put("judger", JSON.toJSONString(judgerList));
        map.put("challengePeriod", JSON.toJSONString(challengePeriod));
        ElasticsearchUtil.updateDataById(map, indexName, esType, id);
        return new Result(0, "SUCCESS", "SUCCESS");
    }

    @ApiOperation(value = "下架", notes = "下架", httpMethod = "POST")
    @DeleteMapping("/{id}")
    public Result deleteData(@PathVariable String id) {
        Map<String, Object> map = ElasticsearchUtil.searchDataById(indexName, esType, id, null);
        if (map == null) {
            return new Result(500, "NOT_FOUND", "");
        }

        map.put("state", 0);
        ElasticsearchUtil.updateDataById(map, indexName, esType, id);
        return new Result(0, "SUCCESS", "SUCCESS");
    }

}