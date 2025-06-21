package com.CodeLab.DB_Service.controller;

import com.CodeLab.DB_Service.enums.Language;
import com.CodeLab.DB_Service.model.Submission;
import com.CodeLab.DB_Service.model.User;
import com.CodeLab.DB_Service.requestDTO.UserRequestDTO;
import com.CodeLab.DB_Service.responseDTO.IsEmailAlreadyPresentResponseDTO;
import com.CodeLab.DB_Service.responseDTO.PasswordChangeResponseDTO;
import com.CodeLab.DB_Service.responseDTO.UserRegisteredResponseDTO;
import com.CodeLab.DB_Service.responseDTO.UserResponse;
import com.CodeLab.DB_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/db/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/is-user-already-present/{email}")
    public ResponseEntity<?> IsUserEmailAlreadyExists(@PathVariable String email){
        IsEmailAlreadyPresentResponseDTO alreadyPresent = new IsEmailAlreadyPresentResponseDTO();

        User user =  userService.getUserByEmail(email);
        if(user == null){
            alreadyPresent.setPresent(false);
            return new ResponseEntity<>(alreadyPresent,HttpStatus.OK);
        }

        alreadyPresent.setPresent(true);
        return new ResponseEntity<>(alreadyPresent,HttpStatus.FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO){
        User user = userService.registerUser(userRequestDTO);
        UserRegisteredResponseDTO responseDTO = new UserRegisteredResponseDTO();
        responseDTO.setUserId(user.getUserId());
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }



    @GetMapping("/get-by-email")
    public User getUserByEmail(@RequestParam String email){
        User user =  userService.getUserByEmail(email);
        return user;
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam UUID userId,@RequestParam String newPassword){
        boolean isChanged = userService.changePassword(userId,newPassword);

        PasswordChangeResponseDTO responseDTO = new PasswordChangeResponseDTO();

        if(isChanged){
            responseDTO.setMessage("Password Changed Successfully.");
            return new ResponseEntity<>(responseDTO,HttpStatus.FOUND);
        }
        else{
            responseDTO.setMessage("Password Not Changed.");
            return new ResponseEntity<>(responseDTO,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-preferred-language")
    private User updatePreferredLanguage(@RequestParam Language language,@RequestParam UUID userId){
        return userService.updatePreferredLanguage(userId,language);
    }





}
