package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.messenger.UserConfig;

public class ShoofSplashActivity extends Activity {

    private final Handler handler = new Handler();
    private Runnable loginChecker;
    private boolean launched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            openCrepixbot();
            return;
        }

        buildUI();
        startLoginPolling();
    }

    private void buildUI() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(0xFF080B10);

        // Top gradient overlay
        View topGrad = new View(this);
        GradientDrawable topGradDraw = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{0xFF1A2744, 0x00000000}
        );
        topGrad.setBackground(topGradDraw);
        RelativeLayout.LayoutParams topGradParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, dp(250)
        );
        topGradParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        topGrad.setLayoutParams(topGradParams);

        // Center content
        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER_HORIZONTAL);
        center.setPadding(dp(32), 0, dp(32), 0);

        // Logo circle with play icon
        FrameLayout logoFrame = new FrameLayout(this);
        GradientDrawable logoCircle = new GradientDrawable();
        logoCircle.setShape(GradientDrawable.OVAL);
        logoCircle.setColors(new int[]{0xFF1565C0, 0xFF0D47A1});
        logoFrame.setBackground(logoCircle);
        int logoSize = dp(100);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(logoSize, logoSize);
        logoParams.gravity = Gravity.CENTER_HORIZONTAL;
        logoFrame.setLayoutParams(logoParams);

        TextView playIcon = new TextView(this);
        playIcon.setText("▶");
        playIcon.setTextSize(42);
        playIcon.setTextColor(0xFFFFFFFF);
        playIcon.setGravity(Gravity.CENTER);
        playIcon.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        logoFrame.addView(playIcon);
        center.addView(logoFrame);

        // App name
        TextView appName = new TextView(this);
        appName.setText("شوف");
        appName.setTextSize(52);
        appName.setTextColor(0xFFFFFFFF);
        appName.setTypeface(Typeface.DEFAULT_BOLD);
        appName.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.topMargin = dp(20);
        nameParams.gravity = Gravity.CENTER_HORIZONTAL;
        appName.setLayoutParams(nameParams);
        center.addView(appName);

        // Tagline
        TextView tagline = new TextView(this);
        tagline.setText("محتوى حصري · مبدعون مميزون");
        tagline.setTextSize(15);
        tagline.setTextColor(0xFF6B8BAE);
        tagline.setGravity(Gravity.CENTER_HORIZONTAL);
        tagline.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tagParams.topMargin = dp(10);
        tagParams.gravity = Gravity.CENTER_HORIZONTAL;
        tagline.setLayoutParams(tagParams);
        center.addView(tagline);

        RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        centerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        centerParams.bottomMargin = dp(80);
        center.setLayoutParams(centerParams);

        // Bottom area
        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.VERTICAL);
        bottom.setGravity(Gravity.CENTER_HORIZONTAL);
        bottom.setPadding(dp(32), 0, dp(32), dp(60));

        // Start button
        TextView startBtn = new TextView(this);
        startBtn.setText("ابدأ الآن");
        startBtn.setTextSize(18);
        startBtn.setTextColor(0xFFFFFFFF);
        startBtn.setTypeface(Typeface.DEFAULT_BOLD);
        startBtn.setGravity(Gravity.CENTER);

        GradientDrawable btnBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{0xFF1565C0, 0xFF1E88E5}
        );
        btnBg.setCornerRadius(dp(14));
        startBtn.setBackground(btnBg);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(56)
        );
        startBtn.setLayoutParams(btnParams);
        startBtn.setOnClickListener(v -> openTelegramLogin());

        // Telegram note
        TextView note = new TextView(this);
        note.setText("يتطلب تسجيل دخول Telegram");
        note.setTextSize(12);
        note.setTextColor(0xFF435A6F);
        note.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        noteParams.topMargin = dp(14);
        noteParams.gravity = Gravity.CENTER_HORIZONTAL;
        note.setLayoutParams(noteParams);

        bottom.addView(startBtn);
        bottom.addView(note);

        RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottom.setLayoutParams(bottomParams);

        root.addView(topGrad);
        root.addView(center);
        root.addView(bottom);
        setContentView(root);
    }

    private void openTelegramLogin() {
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
        startLoginPolling();
    }

    private void openCrepixbot() {
        if (launched) return;
        launched = true;
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tg://resolve?domain=Crepixbot"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void startLoginPolling() {
        if (loginChecker != null) return;
        loginChecker = new Runnable() {
            @Override
            public void run() {
                if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
                    openCrepixbot();
                } else {
                    handler.postDelayed(this, 1500);
                }
            }
        };
        handler.postDelayed(loginChecker, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            openCrepixbot();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginChecker != null) {
            handler.removeCallbacks(loginChecker);
            loginChecker = null;
        }
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }
}
