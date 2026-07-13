package com.quiz.mallzellij_show_android;

import com.quiz.mallzellij_show_android.model.AuthResponse;

public class UserSession {
    private static UserSession instance;
    private Long id;
    private String name;
    private String email;
    private String role;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(AuthResponse response) {
        this.id = response.getId();
        this.name = response.getName();
        this.email = response.getEmail();
        this.role = response.getRole();
    }

    public void logout() {
        this.id = null;
        this.name = null;
        this.email = null;
        this.role = null;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public String getRole() { return role; }
    public String getEmail() { return email; }
}
