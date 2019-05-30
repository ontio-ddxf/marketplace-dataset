package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataVo {
    @ApiModelProperty(name="desc",value = "desc")
    private String desc;
    @ApiModelProperty(name="img",value = "img")
    private String img;
    @ApiModelProperty(name="data",value = "data")
    private List<String> keywords;
    @ApiModelProperty(name="metadata",value = "metadata")
    private String metadata;
    @ApiModelProperty(name="name",value = "name")
    private String name;
}
