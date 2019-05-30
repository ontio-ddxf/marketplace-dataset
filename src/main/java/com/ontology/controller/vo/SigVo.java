package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SigVo {
    @ApiModelProperty(name="txHex",value = "txHex")
    private String txHex;
    @ApiModelProperty(name="pubKeys",value = "pubKeys")
    private String pubKeys;
    @ApiModelProperty(name="sigData",value = "sigData")
    private String sigData;
}
