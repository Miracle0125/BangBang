package com.yzx.bangbang.utils.NetWork;

import com.google.gson.Gson;
import com.yzx.bangbang.utils.Params;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

    public static Retrofit withList() {
        return getBuilder()
                .addConverterFactory(new ListFactoryConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static MultipartBody files2MultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.size(); i++) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file" + i + ".jpg\""), RequestBody.create(MediaType.parse("image/png"), files.get(i)));
        }
        return builder.build();
    }

    private static class ListFactoryConverter extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return (Converter<ResponseBody, Object>) body -> new Gson().fromJson(body.string(), type);
        }
    }
}
