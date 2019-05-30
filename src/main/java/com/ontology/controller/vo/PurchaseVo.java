package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PurchaseVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="demanderOntid",value = "买家ontid")
    private String demanderOntid;
    @ApiModelProperty(name="judger",value = "仲裁者地址")
    private String judger;
    @ApiModelProperty(name="expireTime",value = "订单超时时间")
    private Integer expireTime;
    @ApiModelProperty(name="sigVo",value = "购买交易签名信息")
    private SigVo sigVo;
}
