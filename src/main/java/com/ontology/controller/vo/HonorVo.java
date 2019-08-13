package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HonorVo {
    @ApiModelProperty(name = "ontid", value = "用户账户")
    private String ontid;
    @ApiModelProperty(name="value",value = "荣誉值数量")
    private int value;

}
