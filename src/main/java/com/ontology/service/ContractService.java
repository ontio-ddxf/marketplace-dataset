package com.ontology.service;


import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ContractService {

    String makeTransaction(String action, ContractVo contractVo) throws Exception;

    String sendTransaction(String action, SigVo sigVo) throws Exception;

    String  registerDataId(String action, DataIdVo dataIdVo) throws Exception;

    String sendSyncTransaction(String action, SigVo sigDataVo) throws Exception;

    String invokeResult(String action, String id);

    Map makeTransactionAndCallback(String action, ContractVo contractVo) throws Exception;

    Result invoke(String action, TransactionDto req) throws Exception;

    void postHonor(String action, HonorVo req) throws Exception;

    Long queryHonor(String action, String ontid) throws Exception;

}
