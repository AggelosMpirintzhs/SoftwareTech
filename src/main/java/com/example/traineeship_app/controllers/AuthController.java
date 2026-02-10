package com.example.traineeship_app.controllers;

import com.example.traineeship_app.domainmodel.User;
import com.example.traineeship_app.services.Impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @RequestMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @RequestMapping("/Save")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        String normalizedUsername = user.getUsername().trim().toLowerCase();

        if (normalizedUsername.contains(" ")) {
            model.addAttribute("errorMessage", "Usernames cannot contain spaces.");
            return "register";
        }
        user.setUsername(normalizedUsername);
        if (userService.isUserPresent(user)) {
            model.addAttribute("errorMessage", "A user with that username already exists!");
            return "register";
        }
        System.out.println("User to save: " + user);
        System.out.println("Role to save: " + user.getRole().name());
        userService.saveUser(user);
        return "redirect:/?message=User+registered+successfully!";
    }
}
