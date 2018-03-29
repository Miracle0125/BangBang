package com.yzx.bangbang.Interface.network;

import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Comment;

import java.util.List;

import io.reactivex.Flowable;
import model.Assignment;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/14.
 */

public interface IAssignmentDetail {
    @GET("get_assignment_by_id")
    Flowable<Assignment> get_assignment_by_id(@Query("id") int id);

    @GET("get_comment")
    Flowable<List<Comment>> get_comment(@Query("id") int id);

    @GET("get_sub_comment")
    Flowable<List<Comment>> get_sub_comment(@Query("id") int id);

    @POST("post_comment")
    Flowable<Integer> post_comment(@Query("comment") String comment);

    @GET("get_bids")
    Flowable<List<Bid>> get_bids(@Query("id") int id);

}
