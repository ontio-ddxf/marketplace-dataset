package com.ontology.controller;

import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.OrderService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "合约调用接口")
@RestController
@RequestMapping("/api/v1/data-dealer/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;


    @ApiOperation(value="挂单创建order", notes="挂单创建order" ,httpMethod="POST")
    @PostMapping
    public Result createOrder(@RequestBody OrderVo orderVo) {
        String action = "createOrder";
        String txHash = orderService.createOrder(action,orderVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value="查询所有挂单", notes="查询所有挂单" ,httpMethod="POST")
    @PostMapping("/all")
    public Result getAllOrder(@RequestBody PageQueryVo req) {
        String action = "getAllOrder";
        EsPage allOrder = orderService.getAllOrder(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), allOrder);
    }

    @ApiOperation(value="查询自己的订单", notes="查询自己的订单" ,httpMethod="POST")
    @PostMapping("/self")
    public Result findSelfOrder(@RequestBody SelfOrderVo req) {
        String action = "findSelfOrder";
        EsPage selfOrder = orderService.findSelfOrder(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), selfOrder);
    }

    @ApiOperation(value="购买商品", notes="购买商品" ,httpMethod="POST")
    @PostMapping("/purchase")
    public Result purchase(@RequestBody PurchaseVo req) {
        String action = "purchase";
        String txHash = orderService.purchase(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value="查看数据", notes="查看数据" ,httpMethod="POST")
    @PostMapping("/data")
    public Result getData(@RequestBody CheckVo req) {
        String action = "getData";
        String data = orderService.getData(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), data);
    }

}
