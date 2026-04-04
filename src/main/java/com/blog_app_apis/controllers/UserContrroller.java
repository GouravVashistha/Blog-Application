package com.blog_app_apis.controllers;

import com.blog_app_apis.dtos.UserDTO;
import com.blog_app_apis.exceptions.InvalidMailException;
import com.blog_app_apis.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController("/api/users")
public class UserContrroller {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/createUser", produces = "application/JSON", consumes = "application/JSON")
    public ResponseEntity<UserDTO> createUser( @Valid  @RequestBody UserDTO userDTO) throws Exception{
        UserDTO createUserDTO = this.userService.createUser(userDTO);
        return new ResponseEntity<>(createUserDTO, HttpStatus.CREATED);
    }
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("userId") Integer userId) throws InvalidMailException {
        UserDTO updateUser = this.userService.updateUser(userDTO,userId);
        return ResponseEntity.ok(updateUser);
    }


}
