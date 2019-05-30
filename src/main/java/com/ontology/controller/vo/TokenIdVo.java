package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TokenIdVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="dataId",value = "dataId")
    private String dataId;
    @ApiModelProperty(name="sigVo",value = "购买交易签名信息")
    private SigVo sigVo;
}
