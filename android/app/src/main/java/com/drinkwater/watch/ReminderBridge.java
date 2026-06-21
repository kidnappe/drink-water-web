package com.drinkwater.watch;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.webkit.JavascriptInterface;

public class ReminderBridge {
    private final Context context;

    public ReminderBridge(Context context) {
        this.context = context.getApplicationContext();
    }

    /** 安排一次性提醒，intervalMinutes 后触发；前端在 Toast 显示后再次调用实现循环 */
    @JavascriptInterface
    public void scheduleReminder(int intervalMinutes) {
        long intervalMs = intervalMinutes * 60 * 1000L;
        ReminderReceiver.cancel(context);
        ReminderReceiver.schedule(context, intervalMs);
    }

    /** 取消所有待提醒 */
    @JavascriptInterface
    public void cancelReminder() {
        ReminderReceiver.cancel(context);
    }

    /** 短振动反馈 (watch haptic) */
    @JavascriptInterface
    public void vibrate() {
        Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vib == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vib.vibrate(60);
        }
    }
}
