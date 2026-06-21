package com.drinkwater.watch;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String CRASH_FILE = "crash_log.txt";
    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public CrashHandler(Context context) {
        this.context = context.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            // 保存崩溃信息到文件
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("=== 崩溃时间: " +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date()));
            pw.println("=== 线程: " + thread.getName());
            ex.printStackTrace(pw);
            pw.flush();

            File file = new File(context.getFilesDir(), CRASH_FILE);
            FileWriter fw = new FileWriter(file);
            fw.write(sw.toString());
            fw.close();

            Log.e("DrinkWater", "Crash saved to " + file.getAbsolutePath());
        } catch (Exception ignored) {
            // 不能再出错
        }

        // 继续默认的崩溃处理（系统弹窗 + 关闭应用）
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, ex);
        }
    }

    /** 读取上次崩溃日志，读取后自动删除 */
    public static String readAndClearCrash(Context context) {
        File file = new File(context.getFilesDir(), CRASH_FILE);
        if (!file.exists()) return null;

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            file.delete(); // 读完删除
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
    }
}
