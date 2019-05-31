package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TokenIdVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="dataId",value = "dataId")
    private String dataId;
    @ApiModelProperty(name="sigVo",value = "注册DataId签名")
    private SigVo sigDataVo;
    @ApiModelProperty(name="sigVo",value = "生成dToken签名")
    private SigVo sigTokenVo;
}
