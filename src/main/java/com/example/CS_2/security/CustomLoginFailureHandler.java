package com.example.CS_2.security;

import com.example.CS_2.dao.UserRepository;
import com.example.CS_2.entity.User;
import com.example.CS_2.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        User user = userService.findByUsername(username);
        String errorMessage = "Niepoprawne dane logowania.";

        String locked = (String) session.getAttribute("locked");

        // if the user passed only wrong password
        if(user != null){

            // if the login process in banned yet
            if(locked.equals("true")) {

                errorMessage = "Proces logowania do tego konta jest tymczasowo zablokowane! Pozostalo minut:" + Duration.between(LocalDateTime.now(), user.getLockTime()).toMinutes();

            } else if(locked.equals("false")){
                user.setFailedAttempts(user.getFailedAttempts() + 1);
                user.setLastFailedLogin(LocalDateTime.now());
                user.setLocked(true);

                if (user.isLockAccount()){
                    user.setLockTime(LocalDateTime.now().plusMinutes(user.getFailedAttempts()));
                    errorMessage = "Niepoprawne hasło. Liczba prób: " + user.getFailedAttempts() + "/" + MAX_FAILED_ATTEMPTS;

                    if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                        user.setLocked(true);
                        LocalDateTime unlockTime = LocalDateTime.now().plusMinutes(99999);
                        user.setLockTime(unlockTime);
                        errorMessage = "Konto jest tymczasowo zablokowane!";
                    }
                }
                userRepository.save(user);
            }

        }
        session.removeAttribute("locked");
        request.getSession().setAttribute("loginError", errorMessage);

        response.sendRedirect("/login?error=true");
    }
}
