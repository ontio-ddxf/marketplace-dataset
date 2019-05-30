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
    @ApiModelProperty(name="certifier",value = "certifier")
    private String certifier;
    @ApiModelProperty(name="dataSource",value = "dataSource")
    private String dataSource;
}
