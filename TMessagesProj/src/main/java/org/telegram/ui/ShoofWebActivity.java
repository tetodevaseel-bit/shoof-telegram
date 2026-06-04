package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.telegram.messenger.UserConfig;

/**
 * Redirects to @Crepixbot mini app via Telegram's native handler.
 * Stars and payments work natively through Telegram.
 */
public class ShoofWebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openCrepixbotMiniApp();
    }

    private void openCrepixbotMiniApp() {
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            finish();
            return;
        }
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tg://resolve?domain=Crepixbot"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
