package com.ontology.service;


import com.ontology.controller.vo.TransactionDto;

import java.util.Map;

public interface ClaimService {

    Map<String, Object> getClaim();

    Map<String, Object> getClaimCallback(TransactionDto req);

    Map<String, Object> postClaim();

    Map<String, Object> postClaimCallBack(String id, TransactionDto req);

}
