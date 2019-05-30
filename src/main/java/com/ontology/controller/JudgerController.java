package com.ontology.controller;

import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.JudgeVo;
import com.ontology.entity.Judger;
import com.ontology.service.JudgerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        String action = "getJudger";
        List<Judger> judgerList = judgerService.getJudger();
        return new Result(action,0, "SUCCESS", judgerList);
    }

    @ApiOperation(value = "获取待仲裁列表", notes = "获取待仲裁列表", httpMethod = "POST")
    @PostMapping
    public Result getTobeJudged(@RequestBody JudgeVo req) {
        String action = "getTobeJudged";
        EsPage esPage = judgerService.getTobeJudged(action,req);
        return new Result(action,0, "SUCCESS", esPage);
    }
}