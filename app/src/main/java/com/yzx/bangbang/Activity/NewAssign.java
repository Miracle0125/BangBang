package com.yzx.bangbang.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yzx.bangbang.module.Mysql.AssignmentModule;
import com.yzx.bangbang.module.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.Service.NetworkService;
import com.yzx.bangbang.Utils.ActivityManager;
import com.yzx.bangbang.Utils.SpUtil;
import com.yzx.bangbang.Utils.NetWork.OkHttpUtil;
import com.yzx.bangbang.Utils.Params;
import com.yzx.bangbang.Utils.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NewAssign extends AppCompatActivity implements View.OnClickListener {
    public static final int QUERY_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_asm_layout);
        ActivityManager.getManager().addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        init();
    }

    User user;
    CheckBox isLocationEnable;
    EditText title, content, price;
    List<SimpleDraweeView> imageViewList;
    List<Bitmap> bitmaps;

    private void init() {
        user = (User) getIntent().getSerializableExtra("user");
        layoutChanged = false;
        numOfImages = 0;
        imageViewList = new ArrayList<>();
        bitmaps = new ArrayList<>();
        setMargin();
        initViews();
        getLocation();
    }

    View btn_send;

    private void initViews() {
        imageViewList.add((SimpleDraweeView) findViewById(R.id.new_asm_image0));
        imageViewList.add((SimpleDraweeView) findViewById(R.id.new_asm_image1));
        imageViewList.add((SimpleDraweeView) findViewById(R.id.new_asm_image2));
        isLocationEnable = (CheckBox) findViewById(R.id.main_fr_new_asm_checkbox);
        title = (EditText) findViewById(R.id.new_asm_title);
        content = (EditText) findViewById(R.id.new_asm_content);
        price = (EditText) findViewById(R.id.new_asm_price);
        findViewById(R.id.main_fr_new_asm_cancel).setOnClickListener(this);
        findViewById(R.id.main_fr_new_asm_local_image).setOnClickListener(this);
        btn_send = findViewById(R.id.new_asm_send);
        btn_send.setOnClickListener(this);
        paths = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QUERY_IMAGE) {
            String[] prj = {MediaStore.Images.Media.DATA};
            String path = null;
            //Cursor cursor = managedQuery(data.getData(), pojo, null, null, null);
            if (data == null)
                return;
            Cursor cursor = getContentResolver().query(data.getData(), prj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(index);
            }
            if (numOfImages > 2) {
                setToast("最多只能添加3张图片");
            } else {
                setImage(path);
                paths.add(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean layoutChanged;
    List<String> paths;

    private void setImage(String path) {
        if (path == null)
            return;
        if (!layoutChanged) {
            View divide = findViewById(R.id.main_fr_new_asm_checkbox_bar);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) divide.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.new_asm_images_line1);
            divide.setLayoutParams(params);
            layoutChanged = true;
        }
        SimpleDraweeView draweeView = getImageView();
        if (draweeView != null) {
            draweeView.setImageURI(Uri.fromFile(new File(path)));
        }
    }


    private int numOfImages;

    private SimpleDraweeView getImageView() {
        if (numOfImages == 3)
            return null;
        return imageViewList.get(numOfImages++);
    }

    private void getLocalImage() {
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        //intent.setType("image/*");
        startActivityForResult(intent, QUERY_IMAGE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fr_new_asm_cancel:
                //ActivityManager.getManager().onFinish();
                finish();
                break;
            case R.id.main_fr_new_asm_local_image:
                getLocalImage();
                break;
            case R.id.new_asm_send:
                send();
                break;
        }
    }

    double latitude, longitude;
    private void getLocation() {
        AMapLocationClient locationClient = new AMapLocationClient(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        locationClient.setLocationOption(mLocationOption);
        locationClient.setLocationListener((amapLocation) -> {
            if (amapLocation != null)
                if (amapLocation.getErrorCode() == 0) {
                    latitude = amapLocation.getLatitude();
                    longitude = amapLocation.getLongitude();
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
        });
        locationClient.startLocation();
    }

    private void send() {
        String inputTitle = title.getText().toString();
        if (inputTitle.equals("")) {
            setToast("标题不能为空");
            return;
        }
        String inputPrice = price.getText().toString();
        if (inputPrice.equals("")) {
            setToast("价格不能为空");
            return;
        }
        if (!checkIfThePriceIsValid(inputPrice)) {
            setToast("价格格式错误");
            return;
        }
        btn_send.setClickable(false);
        setToast("开始上传");
        new Thread(() -> {
            Gson gson = new Gson();
            AssignmentModule assignment = new AssignmentModule(0, title.getText().toString(), content.getText().toString(), user.getId(), user.getName(), util.getDate(), Float.valueOf(price.getText().toString()), 0, numOfImages, 0, latitude, longitude);
            //Assignment assignment = new Assignment(user.name, title.getText().toString(), content.getText().toString(), util.getDate(), 0, 0, user.id, numOfImages, Float.valueOf(price.getText().toString()));
            OkHttpUtil okHttp = OkHttpUtil.inst(s -> {
                if (s.equals("success")) {
                    runOnUiThread(() -> setToast("上传成功"));
                    exit();
                    SpUtil.putRefreshFlag(NewAssign.this);
                    updateService();
                }
            });
            okHttp.addPart("assignment", null, gson.toJson(assignment), OkHttpUtil.MEDIA_TYPE_JSON);
            List<File> images;
            if (numOfImages > 0) {
                images = addImageFile();
                for (int i = 0; i < numOfImages; i++) {
                    okHttp.addPart("image", String.valueOf(i), images.get(i));
                }
            }
            okHttp.post("new_assign");
        }).start();

    }

    private void updateService(){
        if (NetworkService.inst!=null)
            NetworkService.inst.getAssignments();
    }

    private List<File> addImageFile() {
        List<File> images = new ArrayList<>();
        for (int i = 0; i < numOfImages; i++) {
            //Bitmap scr = ((BitmapDrawable) imageViewList.get(i).getDrawable()).getBitmap();
/*                        int height, width;
                    if (src.getHeight() > src.getWidth()) {
                        height = 300;
                        width = (300 / src.getWidth()) * src.getWidth();
                    } else {
                        width = 300;
                        height = (300 / src.getHeight()) * src.getHeight();
                    }*/
            try {
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //Thumbnails.of(new ByteArrayInputStream(baos.toByteArray())).size(width, height).toOutputStream(baos);
                //File file = new File(paths.get(i));
                //Thumbnails.of(file).size(width, height).toOutputStream(baos);
                //baos = util.compress(((BitmapDrawable) imageViewList.get(i).getDrawable()).getBitmap(), 200);
                //Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i));
                //bitmaps.add(bitmap);
                ByteArrayOutputStream baos = util.compress(util.loadBitmapFromView(imageViewList.get(i)), 200);
                File file = new File(Params.TEMP_DIR + util.getRandomString(8) + ".png");
                InputStream in = new ByteArrayInputStream(baos.toByteArray());
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                images.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    private boolean checkIfThePriceIsValid(String s) {
        boolean full_stop = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 48 && c <= 57)
                continue;
            if (c == '.' && !full_stop) full_stop = true;
            else return false;
        }
        int p = Integer.valueOf(s);
        if (p <= 0) return false;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityManager.getManager().onFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setMargin() {
        //RelativeLayout mainLayout = (RelativeLayout) findViewById(R.parent_id.new_asm_layout);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.new_asm_status_bar);
        //magic
        if (frameLayout == null)
            return;
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        param.height = Params.statusHeight;
        frameLayout.setLayoutParams(param);
    }

    public NewAssignHandler newAssignHandler = new NewAssignHandler(this);

    @Override
    protected void onDestroy() {
        clear();
        super.onDestroy();
    }

    private void clear() {
        for (Bitmap bitmap : bitmaps) {
            bitmap.recycle();
        }
    }

    public static class NewAssignHandler extends Handler {
        private WeakReference<NewAssign> ref;

        public NewAssignHandler(NewAssign activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewAssign inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {

        }
    }

    private void exit() {
        //ActivityManager.getManager().onFinish();
        finish();
    }

    private class ImageInfo {
        int pos;

        //int asm_id;
        public ImageInfo() {
            this.pos = pos;
            //this.asm_id = asm_id;
        }
    }

    private void setToast(String toShow) {
        Toast.makeText(NewAssign.this, toShow, Toast.LENGTH_SHORT).show();
    }
}
