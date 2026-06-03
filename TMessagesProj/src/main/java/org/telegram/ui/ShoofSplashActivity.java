package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;

public class ShoofSplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // إذا كان المستخدم مسجّل دخوله مسبقاً → افتح شوف مباشرة
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            openShoof();
            return;
        }

        setContentView(R.layout.activity_shoof_splash);

        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
            // افتح شاشة تسجيل دخول تيليجرام
            Intent intent = new Intent(this, LaunchActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openShoof() {
        Intent intent = new Intent(this, ShoofWebActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
