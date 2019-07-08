package com.ontology.service;


import com.ontology.bean.EsPage;
import com.ontology.controller.vo.*;

import java.util.Map;

public interface OrderService {

    String createOrder(String action, OrderVo orderVo);

    EsPage getAllOrder(String action, PageQueryVo req);

    String purchase(String action, PurchaseVo req);

    EsPage findSelfOrder(String action, SelfOrderVo req);

    String getData(String action, CheckVo req);

    int getCurrentTokenId(String action, String id);

    Map<String, Object> getTokenBalance(String action, int tokenId) throws Exception;

    String createSecondOrder(String action, OrderVo orderVo);

    EsPage findSecondOrder(String action, PageQueryVo req);

    String purchaseSecondOrder(String action, PurchaseVo purchaseVo);

}
