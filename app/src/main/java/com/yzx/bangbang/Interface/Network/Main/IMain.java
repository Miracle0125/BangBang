package com.yzx.bangbang.Interface.Network.Main;

import java.util.List;

import io.reactivex.Flowable;
import model.Assignment;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IMain {
    @GET("get_assignment")
    Flowable<List<Assignment>> get_assignment(@Query("mode") int mode);
}

