package com.quiz.backend.controller;

import com.quiz.backend.dto.AuthResponse;
import com.quiz.backend.dto.LoginRequest;
import com.quiz.backend.repository.YmobileUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final YmobileUserRepository userRepository;

    public AuthController(YmobileUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userRepository.findByYlogin0(request.getEmail())
                .filter(user -> request.getPassword().equals(user.getYpass0().trim()))
                .map(user -> ResponseEntity.ok(
                        new AuthResponse(user.getRowid(), user.getYid0(), user.getYlogin0().trim(), "Login successful")))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, null, "Invalid email or password")));
    }
}
