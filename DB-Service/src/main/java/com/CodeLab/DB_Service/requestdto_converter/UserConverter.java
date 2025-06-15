package com.CodeLab.DB_Service.requestdto_converter;


import com.CodeLab.DB_Service.model.User;
import com.CodeLab.DB_Service.model.Location;

import com.CodeLab.DB_Service.requestDTO.UserRequestDTO;

import java.util.ArrayList;


public class UserConverter {
    public static User userConverter(UserRequestDTO userRequestDTO){
        User user = new User();
        user.setName(userRequestDTO.getName().trim());
        user.setEmail(userRequestDTO.getEmail().trim());
        user.setPassword(userRequestDTO.getPassword());
        user.setGender(userRequestDTO.getGender());
        user.setUserCategory(userRequestDTO.getUserCategory());
        user.setPreferredLanguage(userRequestDTO.getPreferredLanguage());

        Location location = LocationConverter.locationConverter(userRequestDTO.getLocationRequestDTO());
        user.setLocation(location);

//        user.setSubmissionList(new ArrayList<>());

//        user.setVerified(false);

        return user;

    }
}
