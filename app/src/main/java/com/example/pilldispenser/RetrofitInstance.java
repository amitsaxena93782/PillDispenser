package com.example.pilldispenser;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    static RetrofitInstance instance;
    ApiService apiInterface;

    private static String baseURl = "http://10.0.2.2:5000/";

    public static void setBaseUrl(String url) {
        baseURl = url;
        instance = null; // Reset retrofit instance
    }


    RetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiService.class);
    }

    public static RetrofitInstance getInstance() {
        if (instance == null) {
            instance = new RetrofitInstance();
        }
        return instance;
    }
}