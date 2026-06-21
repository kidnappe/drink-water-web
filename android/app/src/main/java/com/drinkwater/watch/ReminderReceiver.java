package com.drinkwater.watch;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "drink_reminder";
    public static final String ACTION_REMINDER = "com.drinkwater.watch.REMINDER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_REMINDER.equals(intent.getAction())) {
            showNotification(context);
            // Reschedule next alarm if this was a one-shot (setExact mode)
            // We reschedule from the bridge side after user config
        }
    }

    private void showNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ensureChannel(nm);

        Intent launchIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent pi = PendingIntent.getActivity(context, 0, launchIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("\uD83D\uDCA7 该喝水了！")
                .setContentText("记得补充水分哦 \uD83D\uDCA7")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        nm.notify(1001, builder.build());
    }

    public static void ensureChannel(NotificationManager nm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID,
                    "\u559D\u6C34\u63D0\u9192", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("\u5B9A\u65F6\u559D\u6C34\u63D0\u9192\u901A\u77E5");
            nm.createNotificationChannel(ch);
        }
    }

    public static void schedule(Context context, long intervalMs) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.setAction(ACTION_REMINDER);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerAt = System.currentTimeMillis() + intervalMs;
        // setInexactRepeating 在 API 31+ 虽标记 deprecated 但仍正常运作
        // 最小间隔 ~15 分钟，适合喝水提醒场景
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAt, intervalMs, pi);
    }

    public static void cancel(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.setAction(ACTION_REMINDER);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
        pi.cancel();
    }
}
