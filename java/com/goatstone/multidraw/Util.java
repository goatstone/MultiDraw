package com.goatstone.multidraw;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Goatstone : Jose Collas on 1/9/14.
 * Util : provide utility functionality for the application
 */
public class Util {

    private static final String PROPERTY_REG_ID = "registration_id";
    private static GoogleCloudMessaging gcm;
    private static String GCM_RegistrationID;
    private static final String PROJECT_NUMBER = "1050350966676";
    private static Context context;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(AppUtil.getTagName(), "Device not supported.");
                ((Activity)context).finish();
            }
            return false;
        }
        return true;
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        //int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        //editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     *
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(AppUtil.getTagName(), "Registration not found.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     *
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    public static void registerInBackground(final Context context) {
        //this.context = context;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    GCM_RegistrationID = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + GCM_RegistrationID;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
//                    sendRegistrationIdToBackend();
                    AppBackend.register(context, GCM_RegistrationID);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    Util.storeRegistrationId(context, GCM_RegistrationID);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //messageLogDisplay.append(msg + "\n");
//                Message msgObj = MainActivity.mainHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("message", "Util : ! \n" + new Date());
//                msgObj.setData(b);
//                MainActivity.mainHandler.sendMessage(msgObj);
                ResultReceiver mainResultReceiver = MainActivity.getMainResultReceiver();
                mainResultReceiver.send(Activity.RESULT_OK, bundle);
                Log.i(AppUtil.getTagName(), "onPostExecute : " + msg);

            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

}
