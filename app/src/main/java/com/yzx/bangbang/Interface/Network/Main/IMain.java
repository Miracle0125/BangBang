package com.yzx.bangbang.Interface.Network.Main;

import com.yzx.bangbang.module.receiver.LAssignment;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2018/3/12.
 */

public interface IMain {
    @GET("get_assignment")
    Flowable<LAssignment> get_assignment();
}

