package com.yzx.bangbang.interfaces.network;

import com.yzx.bangbang.model.UserRecord;

import io.reactivex.Flowable;
import model.Assignment;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/29.
 */

public interface ICommon {
    @GET("get_user_record")
    Flowable<UserRecord> get_user_record(@Query("user_id") int user_id);

    @GET("get_user_name_by_id")
    Flowable<String> get_username_by_id(@Query("user_id") int id);
}
