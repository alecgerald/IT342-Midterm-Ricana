package com.ricana.activity_midterm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String displayLoginPage() {
        return "login"; // Render the login page
    }
}