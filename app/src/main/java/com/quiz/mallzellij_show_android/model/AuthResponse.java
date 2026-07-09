package com.quiz.mallzellij_show_android.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String message;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}
