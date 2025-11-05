package com.mastercallapp;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mastercallapp.database.CallDatabaseHelper;
import com.mastercallapp.models.CallLog;

public class IncomingCallActivity extends AppCompatActivity {

    private String number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        // Ensure this activity shows over the lock screen and wakes up the phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(keyguardManager!= null) keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

        TextView numberView = findViewById(R.id.incoming_call_number);
        findViewById(R.id.answer_button).setOnClickListener(v -> answerCall());
        findViewById(R.id.reject_button).setOnClickListener(v -> rejectCall());

        number = getIntent().getStringExtra("number");
        numberView.setText(number != null ? number : "Unknown");
    }

    private void answerCall() {
        CallManager.answer();
        // The CallHandler will now launch the OutgoingCallActivity automatically when the call becomes active.
        finish(); // Just close this screen.
    }

    private void rejectCall() {
        CallManager.reject();
        // The CallHandler will handle the disconnected state.
        finish();
    }
}
