package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.MessageDto;
import com.ontology.controller.vo.OnsLoginDto;
import com.ontology.controller.vo.TransactionDto;
import com.ontology.service.OnsService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "域名接口")
@RestController
@RequestMapping("/api/v1/ons")
@CrossOrigin
public class OnsController {

    @Autowired
    private OnsService onsService;


    @ApiOperation(value="ontid注册域名", notes="ontid注册域名" ,httpMethod="GET")
    @GetMapping("/{ons}")
    public Result register(@PathVariable String ons) throws Exception {
        String action = "register";
        Map<String,Object> result = onsService.registerOns(action, ons);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "获取注册ons交易参数", notes = "获取注册ons交易参数", httpMethod = "GET")
    @GetMapping("/qrcode/{id}")
    public JSONObject getParams(@PathVariable String id) {
        String action = "getParams";
        JSONObject params = onsService.getParams(action,id);
        return params;
    }

    @ApiOperation(value = "回调返回交易hex", notes = "回调返回交易hex", httpMethod = "POST")
    @PostMapping("/invoke")
    public JSONObject invokeResult(@RequestBody TransactionDto req) throws Exception {
        String action = "invokeResult";
        return onsService.invokeResult(action,req);
    }

    @ApiOperation(value = "查询注册是否成功", notes = "查询注册是否成功", httpMethod = "GET")
    @GetMapping("/result/{id}")
    public Result registerResult(@PathVariable String id) {
        String action = "registerResult";
        String isSuccessful = onsService.registerResult(action,id);
        return new Result(action,0, "SUCCESS", isSuccessful);
    }

    @ApiOperation(value = "根据ontid和主域名获取ons列表", notes = "根据ontid和主域名获取ons列表", httpMethod = "GET")
    @GetMapping("/list")
    public Result getOnsList(String ontid,String domain) throws Exception {
        String action = "getOnsList";
        List<String> list = onsService.getOnsList(action,ontid,domain);
        return new Result(action,0, "SUCCESS", list);
    }


    @ApiOperation(value="ontid登录后检查是否存在域名", notes="ontid登录后检查是否存在域名" ,httpMethod="GET")
    @GetMapping(value = "/login")
    public Result login() {
        String action = "login";
        Map<String,Object> result = onsService.loginOns();

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "回调验证", notes = "回调验证", httpMethod = "POST")
    @PostMapping("/login/callback")
    public Map<String,Object> callback(@RequestBody MessageDto req) {
        String action = "login";
        Map<String,Object> result = onsService.callback(action,req);
        return result;
    }

    @ApiOperation(value = "查询登录是否成功", notes = "查询登录是否成功", httpMethod = "GET")
    @GetMapping("/login/result/{id}")
    public Result loginResult(@PathVariable String id) {
        String action = "loginResult";
        Map<String,Object> isSuccessful = onsService.loginResult(action,id);
        return new Result(action,0, "SUCCESS", isSuccessful);
    }
}
