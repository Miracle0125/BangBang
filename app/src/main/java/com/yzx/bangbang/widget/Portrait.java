package com.yzx.bangbang.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.activity.PersonalHomepage;
import com.yzx.bangbang.utils.netWork.Retro;

public class Portrait extends SimpleDraweeView {

    public Portrait(Context context) {
        this(context, null, 0);
    }

    public Portrait(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Portrait(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        if (context instanceof PersonalHomepage) setClickable(false);
        setOnClickListener(v -> {
            if (id == 0) return;
            Intent intent = new Intent(context, PersonalHomepage.class);
            intent.putExtra("person_id", id);
            context.startActivity(intent);
        });
    }

    public void setData(int id) {
        this.id = id;
        setImageURI(Retro.get_portrait_uri(id));
    }

    public String name;
    public int id;
    Context context;
}
