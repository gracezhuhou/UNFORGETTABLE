package com.example.unforgettable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import static android.content.ContentValues.TAG;
import static cn.bmob.v3.Bmob.getApplicationContext;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationCompat.Builder builder;//兼容
    String name = "unforgettable_channel";
    String id = "my_channel"; // The user-visible name of the channel.
    String description = "channel_for_unforgettable"; // The user-visible description of the channel.
    private static final int NOTIFY_ID = 1000;
    private NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("NOTIFICATION")) {
            if (notificationManager == null) {
                notificationManager =
                        (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    // 配置通知渠道的属性
                    mChannel.setDescription(description);

                    notificationManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(context,id);

                Intent intent2 = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

                builder.setContentTitle("提醒")  // required
                        .setSmallIcon(R.drawable.ic_logo) // required
                        .setContentText("您还有任务没有完成！")  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker("UNFORGETTABLE")
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)//设置该通知优先级
                        .setCategory(Notification.CATEGORY_MESSAGE);//设置通知类别
            } else {
                builder = new NotificationCompat.Builder(context);
                Intent intent2 = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

                builder.setContentTitle("提醒")                           // required
                        .setSmallIcon(R.drawable.ic_logo) // required
                        .setContentText("您还有任务没有完成！")  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker("UNFORGETTABLE")
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)//设置该通知优先级
                        .setCategory(Notification.CATEGORY_MESSAGE);//设置通知类别
            }

            Notification notification = builder.build();
            notificationManager.notify(NOTIFY_ID, notification);
        }

    }
}
