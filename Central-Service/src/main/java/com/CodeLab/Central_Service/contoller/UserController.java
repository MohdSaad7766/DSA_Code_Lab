package com.CodeLab.Central_Service.contoller;

import com.CodeLab.Central_Service.exception.UserEmailAlreadyPresentException;
import com.CodeLab.Central_Service.requestDTO.LoginRequestDTO;
import com.CodeLab.Central_Service.requestDTO.UserRequestDTO;
import com.CodeLab.Central_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Central_Service.responseDTO.LoginResponseDTO;
import com.CodeLab.Central_Service.responseDTO.OTPVerificationResponseDTO;
import com.CodeLab.Central_Service.responseDTO.UserRegistrationRequestResponseDTO;
import com.CodeLab.Central_Service.service.AuthenticationService;
import com.CodeLab.Central_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        LoginResponseDTO responseDTO = authenticationService.generateToken(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }
}
