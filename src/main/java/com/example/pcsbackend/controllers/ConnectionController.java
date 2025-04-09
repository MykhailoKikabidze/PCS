package com.example.pcsbackend.controllers;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class ConnectionController {

    @GetMapping
    public String testHello() {
        return "Hello, REST!";
    }

    @PostMapping
    public String postHello(@RequestParam String name, @RequestParam String surname) {
        return "Hello, " + name + ", " + surname;
    }
}

