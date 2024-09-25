package com.example.demo.Model;
import java.util.Optional;
import java.util.Collections;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Primary
@AllArgsConstructor
public class MyAppUserService implements UserDetailsService {
    private final MyAppUserRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(MyAppUserService.class);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyAppUser> user = repository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities(Collections.emptyList())
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        } else {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException(username);
        }
    }
    public boolean existsByUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }
}
