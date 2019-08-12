package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class MultiTransactionDto {
    @ApiModelProperty(name="action",value = "action")
    private String action;
    @ApiModelProperty(name="version",value = "version")
    private String version;
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="params",value = "params")
    private List<Map<String,Object>> params;

}
