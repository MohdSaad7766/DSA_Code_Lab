package com.CodeLab.Central_Service.contoller;

import com.CodeLab.Central_Service.enums.Language;
import com.CodeLab.Central_Service.exception.UserEmailAlreadyPresentException;
import com.CodeLab.Central_Service.model.User;
import com.CodeLab.Central_Service.requestDTO.LoginRequestDTO;
import com.CodeLab.Central_Service.requestDTO.UserRequestDTO;
import com.CodeLab.Central_Service.responseDTO.*;
import com.CodeLab.Central_Service.service.AuthenticationService;
import com.CodeLab.Central_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/central/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/registration-request")
    public  ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserRegistrationRequestResponseDTO responseDTO = new UserRegistrationRequestResponseDTO();
      try{
          userService.registerUser(userRequestDTO);

          responseDTO.setMessage("An OTP is sent you on your Email-"+userRequestDTO.getEmail());
          responseDTO.setValidEmail(true);

      } catch (UserEmailAlreadyPresentException e) {
          responseDTO.setMessage(e.getMessage());
          responseDTO.setValidEmail(false);
      }
        return new ResponseEntity<>(responseDTO,HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> verifyOtp(@RequestBody UserRequestDTO userRequestDTO,@RequestParam String otp){
        OTPVerificationResponseDTO responseDTO = userService.verifyOtp(userRequestDTO,otp);
        return new ResponseEntity<>(responseDTO,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO){
        UserLoginResponseDTO responseDTO = authenticationService.generateUserToken(requestDTO);
        if(!responseDTO.isValid()){
            return new ResponseEntity<>(responseDTO,HttpStatus.UNAUTHORIZED);

        }
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @PutMapping("/update-preferred-language")
    private ResponseEntity<?> updatePreferredLanguage(@RequestParam Language langauge,@RequestHeader(value = "Authorization", required = false) String header){

        TokenValidationResponseDTO responseDTO = authenticationService.validateUserToken(header);

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }

        UUID userId = responseDTO.getUserId();

        User profile = userService.updatePreferredLanguage(userId,langauge);

        if(profile == null){
            GeneralResponseDTO generalResponseDTO = new GeneralResponseDTO();
            generalResponseDTO.setMessage("User Not Found");
            return new ResponseEntity<>(generalResponseDTO,HttpStatus.OK);
        }

        return new ResponseEntity<>(profile,HttpStatus.OK);




    }
}
