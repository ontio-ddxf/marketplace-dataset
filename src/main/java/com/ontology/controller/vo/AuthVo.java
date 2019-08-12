package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthVo {
    @ApiModelProperty(name = "dataIdVo", value = "注册DataId的参数")
    private DataIdVo dataIdVo;
    @ApiModelProperty(name="orderVo",value = "授权挂单的参数")
    private OrderVo orderVo;

}
