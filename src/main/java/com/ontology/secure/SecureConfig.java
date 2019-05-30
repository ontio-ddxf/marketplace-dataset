package com.ontology.secure;


import com.ontology.utils.Base64ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * 通信加密使用的参数
 */
@Configuration
@Component
public class SecureConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecureConfig.class);

    private static String CONTRACT_HASH;


    private static String PAYER_ADDR;
    @Value("${payer.addr}")
    public void setPayerAddr(String payerAddr) {
        SecureConfig.PAYER_ADDR = payerAddr;
    }

    public String getPayerAddr() {
        try {
            return SecureConfig.PAYER_ADDR;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    private static String PAYER_WIF;
    @Value("${payer.wif}")
    public void setPayer(String wif) {
        SecureConfig.PAYER_WIF = wif;
    }

    public String getPayer() {
        try {
            return Base64ConvertUtil.decode(SecureConfig.PAYER_WIF);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }
}
