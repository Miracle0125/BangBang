package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.Service.DeprecatedService;
import com.yzx.bangbang.activity.IndvInfo;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.netWork.OkHttpUtil;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.sql.SpUtil;
import com.yzx.bangbang.utils.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class FrUser extends Fragment implements View.OnClickListener {
    View view;
    FrUser frUser;
    User user;
    SimpleDraweeView draweeView;
    private static final int DOWNLOAD_COMPLETE = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        frUser = this;
        view = inflater.inflate(R.layout.main_fr_user, container, false);
        View v = view.findViewById(R.id.main_fr_user_exit);
        View v2 = view.findViewById(R.id.main_fr_user_portrait_bar);
        draweeView = (SimpleDraweeView) view.findViewById(R.id.main_fr_user_portrait);
        //((RippleView) v).setRippleColor();
        v2.setOnClickListener(this);
        if (v != null)
            v.setOnClickListener(FrUser.this);
        init();
        return view;
    }

    private void init() {
        //user = gson.fromJson(util.getString(R.string.key_user, getActivity()), User.class);
        user= (User) SpUtil.getObject(SpUtil.USER);
        TextView v = (TextView) view.findViewById(R.id.main_fr_user_name);
        v.setText(user.getName());
        //downloadUserRecord();
        new Thread(this::downloadPortrait).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fr_user_exit:
                //DeprecatedService.inst.close();
                Flowable.just(util.obtain_message(Main.ACTION_EXIT_LOG_IN))
                        .delay(500, TimeUnit.MILLISECONDS)
                        .compose(context().<Message>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(getConsumer());
                break;
            case R.id.main_fr_user_portrait_bar:
                Intent intent = new Intent(context(), IndvInfo.class);
                SimpleIndividualInfo info = new SimpleIndividualInfo(user.getId(), user.getName());
                intent.putExtra("info", info);
/*                if (image_path != null)
                    intent.putExtra("path", image_path);*/
                startActivity(intent);
                break;
        }
    }

    String image_base64;
    String image_path;
    static int code;

    private void downloadUserRecord() {
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(new OkHttpUtil.simpleOkHttpCallback() {
            @Override
            public void onResponse(String s) {
                if (s.charAt(0) == '<')
                    return;
                Gson gson = new Gson();
                final UserRecord userRecord = gson.fromJson(s, UserRecord.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //loadUserRecord(userRecord);
                    }
                });
            }
        });
        okHttpUtil.addPart("user_id", String.valueOf(user.getId()));
        okHttpUtil.post("user_record");
    }

    private void loadUserRecord(UserRecord userRecord) {
        if (userRecord == null)
            return;
/*        TextView v= (TextView) view.findViewById(R.id.main_fr_user_num_asm);
        v.setText(String.valueOf(userRecord.num_asm));
        v = (TextView) view.findViewById(R.id.main_fr_user_num_accept);
        v.setText(String.valueOf(userRecord.num_accept));
        v = (TextView) view.findViewById(R.id.main_fr_user_num_concern);
        v.setText(String.valueOf(userRecord.num_concern));
        v = (TextView) view.findViewById(R.id.main_fr_user_num_coll);
        v.setText(String.valueOf(userRecord.num_coll));*/
    }

    private void downloadPortrait() {
        if (user == null)
            return;
        code = util.getRandomInt(8);
        OkHttpUtil okHttpUtil = OkHttpUtil.inst(new OkHttpUtil.simpleOkHttpCallback() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                image_base64 = s;
                if (s.length() < 10)
                    return;
                DeprecatedService.CommonImageReceiver receiver = gson.fromJson(s, DeprecatedService.CommonImageReceiver.class);
                if (receiver.code != code)
                    return;
                image_path = Params.TEMP_DIR + util.getRandomString(8) + ".png";
                util.writeFile(Base64.decode(receiver.image_base64, Base64.DEFAULT), image_path, new util.writeFileCallback() {
                    @Override
                    public void onFinish() {
                        //setImage(image_path);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (draweeView != null)
                                    draweeView.setImageURI(Uri.fromFile(new File(image_path)));
                            }
                        });
                    }
                });
                frUserHandler.sendEmptyMessage(DOWNLOAD_COMPLETE);
            }
        });
        okHttpUtil.addPart(String.valueOf(code), "select image from portrait where user_id = " + user.getId());
        okHttpUtil.post("query_image");
    }

/*    private void setImage(String path) {
        if (image_base64.equals("") || draweeView == null)
            return;
        //Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        draweeView.setImageURI(path);
    }*/

    FrUserHandler frUserHandler = new FrUserHandler(this);

    public static class FrUserHandler extends Handler {
        private WeakReference<FrUser> ref;

        public FrUserHandler(FrUser fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrUser inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {
            case DOWNLOAD_COMPLETE:
                break;
        }
    }

    private class UserRecord {
        public int user_id, num_asm, num_accept, num_concern, num_coll;

        public UserRecord(int user_id, int num_asm, int num_accept, int num_concern, int num_coll) {
            this.user_id = user_id;
            this.num_asm = num_asm;
            this.num_accept = num_accept;
            this.num_concern = num_concern;
            this.num_coll = num_coll;
        }
    }

    private Consumer<Message> getConsumer() {
        return ((Main) getActivity()).consumer;
    }

    private Main context(){
        return (Main) getActivity();
    }
}
