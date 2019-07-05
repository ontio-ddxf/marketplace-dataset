package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="demanderOntid",value = "买家ontid")
    private String demanderOntid;
    @ApiModelProperty(name="demanderAddress",value = "买家钱包地址")
    private String demanderAddress;
    @ApiModelProperty(name="judger",value = "仲裁者地址")
    private String judger;
    @ApiModelProperty(name="name",value = "商品名称")
    private String name;
    @ApiModelProperty(name="desc",value = "商品描述")
    private String desc;
    @ApiModelProperty(name="img",value = "商品图片")
    private String img;
    @ApiModelProperty(name="keywords",value = "商品关键字")
    private List<String> keywords;
    @ApiModelProperty(name="expireTime",value = "订单超时时间")
    private Integer expireTime;
    @ApiModelProperty(name="sigVo",value = "购买交易签名信息")
    private SigVo sigVo;
}
