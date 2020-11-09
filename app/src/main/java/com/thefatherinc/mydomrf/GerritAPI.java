package com.thefatherinc.mydomrf;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface GerritAPI {
    @POST("/login/")
    Call<RequestLogin> logIn(@Body RequestLoginBody message);

    @POST("/get_news/")
    Call<RequestGetNews> getNews(@Body RequestLoginBody message);

    @POST("/get_news_full/")
    Call<RequestGetNewsFull> getNewsFull(@Body RequestGetNewsFullBody message);

    @Multipart
    @POST("/set_new_goods/")
    Call<RequestLogin> upload(@Part MultipartBody.Part file);
}