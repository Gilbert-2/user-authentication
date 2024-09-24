package com.example.demo.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;

import java.util.Optional;

@RestController
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/req/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@Validated @RequestBody MyAppUser user, BindingResult result) {
        logger.info("Received signup request for username: {}", user.getUsername());

        if (result.hasErrors()) {
            String errorMessage = Optional.ofNullable(result.getFieldError())
                                          .map(error -> error.getDefaultMessage())
                                          .orElse("Validation error");
            logger.error("Validation errors: {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        if (myAppUserRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("Username already exists: {}", user.getUsername());
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        }

        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            logger.error("Passwords do not match for username: {}", user.getUsername());
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        if (user.getPassword().length() < 8) {
            logger.error("Password is too short for username: {}", user.getUsername());
            return new ResponseEntity<>("Password must be at least 8 characters long", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        myAppUserRepository.save(user);
        logger.info("Signup successful for username: {}", user.getUsername());

        return new ResponseEntity<>("Signup successful! You can now log in.", HttpStatus.CREATED);
    }
}
