package com.ontology.service;


import com.ontology.controller.vo.ContractVo;
import com.ontology.controller.vo.DataIdVo;
import com.ontology.controller.vo.SigVo;

import java.util.List;

public interface ContractService {

    String makeTransaction(String action, ContractVo contractVo) throws Exception;

    String sendTransaction(String action, SigVo sigVo) throws Exception;

    List<String> registerDataId(String action, DataIdVo dataIdVo) throws Exception;
}
