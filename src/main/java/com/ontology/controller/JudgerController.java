package com.ontology.controller;

import com.ontology.bean.EsPage;
import com.ontology.bean.Result;
import com.ontology.controller.vo.JudgeResultVo;
import com.ontology.entity.Judger;
import com.ontology.exception.MarketplaceException;
import com.ontology.service.JudgerService;
import com.ontology.utils.ErrorInfo;
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

    @ApiOperation(value = "获取待仲裁列表", notes = "获取待仲裁列表", httpMethod = "GET")
    @GetMapping("/{ontid}")
    public Result getTobeJudged(@PathVariable String ontid,@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
        String action = "getTobeJudged";
        EsPage esPage = judgerService.getTobeJudged(action,ontid,pageNum,pageSize);
        return new Result(action,0, "SUCCESS", esPage);
    }

    @ApiOperation(value = "发送仲裁结果", notes = "发送仲裁结果", httpMethod = "POST")
    @PostMapping("/result")
    public Result judgeResult(@RequestBody JudgeResultVo req) {
        String action = "judgeResult";
        String txHash = judgerService.judgeResult(action,req);
        if (txHash == null) {
            throw new MarketplaceException(action, ErrorInfo.PARAM_ERROR.descCN(),ErrorInfo.PARAM_ERROR.descEN(),ErrorInfo.PARAM_ERROR.code());
        }
        return new Result(action,0, "SUCCESS", txHash);
    }
}