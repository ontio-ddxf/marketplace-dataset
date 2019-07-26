package com.ontology.controller;

import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageDto;
import com.ontology.entity.Certifier;
import com.ontology.service.CertifierService;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/certifier")
@CrossOrigin
public class CertifierController {

    @Autowired
    private CertifierService certifierService;

    @ApiOperation(value = "获取认证人列表", notes = "获取认证人列表", httpMethod = "GET")
    @GetMapping
    public Result getCertifier() {
        String action = "getCertifier";
        List<Certifier> certifierList = certifierService.getCertifier();
        return new Result(action,0, "SUCCESS", certifierList);
    }

    @ApiOperation(value = "认证人获取待认证列表", notes = "认证人获取待认证列表", httpMethod = "GET")
    @GetMapping("/{certifier}")
    public Result getToBeCertificated(@PathVariable String certifier,@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        String action = "getToBeCertificated";
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryCertifier = QueryBuilders.matchQuery("certifier", certifier);
        MatchQueryBuilder queryIsCertificated = QueryBuilders.matchQuery("isCertificated", 0);
        MatchQueryBuilder queryState = QueryBuilders.matchQuery("state", 0);
        boolQuery.must(queryCertifier);
        boolQuery.must(queryIsCertificated);
        boolQuery.must(queryState);
        boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_DATASET);
        if (!indexExist) {
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), null);
        }
        EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, pageNum, pageSize, boolQuery, null, "createTime.keyword", null);
        List<Map<String, Object>> certificationList = esPage.getRecordList();
        for (Map<String, Object> result:certificationList) {
            ElasticsearchUtil.formatResult(result);
        }
        return new Result(action,0, "SUCCESS", esPage);
    }

    @ApiOperation(value = "获取message", notes = "获取message", httpMethod = "GET")
    @GetMapping("/message/{id}")
    public Result getMessage(@PathVariable String id) {
        String action = "getMessage";
        Map<String,Object> result = certifierService.getMessage(id);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "回调验证", notes = "回调验证", httpMethod = "POST")
    @PostMapping("/callback")
    public Map<String,Object> callback(@RequestBody MessageDto req) {
        String action = "certificate";
        Map<String,Object> result = certifierService.callback(action,req);
        return result;
    }

    @ApiOperation(value = "查询登录是否成功", notes = "查询登录是否成功", httpMethod = "GET")
    @GetMapping("/result/{id}")
    public Result registerResult(@PathVariable String id) {
        String action = "certificateResult";
        String isSuccessful = certifierService.certificateResult(action,id);
        return new Result(action,0, "SUCCESS", isSuccessful);
    }
}