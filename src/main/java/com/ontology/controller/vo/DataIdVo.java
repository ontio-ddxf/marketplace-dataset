package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataIdVo {
    @ApiModelProperty(name="dataId",value = "dataId")
    private String dataId;
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="pubKey",value = "pubKey")
    private Integer pubKey;
}
