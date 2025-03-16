package com.example.CS_2.security;

import com.example.CS_2.dao.UserRepository;
import com.example.CS_2.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession httpSession;

    public CustomUserDetailsService(UserRepository userRepository, HttpSession httpSession) {
        this.userRepository = userRepository;
        this.httpSession = httpSession;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        httpSession.setAttribute("locked", "false"); // using session to inform loginfailhand that its not locked

        if (user.isLocked()) {
            // how much time the login is banned yet
            long minutesLocked = Duration.between(user.getLockTime(), LocalDateTime.now()).toMinutes();


            if (minutesLocked < 0) { // if time didnt pass yet
                httpSession.setAttribute("locked", "true"); // using session to inform loginfailhand that its locked
                throw new LockedException("Konto jest zablokowane. Spróbuj ponownie za " + (minutesLocked) + " minut(y).");

            } else {

                // unlock the user
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
