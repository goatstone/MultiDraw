package com.goatstone.multidraw;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.goatstone.multidraw.trans.BackgroundProps;
import com.goatstone.multidraw.trans.TextMessage;
import com.goatstone.multidraw.trans.TransientContainer;
import com.google.gson.Gson;


public class MainActivity extends Activity {


    public static TextView messageLogDisplay;
    private static MainResultReceiver mainResultReceiver;
    private Button redButton, greenButton, blueButton, pingButton, clearButton;
    private View rootView;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        messageLogDisplay = (TextView) findViewById(R.id.display);
        rootView = this.findViewById(android.R.id.content).getRootView();

        redButton = (Button) findViewById(R.id.red);
        greenButton = (Button) findViewById(R.id.green);
        blueButton = (Button) findViewById(R.id.blue);
        clearButton = (Button) findViewById(R.id.clear);

        redButton.requestFocus();

        if ((Util.getRegistrationId(getApplicationContext())).isEmpty()) {
            Util.registerInBackground(getApplicationContext());
        }

        messageLogDisplay.setText(   Util.getRegistrationId(getApplicationContext())  );

        setupServiceReceiver();

        setMainViewBackground(Color.argb(255, 100,100,100)) ;

        EditText editText = (EditText) findViewById(R.id.editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_DONE && !v.getText().equals("")) {
                    //handled = true; // setting to true leaves the keyboard open on Samsung Tab
                    final TextMessage textMessage = new TextMessage(v.getText().toString());
                    TransientContainer transientContainer = new TransientContainer(textMessage);
                    AppBackend.sendJSON(gson.toJson(transientContainer));
                    v.setText("");
                    redButton.requestFocus();
                }
                return handled;
            }
        });

    }

    private void setMainViewBackground(int color) {
        rootView.setBackgroundColor(color);
    }


    private void setupServiceReceiver() {

        mainResultReceiver = new MainResultReceiver(new Handler());

        mainResultReceiver.setReceiver(new MainResultReceiver.Receiver() {

            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {

                    messageLogDisplay.append("  -  ");

                    // Expect to find the Sting with key 'data'
                    String mainBundleString = resultData.getString("data");
                    if (mainBundleString == null) return;

                    TransientContainer transientPackage1 = gson.fromJson(mainBundleString, TransientContainer.class);
                    if (transientPackage1.backgroundProps != null) {
                        messageLogDisplay.append(String.valueOf(transientPackage1.backgroundProps.getHexColor()));
                        setMainViewBackground(transientPackage1.backgroundProps.getColor());
                    }
                    if (transientPackage1.textMessage != null) {
                        messageLogDisplay.append((transientPackage1.textMessage.message));
                    }

                } // END OK
            }
        });
    }

    public static ResultReceiver getMainResultReceiver() {
        return mainResultReceiver;
    }

    private boolean doBackgroundColorSelect(int r, int g, int b) {

        final BackgroundProps backgroundProps = new BackgroundProps(r, g, b);

        // set this background to selected color
        setMainViewBackground(backgroundProps.getColor());

        // send a transientContainer to the backend
        TransientContainer transientContainer = new TransientContainer(backgroundProps);
        AppBackend.sendJSON(gson.toJson(transientContainer));

        return true;
    }

    public void onClick(final View view) {
        messageLogDisplay.append(".");

        // Color Select :
        if (view == redButton || view == greenButton || view == blueButton) {
            int r = 0, g = 0, b = 0;
            if (view == redButton) {
                r = 255;
            } else if (view == greenButton) {
                g = 255;
            } else if (view == blueButton) {
                b = 255;
            }
            if (r + g + b != 0) {
                doBackgroundColorSelect(r, g, b);
            }
        }
        // Clear the log :
        else if (view == clearButton) {
            messageLogDisplay.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.checkPlayServices(getApplicationContext());
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
