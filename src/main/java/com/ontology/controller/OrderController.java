package com.ontology.controller;

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

    /**
     * 为防止多人重复购买，本地先记录购买状态
     * @param req
     * @return
     */
    @ApiOperation(value="购买商品", notes="购买商品" ,httpMethod="POST")
    @PostMapping("/purchase")
    public Result purchase(@RequestBody PurchaseVo req) {
        String action = "purchase";
        String txHash = orderService.purchase(action,req);
        if (txHash == null) {
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(),ErrorInfo.PARAM_ERROR.descEN(),ErrorInfo.PARAM_ERROR.code());
        }
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value="查验数据", notes="查验数据" ,httpMethod="POST")
    @PostMapping("/data")
    public Result getData(@RequestBody CheckVo req) {
        String action = "getData";
        String data = orderService.getData(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), data);
    }

    @ApiOperation(value="查询当前tokenId", notes="查询当前tokenId" ,httpMethod="POST")
    @GetMapping("/token/{id}")
    public Result getCurrentTokenId(@PathVariable String id) {
        String action = "getCurrentTokenId";
        int tokenId = orderService.getCurrentTokenId(action,id);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), tokenId);
    }

    @ApiOperation(value="查询token剩余流转次数和访问次数", notes="查询token剩余流转次数和访问次数" ,httpMethod="POST")
    @GetMapping("/token/balance/{tokenId}")
    public Result getTokenBalance(@PathVariable int tokenId) throws Exception {
        String action = "getTokenBalance";
        Map<String,Object> data = orderService.getTokenBalance(action,tokenId);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), data);
    }

    @ApiOperation(value="挂单创建order", notes="挂单创建order" ,httpMethod="POST")
    @PostMapping("/second")
    public Result createSecondOrder(@RequestBody OrderVo orderVo) {
        String action = "createSecondOrder";
        String txHash = orderService.createSecondOrder(action,orderVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

}
