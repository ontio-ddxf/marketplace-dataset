package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PageQueryVo {
    @ApiModelProperty(name="pageNum",value = "pageNum")
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(name="pageSize",value = "pageSize")
    @NotNull
    private Integer pageSize;
    @ApiModelProperty(name="queryVo",value = "queryVo")
    private List<QueryVo> queryParams;
}
