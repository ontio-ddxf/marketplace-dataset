package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.EsPage;
import com.ontology.controller.vo.*;
import com.ontology.exception.MarketplaceException;
import com.ontology.service.ContractService;
import com.ontology.service.OrderService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetResponse;
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
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private SDKUtil sdkUtil;

    /**
     * 修改dataset表状态代表上架
     *
     * @param action
     * @param orderVo
     * @return
     */
    @Override
    public String createOrder(String action, OrderVo orderVo) {
        String id = orderVo.getId();
        String token = orderVo.getToken();
        String price = orderVo.getPrice();
        Integer amount = orderVo.getAmount();
        List<String> ojList = orderVo.getOjList();
        SigVo sigVo = orderVo.getSigVo();

        GetResponse response = ElasticsearchUtil.searchVersionById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        long version = response.getVersion();
        Map<String, Object> data = response.getSource();
        String state = (String) data.get("state");
        if ("0".equals(state) || "2".equals(state)) {
            throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(), ErrorInfo.NO_PERMISSION.descEN(), ErrorInfo.NO_PERMISSION.code());
        }
        try {
            String txHash = contractService.sendTransaction("snedTransaction", sigVo);

            Map<String, Object> map = new HashMap<>();
            map.put("state", "2");
            map.put("token", token);
            map.put("price", price);
            map.put("amount", amount);
            map.put("judger", JSON.toJSONString(ojList));
            ElasticsearchUtil.updateDataByIdAndVersion(map, Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, version);
            return txHash;
        } catch (Exception e) {
            log.error("catch exception:", e);
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
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
        MatchQueryBuilder queryState = QueryBuilders.matchQuery("state", "2");
        boolQuery.must(queryState);
        MatchQueryBuilder queryAuthId = QueryBuilders.matchQuery("authId.keyword", "");
        boolQuery.mustNot(queryAuthId);
        try {
            // 查询索引是否存在，不存在直接返回空
            boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_DATASET);
            if (!indexExist) {
                return null;
            }
            EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_DATASET, Constant.ES_INDEX_DATASET, pageNum, pageSize, boolQuery, null, "createTime.keyword", null);

            List<Map<String, Object>> recordList = esPage.getRecordList();
            for (Map<String, Object> result : recordList) {
                ElasticsearchUtil.formatOrderResult(result);
                result.remove("dataSource");
                JSONArray judger = JSONArray.parseArray((String) result.get("judger"));
                result.put("judger", judger);
            }
            return esPage;
        } catch (IndexNotFoundException e) {
            log.error("catch exception:", e);
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
        String sort = null;
        if (type == 1) {
            // 买家
            queryType = QueryBuilders.matchQuery("demander", ontid);
            MatchQueryBuilder queryState = QueryBuilders.matchQuery("state", "6");
            boolQuery.mustNot(queryState);
            sort = "boughtTime.keyword";
        } else if (type == 2) {
            // 卖家
            queryType = QueryBuilders.matchQuery("provider", ontid);
            sort = "createTime.keyword";
        }
        boolQuery.must(queryType);

        MatchQueryBuilder queryOrderId = QueryBuilders.matchQuery("orderId.keyword", "");
        boolQuery.mustNot(queryOrderId);

        boolean indexExist = ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_ORDER);
        if (!indexExist) {
            return null;
        }

        EsPage esPage = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, pageNum, pageSize, boolQuery, null, sort, null);

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
                    log.error("catch exception:", e);
                }
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
    }

    @Override
    public String purchase(String action, PurchaseVo req) {
        String id = req.getId();
        String demander = req.getDemanderOntid();
        String demanderAddress = req.getDemanderAddress();
        String judger = req.getJudger();
        SigVo sigVo = req.getSigVo();
        String name = req.getName();
        String desc = req.getDesc();
        String img = req.getImg();
        List<String> keywords = req.getKeywords();

        Integer expireTime = 10;
        long time = new Date().getTime() + (expireTime * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expireDate = sdf.format(new Date(time));
        String createTime = sdf.format(new Date());

        Map<String, Object> data = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_INDEX_DATASET, id, null);
        String authId = (String) data.get("authId");
        String dataId = (String) data.get("dataId");
        String provider = (String) data.get("ontid");
        String token = (String) data.get("token");
        String price = (String) data.get("price");

        String txHash = sendAndCreateOrder(action,sigVo,"",authId,dataId,name,desc,img,provider,demander,demanderAddress,
                token,price,judger,"2",createTime,createTime,expireDate,keywords);
        return txHash;
    }


    @Override
    public String getData(String action, CheckVo req) {
        String id = req.getId();
        String ontid = req.getOntid();
        SigVo sigVo = req.getSigVo();

        try {
            // 先发送交易
            String txHash = contractService.sendTransaction("snedTransaction", sigVo);

            Map<String, Object> order = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, id, null);
            String demanderOntid = (String) order.get("demanderOntid");
            String state = (String) order.get("state");
            // 验证订单状态和买家身份
            if (!demanderOntid.equals(ontid)) {
                throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(), ErrorInfo.NO_PERMISSION.descEN(), ErrorInfo.NO_PERMISSION.code());
            }
            if ("1".equals(state)) {
                throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(), ErrorInfo.NO_PERMISSION.descEN(), ErrorInfo.NO_PERMISSION.code());
            }
            // 查找数据源
            String dataId = (String) order.get("dataId");
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            MatchQueryBuilder queryToken = QueryBuilders.matchQuery("dataId", dataId);
            boolQuery.must(queryToken);
            List<Map<String, Object>> dataList = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, boolQuery, null, null, null, null);
            if (CollectionUtils.isEmpty(dataList)) {
                throw new MarketplaceException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
            }
            Map<String, Object> dataset = dataList.get(0);
            String data = (String) dataset.get("dataSource");
            return data;
        } catch (Exception e) {
            log.error("catch exception:",e);
            throw new MarketplaceException(action, ErrorInfo.NO_PERMISSION.descCN(), ErrorInfo.NO_PERMISSION.descEN(), ErrorInfo.NO_PERMISSION.code());
        }
    }

    @Override
    public int getCurrentTokenId(String action, String id) {
        Map<String, Object> data = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, id, null);
        String tokenRange = (String) data.get("tokenRange");
        String[] split = tokenRange.split(",");
        int length = split.length;
        if (length % 2 == 1) {
            throw new MarketplaceException(action, ErrorInfo.DB_ERROR.descCN(), ErrorInfo.DB_ERROR.descEN(), ErrorInfo.DB_ERROR.code());
        }
        int currentTokenId = 0;
        boolean haveToken = false;
        for (int j = 0; j < (length / 2); j++) {
            int startToken = Integer.parseInt(split[j * 2]);
            int endToken = Integer.parseInt(split[j * 2 + 1]);
            if (startToken > endToken) {
                continue;
            }
            currentTokenId = startToken;
            haveToken = true;
            break;
        }

        // 没有可用token，结束挂单
        if (!haveToken) {
            throw new MarketplaceException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        return currentTokenId;
    }

    @Override
    public Map<String, Object> getTokenBalance(String action, int tokenId) throws Exception {
        List<Map<String, Object>> argList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "tokenId");
        map.put("value", tokenId);
        argList.add(map);
        String transferCountParams = Helper.getParams("", configParam.CONTRACT_HASH_DTOKEN, "getTransferCount", argList, configParam.PAYER_ADDRESS);
        String accessCountParams = Helper.getParams("", configParam.CONTRACT_HASH_DTOKEN, "getAccessCount", argList, configParam.PAYER_ADDRESS);
        String expireTimeParams = Helper.getParams("", configParam.CONTRACT_HASH_DTOKEN, "getExpireTime", argList, configParam.PAYER_ADDRESS);
        JSONObject transferObj = (JSONObject) sdkUtil.sendPreTransaction(transferCountParams);
        JSONObject accessObj = (JSONObject) sdkUtil.sendPreTransaction(accessCountParams);
        JSONObject expireObj = (JSONObject) sdkUtil.sendPreTransaction(expireTimeParams);
        String transferResult = transferObj.getString("Result");
        String accessResult = accessObj.getString("Result");
        String expireResult = expireObj.getString("Result");

        log.info("transferResult:{}", transferResult);
        log.info("accessResult:{}", accessResult);
        log.info("expireResult:{}", expireResult);

        Map<String, Object> result = new HashMap<>();
        result.put("transferCount", Helper.isEmptyOrNull(transferResult) ? 0 : Long.parseLong(com.github.ontio.common.Helper.reverse(transferResult), 16));
        result.put("accessCount", Helper.isEmptyOrNull(accessResult) ? 0 : Long.parseLong(com.github.ontio.common.Helper.reverse(accessResult), 16));
        result.put("expireTimeCount", Helper.isEmptyOrNull(expireResult) ? 1 : Long.parseLong(com.github.ontio.common.Helper.reverse(expireResult), 16));
        return result;
    }

    @Override
    public String createSecondOrder(String action, OrderVo orderVo) {
        String dataId = orderVo.getDataId();
        String tokenId = orderVo.getTokenId();
        String name = orderVo.getName();
        String desc = orderVo.getDesc();
        String img = orderVo.getImg();
        String providerOntid = orderVo.getProviderOntid();
        String token = orderVo.getToken();
        String price = orderVo.getPrice();
//        Integer amount = orderVo.getAmount();
        List<String> ojList = orderVo.getOjList();
        List<String> keywords = orderVo.getKeywords();
        SigVo sigVo = orderVo.getSigVo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sdf.format(new Date());

        String txHash = sendAndCreateOrder(action,sigVo,tokenId,"",dataId,name,desc,img,providerOntid,"","",token,price,JSON.toJSONString(ojList),"1",createTime,"","",keywords);

        // 修改原order状态
        String id = orderVo.getId();
        Map<String, Object> origin = new HashMap<>();
        origin.put("state", "6");
        ElasticsearchUtil.updateDataById(origin, Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, id);

        return txHash;

    }

    @Override
    public String purchaseSecondOrder(String action, PurchaseVo purchaseVo) {
        String id = purchaseVo.getId();
        String demander = purchaseVo.getDemanderOntid();
        String demanderAddress = purchaseVo.getDemanderAddress();
        SigVo sigVo = purchaseVo.getSigVo();
        Integer expireTime = 10;
        long time = new Date().getTime() + (expireTime * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expireDate = sdf.format(new Date(time));
        String boughtTime = sdf.format(new Date());

        try {
            String txHash = contractService.sendTransaction("snedTransaction", sigVo);
            Map<String, Object> order = new HashMap<>();
            order.put("demander", demander);
            order.put("demanderAddress", demanderAddress);
            order.put("boughtTime", boughtTime);
            order.put("expireTime", expireDate);
            order.put("state", "2");
            ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER,id);
            return txHash;
        } catch (Exception e) {
            log.error("catch exception:", e);
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    private String sendAndCreateOrder(String action, SigVo sigVo, String tokenId, String authId, String dataId, String name, String desc,
                                      String img, String provider, String demander, String demanderAddress, String token, String price,
                                      String judger, String state, String createTime, String boughtTime, String expireTime, List<String> keywords) {
        try {
            // 先发送交易
            String txHash = contractService.sendTransaction("snedTransaction", sigVo);

            Map<String, Object> order = new LinkedHashMap<>();
            order.put("orderId", "");
            order.put("tokenId", tokenId);
            order.put("authId", authId);
            order.put("dataId", dataId);
            order.put("name", name);
            order.put("desc", desc);
            order.put("img", img);
            order.put("provider", provider);
            order.put("demander", demander);
            order.put("demanderAddress", demanderAddress);
            order.put("token", token);
            order.put("price", price);
            order.put("judger", judger);
            order.put("arbitrage", "");
            order.put("state", state);
            order.put("createTime", createTime);
            order.put("boughtTime", boughtTime);
            order.put("expireTime", expireTime);
            order.put("cancelTime", "");
            order.put("confirmTime", "");
            for (int i = 0; i < keywords.size(); i++) {
                order.put("column" + i, keywords.get(i));
            }
            ElasticsearchUtil.addData(order, Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER);
            return txHash;
        } catch (Exception e) {
            log.error("catch exception:", e);
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

}
