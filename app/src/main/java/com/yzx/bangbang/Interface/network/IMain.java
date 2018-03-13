package com.yzx.bangbang.Interface.network;

import java.util.List;

import io.reactivex.Flowable;
import model.Assignment;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMain {
    @GET("get_assignment")
    Flowable<List<Assignment>> get_assignment(@Query("mode") int mode);

    @POST("new_assignment")
    Flowable<Integer> new_assignment(@Query("assignment") String s, @Body MultipartBody body);
}

