package com.yzx.bangbang.Interface.network;

import io.reactivex.Flowable;
import model.Assignment;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/14.
 */

public interface IAssignmentDetail {
    @GET("get_assignment_by_id")
    Flowable<Assignment> get_assignment_by_id(@Query("id") int id);
}
