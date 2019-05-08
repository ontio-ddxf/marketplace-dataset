package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryVo {
    @ApiModelProperty(name="text",value = "text")
    private String text;
    @ApiModelProperty(name="percent",value = "percent")
    private int percent;

}
