package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class JudgeVo {
    @ApiModelProperty(name="pageIndex",value = "pageIndex")
    @NotNull
    private Integer pageIndex;
    @ApiModelProperty(name="pageSize",value = "pageSize")
    @NotNull
    private Integer pageSize;
    @ApiModelProperty(name="ontid",value = "仲裁者ontid")
    private String ontid;
}
