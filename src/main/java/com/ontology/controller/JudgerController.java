package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.entity.Judger;
import com.ontology.service.JudgerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/judger")
@CrossOrigin
public class JudgerController {

    @Autowired
    private JudgerService judgerService;
    @ApiOperation(value = "获取仲裁者列表", notes = "获取仲裁者列表", httpMethod = "GET")
    @GetMapping
    public Result getJudger() {
        List<Judger> judgerList = judgerService.getJudger();
        return new Result(0, "SUCCESS", judgerList);
    }
}