package com.yzx.bangbang.interfaces.network;

import java.util.List;

import io.reactivex.Flowable;
import model.ChatRecord;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IChat {
    @GET("get_chat_record")
    Flowable<List<ChatRecord>> get_chat_record(@Query("user_id") int user_id,@Query("other_id") int other_id);

//    @POST
//    Flowable<Integer> post_message(@Query("user_id") int user_id,@Query("other_id") int other_id);

}
