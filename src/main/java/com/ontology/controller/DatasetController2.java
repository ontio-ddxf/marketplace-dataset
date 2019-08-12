package com.ontology.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.exception.MarketplaceException;
import com.ontology.service.ContractService;
import com.ontology.service.DatasetService;
import com.ontology.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/dataset")
@CrossOrigin
public class DatasetController2 {
    @Autowired
    private ContractService contractService;
    @Autowired
    private SDKUtil sdkUtil;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private DatasetService datasetService;


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
        String name = data.getName();
        String desc = data.getDesc();
        String img = data.getImg();
        List<String> keywords = data.getKeywords();
        String dataSource = req.getDataSource();
        if (CollectionUtils.isEmpty(keywords)) {
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
        obj.put("authId", "");
        obj.put("name", name);
        obj.put("desc", desc);
        obj.put("img", img);
        obj.put("provider", ontid);
        obj.put("token", "");
        obj.put("price", "");
        obj.put("amount", 0);
        obj.put("createTime", date);
        obj.put("certifier", certifier);
        obj.put("isCertificated", 0);
        obj.put("judger", "");
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
            // 新增数据 state:0-未上传；1-已上传（生成dataId）；2-挂单；3-撤单；4-售完
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
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), id);
    }


    @ApiOperation(value = "根据id查询数据", notes = "根据id查询数据", httpMethod = "GET")
    @GetMapping("/{id}")
    public Result getData(@PathVariable String id) {
        String action = "getData";
        Map<String, Object> result = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        ElasticsearchUtil.formatResult(result);
        result.remove("dataSource");
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }


    @ApiOperation(value = "根据卖家ontid查询数据", notes = "根据卖家ontid查询数据", httpMethod = "GET")
    @GetMapping("/provider/{ontid}")
    public Result getDataByProvider(@PathVariable String ontid, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        String action = "getDataByProvider";
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryProvider = QueryBuilders.matchQuery("provider", ontid);
        boolQuery.must(queryProvider);
        boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_DATASET);
        if (!indexExist) {
            return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), null);
        }
        EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, pageNum, pageSize, boolQuery, null, "createTime.keyword", null);
        List<Map<String, Object>> recordList = esPage.getRecordList();
        for (Map<String, Object> map : recordList) {
            ElasticsearchUtil.formatResult(map);
        }
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), esPage);
    }

    @ApiOperation(value = "构造注册dataId的交易", notes = "构造注册dataId的交易", httpMethod = "POST")
    @PostMapping("/dataId")
    public Result registerDataId(@RequestBody AuthVo req) throws Exception {
        String action = "registerDataId";
        Map<String,Object> result = datasetService.registerDataIdAndPost(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "回调返回交易签名数据并发送交易", notes = "回调返回交易签名数据并发送交易", httpMethod = "POST")
    @PostMapping("/dataId/invoke")
    public JSONObject invokeResult(@RequestBody MultiTransactionDto req) throws Exception {
        String action = "invoke";
        return datasetService.invokeResult(action,req);
    }

    @ApiOperation(value = "根据dataId查询数据", notes = "根据dataId查询数据", httpMethod = "GET")
    @GetMapping("/data/{dataId}")
    public Result getDatabyDataId(@PathVariable String dataId) {
        String action = "getDatabyDataId";
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryToken = QueryBuilders.matchQuery("dataId", dataId);
        boolQuery.must(queryToken);
        List<Map<String, Object>> dataList = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, boolQuery, null, null, null, null);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        Map<String, Object> dataset = dataList.get(0);
        ElasticsearchUtil.formatResult(dataset);
        dataset.remove("dataSource");
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), dataset);
    }

}