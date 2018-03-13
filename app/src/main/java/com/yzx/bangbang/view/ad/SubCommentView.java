package com.yzx.bangbang.view.ad;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.yzx.bangbang.model.Comment;

public class SubCommentView extends RelativeLayout {
    public SubCommentView(Context context) {
        this(context,null,0);
    }

    public SubCommentView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SubCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    GestureDetectorCompat detector;
    Listener listener;
    Comment comment;
    String poster_name;
    int poster_id,id;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public interface Listener{
        void onSubCommentTouched(String poster_name,int poster_id,int id);
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public void setParams(Comment comment){
        this.comment = comment;
        poster_name = comment.poster_name;
        poster_id = comment.poster_id;
        id = comment.id;
    }

}
