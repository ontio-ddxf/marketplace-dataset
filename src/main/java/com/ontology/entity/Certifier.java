package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tbl_certifier")
@Data
public class Certifier {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String ontid;

}
