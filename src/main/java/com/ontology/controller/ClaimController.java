package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageDto;
import com.ontology.controller.vo.TransactionDto;
import com.ontology.service.ClaimService;
import com.ontology.service.OnsService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "claim接口")
@RestController
@RequestMapping("/api/v1/claim")
@CrossOrigin
public class ClaimController {

    @Autowired
    private ClaimService claimService;


    @ApiOperation(value="获取claim", notes="获取claim" ,httpMethod="GET")
    @GetMapping("/getClaim")
    public Result getClaim() throws Exception {
        String action = "getClaim";
        Map<String,Object> result = claimService.getClaim();

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "签名回调获取claim", notes = "签名回调获取claim", httpMethod = "POST")
    @PostMapping("/getClaim/callback")
    public Result getClaimCallback(@RequestBody TransactionDto req) {
        String action = "getClaim";
        Map<String, Object> result = claimService.getClaimCallback(req);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "claim授权", notes = "claim授权", httpMethod = "GET")
    @GetMapping("/postClaim")
    public Result postClaim() throws Exception {
        String action = "postClaim";
        Map<String, Object> result =  claimService.postClaim();
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "回调授权", notes = "回调授权", httpMethod = "POST")
    @PostMapping("/postClaim/callback/{id}")
    public Result postClaimCallBack(@PathVariable String id,@RequestBody TransactionDto req) {
        String action = "registerResult";
        Map<String, Object> result = claimService.postClaimCallBack(id,req);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

}
