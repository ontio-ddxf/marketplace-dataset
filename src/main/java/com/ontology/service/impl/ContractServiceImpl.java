package com.ontology.service.impl;


import com.github.ontio.account.Account;
import com.ontology.controller.vo.ContractVo;
import com.ontology.controller.vo.DataIdVo;
import com.ontology.controller.vo.SigVo;
import com.ontology.secure.SecureConfig;
import com.ontology.service.ContractService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private SDKUtil sdk;
    @Autowired
    private ConfigParam configParam;

    @Override
    public String makeTransaction(String action, ContractVo contractVo) throws Exception {
        String method = contractVo.getMethod();
        String contractHash = contractVo.getContractHash();
        List argsList = contractVo.getArgsList();
        String payerAddr = configParam.PAYER_ADDRESS;
        String params = Helper.getParams("", contractHash, method, argsList, payerAddr);
        String txHex = (String) sdk.makeTransaction(params);
        log.info("txHex:{}",txHex);
        return txHex;
    }

    @Override
    public String sendTransaction(String action, SigVo sigVo) throws Exception {
        String txHash = sdk.sendTransaction(sigVo);
        return txHash;
    }

    @Override
    public List<String> registerDataId(String action, DataIdVo dataIdVo) throws Exception {
        String dataIdTxHex = sdk.makeRegIdWithController(dataIdVo.getDataId(), dataIdVo.getOntid(), dataIdVo.getPubKey());
        ContractVo contractVo = dataIdVo.getContractVo();
        String method = contractVo.getMethod();
        String contractHash = contractVo.getContractHash();
        List argsList = contractVo.getArgsList();
        String payerAddr = configParam.PAYER_ADDRESS;
        String params = Helper.getParams("", contractHash, method, argsList, payerAddr);
        String tokenIdTxHex = (String) sdk.makeTransaction(params);
        List<String> txList = new ArrayList<>();
        txList.add(dataIdTxHex);
        txList.add(tokenIdTxHex);
        return txList;
    }

}
