package com.quiz.mallzellij_show_android.api;

import com.quiz.mallzellij_show_android.model.Article;
import com.quiz.mallzellij_show_android.model.ArticleStock;
import com.quiz.mallzellij_show_android.model.AuthResponse;
import com.quiz.mallzellij_show_android.model.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("api/articles")
    Call<List<Article>> getArticles();

    @GET("api/articles/search")
    Call<List<Article>> searchArticles(@Query("q") String query);

    @GET("api/articles/{articleId}/stocks")
    Call<List<ArticleStock>> getArticleStocks(@Path("articleId") Long articleId);
}
