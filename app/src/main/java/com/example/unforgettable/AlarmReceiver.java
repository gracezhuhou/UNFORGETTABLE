package com.example.unforgettable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;
    private NotificationCompat.Builder builder;

    // There are hardcoding only for show it's just strings
    String name = "my_package_channel";
    String id = "my_package_channel_1"; // The user-visible name of the channel.
    String description = "my_package_first_channel"; // The user-visible description of the channel.
    private static final int NOTIFY_ID = 1000;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("NOTIFICATION")) {
//            NotificationManager manager = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            Intent intent2 = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
//            Notification notify = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.drawable.ic_logo)
//                    .setTicker("UNFORGETTABLE")
//                    .setContentTitle("警告")
//                    .setContentText("快点儿学习！")
//                    //.setStyle(new NotificationCompat.BigTextStyle().bigText("此处注明的是有关需要提醒项目的某些重要内容"))
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true)
//                    .setNumber(1).build();
//            notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//            manager.notify(NOTIFICATION_ID, notify);

            if (notificationManager == null) {
                notificationManager =
                        (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(context, id);

                Intent intent2 = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

                builder.setContentTitle("警告")  // required
                        .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                        .setContentText("快点干活！")  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker("UNFORGETTABLE")
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//                builder.setDefaults(Notification.DEFAULT_SOUND);//设置声音
//                builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯
//                builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动
//                builder.setDefaults(Notification.DEFAULT_ALL);//设置全部
            } else {

                builder = new NotificationCompat.Builder(context);

                Intent intent2 = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

                builder.setContentTitle("警告")                           // required
                        .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                        .setContentText("快点干活！")  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker("UNFORGETTABLE")
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setPriority(Notification.PRIORITY_HIGH);
            } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            Notification notification = builder.build();
            notificationManager.notify(NOTIFY_ID, notification);
        }

    }

}
