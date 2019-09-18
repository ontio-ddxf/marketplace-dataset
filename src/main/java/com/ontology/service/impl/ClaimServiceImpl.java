package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.core.transaction.Transaction;
import com.ontology.controller.vo.MessageDto;
import com.ontology.controller.vo.TransactionDto;
import com.ontology.entity.Invoke;
import com.ontology.entity.Login;
import com.ontology.entity.Ons;
import com.ontology.exception.MarketplaceException;
import com.ontology.mapper.InvokeMapper;
import com.ontology.mapper.LoginMapper;
import com.ontology.mapper.OnsMapper;
import com.ontology.service.ClaimService;
import com.ontology.service.OnsService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Executors;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private InvokeMapper invokeMapper;
    @Autowired
    private ConfigParam configParam;

    private static final long oneYear = 365 * 24 * 60 * 60 * 1000;

    @Override
    public Map<String, Object> getClaim() {
        String id = UUID.randomUUID().toString();
        id = id.substring(0, id.indexOf("-"));
        String message = "helloWorld";

        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setSuccess(0);
        invoke.setParams(message);
        invokeMapper.insert(invoke);

//        String callback = String.format(configParam.CALLBACK_URL,"api/v1/claim/getClaim/callback");
        String callback = String.format(configParam.CALLBACK_URL,"back/getClaim");
        long  expire = (long) (new Date().getTime() + oneYear)/1000L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("message", message);
        map.put("expire", expire);
        map.put("callback", callback);
        return map;
    }

    @Override
    public Map<String, Object> getClaimCallback(TransactionDto req) {
        String id = req.getId();
        String message = (String) req.getParams().get("message");
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setSuccess(1);
        invokeMapper.updateByPrimaryKeySelective(invoke);

        Map<String, Object> result = new HashMap<>();
        result.put("claimTemplate","claims:yus_chinese_id_authentication");
        result.put("claim","eyJraWQiOiJkaWQ6b250OkFhUEVnNzdmR3FqM2RZUDcxYUFrWnU3M0ZLc01KUWVxaTEja2V5cy0xIiwidHlwIjoiSldULVgiLCJhbGciOiJPTlQtRVMyNTYifQ==.eyJjbG0tcmV2Ijp7InR5cCI6IkF0dGVzdENvbnRyYWN0IiwiYWRkciI6IjM2YmI1YzA1M2I2YjgzOWM4ZjZiOTIzZmU4NTJmOTEyMzliOWZjY2MifSwic3ViIjoiZGlkOm9udDpBSnVhN0M2dGVvRlVzMktoUmVjcWJmYlB3ckY5OWtISGdqIiwidmVyIjoidjEuMCIsImNsbSI6eyJJc3N1ZXJOYW1lIjoiU2Vuc2V0aW1lIiwi5aeT5ZCNIjoi5LiB5bCP57KJIiwi6Lqr5Lu96K+B5Y+3IjoiMzQxMjgxMTk4NzA4MzA2OTA4In0sImlzcyI6ImRpZDpvbnQ6QWFQRWc3N2ZHcWozZFlQNzFhQWtadTczRktzTUpRZXFpMSIsImV4cCI6MTU5NTQxNDMzMywiaWF0IjoxNTYzNzkxOTM1LCJAY29udGV4dCI6ImNsYWltOnl1c19jaGluZXNlX2lkX2F1dGhlbnRpY2F0aW9uIiwianRpIjoiZDNlYzBjZWFkNWEzN2JjNTQ2OTAzODUwY2QxMGY4OTM0NGUyZWVlZGUwM2UxMGJmNTNhZjA1ZGI3YmY4NjY1NCJ9.AZniJRQtytUzoaWAS5CjnqQdTHD4mW9lQnyepwuzwkqA5ZeOM6Jr2ZnHI42R981YHCyRse7qHpC6xhxeQc0XunM=\\.eyJUeXBlIjoiTWVya2xlUHJvb2YiLCJNZXJrbGVSb290IjoiYjFmNjUwMGI3MGM0ZGY3YmNlNDQ2MDgxNzIxNDQ1M2E3ZmI4MTZiNjMwZGI1NTRmZDFhM2FhMjgwZDM1ZTA3MSIsIlR4bkhhc2giOiI0MTEyYzE3MDM1OTljMWM1ZThmNmM5NWY4YTNjMGI0ZGYwMDk2MWU0NmIxZDdiMjk3MmY5MjVhYjIyZGM5OTViIiwiQmxvY2tIZWlnaHQiOjMwMzMzNDAsIk5vZGVzIjpbeyJUYXJnZXRIYXNoIjoiZTE0MTcyYzhhNmUxOTM5NDM0NjU2NDhlMWM1ODZhOTE4NmEzNzg0ZWU3ZWUyOWRiOWVkYmY2YWZlMDRmNTM5MCIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiJmNDQwNTMxOTk5YzU0N2RiMDhmNTE2Njc3YzE1MjIxNTQ3NWE2OWRjY2I4MjE3NmU0YmNhMWI3MjYyNjFhMWJlIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6IjMzZDQ0ZmIxOTMxZWMwYTVjMjZiMzg1ZmZhYmQwYjUzYTQ5YTE4MDIxZWQxYjljMTEyNmU5ODAzNGFjNzZkNjAiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiYzMzZTg5ZTZhYjcwZjg0YjY2MDkyYThjY2FjMjk5ZWY5MjBlNWQ0NTg2MDc3ZGFlNDk1M2I2MjFhN2Q4NDhjOCIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiI3YTI1MTA1NzkyNmQ5MTc0NGRlYjcyMWYyMjExYjZlZTIwODMwMzRkM2EzOWM5NzFjZDY2ZDhhMzNhMjI1OGU1IiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6Ijk1ZDY0YThhYmM2NzU5YzU1ZWJjYThiNzU0MGM5OGU0NWUxYzI4NWE4MDk0Zjg4MDdlMjI1NDI4NTRhMDZhOGIiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiNjgxMTY1MmY1ZTI2ZDNjZDk0NjY2ZWI3MDkyMTMxYzU0NThkYTUxYzZmOTBlM2YxMDg3MDU5ODc2M2NjZGVkMSIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiJkNzE1YzllODE3ZmU4NjYzMTkzYjU5N2MzYjZhMjhiNTlmYTY4NWQxZjNmMjVhNjhkZmJhMGYyYzA2Y2I5MjFiIiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6IjFmMGU4ZTA2YmU5MGQzYTM2MWNlZTk5OWMwYWM5OGVkYjBmNjA4YTViMzNhMTU3ODM4ZTMyNWU0ZmFlMjRkY2MiLCJEaXJlY3Rpb24iOiJMZWZ0In0seyJUYXJnZXRIYXNoIjoiNjA5NDQyYmEyYjk5NWEzYTA4ZTk5ZmE3MTQ3ZDY4NDQ2YjNmY2IzYjNiZTU4ZmQ2MzI3NWU1NDAxM2M1YmM1MSIsIkRpcmVjdGlvbiI6IkxlZnQifSx7IlRhcmdldEhhc2giOiJiMGNkY2I2ZmM1NzRjMzQyMDgyMDYwNzllYzBiNDc4NDM1NGE4YzUwZGI4NTJkNGQ3ZGMzMjY1NjkwOTE5N2Q4IiwiRGlyZWN0aW9uIjoiTGVmdCJ9LHsiVGFyZ2V0SGFzaCI6Ijc5YTgyYzkzZGZiZWNjODhmNzZhYjMzMDk1NWMyZjY0ODlhMmYyNmFkOThhY2FiOGI3NDY4MWViNzJhYjM4YzYiLCJEaXJlY3Rpb24iOiJMZWZ0In1dLCJDb250cmFjdEFkZHIiOiIzNmJiNWMwNTNiNmI4MzljOGY2YjkyM2ZlODUyZjkxMjM5YjlmY2NjIn0=");
        return result;
    }

    @Override
    public Map<String, Object> postClaim() {
        String id = UUID.randomUUID().toString();
        id = id.substring(0, id.indexOf("-"));
//        String callback = String.format(configParam.CALLBACK_URL,"api/v1/claim/postClaim/callback/");
        String callback = String.format(configParam.CALLBACK_URL,"back/postClaim/");
        long  expire = (new Date().getTime() + oneYear)/1000L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("expire", expire);
        map.put("callback", callback+id);
        map.put("claimTemplate","claims:yus_chinese_id_authentication");
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setSuccess(0);
        invoke.setParams(JSON.toJSONString(map));
        invokeMapper.insert(invoke);
        return map;
    }

    @Override
    public Map<String, Object> postClaimCallBack(String id, TransactionDto req) {
        String claim = (String) req.getParams().get("claim");

        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setSuccess(1);
        invokeMapper.updateByPrimaryKeySelective(invoke);
        return null;
    }

}
