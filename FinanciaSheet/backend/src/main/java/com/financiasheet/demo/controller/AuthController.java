package com.financiasheet.demo.controller;

import com.financiasheet.demo.dto.*;
import com.financiasheet.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService s){ this.service = s; }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest req){ return service.register(req); }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest req){ return service.login(req); }
}
