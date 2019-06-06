package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class CheckVo {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;

}
