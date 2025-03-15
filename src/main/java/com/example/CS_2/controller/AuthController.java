package com.example.CS_2.controller;

import com.example.CS_2.dao.UserRepository;
import com.example.CS_2.entity.User;
import com.example.CS_2.security.CustomUserDetailsService;
import com.example.CS_2.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/loginUser")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userService.findByUsername(username);

        if (user == null) {
            userDetailsService.trackFailedLogin(username);
            return "Wrong username!";
        }


        if (user.isLocked()) {
            return "Account has been locked!";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            userDetailsService.trackFailedLogin(username);
            return "Wrong password!";
        }

        userDetailsService.resetFailedAttempts(user);
        return "dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password) {

        userService.registerUser(username, passwordEncoder.encode(password));
        return "redirect:/login";
    }


}
