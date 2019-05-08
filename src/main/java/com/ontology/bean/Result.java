package com.ontology.bean;

import lombok.Data;

@Data
public class Result {
    public int code;
    public String msg;
    public Object result;

    public Result() {
    }

    public Result(int code, String msg, Object result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

}
