package com.example.CS_2.security;

import com.example.CS_2.dao.UnknownUserRepository;
import com.example.CS_2.dao.UserRepository;
import com.example.CS_2.entity.UnknownUser;
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
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UnknownUserRepository unknownUserRepository;
    private final HttpSession httpSession;

    public CustomUserDetailsService(UserRepository userRepository, HttpSession httpSession, UnknownUserRepository unknownUserRepository) {
        this.userRepository = userRepository;
        this.httpSession = httpSession;
        this.unknownUserRepository = unknownUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        httpSession.setAttribute("locked", "false"); // using session to inform loginfailhand that its not locked
        try{
            Optional<User> userOpt = userRepository.findByUsername(username);
            user = userOpt.orElseThrow(EntityNotFoundException::new);

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
        } catch (EntityNotFoundException e) {
            UnknownUser unknownUser = new UnknownUser(username, LocalDateTime.now());
            unknownUserRepository.save(unknownUser);
            System.out.println("Exception we got : " + e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        } catch (LockedException e){
            httpSession.setAttribute("locked", "true"); // using session to inform loginfailhand that its locked
        }



        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}
