package com.yzx.bangbang.view.ad;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yzx.bangbang.model.Comment;

import java.lang.ref.WeakReference;


public class CommentView extends LinearLayout {


    public CommentView(Context context) {
        this(context, null, 0);
    }

    public CommentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = new WeakReference<>(context);
        detector = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (listener != null) listener.onItemTouched(poster_name,poster_id, idToAdd,isSubComment);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }
    WeakReference<Context> context;
    View v;
    LayoutInflater inflater;
    Listener listener;
    Comment comment;

    GestureDetectorCompat detector;
    String poster_name;
    int poster_id, idToAdd,parent;
    boolean isSubComment;
/*    public View inflate(){
        if (inflater==null||comment==null||parent==null)
            return null;
        v = inflater.inflate(R.layout.ad_commet, parent, false);
        TextView tv = (TextView) v.findViewById(R.idToAdd.ad_comment_content);
        tv.setText(comment.content);
        tv = (TextView) v.findViewById(R.idToAdd.ad_comment_date);
        tv.setText(util.CustomDate(comment.date));
        tv = (TextView) v.findViewById(R.idToAdd.ad_comment_name);
        tv.setText(comment.poster_name);
        tv = (TextView) v.findViewById(R.idToAdd.ad_comment_flour);
        tv.setText((comment.pos) + "楼");
        detector = new GestureDetectorCompat(context.get(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (listener != null) listener.onItemTouched(poster_name,poster_id,idToAdd);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        setClickable(true);
        return v;
    }*/

    public void setParams(Comment comment){
        this.comment = comment;
        poster_name = comment.poster_name;
        poster_id = comment.poster_id;
        idToAdd = comment.id;
        parent = comment.parent;
        if (comment.parent!=0){
            isSubComment = true;
        }
        if (isSubComment)
            idToAdd = parent;
        else
            idToAdd = comment.id;
    }

    public View getView(){
        return v;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public interface Listener{
        void onItemTouched(String poster_name,int poster_id,int id,boolean isSubComment);
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

}
