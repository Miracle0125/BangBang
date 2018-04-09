package com.yzx.bangbang.interfaces.network;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ISignUp {
    @GET("sign_up")
    Flowable<Integer> impl(@Query("username") String username, @Query("account") String account, @Query("password") String password);
}

