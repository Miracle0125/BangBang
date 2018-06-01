package com.yzx.bangbang.interfaces.network;

import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Comment;
import java.util.List;
import io.reactivex.Flowable;
import model.Assignment;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @POST("post_bid")
    Flowable<Integer> post_bid(@Query("bid") String bid);

    @POST("choose_bid")
    Flowable<Integer> choose_bid(@Query("bid") String bid);

    @GET("get_on_going_bid")
    Flowable<Bid> get_on_going_bid(@Query("asm_id") int asm_id);

    @GET("get_on_going_remain_time")
    Flowable<String> get_on_going_remain_time(@Query("asm_id") int asm_id);

    @POST("apply_for_checking")
    Flowable<Integer> apply_for_checking(@Query("asm_id") int asm_id);

    @POST("check_qualified")
    Flowable<Integer> check_qualified(@Query("asm_id") int asm_id);

    @POST("check_unqualified")
    Flowable<Integer> check_unqualified(@Query("asm_id") int asm_id);

    @POST("unable_to_finish")
    Flowable<Integer> unable_to_finish(@Query("asm_id") int asm_id);

    @POST("evaluate")
    Flowable<Integer> evaluate(@Query("evaluate") String evaluate);

    @GET("check_evaluate")
    Flowable<Integer> check_evaluate(@Query("asm_id") int asm_id,@Query("user_id") int user_id);


}
