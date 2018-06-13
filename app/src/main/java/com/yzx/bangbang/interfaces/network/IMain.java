package com.yzx.bangbang.interfaces.network;

import android.support.annotation.Nullable;

import com.yzx.bangbang.model.Contact;
import com.yzx.bangbang.model.Notify;

import java.util.List;

import io.reactivex.Flowable;
import model.Assignment;
import model.ChatRecord;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMain {
    @GET("get_assignment")
    Flowable<List<Assignment>> get_assignment(@Query("mode") int mode, @Query("what") int what);

    @POST("new_assignment")
    Flowable<Integer> new_assignment(@Query("assignment") String s, @Body MultipartBody body);

    @POST("new_assignment")
    Flowable<Integer> new_assignment(@Query("assignment") String s);

    @GET("get_user_assignment")
    Flowable<List<Assignment>> get_user_assignment(@Query("user_id") int user_id, @Query("filter") int filter);

    @GET("get_notify")
    Flowable<List<Notify>> get_notify(@Query("user_id") int user_id);

    @POST("read_notify")
    Flowable<Integer> read_notify(@Query("id") int id);

    @GET("get_contacts")
    Flowable<List<Contact>> get_contacts(@Query("owner") int user_id);

    @POST("add_to_contact")
    Flowable<Integer> add_to_contact(@Query("owner") int owner, @Query("person_id") int person_id);

    @GET("get_recent_conversations")
    Flowable<List<ChatRecord>> get_recent_conversations(@Query("user_id") int user_id);

    @POST("charge")
    Flowable<Integer> charge(@Query("user_id") int user_id, @Query("amount") int amount);

    @GET("get_balance")
    Flowable<Integer> get_balance(@Query("user_id") int user_id);


}

