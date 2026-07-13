package com.quiz.mallzellij_show_android.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

   
//    private static final String BASE_URL = "http://172.29.144.1:8080/";
    // private static final String BASE_URL = "http://localhost:8080/";
    private static final String BASE_URL = "http://192.168.1.53:8080/";
    // private static final String BASE_URL = "http://10.0.2.2:8080/";


    //  private static final String BASE_URL = "http://localhost:8080/";


    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
