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

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        System.out.println("username: " + username);

        model.addAttribute("username", username);
        model.addAttribute("lastFailedLogin", user.getLastFailedLogin());
        model.addAttribute("lastSuccessfulLogin", user.getLastSuccessfulLogin());
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(){
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
