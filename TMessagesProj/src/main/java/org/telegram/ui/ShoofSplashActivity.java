package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShoofSplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        buildUI();
        // بعد ثانية واحدة → نفتح LaunchActivity مع رابط البوت
        // LaunchActivity تتحقق من الدخول تلقائياً:
        // - مسجل → يفتح @Crepixbot مباشرة
        // - غير مسجل → يعرض شاشة الدخول ثم يفتح @Crepixbot
        new Handler(Looper.getMainLooper()).postDelayed(this::openApp, 1000);
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#080808"));
        root.setGravity(Gravity.CENTER);
        root.setPadding(64, 0, 64, 0);

        TextView icon = new TextView(this);
        icon.setText("🎬");
        icon.setTextSize(72);
        icon.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("شوف");
        title.setTextColor(Color.WHITE);
        title.setTextSize(56);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView tvSub = new TextView(this);
        tvSub.setText("TV");
        tvSub.setTextColor(Color.parseColor("#E5001A"));
        tvSub.setTextSize(28);
        tvSub.setTypeface(null, Typeface.BOLD);
        tvSub.setGravity(Gravity.CENTER);

        View spacer = new View(this);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 32);
        spacer.setLayoutParams(sp);

        TextView desc = new TextView(this);
        desc.setText("شاهد آلاف المسلسلات والأفلام\nبجودة عالية في أي مكان");
        desc.setTextColor(Color.parseColor("#66FFFFFF"));
        desc.setTextSize(14);
        desc.setGravity(Gravity.CENTER);

        root.addView(icon);
        root.addView(title);
        root.addView(tvSub);
        root.addView(spacer);
        root.addView(desc);
        setContentView(root);
    }

    private void openApp() {
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tg://resolve?domain=Crepixbot"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
