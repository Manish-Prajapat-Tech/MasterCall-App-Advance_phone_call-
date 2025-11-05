package com.mastercallapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.telecom.Call;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mastercallapp.database.CallDatabaseHelper;
import com.mastercallapp.models.CallLog;

public class OutgoingCallActivity extends AppCompatActivity implements CallManager.StateListener {

    private Chronometer callTimer;
    private CallDatabaseHelper db;
    private long startTime;
    private boolean isMuted = false;
    private boolean isSpeakerOn = false;
    private boolean isHeld = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);

        db = new CallDatabaseHelper(this);
        CallManager.addListener(this); // Use the new multi-listener method

        TextView outgoingCallNumber = findViewById(R.id.outgoing_call_number);
        callTimer = findViewById(R.id.call_timer);
        MaterialButton muteButton = findViewById(R.id.mute_button);
        MaterialButton speakerButton = findViewById(R.id.speaker_button);
        MaterialButton holdButton = findViewById(R.id.hold_button);
        FloatingActionButton endCallButton = findViewById(R.id.end_call_button);

        String number = getIntent().getStringExtra("number");
        outgoingCallNumber.setText(number != null ? number : "Unknown");

        callTimer.setVisibility(View.INVISIBLE);

        muteButton.setOnClickListener(v -> toggleMute());
        speakerButton.setOnClickListener(v -> toggleSpeaker());
        holdButton.setOnClickListener(v -> toggleHold());
        endCallButton.setOnClickListener(v -> CallManager.reject());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallManager.removeListener(this); // Use the new multi-listener method
    }

    @Override
    public void onStateChanged(int state) {
        if (state == Call.STATE_ACTIVE) {
            startTime = SystemClock.elapsedRealtime();
            callTimer.setBase(startTime);
            callTimer.start();
            callTimer.setVisibility(View.VISIBLE);
        } else if (state == Call.STATE_DISCONNECTED) {
            callTimer.stop();
            long endTime = SystemClock.elapsedRealtime();
            int duration = (int) ((endTime - startTime) / 1000);
            logCall("OUTGOING", duration);
            finish();
        }
    }

    private void toggleMute() {
        isMuted = !isMuted;
        CallManager.mute(isMuted);
    }

    private void toggleSpeaker() {
        isSpeakerOn = !isSpeakerOn;
        CallManager.speaker(isSpeakerOn);
    }

    private void toggleHold() {
        isHeld = !isHeld;
        CallManager.hold(isHeld);
    }

    private void logCall(String type, int duration) {
        String number = ((TextView) findViewById(R.id.outgoing_call_number)).getText().toString();
        db.addCallLog(new CallLog(number, type, System.currentTimeMillis(), duration));
    }
}
