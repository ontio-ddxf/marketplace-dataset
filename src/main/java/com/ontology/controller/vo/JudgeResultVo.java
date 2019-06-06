package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class JudgeResultVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="winOrLose",value = "仲裁结果")
    private Boolean winOrLose;
    @ApiModelProperty(name="sigVo",value = "仲裁交易签名信息")
    private SigVo sigVo;
}
