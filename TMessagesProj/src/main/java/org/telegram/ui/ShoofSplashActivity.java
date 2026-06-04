package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;

public class ShoofSplashActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoof_splash);

        Button btnStart = findViewById(R.id.btn_start);
        // Goes to custom شوف TV phone login (not Telegram default)
        btnStart.setOnClickListener(v -> startActivity(new Intent(this, ShoofPhoneActivity.class)));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
                    openCrepixbot();
                    return;
                }
            } catch (Exception ignored) {}
            try {
                NotificationCenter.getInstance(UserConfig.selectedAccount)
                    .addObserver(this, NotificationCenter.mainUserInfoChanged);
            } catch (Exception ignored) {}
        }, 500);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.mainUserInfoChanged) {
            try {
                if (UserConfig.getInstance(account).isClientActivated()) {
                    runOnUiThread(this::openCrepixbot);
                }
            } catch (Exception ignored) {}
        }
    }

    private void openCrepixbot() {
        try {
            NotificationCenter.getInstance(UserConfig.selectedAccount)
                .removeObserver(this, NotificationCenter.mainUserInfoChanged);
        } catch (Exception ignored) {}
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tg://resolve?domain=Crepixbot"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            NotificationCenter.getInstance(UserConfig.selectedAccount)
                .removeObserver(this, NotificationCenter.mainUserInfoChanged);
        } catch (Exception ignored) {}
    }
}