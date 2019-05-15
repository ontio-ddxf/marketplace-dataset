package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class CertificationVo {
    @NotNull
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @NotNull
    @ApiModelProperty(name="certifier",value = "certifier")
    private String certifier;
}
