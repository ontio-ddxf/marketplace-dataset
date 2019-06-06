package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ontology.bean.EsPage;
import com.ontology.controller.vo.*;
import com.ontology.exception.MarketplaceException;
import com.ontology.service.ContractService;
import com.ontology.service.OrderService;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ContractService contractService;

    /**
     * 需要记录身份ontid，检索条件等，所以需要同步到区块事件前提前创建/修改order
     * @param action
     * @param orderVo
     * @return
     */
    @Override
    public String createOrder(String action, OrderVo orderVo) {
        String dataId = orderVo.getDataId();
        String tokenId = orderVo.getTokenId();
        String name = orderVo.getName();
        String desc = orderVo.getDesc();
        String img = orderVo.getImg();
        String providerOntid = orderVo.getProviderOntid();
        String tokenHash = orderVo.getTokenHash();
        String price = orderVo.getPrice();
        Integer amount = orderVo.getAmount();
        List<String> ojList = orderVo.getOjList();
        List<String> keywords = orderVo.getKeywords();
        SigVo sigVo = orderVo.getSigVo();
        // 先发送交易
        try {
            String txHash = contractService.sendTransaction("snedTransaction", sigVo);
            // es创建order
            Map<String,Object> order = new LinkedHashMap<>();
            order.put("orderId","");
            order.put("dataId",dataId);
            order.put("tokenId",tokenId);
            order.put("name",name);
            order.put("desc",desc);
            order.put("img",img);
            order.put("providerOntid",providerOntid);
            order.put("demanderOntid","");
            order.put("tokenHash",tokenHash);
            order.put("price",price);
            order.put("amount",amount);
            order.put("judger",JSON.toJSONString(ojList));
            order.put("arbitrage","");
            // state:1-挂单；2-挂单上链；3-购买；4-购买上链；5-确认；6-确认上链；7-仲裁；8-仲裁上链；0-取消
            // 对应显示：1-挂单中；2-正在出售；3-购买中；4-购买成功；5-确认中；6-已确认
            // state:1-挂单；2-购买；3-确认；4-仲裁；5-仲裁结果；0-取消
            // 对应显示：1-正在出售；2-购买成功；3-已确认；4-仲裁中，5-仲裁结束
            order.put("state","");
            order.put("createTime", JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss").replace("\"", ""));
            order.put("boughtTime", "");
            order.put("cancelTime", "");
            order.put("confirmTime", "");
            order.put("expireTime", "");
            for (int i = 0; i < keywords.size(); i++) {
                order.put("column" + i, keywords.get(i));
            }
            ElasticsearchUtil.addData(order, Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER);
            return txHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EsPage getAllOrder(String action, PageQueryVo req) {
        Integer pageNum = req.getPageNum();
        Integer pageSize = req.getPageSize();
        List<QueryVo> queryParams = req.getQueryParams();

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
        MatchQueryBuilder queryState = QueryBuilders.matchQuery("state", "1");
        boolQuery.must(queryState);
        try {
            // 查询索引是否存在，不存在直接返回空
            boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_ORDER);
            if (!indexExist) {
                return null;
            }
            EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, pageNum, pageSize, boolQuery, null, "createTime.keyword", null);

            List<Map<String, Object>> recordList = esPage.getRecordList();
            for (Map<String, Object> result : recordList) {
                ElasticsearchUtil.formatOrderResult(result);
                JSONArray judger = JSONArray.parseArray((String) result.get("judger"));
                result.put("judger", judger);
            }
            return esPage;
        } catch (IndexNotFoundException e) {
            ElasticsearchUtil.createIndex(Constant.ES_INDEX_ORDER);
        }
        return null;
    }

    @Override
    public EsPage findSelfOrder(String action, SelfOrderVo req) {
        Integer pageNum = req.getPageNum();
        Integer pageSize = req.getPageSize();
        Integer type = req.getType();
        String ontid = req.getOntid();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryType = null;
        if (type==1) {
            // 买家
            queryType = QueryBuilders.matchQuery("demanderOntid", ontid);
        } else if (type==2) {
            // 卖家
            queryType = QueryBuilders.matchQuery("providerOntid", ontid);
        }
        boolQuery.must(queryType);
        try {
            boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_ORDER);
            if (!indexExist) {
                return null;
            }
            EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, pageNum, pageSize, boolQuery, null, "createTime.keyword", null);
            // 判断订单是否超时:1-超时；2-未超时
            List<Map<String, Object>> recordList = esPage.getRecordList();
            for (Map<String, Object> order : recordList) {
                ElasticsearchUtil.formatOrderResult(order);
                String expireTime = (String) order.get("expireTime");
                if (StringUtils.isNotEmpty(expireTime)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date expireDate = null;
                    try {
                        expireDate = sdf.parse(expireTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    Date expireDate = JSON.parseObject(expireTime, Date.class);
                    if (new Date().after(expireDate)) {
                        // 超时
                        order.put("isExpired", "1");
                    } else {
                        // 未超时
                        order.put("isExpired", "0");
                    }
                } else {
                    order.put("isExpired", "0");
                }
            }
            return esPage;
        } catch (IndexNotFoundException e) {

        }
        return null;
    }

    @Override
    public String purchase(String action, PurchaseVo req) {
        String id = req.getId();
        String demanderOntid = req.getDemanderOntid();
        String judger = req.getJudger();
        Integer expireTime = 10;
        long i = new Date().getTime() + (expireTime * 60 * 1000);
        Date expireDate = new Date(i);
        SigVo sigVo = req.getSigVo();
        try {
            // 先发送交易
            String txHash = contractService.sendTransaction("snedTransaction", sigVo);

            Map<String, Object> order = new HashMap<>();
            order.put("demanderOntid",demanderOntid);
            order.put("judger",judger);
            order.put("state","2");
            order.put("boughtTime", JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss").replace("\"", ""));
            order.put("expireTime", JSON.toJSONStringWithDateFormat(expireDate, "yyyy-MM-dd HH:mm:ss").replace("\"", ""));
            ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_ORDER,Constant.ES_TYPE_ORDER,id);
            return txHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getData(String action, CheckVo req) {
        String id = req.getId();
        String ontid = req.getOntid();
        Map<String, Object> order = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, id, null);
        String demanderOntid = (String) order.get("demanderOntid");
        String state = (String) order.get("state");
        // 验证订单状态和买家身份
        if (!demanderOntid.equals(ontid)) {
            throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(),ErrorInfo.NO_PERMISSION.descEN(),ErrorInfo.NO_PERMISSION.code());
        }
        if ("1".equals(state)) {
            throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(),ErrorInfo.NO_PERMISSION.descEN(),ErrorInfo.NO_PERMISSION.code());
        }
        // 查找数据源
        String tokenId = (String) order.get("tokenId");
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        MatchQueryBuilder queryToken = QueryBuilders.matchQuery("tokenId", tokenId);
        boolQuery.must(queryToken);
        List<Map<String, Object>> dataList = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, boolQuery, null, null, null, null);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(),ErrorInfo.NOT_FOUND.descEN(),ErrorInfo.NOT_FOUND.code());
        }
        Map<String, Object> dataset = dataList.get(0);
        String data = (String) dataset.get("dataSource");
        return data;
    }

}
