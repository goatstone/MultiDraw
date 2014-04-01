package com.goatstone.multidraw;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Goatstone : Jose Collas on 1/9/14.
 */
public class GCM_IntentService extends IntentService {

    public GCM_IntentService() {
        super("GCM_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(AppUtil.getTagName(), "MESSAGE_TYPE_SEND_ERROR");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(AppUtil.getTagName(), "MESSAGE_TYPE_DELETED");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Bundle bundle = new Bundle();
                //bundle.putString("resultValue", " message from server : " + extras.getString("a"));
                bundle.putString("data", extras.getString("data"));

                // MainResultReceiver
                ResultReceiver mainResultReceiver = MainActivity.getMainResultReceiver();
                mainResultReceiver.send(Activity.RESULT_OK, bundle);
                Log.i(AppUtil.getTagName(), "Received: " + extras.toString());

            }
        }
        GCM_BroadcastReceiver.completeWakefulIntent(intent);
    }

}
