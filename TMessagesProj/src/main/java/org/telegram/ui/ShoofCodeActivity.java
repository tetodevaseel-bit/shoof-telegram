package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.messenger.ConnectionsManager;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

public class ShoofCodeActivity extends Activity {

    private EditText codeField;
    private Button verifyBtn;
    private TextView statusText;
    private ProgressBar progress;
    private String phone, phoneHash;
    private static final int ACCOUNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#050505"));

        phone = getIntent().getStringExtra("phone");
        phoneHash = getIntent().getStringExtra("phone_hash");

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#050505"));

        // top logo
        LinearLayout topSection = new LinearLayout(this);
        topSection.setOrientation(LinearLayout.VERTICAL);
        topSection.setGravity(Gravity.CENTER_HORIZONTAL);
        topSection.setPadding(0, dp(72), 0, 0);

        TextView logo = new TextView(this);
        logo.setText("شوف");
        logo.setTextColor(Color.WHITE);
        logo.setTextSize(52);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        TextView logoSub = new TextView(this);
        logoSub.setText("TV");
        logoSub.setTextColor(Color.parseColor("#2CA5E0"));
        logoSub.setTextSize(28);
        logoSub.setTypeface(null, Typeface.BOLD);
        logoSub.setGravity(Gravity.CENTER);
        logoSub.setPadding(0, -dp(6), 0, dp(16));

        topSection.addView(logo);
        topSection.addView(logoSub);

        RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(topSection, topParams);

        // center
        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER_HORIZONTAL);
        center.setPadding(dp(28), 0, dp(28), 0);

        TextView title = new TextView(this);
        title.setText("أدخل رمز التحقق");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("تم إرسال رمز إلى " + phone);
        subtitle.setTextColor(Color.parseColor("#99FFFFFF"));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.setMargins(0, dp(6), 0, dp(28));
        subtitle.setLayoutParams(subParams);

        codeField = new EditText(this);
        codeField.setHint("· · · · ·");
        codeField.setHintTextColor(Color.parseColor("#44FFFFFF"));
        codeField.setTextColor(Color.WHITE);
        codeField.setTextSize(28);
        codeField.setInputType(InputType.TYPE_CLASS_NUMBER);
        codeField.setGravity(Gravity.CENTER);
        codeField.setBackgroundColor(Color.parseColor("#141414"));
        codeField.setPadding(dp(20), dp(16), dp(20), dp(16));
        codeField.setLetterSpacing(0.3f);
        LinearLayout.LayoutParams fieldParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(64));
        fieldParams.setMargins(0, 0, 0, dp(14));
        codeField.setLayoutParams(fieldParams);

        progress = new ProgressBar(this);
        progress.setVisibility(View.GONE);

        verifyBtn = new Button(this);
        verifyBtn.setText("تأكيد الدخول");
        verifyBtn.setTextColor(Color.WHITE);
        verifyBtn.setTextSize(16);
        verifyBtn.setTypeface(null, Typeface.BOLD);
        verifyBtn.setBackgroundColor(Color.parseColor("#2CA5E0"));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54));
        btnParams.setMargins(0, 0, 0, dp(16));
        verifyBtn.setLayoutParams(btnParams);

        statusText = new TextView(this);
        statusText.setTextColor(Color.parseColor("#FF5555"));
        statusText.setTextSize(13);
        statusText.setGravity(Gravity.CENTER);

        center.addView(title);
        center.addView(subtitle);
        center.addView(codeField);
        center.addView(progress);
        center.addView(verifyBtn);
        center.addView(statusText);

        RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        centerParams.addRule(RelativeLayout.CENTER_VERTICAL);
        root.addView(center, centerParams);

        setContentView(root);

        verifyBtn.setOnClickListener(v -> verifyCode());
    }

    private void verifyCode() {
        String code = codeField.getText().toString().trim();
        if (code.length() < 4) {
            statusText.setText("الرجاء إدخال الرمز كاملاً");
            return;
        }

        verifyBtn.setEnabled(false);
        verifyBtn.setAlpha(0.6f);
        progress.setVisibility(View.VISIBLE);
        statusText.setText("");

        TLRPC.TL_auth_signIn req = new TLRPC.TL_auth_signIn();
        req.phone_number = phone;
        req.phone_code = code;
        req.phone_code_hash = phoneHash;

        ConnectionsManager.getInstance(ACCOUNT).sendRequest(req, (response, error) -> runOnUiThread(() -> {
            progress.setVisibility(View.GONE);
            verifyBtn.setEnabled(true);
            verifyBtn.setAlpha(1f);

            if (error != null) {
                if ("SESSION_PASSWORD_NEEDED".equals(error.text)) {
                    statusText.setText("حسابك محمي بكلمة مرور — استخدم تيليجرام العادي أولاً");
                } else {
                    statusText.setText("رمز خاطئ، حاول مجدداً");
                }
                return;
            }

            TLRPC.TL_auth_authorization auth = (TLRPC.TL_auth_authorization) response;
            UserConfig.getInstance(ACCOUNT).setCurrentUser(auth.user);
            UserConfig.getInstance(ACCOUNT).saveConfig(true);
            MessagesController.getInstance(ACCOUNT).putUser(auth.user, false);
            MessagesStorage.getInstance(ACCOUNT).putUsersAndChats(null, null, false, true);
            NotificationCenter.getInstance(ACCOUNT).postNotificationName(NotificationCenter.mainUserInfoChanged);

            Intent intent = new Intent(this, LaunchActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("tg://resolve?domain=Crepixbot"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }), ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}