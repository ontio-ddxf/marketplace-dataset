package com.ontology.controller;

import com.alibaba.fastjson.JSON;
import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.DataVo;
import com.ontology.controller.vo.PageQueryVo;
import com.ontology.controller.vo.QueryVo;
import com.ontology.utils.ElasticsearchUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dataset")
@CrossOrigin
public class DatasetController {

    private String indexName = "dataset_index";

    private String esType = "dataTag";

    /**
     * 新增或更新数据
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "新增或修改数据", notes = "新增修改数据", httpMethod = "PUT")
    @PutMapping
    public Result updateData(@RequestBody DataVo req) {
            String id = req.getId();
            String ontid = req.getOntid();
            String coin = req.getCoin();
            String price = req.getPrice();
            List<String> data = req.getData();
            if (CollectionUtils.isEmpty(data)) {
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
            obj.put("coin", coin);
            obj.put("price", price);
            obj.put("createTime", date);
            for (int i = 0; i < data.size(); i++) {
                obj.put("tag" + i, data.get(i));
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
                ElasticsearchUtil.addData(obj, indexName, esType, id);
            } else {
                // 旧数据的tag数
                int j = 0;
                for (int i = 0; ; i++) {
                    if (!exist.containsKey("tag" + i)) {
                        j = i;
                        break;
                    }
                }
                // 旧数据tag字段长于新数据，将旧数据多出的tag置为"";
                if (j > data.size()) {
                    for (int k = data.size(); k < j; k++) {
                        obj.put("tag" + k, "");
                    }
                }
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
        if (CollectionUtils.isEmpty(queryParams)) {
            return new Result(400, "PARAMS_ERROR", "");
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        int i = 0;
        for (QueryVo vo : queryParams) {
            if (StringUtils.isEmpty(vo.getText())) {
                i++;
                continue;
            }
            if (vo.getPercent() > 100) {
                vo.setPercent(100);
            } else if (vo.getPercent() < 0) {
                vo.setPercent(0);
            }
            MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("tag" + i, vo.getText()).minimumShouldMatch(vo.getPercent() + "%");
            boolQuery.must(queryBuilder);
            i++;
        }
        EsPage list = ElasticsearchUtil.searchDataPage(indexName, esType, pageIndex, pageSize, boolQuery, null, "createTime.keyword", null);
        return new Result(0, "SUCCESS", list);
    }

    @ApiOperation(value = "根据id查询数据", notes = "根据id查询数据", httpMethod = "GET")
    @GetMapping("/{id}")
    public Result getData(@PathVariable String id) {
        Map<String, Object> result = ElasticsearchUtil.searchDataById(indexName, esType, id, null);
        return new Result(0, "SUCCESS", result);
    }

}