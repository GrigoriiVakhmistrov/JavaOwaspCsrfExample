package ru.netology.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringSecurityApplication {
    @GetMapping("/hi")
    public String hi() {
        return "Hi";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, authorized user";
    }

    @GetMapping("/read")
    public String read() {
        return "Read";
    }

    @GetMapping("/write")
    public String write() {
        return "Write";
    }

    @PostMapping("/api/posts")
    public String post() {
        return "Hello from POST";
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }
}
