package com.ontology.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.exception.MarketplaceException;
import com.ontology.service.ContractService;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/dataset")
@CrossOrigin
public class DatasetController2 {
    @Autowired
    private ContractService contractService;

    /**
     * 新增或更新数据
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "新增或修改数据", notes = "新增修改数据", httpMethod = "PUT")
    @PutMapping
    public Result updateData(@RequestBody AttributeVo req) {
        String action = "addOrUpdate";
        String id = req.getId();
        String ontid = req.getOntid();
        String certifier = req.getCertifier();
        DataVo data = req.getData();
        List<String> keywords = data.getKeywords();
        String dataSource = req.getDataSource();
        if (keywords == null) {
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(),ErrorInfo.PARAM_ERROR.descEN(),ErrorInfo.PARAM_ERROR.code());
        }


        if (StringUtils.isEmpty(id)) {
            // 自动分配id
            id = UUID.randomUUID().toString().replace("-", "");
        }

        Map<String, Object> obj = new LinkedHashMap<>();
        String date = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss").replace("\"", "");
        obj.put("id", id);
        obj.put("dataId", "");
        obj.put("tokenId", "");
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
            exist = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        } catch (IndexNotFoundException e) {
            ElasticsearchUtil.createIndex(Constant.ES_INDEX_DATASET);
            log.info("索引不存在，创建索引");
            exist = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        }
        if (exist == null) {
            // 新增数据 state:0-未上传；1-已上传（生成tokenId）
            obj.put("state", 0);
            ElasticsearchUtil.addData(obj, Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id);
        } else {
            int state = (int) exist.get("state");
            if (state == 1) {
                throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(),ErrorInfo.NO_PERMISSION.descEN(),ErrorInfo.NO_PERMISSION.code());
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
            ElasticsearchUtil.updateDataById(obj, Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id);
        }
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descCN(), id);
    }

    @ApiOperation(value = "分页查询数据", notes = "分页查询数据", httpMethod = "POST")
    @PostMapping
    public Result getPageData(@RequestBody PageQueryVo req) {
        String action = "getPageData";
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
        EsPage list = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, pageIndex, pageSize, boolQuery, null, "createTime.keyword", null);

        List<Map<String, Object>> recordList = list.getRecordList();
        for (Map<String, Object> result : recordList) {
            ElasticsearchUtil.formatResult(result);
            result.remove("dataSource");
            JSONArray judger = JSONArray.parseArray((String) result.get("judger"));
            JSONArray challengePeriod = JSONArray.parseArray((String) result.get("challengePeriod"));
            result.put("judger", judger);
            result.put("challengePeriod", challengePeriod);
        }

        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descCN(), list);
    }

    @ApiOperation(value = "根据id查询数据", notes = "根据id查询数据", httpMethod = "GET")
    @GetMapping("/{id}")
    public Result getData(@PathVariable String id) {
        String action = "getData";
        Map<String, Object> result = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        ElasticsearchUtil.formatResult(result);
        result.remove("dataSource");
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descCN(), result);
    }


    @ApiOperation(value = "根据卖家ontid查询数据", notes = "根据卖家ontid查询数据", httpMethod = "GET")
    @GetMapping("/provider/{ontid}")
    public Result getDataByProvider(@PathVariable String ontid) {
        String action = "getDataByProvider";
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryProvider = QueryBuilders.matchQuery("ontid", ontid);
        boolQuery.must(queryProvider);
        List<Map<String, Object>> result = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, boolQuery, null, null, null, null);
        for (Map<String, Object> map : result) {
            ElasticsearchUtil.formatResult(map);
        }
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descCN(), result);
    }

    @ApiOperation(value = "卖家生成dataId和dataToken", notes = "卖家生成dataId和dataToken", httpMethod = "POST")
    @PostMapping("/tokenId")
    public Result createDataIdAndTokenId(@RequestBody TokenIdVo req) {
        String action = "createDataIdAndTokenId";
        try {
            String txHash = contractService.sendTransaction(action, req.getSigVo());
            Map<String,Object> dataset = new HashMap<>();
            dataset.put("dataId",req.getDataId());
            ElasticsearchUtil.updateDataById(dataset,Constant.ES_INDEX_DATASET,Constant.ES_TYPE_DATASET,req.getId());
            return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descCN(), txHash);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(),ErrorInfo.PARAM_ERROR.descEN(),ErrorInfo.PARAM_ERROR.code());
        }
    }

}