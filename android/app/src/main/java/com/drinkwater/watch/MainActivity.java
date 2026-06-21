package com.drinkwater.watch;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.ScrollView;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "DrinkWater";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ========== 崩溃自诊：检查上次是否崩溃 ==========
        String crashLog = CrashHandler.readAndClearCrash(this);
        if (crashLog != null) {
            showCrashScreen(crashLog);
            return; // 显示崩溃信息，不启动 WebView
        }

        // ========== 注册全局崩溃抓取器 ==========
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));

        // ========== 正常启动 WebView ==========
        try {
            webView = new WebView(this);
            // 去掉启动白闪：WebView 背景设为与页面一致
            webView.setBackgroundColor(Color.parseColor("#0f1923"));
            setContentView(webView);

            // 全屏沉浸
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            // WebView 配置
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setAllowFileAccess(true);
            settings.setAllowContentAccess(true);
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            settings.setBuiltInZoomControls(false);
            settings.setDisplayZoomControls(false);
            settings.setMinimumFontSize(8);
            settings.setTextZoom(100);

            String ua = settings.getUserAgentString();
            settings.setUserAgentString(ua + " DrinkWaterWatch/1.0");

            webView.setInitialScale(100);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.e(TAG, "WebView error: " + errorCode + " " + description + " @ " + failingUrl);
                }
            });

            webView.setWebChromeClient(new WebChromeClient());

            // 注册 JS 桥接
            webView.addJavascriptInterface(new ReminderBridge(this), "AndroidBridge");

            // 创建通知渠道 (API 26+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) {
                    ReminderReceiver.ensureChannel(nm);
                }
            }

            webView.loadUrl("file:///android_asset/www/index.html");

        } catch (Exception e) {
            Log.e(TAG, "onCreate crashed", e);
            if (webView != null) {
                String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
                if (msg.length() > 200) msg = msg.substring(0, 200);
                webView.loadDataWithBaseURL(null,
                        "<html><body style='background:#0f1923;color:#e74c3c;padding:20px;font-family:sans-serif;'>" +
                                "<h2>⚠️ 启动失败</h2><p style='color:#8a9aaa;font-size:14px;'>" + msg +
                                "</p><p style='color:#5a6a7a;font-size:10px;margin-top:8px;'>" +
                                "请截图后发送给开发者</p></body></html>",
                        "text/html", "UTF-8", null);
            }
        }
    }

    /** 显示上次崩溃的详细信息 */
    private void showCrashScreen(String crashLog) {
        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(Color.parseColor("#0f1923"));
        sv.setPadding(20, 20, 20, 20);

        TextView tv = new TextView(this);
        tv.setTextColor(Color.parseColor("#e74c3c"));
        tv.setTextSize(12);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setLineSpacing(4, 1.2f);
        tv.setText("⚠️ 上次启动崩溃了\n\n" + crashLog +
                "\n\n——————————\n请截图发给开发者。\n关闭此页再打开应用即可重新尝试。");

        sv.addView(tv);
        setContentView(sv);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } catch (Exception ignored) {}
    }
}
