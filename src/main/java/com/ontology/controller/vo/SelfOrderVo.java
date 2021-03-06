package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelfOrderVo {
    @ApiModelProperty(name="pageNum",value = "pageNum")
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(name="pageSize",value = "pageSize")
    @NotNull
    private Integer pageSize;
    @ApiModelProperty(name="type",value = "用户类型：1-买家；2-卖家")
    @NotNull
    private Integer type;
    @ApiModelProperty(name="ontid",value = "用户ontid")
    private String ontid;
}
