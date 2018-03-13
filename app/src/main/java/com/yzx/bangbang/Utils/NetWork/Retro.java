package com.yzx.bangbang.Utils.NetWork;

import com.yzx.bangbang.Utils.Params;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/3/10.
 */

public class Retro {
    public static Retrofit inst(){
        return new Retrofit.Builder()
                .baseUrl("http://" + Params.ip + ":8080/BangBang/")
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30,TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
