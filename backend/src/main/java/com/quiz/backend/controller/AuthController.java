package com.quiz.backend.controller;

import com.quiz.backend.dto.AuthResponse;
import com.quiz.backend.dto.LoginRequest;
import com.quiz.backend.dto.SignupRequest;
import com.quiz.backend.entity.User;
import com.quiz.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(null, null, null, "Email already registered"));
        }
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName());
        user = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(user.getId(), user.getName(), user.getEmail(), "Signup successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> ResponseEntity.ok(
                        new AuthResponse(user.getId(), user.getName(), user.getEmail(), "Login successful")))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, null, "Invalid email or password")));
    }
}
