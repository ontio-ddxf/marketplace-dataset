package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ParamsDto {
    @ApiModelProperty(name="type",value = "type")
    private String type;
    @ApiModelProperty(name="user",value = "user")
    private String user;
    @ApiModelProperty(name="domain",value = "domain")
    private String domain;
    @ApiModelProperty(name="message",value = "message")
    private String message;
    @ApiModelProperty(name="publickey",value = "publickey")
    private String publickey;
    @ApiModelProperty(name="signature",value = "signature")
    private String signature;
}
