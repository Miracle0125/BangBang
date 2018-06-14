package com.yzx.bangbang.utils.netWork;

import android.net.Uri;

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

    public static final int IMAGE_ASSIGNMENT = 0;
    public static final int IMAGE_PORTRAIT = 1;
    private static final String[] image_dir = {"", "portrait/"};

    private static Retrofit.Builder getBaseBuilder() {
        return new Retrofit.Builder()
                .baseUrl("http://" + Params.ip + "BangBang/")
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS).build());
    }

    private static Retrofit build(Converter.Factory factory) {
        return getBaseBuilder()
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit single() {
        return build(GsonConverterFactory.create());
    }

    public static Retrofit list() {
        return build(new ListFactoryConverter());
    }

    public static Retrofit str() {
        return build(new StringFactoryConverter());
    }

    public static Uri get_image_uri(String name, int type) {
        return Uri.parse("http://" + Params.ip + "server/image/" + image_dir[type] + name + ".jpg");
    }

    public static Uri get_image_uri(String name) {
        return get_image_uri(name, IMAGE_ASSIGNMENT);
    }

    public static Uri get_portrait_uri(int id) {
        return get_image_uri(String.valueOf(id), Retro.IMAGE_PORTRAIT);
    }

    public static MultipartBody files2MultipartBody(List<File> files) {
        if (files.isEmpty()) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.size(); i++) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"image" + i + ".jpg\""), RequestBody.create(MediaType.parse("image/png"), files.get(i)));
        }
        return builder.build();
    }

    private static class ListFactoryConverter extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return (Converter<ResponseBody, Object>) body -> new Gson().fromJson(body.string(), type);
        }
    }

    private static class StringFactoryConverter extends Converter.Factory {
        @Override
        public Converter<ResponseBody, String> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return ResponseBody::string;
        }
    }
}
