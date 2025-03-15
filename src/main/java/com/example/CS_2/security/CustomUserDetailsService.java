package com.example.CS_2.security;

import com.example.CS_2.dao.UserRepository;
import com.example.CS_2.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user.isLocked() && user.getLockTime().plusMinutes(1).isAfter(LocalDateTime.now())) {
            throw new UsernameNotFoundException("User account is temp locked. See yaa later!");
        }

        resetFailedAttempts(user);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLastSuccessfulLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public void trackFailedLogin(String username){
        User user = userRepository.findByUsername(username);
        if (user != null){
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            user.setLastFailedLogin(LocalDateTime.now());
            if(user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS){
                user.setLocked(true);
                user.setLockTime(LocalDateTime.now());
            }
            userRepository.save(user);
        }
    }
}
