package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.exception.MarketplaceException;
import com.ontology.service.OrderService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;


//    @ApiOperation(value="挂单授权MP生成token", notes="挂单授权MP生成token" ,httpMethod="POST")
//    @PostMapping
//    public Result createOrder(@RequestBody OrderVo orderVo) {
//        String action = "authOrder";
//        String txHash = orderService.createOrder(action,orderVo);
//        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
//    }

    @ApiOperation(value = "构造挂单的交易", notes = "构造挂单的交易", httpMethod = "POST")
    @PostMapping
    public Result authOrder(@RequestBody OrderVo req) throws Exception {
        String action = "authOrder";
        Map<String,Object> result = orderService.authOrder(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "回调返回交易签名数据并发送交易", notes = "回调返回交易签名数据并发送交易", httpMethod = "POST")
    @PostMapping("/invoke/auth")
    public Result invokeAuth(@RequestBody TransactionDto req) throws Exception {
        String action = "invoke";
        return orderService.invokeAuth(action,req);
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

    /**
     * 为防止多人重复购买，本地先记录购买状态
     * @param req
     * @return
     */
//    @ApiOperation(value="购买商品", notes="购买商品" ,httpMethod="POST")
//    @PostMapping("/purchase")
//    public Result purchase(@RequestBody PurchaseVo req) {
//        String action = "purchase";
//        String txHash = orderService.purchase(action,req);
//        if (txHash == null) {
//            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(),ErrorInfo.PARAM_ERROR.descEN(),ErrorInfo.PARAM_ERROR.code());
//        }
//        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
//    }

    @ApiOperation(value = "构造下单的交易", notes = "构造下单的交易", httpMethod = "POST")
    @PostMapping("/purchase")
    public Result purchaseOrder(@RequestBody PurchaseVo req) throws Exception {
        String action = "purchaseOrder";
        Map<String,Object> result = orderService.purchaseOrder(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "回调返回交易签名数据并发送交易", notes = "回调返回交易签名数据并发送交易", httpMethod = "POST")
    @PostMapping("/invoke/purchase")
    public Result invokePurchase(@RequestBody TransactionDto req) throws Exception {
        String action = "invokePurchase";
        return orderService.invokePurchase(action,req);
    }

    @ApiOperation(value="查看数据", notes="查看数据" ,httpMethod="POST")
    @PostMapping("/data")
    public Result getData(@RequestBody CheckVo req) {
        String action = "getData";
        String data = orderService.getData(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), data);
    }

    /**
     * 构造交易的时候需要tokrnId
     * @param id
     * @return
     */
    @ApiOperation(value="查询当前tokenId", notes="查询当前tokenId" ,httpMethod="GET")
    @GetMapping("/token/{id}")
    public Result getCurrentTokenId(@PathVariable String id) {
        String action = "getCurrentTokenId";
        int tokenId = orderService.getCurrentTokenId(action,id);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), tokenId);
    }

    @ApiOperation(value="查询token剩余流转次数和访问次数", notes="查询token剩余流转次数和访问次数" ,httpMethod="GET")
    @GetMapping("/token/balance/{tokenId}")
    public Result getTokenBalance(@PathVariable int tokenId) throws Exception {
        String action = "getTokenBalance";
        Map<String,Object> data = orderService.getTokenBalance(action,tokenId);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), data);
    }

    @ApiOperation(value="二次挂单创建order", notes="二次挂单创建order" ,httpMethod="POST")
    @PostMapping("/second")
    public Result createSecondOrder(@RequestBody OrderVo orderVo) {
        String action = "createSecondOrder";
        String txHash = orderService.createSecondOrder(action,orderVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value="查询二手商品", notes="查询二手商品" ,httpMethod="POST")
    @PostMapping("/all/second")
    public Result findSecondOrder(@RequestBody PageQueryVo req) {
        String action = "findSecondOrder";
        EsPage allOrder = orderService.findSecondOrder(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), allOrder);
    }

    @ApiOperation(value="购买二手商品", notes="购买二手商品" ,httpMethod="POST")
    @PostMapping("/purchase/second")
    public Result purchaseSecondOrder(@RequestBody PurchaseVo purchaseVo) {
        String action = "purchaseSecondOrder";
        String txHash = orderService.purchaseSecondOrder(action,purchaseVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

}
