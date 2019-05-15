package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "tbl_certifier")
@Data
public class Certifier {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String ontid;

}
