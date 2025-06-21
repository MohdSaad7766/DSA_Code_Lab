package com.CodeLab.DB_Service.controller;

import com.CodeLab.DB_Service.model.Admin;
import com.CodeLab.DB_Service.requestDTO.AdminRequestDTO;
import com.CodeLab.DB_Service.responseDTO.*;
import com.CodeLab.DB_Service.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/db/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/is-admin-already-present/{email}")
    public ResponseEntity<?> IsAdminEmailAlreadyExists(@PathVariable String email){
        IsEmailAlreadyPresentResponseDTO alreadyPresent = new IsEmailAlreadyPresentResponseDTO();

        Admin admin =  adminService.getAdminByEmail(email);
        if(admin == null){
            alreadyPresent.setPresent(false);
            return new ResponseEntity<>(alreadyPresent, HttpStatus.OK);
        }

        alreadyPresent.setPresent(true);
        return new ResponseEntity<>(alreadyPresent,HttpStatus.FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRequestDTO adminRequestDTO){
        Admin admin = adminService.registerAdmin(adminRequestDTO);
        AdminRegisteredResponseDTO responseDTO = new AdminRegisteredResponseDTO();
        responseDTO.setAdminId(admin.getAdminId());
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/get-by-email")
    public Admin geAdminByEmail(@RequestParam String email){
        Admin admin = adminService.getAdminByEmail(email);

        return admin;
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam UUID userId, @RequestParam String newPassword){
        boolean isChanged = adminService.changePassword(userId,newPassword);

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

}
