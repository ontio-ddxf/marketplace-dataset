package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.ContractService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Api(tags = "合约调用接口")
@RestController
@RequestMapping("/api/v1/contract")
@CrossOrigin
public class ContractController {

    @Autowired
    private ContractService contractService;

    @ApiOperation(value = "构造交易", notes = "构造交易", httpMethod = "POST")
    @PostMapping(value = "/transaction")
    public Result makeTransaction(@RequestBody ContractVo contractVo) throws Exception {
        String action = "makeTransaction";
        String txHex = contractService.makeTransaction(action, contractVo);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHex);
    }

    @ApiOperation(value = "构造交易", notes = "构造交易", httpMethod = "POST")
    @PostMapping
    public Result makeTransactionAndCallback(@RequestBody ContractVo contractVo) throws Exception {
        String action = "makeTransactionAndCallback";

        Map result = contractService.makeTransactionAndCallback(action, contractVo);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "回调返回交易签名数据并发送交易", notes = "回调返回交易签名数据并发送交易", httpMethod = "POST")
    @PostMapping("/invoke")
    public Result invokeResult(@RequestBody TransactionDto req) throws Exception {
        String action = "invoke";
        return contractService.invoke(action, req);
    }

    @ApiOperation(value = "发送交易", notes = "发送交易", httpMethod = "POST")
    @PostMapping(value = "/send")
    public Result sendTransaction(@RequestBody SigVo sigVo) throws Exception {
        String action = "sendTransaction";
        String txHash = contractService.sendTransaction(action, sigVo);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value = "注册dataId", notes = "注册dataId", httpMethod = "POST")
    @PostMapping(value = "/dataid")
    public Result dataid(@RequestBody DataIdVo dataIdVo) throws Exception {
        String action = "registerDataId";
        log.info(action);
        String txHex = contractService.registerDataId(action, dataIdVo);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHex);
    }

    @ApiOperation(value = "查询交易是否成功", notes = "查询交易是否成功", httpMethod = "GET")
    @GetMapping("/result/{id}")
    public Result registerResult(@PathVariable String id) {
        String action = "invokeResult";
        String isSuccessful = contractService.invokeResult(action, id);
        return new Result(action, 0, "SUCCESS", isSuccessful);
    }

    @ApiOperation(value = "发送荣誉值", notes = "发送荣誉值", httpMethod = "POST")
    @PostMapping("/honor")
    public Result postHonor(@RequestBody HonorVo req) throws Exception {
        String action = "postHonor";
        contractService.postHonor(action, req);
        return new Result(action, 0, "SUCCESS", "SUCCESS");
    }
}
