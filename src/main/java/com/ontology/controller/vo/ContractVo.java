package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContractVo {
    @ApiModelProperty(name="contractHash",value = "合约hash地址")
    private String contractHash;
    @ApiModelProperty(name="method",value = "合约方法名")
    private String method;
    @ApiModelProperty(name="argsList",value = "参数列表")
    private List argsList;
}
