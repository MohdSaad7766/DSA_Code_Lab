package com.CodeLab.Central_Service.contoller;


import com.CodeLab.Central_Service.exception.UserEmailAlreadyPresentException;
import com.CodeLab.Central_Service.requestDTO.AdminRequestDTO;
import com.CodeLab.Central_Service.requestDTO.LoginRequestDTO;
import com.CodeLab.Central_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Central_Service.responseDTO.LoginResponseDTO;
import com.CodeLab.Central_Service.responseDTO.OTPVerificationResponseDTO;
import com.CodeLab.Central_Service.responseDTO.TokenValidationResponseDTO;
import com.CodeLab.Central_Service.service.AdminService;
import com.CodeLab.Central_Service.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/central/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/registration-request")
    public ResponseEntity<?> registerUser(@RequestBody AdminRequestDTO adminRequestDTO) {
        GeneralResponseDTO responseDTO = new GeneralResponseDTO();
        try{
            adminService.registerAdmin(adminRequestDTO);
            responseDTO.setMessage("An OTP is sent you on your Email-"+adminRequestDTO.getEmail());

        } catch (UserEmailAlreadyPresentException e) {
            responseDTO.setMessage(e.getMessage());
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> verifyOtp(@RequestBody AdminRequestDTO adminRequestDTO,@RequestParam String otp){
        OTPVerificationResponseDTO responseDTO = adminService.verifyOtp(adminRequestDTO,otp);
        return new ResponseEntity<>(responseDTO,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO){
        LoginResponseDTO responseDTO = authenticationService.generateToken(requestDTO,true);

        if(responseDTO.isValid() == false){
            return new ResponseEntity<>(responseDTO,HttpStatus.UNAUTHORIZED);

        }
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @PostMapping("/validate-token")
    private ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization",required = false) String header){
        TokenValidationResponseDTO responseDTO = authenticationService.validateAdminToken(header);
        System.out.println(responseDTO.getMessage());

        if (!responseDTO.isValid()) {
            return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
