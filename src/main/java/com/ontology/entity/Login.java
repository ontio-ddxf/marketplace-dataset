package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tbl_login")
@Data
public class Login {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String ontid;

    private String domain;

    private String message;

    private Integer isVerified;

}
