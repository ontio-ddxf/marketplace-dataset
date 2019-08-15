package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageDto;
import com.ontology.controller.vo.MultiTransactionDto;
import com.ontology.controller.vo.TransactionDto;
import com.ontology.service.*;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "回调接口")
@RestController
@RequestMapping("/back")
@CrossOrigin
public class CallbackController {

    @Autowired
    private ClaimService claimService;
    @Autowired
    private CertifierService certifierService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DatasetService datasetService;
    @Autowired
    private OnsService onsService;

    @ApiOperation(value = "注册回调", notes = "注册回调", httpMethod = "POST")
    @PostMapping("/reg")
    public Result registerResult(@RequestBody TransactionDto req) throws Exception {
        String action = "registerResult";
        return onsService.invokeResult(action,req);
    }

    @ApiOperation(value = "登录回调", notes = "登录回调", httpMethod = "POST")
    @PostMapping("/login")
    public Map<String,Object> callback(@RequestBody MessageDto req) {
        String action = "login";
        Map<String,Object> result = onsService.callback(action,req);
        return result;
    }

    @ApiOperation(value = "认证回调", notes = "认证回调", httpMethod = "POST")
    @PostMapping("/cert")
    public Result certificate(@RequestBody MessageDto req) {
        String action = "certificate";
        return certifierService.callback(action,req);
    }

    @ApiOperation(value = "获取claim", notes = "获取claim", httpMethod = "POST")
    @PostMapping("/getClaim")
    public Result getClaim(@RequestBody TransactionDto req) {
        String action = "getClaim";
        Map<String, Object> result = claimService.getClaimCallback(req);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "授权claim", notes = "授权claim", httpMethod = "POST")
    @PostMapping("/postClaim/{id}")
    public Result postClaim(@PathVariable String id,@RequestBody TransactionDto req) {
        String action = "postClaim";
        Map<String, Object> result = claimService.postClaimCallBack(id,req);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "dataId及挂单回调", notes = "dataId及挂单回调", httpMethod = "POST")
    @PostMapping("/dataId")
    public Result invokeResult(@RequestBody MultiTransactionDto req) throws Exception {
        String action = "invokeDataIdAndAuth";
        return datasetService.invokeResult(action,req);
    }

    @ApiOperation(value = "挂单回调", notes = "挂单回调", httpMethod = "POST")
    @PostMapping("/auth")
    public Result invokeAuth(@RequestBody TransactionDto req) throws Exception {
        String action = "invokeAuth";
        return orderService.invokeAuth(action,req);
    }

    @ApiOperation(value = "下单回调", notes = "下单回调", httpMethod = "POST")
    @PostMapping("/purchase")
    public Result invokePurchase(@RequestBody TransactionDto req) throws Exception {
        String action = "invokePurchase";
        return orderService.invokePurchase(action,req);
    }

    @ApiOperation(value = "通用回调", notes = "通用回调", httpMethod = "POST")
    @PostMapping("/invoke")
    public Result invoke(@RequestBody TransactionDto req) throws Exception {
        String action = "invoke";
        return contractService.invoke(action, req);
    }

}
