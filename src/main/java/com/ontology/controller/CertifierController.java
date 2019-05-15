package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.CertificationVo;
import com.ontology.entity.Certifier;
import com.ontology.service.CertifierService;
import com.ontology.utils.ElasticsearchUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/certifier")
@CrossOrigin
public class CertifierController {

    @Autowired
    private CertifierService certifierService;

    private String indexName = "dataset_index";
    private String esType = "dataset";

    @ApiOperation(value = "获取认证人列表", notes = "获取认证人列表", httpMethod = "GET")
    @GetMapping
    public Result getCertifier() {
        List<Certifier> certifierList = certifierService.getCertifier();
        return new Result(0, "SUCCESS", certifierList);
    }

    @ApiOperation(value = "认证人获取待认证列表", notes = "认证人获取待认证列表", httpMethod = "GET")
    @GetMapping("/{certifier}")
    public Result getToBeCertificated(@PathVariable String certifier) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryCertifier = QueryBuilders.matchQuery("certifier", certifier);
        MatchQueryBuilder queryIsCertificated = QueryBuilders.matchQuery("isCertificated", 0);
        boolQuery.must(queryCertifier);
        boolQuery.must(queryIsCertificated);
        List<Map<String, Object>> certificationList = ElasticsearchUtil.searchListData(indexName, esType, boolQuery, null, null, null, null);
        return new Result(0, "SUCCESS", certificationList);
    }

    @ApiOperation(value = "认证人获取待认证列表", notes = "认证人获取待认证列表", httpMethod = "GET")
    @PostMapping
    public Result certificate(@RequestBody CertificationVo req) {
        Map<String, Object> map = ElasticsearchUtil.searchDataById(indexName, esType, req.getId(), null);
        if (map == null) {
            return new Result(500, "NOT_FOUND", "");
        }
        if (!map.get("certifier").equals(req.getCertifier())) {
            return new Result(500, "NO_PERMISSION", "");
        }
        Map<String, Object> cert = new HashMap<>();
        cert.put("isCertificated", 1);
        ElasticsearchUtil.updateDataById(cert, indexName, esType, req.getId());
        return new Result(0, "SUCCESS", "SUCCESS");
    }
}