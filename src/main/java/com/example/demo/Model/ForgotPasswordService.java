package com.example.demo.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
public class ForgotPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);
    @Autowired
    private MyAppUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public boolean validateUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    public boolean updatePassword(String username, String newPassword) {
        Optional<MyAppUser> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            MyAppUser user = userOptional.get();
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            userRepository.save(user);
            logger.info("Password updated successfully for username: {}", username);
            return true;
        } else {
            logger.error("Failed to update password. Username {} not found.", username);
            return false;
        }
    }
}
