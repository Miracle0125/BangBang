package com.yzx.bangbang.Fragment.AD;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.yzx.bangbang.R;


public class FrCommentOpt extends Fragment {
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.ad_comment_option, container, false);
        init();
        return v;
    }

    Animation animation;
    View AnimateBackGround;

    private void init() {
        //AnimateBackGround = ((ViewGroup)v).findViewById(R.id.ad_comment_body);
        //what the fuck?
        AnimateBackGround = ((ViewGroup) v).getChildAt(0);
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //int current = (int) (0xffffff - (0xffffff - 0x363636)* interpolatedTime);
                //String color =  "#D2" + Integer.toHexString(current);
                //AnimateBackGround.setBackgroundColor(Color.parseColor(color));
                Log.d("ani" , "    "+   interpolatedTime );
                int end = 0xd2;
                int o = (int) (end * interpolatedTime);
                String s = Integer.toHexString(o);
                if (s.length()==1)
                    s = "0"+s;
                //v.setBackgroundColor(Color.parseColor("#363636"));
                //v.setAlpha((int)(255* interpolatedTime));
                v.setBackgroundColor(Color.parseColor("#" + s + "363636"));
            }
        };
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimateBackGround.findViewById(R.id.ad_comment_opt_body).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if (AnimateBackGround!=null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AnimateBackGround.startAnimation(animation);
                            }
                        });
                        break;
                    }else{
                        AnimateBackGround = v.findViewById(R.id.ad_comment_body);
                    }
                }
            }
        }).start();*/
    }

    private String toHex(int i) {
        String r = "";
        int ten = i / 16;
        if (ten < 10)
            r += String.valueOf(ten);
        else
            switch (ten) {
                case 10:
                    r += "a";
                    break;
                case 11:
                    r += "b";
                    break;
                case 12:
                    r += "c";
                    break;
                case 13:
                    r += "d";
                    break;
                case 14:
                    r += "e";
                    break;
                case 15:
                    r += "f";
                    break;
            }
        int unit = i % 16;
        if (unit < 10)
            r += String.valueOf(unit);
        else
            switch (unit) {
                case 10:
                    r += "a";
                    break;
                case 11:
                    r += "b";
                    break;
                case 12:
                    r += "c";
                    break;
                case 13:
                    r += "d";
                    break;
                case 14:
                    r += "e";
                    break;
                case 15:
                    r += "f";
                    break;
            }
        return r;
    }
}
