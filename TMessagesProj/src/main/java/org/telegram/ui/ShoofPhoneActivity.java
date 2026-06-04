package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

public class ShoofPhoneActivity extends Activity {

    private EditText phoneField;
    private Button sendBtn;
    private TextView statusText;
    private ProgressBar progress;
    private static final int ACCOUNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#050505"));

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#050505"));

        // -- top logo section --
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

        // -- center input section --
        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER_HORIZONTAL);
        center.setPadding(dp(28), 0, dp(28), 0);

        TextView title = new TextView(this);
        title.setText("تسجيل الدخول");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("أدخل رقم هاتفك مع رمز البلد");
        subtitle.setTextColor(Color.parseColor("#99FFFFFF"));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.setMargins(0, dp(6), 0, dp(28));
        subtitle.setLayoutParams(subParams);

        phoneField = new EditText(this);
        phoneField.setHint("+966 500 000 000");
        phoneField.setHintTextColor(Color.parseColor("#44FFFFFF"));
        phoneField.setTextColor(Color.WHITE);
        phoneField.setTextSize(18);
        phoneField.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneField.setGravity(Gravity.CENTER);
        phoneField.setBackgroundColor(Color.parseColor("#141414"));
        phoneField.setPadding(dp(20), dp(16), dp(20), dp(16));
        LinearLayout.LayoutParams fieldParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56));
        fieldParams.setMargins(0, 0, 0, dp(14));
        phoneField.setLayoutParams(fieldParams);

        progress = new ProgressBar(this);
        progress.setVisibility(View.GONE);

        sendBtn = new Button(this);
        sendBtn.setText("إرسال رمز التحقق");
        sendBtn.setTextColor(Color.WHITE);
        sendBtn.setTextSize(16);
        sendBtn.setTypeface(null, Typeface.BOLD);
        sendBtn.setBackgroundColor(Color.parseColor("#2CA5E0"));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54));
        btnParams.setMargins(0, 0, 0, dp(16));
        sendBtn.setLayoutParams(btnParams);

        statusText = new TextView(this);
        statusText.setTextColor(Color.parseColor("#FF5555"));
        statusText.setTextSize(13);
        statusText.setGravity(Gravity.CENTER);

        center.addView(title);
        center.addView(subtitle);
        center.addView(phoneField);
        center.addView(progress);
        center.addView(sendBtn);
        center.addView(statusText);

        RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        centerParams.addRule(RelativeLayout.CENTER_VERTICAL);
        root.addView(center, centerParams);

        setContentView(root);

        sendBtn.setOnClickListener(v -> sendCode());
    }

    private void sendCode() {
        String phone = phoneField.getText().toString().trim().replaceAll("\s", "");
        if (phone.isEmpty()) {
            statusText.setText("الرجاء إدخال رقم الهاتف");
            return;
        }

        sendBtn.setEnabled(false);
        sendBtn.setAlpha(0.6f);
        progress.setVisibility(View.VISIBLE);
        statusText.setText("");

        TLRPC.TL_auth_sendCode req = new TLRPC.TL_auth_sendCode();
        req.phone_number = phone;
        req.api_id = BuildVars.APP_ID;
        req.api_hash = BuildVars.APP_HASH;
        req.settings = new TLRPC.TL_codeSettings();

        ConnectionsManager.getInstance(ACCOUNT).sendRequest(req, (response, error) -> runOnUiThread(() -> {
            progress.setVisibility(View.GONE);
            sendBtn.setEnabled(true);
            sendBtn.setAlpha(1f);
            if (error != null) {
                statusText.setText("خطأ: " + error.text);
                return;
            }
            TLRPC.TL_auth_sentCode sentCode = (TLRPC.TL_auth_sentCode) response;
            Intent intent = new Intent(this, ShoofCodeActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("phone_hash", sentCode.phone_code_hash);
            startActivity(intent);
        }), ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}