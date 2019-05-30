package com.ontology.service;


import com.ontology.bean.EsPage;
import com.ontology.controller.vo.*;

public interface OrderService {

    String createOrder(String action, OrderVo orderVo);

    EsPage getAllOrder(String action, PageQueryVo req);

    EsPage findSelfOrder(String action, SelfOrderVo req);

    String purchase(String action, PurchaseVo req);

    String getData(String action, CheckVo req);
}
