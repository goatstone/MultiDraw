package com.goatstone.multidraw;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by Goatstone : Jose Collas on 1/7/14.
 * AppBackend : Provide functionality for client application to a backend server.
 * public ping() send a simple request
 * public regisister()
 * public unregister()
 * private post()
 */
public class AppBackend {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    static final String SERVER_URL = "http://clay-goatstone.appspot.com";

    public static void doPing(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AppBackend.ping();
                return "AppBackend.ping.";
            }

            @Override
            protected void onPostExecute(String msg) {
                //messageLogDisplay.append(msg + "onPostEx\n");
            }
        }.execute(null, null, null);
    }
    public static void sendJSON(final String jSONToSend) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                HttpPost post = new HttpPost(SERVER_URL + "/ping");

                try {
                    StringEntity se = new StringEntity(jSONToSend);
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if (response != null) {
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //createDialog("Error", "Cannot Estabilish Connection");
                }

                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                //messageLogDisplay.append(msg + "onPostEx\n");
            }
        }.execute(null, null, null);
    }

    public static void ping() {
        Log.i(AppUtil.getTagName(), "ping");

        String fullURL = SERVER_URL + "/ping";
        URL url;
        HttpURLConnection connection;
        String line;
        Bundle bundle;
        InputStreamReader isr;
        BufferedReader reader;
        try {
            url = new URL(fullURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            isr = new InputStreamReader(connection.getInputStream());
            reader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();
            reader.close();

            bundle = new Bundle();
            bundle.putString("resultValue", " Call from server returned. ");

            ResultReceiver mainResultReceiver = MainActivity.getMainResultReceiver();
            mainResultReceiver.send(Activity.RESULT_OK, bundle);

        } catch (MalformedURLException mfe) {
            Log.i(AppUtil.getTagName(), mfe.toString());

        } catch (IOException e) {
            Log.i(AppUtil.getTagName(), e.toString());
        }
    }

    static void register(final Context context, final String regId) {
        Log.i(AppUtil.getTagName(), "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/register";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(AppUtil.getTagName(), "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                return;
            } catch (IOException e) {
                Log.e(AppUtil.getTagName(), "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(AppUtil.getTagName(), "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Log.d(AppUtil.getTagName(), "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                backoff *= 2;
            }
        }
    }

    static void unregister(final Context context, final String regId) {
        Log.i(AppUtil.getTagName(), "unregister : " + regId);
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            String message = context.getString(R.string.server_unregistered);
        } catch (IOException e) {
        }
    }

    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(AppUtil.getTagName(), "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
