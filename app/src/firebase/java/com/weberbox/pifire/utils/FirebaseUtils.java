package com.weberbox.pifire.utils;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.dialogs.FirebaseTokenDialog;

import timber.log.Timber;

public class FirebaseUtils {

    public static void getFirebaseToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.w(task.getException(),"Fetching FCM registration token failed");
                        return;
                    }
                    String token = task.getResult();
                    FirebaseTokenDialog dialog = new FirebaseTokenDialog(context, token);
                    dialog.showDialog();
                });
    }

    public static void toggleFirebaseSubscription(boolean subscribe) {
        if (!subscribe) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FIREBASE_TOPIC_GRILL)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Timber.w(task.getException(),"Firebase unsubscribe failed");
                            return;
                        }
                        Timber.d("Firebase unsubscribe successful");
                    });
        } else {
            subscribeFirebase();
        }
    }

    public static void subscribeFirebase() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_TOPIC_GRILL)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.w(task.getException(),"Firebase subscribe failed");
                        return;
                    }
                    Timber.d("Firebase subscribe successful");
                });
    }
}
