package com.quiz.mallzellij_show_android.api;

import com.quiz.mallzellij_show_android.model.AuthResponse;
import com.quiz.mallzellij_show_android.model.LoginRequest;
import com.quiz.mallzellij_show_android.model.SignupRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/signup")
    Call<AuthResponse> signup(@Body SignupRequest request);
}
