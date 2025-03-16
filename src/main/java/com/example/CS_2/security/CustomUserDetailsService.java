package com.example.CS_2.security;

import com.example.CS_2.dao.UserRepository;
import com.example.CS_2.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (user.isLocked()) {
            long minutesLocked = Duration.between(user.getLockTime(), LocalDateTime.now()).toMinutes();
            if (minutesLocked < 0) {
                throw new LockedException("Konto jest zablokowane. Spróbuj ponownie za " + (minutesLocked) + " minut.");

            } else {
                // Odblokowanie użytkownika
                user.setLocked(false);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}
