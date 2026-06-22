package com.quiz.mallzellij_show_android.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String message;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
}
