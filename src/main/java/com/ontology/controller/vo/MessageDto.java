package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MessageDto {
    @ApiModelProperty(name="action",value = "action")
    private String action;
    @ApiModelProperty(name="version",value = "version")
    private String version;
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="error",value = "error")
    private int error;
    @ApiModelProperty(name="params",value = "params")
    private ParamsDto params;
}
