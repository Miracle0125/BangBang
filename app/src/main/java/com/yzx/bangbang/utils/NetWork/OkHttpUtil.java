package com.yzx.bangbang.utils.NetWork;

import com.yzx.bangbang.utils.Params;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType OCTET = MediaType.parse("application/octet-stream");
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static final String HTTP = "http://";
    simpleOkHttpCallback callback;
    OkHttpCallback okHttpCallback;
    MultipartBody.Builder builder;
    OkHttpClient client;
    public OkHttpUtil(){
        builder = new MultipartBody.Builder();
        client = new OkHttpClient();
        builder.setType(MultipartBody.FORM);
    }
    public OkHttpUtil(simpleOkHttpCallback callback){
        this.callback = callback;
        //MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).build();
         builder = new MultipartBody.Builder();
        client = new OkHttpClient();
        builder.setType(MultipartBody.FORM);
    }

    public OkHttpUtil addPart(String name,String filename,File file,MediaType type){
        builder.addFormDataPart(name, filename, RequestBody.create(type, file));
        return this;
    }
    public OkHttpUtil addPart(String name,String filename,String value,MediaType type){
        builder.addFormDataPart(name, filename, RequestBody.create(type, value));
        return this;
    }
    public OkHttpUtil addPart(String name,String filename,File file){
       return this.addPart(name,filename,file,MEDIA_TYPE_PNG);
    }
    public OkHttpUtil addPart(String name,String value){
        builder.addFormDataPart(name,value);
        return this;
    }
    public MultipartBody build(){
        return builder.build();
    }

    public void post(String servlet) {
        Request request = new Request.Builder().url(getUrl(servlet)).post(build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call arg0, IOException arg1) {
            }

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                if (callback != null)
                    callback.onResponse(arg1.body().string());
                if (okHttpCallback!=null)
                    okHttpCallback.onResponse(arg1);
            }
        });
    }
    private String getUrl(String servlet){
        return "http://"+ Params.ip+":8080/BangBang/"+servlet;
    }

    public static OkHttpUtil inst(simpleOkHttpCallback callback){
        return new OkHttpUtil(callback);
    }
    public interface simpleOkHttpCallback {
        void onResponse(String s);
    }
    public interface OkHttpCallback {
        void onResponse(Response response);
    }
    public void setOkHttpListener(OkHttpCallback okHttpCallback){
        this.okHttpCallback = okHttpCallback;
    }

}
