package com.quiz.mallzellij_show_android.api;

import com.quiz.mallzellij_show_android.model.Article;
import com.quiz.mallzellij_show_android.model.ArticleStock;
import com.quiz.mallzellij_show_android.model.AuthResponse;
import com.quiz.mallzellij_show_android.model.DevisRequest;
import com.quiz.mallzellij_show_android.model.BpCustomerResponse;
import com.quiz.mallzellij_show_android.model.InventoryRequest;
import com.quiz.mallzellij_show_android.model.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

import okhttp3.ResponseBody;

public interface ApiService {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("api/articles")
    Call<List<Article>> getArticles();

    @GET("api/articles/search")
    Call<List<Article>> searchArticles(@Query("q") String query);

    @GET("api/articles/barcode/{ean}")
    Call<Article> getArticleByBarcode(@Path("ean") String ean);

    @GET("api/articles/{articleId}/stocks")
    Call<List<ArticleStock>> getArticleStocks(@Path("articleId") Long articleId);

    @POST("api/inventory")
    Call<Void> submitInventory(@Body InventoryRequest request);

    @GET("api/inventory/export")
    Call<ResponseBody> downloadCsv(@Query("depot") String depot,
                                   @Query("equipe") String equipe,
                                   @Query("zone") String zone,
                                   @Query("format") String format);

    @POST("api/devis")
    Call<Void> submitDevis(@Body DevisRequest request);

    @GET("api/devis/clients")
    Call<List<BpCustomerResponse>> searchClients(@Query("q") String q);

    @GET("api/devis/export")
    Call<ResponseBody> exportDevisCsv();
}
