package com.example.reminderapp.Notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.reminderapp.MainActivity;
import com.example.reminderapp.Model.Reminder;
import com.example.reminderapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;


public class AlarmReceiver extends BroadcastReceiver {
    final String CHANEL_ID = "10001";

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("---noti---");
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime());

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANEL_ID, "Nhắc nhở", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Mieu ta");
            notificationManager.createNotificationChannel(channel);

        }

        Intent intentHome = new Intent(context, MainActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentHome, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANEL_ID)
                .setContentTitle("Bạn có công việc cần phải hoàn thành")
                .setSmallIcon(R.drawable.ic_notifications)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(getNotificationid(), builder.build());

    }

    private int getNotificationid() {
        int time=(int) new Date().getTime();
        return time;
    }

}