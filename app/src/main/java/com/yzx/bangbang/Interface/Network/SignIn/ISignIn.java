package com.yzx.bangbang.Interface.Network.SignIn;
import com.yzx.bangbang.model.receiver.RSignIn;
import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/10.
 */

public interface ISignIn{
    @GET("sign_in")
    Flowable<RSignIn> impl(@Query("account") String account, @Query("password") String password);
}


