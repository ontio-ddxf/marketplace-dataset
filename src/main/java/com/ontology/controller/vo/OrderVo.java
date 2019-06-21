package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="dataId",value = "dataId")
    private String dataId;
    @ApiModelProperty(name="tokenId",value = "tokenId")
    private int tokenId;
    @ApiModelProperty(name="dataId",value = "dataId")
    private String name;
    @ApiModelProperty(name="tokenId",value = "tokenId")
    private String desc;
    @ApiModelProperty(name="dataId",value = "dataId")
    private String img;
    @ApiModelProperty(name="tokenHash",value = "tokenHash")
    private String tokenHash;
    @ApiModelProperty(name="price",value = "价格")
    private String price;
//    @ApiModelProperty(name="amount",value = "数量")
//    private Integer amount;
    @ApiModelProperty(name="providerOntid",value = "卖家ontid")
    private String providerOntid;
    @ApiModelProperty(name="ojList",value = "仲裁者地址列表")
    private List<String> ojList;
    @ApiModelProperty(name="keywords",value = "商品关键字")
    private List<String> keywords;
    @ApiModelProperty(name="sigVo",value = "挂单交易签名信息")
    private SigVo sigVo;

}
