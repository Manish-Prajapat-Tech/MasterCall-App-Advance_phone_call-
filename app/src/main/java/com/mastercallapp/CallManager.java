package com.mastercallapp;

import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.PhoneAccountHandle;

import java.util.ArrayList;
import java.util.List;

public class CallManager {

    public static Call call;
    public static PhoneAccountHandle phoneAccountHandle;
    private static InCallService service;
    private static NotificationHelper notificationHelper;
    // Now supports multiple listeners
    private static List<StateListener> listeners = new ArrayList<>();

    public interface StateListener {
        void onStateChanged(int state);
    }

    public static void addListener(StateListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(StateListener listener) {
        listeners.remove(listener);
    }

    public static void onStateChanged(int state) {
        if (state == Call.STATE_DISCONNECTED) {
            dismissNotification();
        }
        // Notify all listeners
        for (StateListener listener : listeners) {
            listener.onStateChanged(state);
        }
    }

    public static void setService(InCallService inCallService) {
        service = inCallService;
    }

    public static void setNotificationHelper(NotificationHelper helper) {
        notificationHelper = helper;
    }

    public static void showOutgoingCallNotification(String number) {
        if (notificationHelper != null) {
            notificationHelper.showOutgoingCallNotification(number);
        }
    }

    public static void showIncomingCallNotification(String number) {
        if (notificationHelper != null) {
            notificationHelper.showIncomingCallNotification(number);
        }
    }

    public static void dismissNotification() {
        if (notificationHelper != null) {
            notificationHelper.dismissNotification();
        }
    }

    public static void answer() {
        if (call != null) {
            call.answer(0);
        }
    }

    public static void reject() {
        if (call != null) {
            if (call.getState() == Call.STATE_RINGING) {
                call.reject(false, "");
            } else {
                call.disconnect();
            }
        } else {
            // If there's no active Telecom Call object (e.g., in a simulated environment)
            // we still need to inform listeners that the call is disconnected for UI updates.
            onStateChanged(Call.STATE_DISCONNECTED);
        }
    }

    public static void mute(boolean isMuted) {
        if (service != null) {
            service.setMuted(isMuted);
        }
    }

    public static void speaker(boolean isOn) {
        if (service != null) {
            service.setAudioRoute(isOn ? CallAudioState.ROUTE_SPEAKER : CallAudioState.ROUTE_EARPIECE);
        }
    }

    public static void hold(boolean isHeld) {
        if (call != null) {
            if (isHeld) {
                call.hold();
            } else {
                call.unhold();
            }
        }
    }
}
