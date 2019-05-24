package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class ProvideVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="price",value = "price")
    private String price;
    @ApiModelProperty(name="coin",value = "coin")
    private String coin;
    @ApiModelProperty(name="judger",value = "judger")
    private List<String> judger;
    @ApiModelProperty(name="challengePeriod",value = "challengePeriod")
    private List<Integer> challengePeriod;

}
