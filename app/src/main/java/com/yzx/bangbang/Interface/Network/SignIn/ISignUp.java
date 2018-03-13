package com.yzx.bangbang.Interface.Network.SignIn;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/10.
 */

public interface ISignUp {
    @GET("sign_up")
    Flowable<Integer> impl(@Query("username") String username, @Query("account") String account, @Query("password") String password);
}

