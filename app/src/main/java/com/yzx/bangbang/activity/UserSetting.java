package com.yzx.bangbang.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.model.SimpleIndividualInfo;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.netWork.OkHttpUtil;
import com.yzx.bangbang.utils.netWork.UniversalImageDownloader;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.sql.SpUtil;
import com.yzx.bangbang.utils.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class UserSetting extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {
    User user;
    private static final int QUERY_IMAGE = 1;
    public static final int STATE_FAILED = 10;
    public static final int STATE_SUCCESS = 11;
    boolean changed;
    boolean portraitChanged;
    SimpleIndividualInfo info;
    Gson gson = new Gson();
    UniversalImageDownloader downloader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting_layout);
        init();
    }

    private void init() {
        //user = (User) getIntent().getSerializableExtra("user");
        info = (SimpleIndividualInfo) getIntent().getSerializableExtra("info");
        if (info==null) return;
        downloader = new UniversalImageDownloader(this);
        user = (User) SpUtil.getObject(SpUtil.USER);
        initView();
    }

    CheckBox checkBox1, checkBox2;
    EditText userName, bbContact, region, signature;
    SimpleDraweeView portrait;
    private void initView() {
        findViewById(R.id.user_setting_back).setOnClickListener(this);
        portrait = (SimpleDraweeView) findViewById(R.id.user_setting_portrait);
        portrait.setOnClickListener(this);
        portrait.setDrawingCacheEnabled(true);
        downloader.downLoadPortrait(info.id, portrait);
        //findViewById(R.indv_id.user_setting_back).setOnClickListener(this);
        checkBox1 = (CheckBox) findViewById(R.id.user_setting_checkbox1);
        checkBox2 = (CheckBox) findViewById(R.id.user_setting_checkbox2);
        checkBox1.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);

        userName = (EditText) findViewById(R.id.user_setting_user_name);
        userName.addTextChangedListener(this);
        bbContact = (EditText) findViewById(R.id.user_setting_bb_contact);
        bbContact.addTextChangedListener(this);
        region = (EditText) findViewById(R.id.user_setting_region);
        region.addTextChangedListener(this);
        signature = (EditText) findViewById(R.id.user_setting_sig);
        signature.addTextChangedListener(this);
        if (user != null) {
            userName.setText(user.getName());
            if (user.getBbContact() != null)
                bbContact.setText(user.getBbContact());
            if (user.getRegion() != null)
                region.setText(user.getRegion());
            if (user.getSignature() != null)
                signature.setText(user.getSignature());
            if (user.getSex() == 0) {
                checkBox2.setChecked(true);
            }
        }
        initDialog();
    }

    Dialog dialog;

    private void initDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_setting_dialog);
        View v = dialog.findViewById(R.id.user_setting_select_local);
        v.setOnClickListener(view -> {
            getLocalImage();
            dialog.hide();
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.user_setting_portrait:
                dialog.show();
                //getLocalImage();
                break;
            case R.id.user_setting_back:
                finish();
                break;
            case R.id.user_setting_save:
                uploadData();
                break;
        }
    }
    int response_state;
    private void uploadData() {
        if (userName.getText().toString().equals("")) {
            Toast.makeText(UserSetting.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setName(userName.getText().toString());
        user.setRegion(region.getText().toString());
        user.setSignature(signature.getText().toString());
        if (checkBox1.isChecked())
            user.setSex(1);
        else user.setSex(0);
        user.setBbContact(bbContact.getText().toString());

        OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
            response_state = Integer.valueOf(s);
            runOnUiThread(() -> {
                switch (response_state) {
                    case STATE_FAILED:
                        setToast("上传失败");
                    case STATE_SUCCESS:
                        setToast("上传成功");
                }
            });
        });
        okHttp.addPart("user", null, gson.toJson(user), OkHttpUtil.MEDIA_TYPE_JSON);
        if (portraitChanged && path != null) {
            InputStream in;
            try {
                //ByteArrayOutputStream baos = util.compress(portrait.getDrawingCache(), 100);
                ByteArrayOutputStream baos = util.compress(BitmapFactory.decodeFile(path), 100);
                File file = new File(Params.TEMP_DIR + util.getRandomString(8) + ".png");
                in = new ByteArrayInputStream(baos.toByteArray());
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) fos.write(buffer, 0, len);
                okHttp.addPart("image", null, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        okHttp.post("user_setting");
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.user_setting_checkbox1 && b) {
            checkBox2.setChecked(false);
        }
        if (compoundButton.getId() == R.id.user_setting_checkbox2 && b)
            checkBox1.setChecked(false);
    }

    String path = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QUERY_IMAGE) {
            String[] prj = {MediaStore.Images.Media.DATA};
            //Cursor cursor = managedQuery(data.getData(), pojo, null, null, null);
            if (data == null)
                return;
            Cursor cursor = getContentResolver().query(data.getData(), prj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(index);
                cursor.close();
            }
            setImage(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(String path) {
        if (path == null)
            return;
        if (portrait != null) {
            portrait.setImageURI(Uri.fromFile(new File(path)));
        }
        if (!changed) {
            setSaveButton();
            changed = true;
        }
        portraitChanged = true;
    }

    private void getLocalImage() {
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        //intent.setType("image/*");
        startActivityForResult(intent, QUERY_IMAGE);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!changed) {
            setSaveButton();
            changed = true;
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    View save;

    private void setSaveButton() {
        save = findViewById(R.id.user_setting_save);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(this);
    }

    private void setToast(String s) {
        Toast.makeText(UserSetting.this, s, Toast.LENGTH_SHORT).show();
    }

}
