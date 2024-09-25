package com.example.demo.Controller;

import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;
import com.example.demo.Security.JwtUtil;

@Controller
public class ContentController {
    private static final Logger logger = LoggerFactory.getLogger(ContentController.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MyAppUserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @GetMapping("/req/login")
    public String loginPage(Model model) {
        return "login"; 
    }
    @PostMapping("/req/login")
    public ResponseEntity<?> login(@RequestBody MyAppUser user) {
        Optional<MyAppUser> existingUser = userRepository.findByUsername(user.getUsername());
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        logger.info("User '{}' found in database, proceeding with authentication.", user.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            String token = jwtUtil.generateToken(authentication.getName());
            logger.info("Generated token for user {}: {}", user.getUsername(), token);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            logger.error("Login failed for user: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            logger.error("An unexpected error occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }
    @GetMapping("/req/signup")
    public String signupPage(Model model) {
        return "signup"; 
    }
    @GetMapping("/req/index")
    public String indexPage(Model model) {
        return "index"; 
    }
    @PostMapping("/req/check-username")
    public ResponseEntity<String> checkUsername(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        Optional<MyAppUser> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok("Username exists");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found");
        }
    }
    @PostMapping("/req/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String newPassword = payload.get("newPassword");
        Optional<MyAppUser> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            MyAppUser user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}