package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.OnsVo;
import com.ontology.service.OnsService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "域名接口")
@RestController
@RequestMapping("/api/v1/ons")
@CrossOrigin
public class OnsController {

    @Autowired
    private OnsService onsService;


    @ApiOperation(value="ontid注册域名", notes="ontid注册域名" ,httpMethod="POST")
    @PostMapping("/register")
    public Result register(@RequestBody OnsVo req) throws Exception {
        String action = "register";
        String ontid = req.getOntid();
        String domain = req.getDomain();
        Map<String,Object> result = onsService.registerOns(action, ontid, domain);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value="ontid登录后检查是否存在域名", notes="ontid登录后检查是否存在域名" ,httpMethod="GET")
    @GetMapping(value = "/api/v1/data-dealer/ons/login/{ontid}")
    public Result login(@PathVariable String ontid) {
        String action = "login";

        String ons = onsService.loginOns(action, ontid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ons);
    }
}
