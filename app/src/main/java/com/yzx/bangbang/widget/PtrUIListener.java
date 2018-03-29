package com.yzx.bangbang.widget;

/**
 * Created by Administrator on 2016/8/1.
 */
public interface PtrUIListener {



    void onUIReset();

   void onUIRefreshBegin();


    void onUIPositionChange(int translation);

    void onUIRefreshCanceled();
}
