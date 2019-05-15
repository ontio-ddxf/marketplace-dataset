package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AttributeVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="data",value = "data")
    private DataVo data;
    @ApiModelProperty(name="price",value = "price")
    private String price;
    @ApiModelProperty(name="coin",value = "coin")
    private String coin;
    @ApiModelProperty(name="certifier",value = "certifier")
    private String certifier;
    @ApiModelProperty(name="judger",value = "judger")
    private String judger;
}
