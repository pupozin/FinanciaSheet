package com.financiasheet.demo.service;

import com.financiasheet.demo.dto.*;
import com.financiasheet.demo.entity.User;
import com.financiasheet.demo.repository.UserRepository;
import com.financiasheet.demo.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository r, PasswordEncoder e, AuthenticationManager am, JwtService jwt){
        this.repo = r; this.encoder = e; this.authManager = am; this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req){
        if (repo.existsByEmail(req.email())) throw new RuntimeException("Email já cadastrado");
        var user = new User();                 // <- sem Lombok
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPasswordHash(encoder.encode(req.password()));
        repo.save(user);
        var token = jwt.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }


    public AuthResponse login(LoginRequest req){
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var user = repo.findByEmail(req.email()).orElseThrow();
        var token = jwt.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }
}
