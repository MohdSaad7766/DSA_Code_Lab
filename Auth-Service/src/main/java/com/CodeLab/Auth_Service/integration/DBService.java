package com.CodeLab.Auth_Service.integration;

import com.CodeLab.Auth_Service.model.Admin;
import com.CodeLab.Auth_Service.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class DBService extends RestAPI {

    @Value("${db.service.base}")
    String baseURL;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    public User callGetUserByEmail(String email){
        String endpoint = "/user/get-by-email";
        HashMap<String,String> map = new HashMap<>();
        map.put("email",email);
        Object object = this.makeGetCall(baseURL,endpoint,map);

        if(object == null){
            return null;
        }

        return objectMapper.convertValue(object, User.class);
    }

    public Admin callGetAdminByEmail(String email){
        String endpoint = "/admin/get-by-email";
        HashMap<String,String> map = new HashMap<>();
        map.put("email",email);
        Object object = this.makeGetCall(baseURL,endpoint,map);

        if(object == null){
            return null;
        }

        return objectMapper.convertValue(object,Admin.class);
    }
}
