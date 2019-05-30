package com.ontology.exception;


/**
 * 自定义异常
 */
public class MarketplaceException extends RuntimeException {

    private String errDesCN;

    private String errDesEN;

    private int errCode;

    private String action;

    public MarketplaceException(String msg) {
        super(msg);
    }

    public MarketplaceException() {
        super();
    }

    public MarketplaceException(String action, String errDesCN, String errDesEN, int errCode) {
        this.action = action;
        this.errDesCN = errDesCN;
        this.errDesEN = errDesEN;
        this.errCode = errCode;
    }

    public String getErrDesCN() {
        return errDesCN;
    }

    public String getErrDesEN() {
        return errDesEN;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getAction() {
        return action;
    }
}
