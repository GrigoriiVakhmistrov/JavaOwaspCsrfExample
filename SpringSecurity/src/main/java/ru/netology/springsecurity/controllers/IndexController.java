package ru.netology.springsecurity.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/")
@Controller
public class IndexController {
    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping
    @ResponseBody
    public String indexPost() {
        return "CSRF is ok";
    }
}
