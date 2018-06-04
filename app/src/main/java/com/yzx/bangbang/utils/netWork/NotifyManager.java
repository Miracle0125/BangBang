package com.yzx.bangbang.utils.netWork;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.yzx.bangbang.R;

public class NotifyManager {

    public static void on_new_notify(String text){

    }

    public static void show_not_read_notify(Context context,int not_read) {
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.main_icon_portrait)
                .setContentTitle(not_read + "条新消息")
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.notify(1, notification);
    }
}
