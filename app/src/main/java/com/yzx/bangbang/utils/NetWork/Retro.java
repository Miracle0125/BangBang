package com.yzx.bangbang.utils.NetWork;

import com.google.gson.Gson;
import com.yzx.bangbang.utils.Params;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



public class Retro {


    private static Retrofit.Builder getBuilder() {
        return new Retrofit.Builder()
                .baseUrl("http://" + Params.ip + ":8080/BangBang/")
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS).build());
    }

    public static Retrofit inst() {
        return getBuilder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
    public static Retrofit withList(){
        return getBuilder()
                .addConverterFactory(new ListFactoryConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static class ListFactoryConverter extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return (Converter<ResponseBody, Object>) body -> new Gson().fromJson(body.string(),type);
        }
    }
}
