package com.yzx.bangbang.interfaces.network;
import com.yzx.bangbang.model.receiver.RSignIn;
import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/10.
 */

public interface ISignIn{
    @GET("sign_in")
    Flowable<RSignIn> sign_in(@Query("account") String account, @Query("password") String password);

    @POST("sign_up")
    Flowable<Integer> sign_up(@Query("username") String username, @Query("account") String account, @Query("password") String password);

}


