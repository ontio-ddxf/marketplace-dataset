package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.ContractVo;
import com.ontology.controller.vo.DataIdVo;
import com.ontology.controller.vo.SigVo;
import com.ontology.service.ContractService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "合约调用接口")
@RestController
@RequestMapping("/api/v1/data-dealer/contract")
@CrossOrigin
public class ContractController {

    @Autowired
    private ContractService contractService;


    @ApiOperation(value="构造交易", notes="构造交易" ,httpMethod="POST")
    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public Result makeTransaction(@RequestBody ContractVo contractVo) throws Exception {
        String action = "makeTransaction";
        String txHex = contractService.makeTransaction(action,contractVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHex);
    }

    @ApiOperation(value="发送交易", notes="发送交易" ,httpMethod="POST")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public Result sendTransaction(@RequestBody SigVo sigVo) throws Exception {
        String action = "sendTransaction";
        String txHash = contractService.sendTransaction(action,sigVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value="注册dataId和tokenId", notes="注册dataId和tokenId" ,httpMethod="POST")
    @RequestMapping(value = "/dataid", method = RequestMethod.POST)
    public Result dataid(@RequestBody DataIdVo dataIdVo) throws Exception {
        String action = "dataid";
        List<String> txHex = contractService.registerDataId(action,dataIdVo);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHex);
    }

}
