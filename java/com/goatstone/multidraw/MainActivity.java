package com.goatstone.multidraw;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goatstone.multidraw.trans.BackgroundProps;
import com.goatstone.multidraw.trans.Stroke;
import com.goatstone.multidraw.trans.TextMessage;
import com.goatstone.multidraw.trans.TransientContainer;
import com.google.gson.Gson;


public class MainActivity extends Activity {

    private static TextView messageLogDisplay;
    private static  MainResultReceiver mainResultReceiver;
    private Button redButton, greenButton, blueButton, clearButton;
    private Gson gson = new Gson();
    private LinearLayout myLayout;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup layout
        setContentView(R.layout.main);
        myLayout = (LinearLayout) findViewById(R.id.layout1);
        myLayout.setOnTouchListener(onTouchListener);
        messageLogDisplay = (TextView) findViewById(R.id.display);
        messageLogDisplay.setText("init");
        redButton = (Button) findViewById(R.id.red);
        greenButton = (Button) findViewById(R.id.green);
        blueButton = (Button) findViewById(R.id.blue);
        clearButton = (Button) findViewById(R.id.clear);
        editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(onEditorActionListener);

        // set up registration / serviceReceiver
        if ((Util.getRegistrationId(getApplicationContext())).isEmpty()) {
            Util.registerInBackground(getApplicationContext());
        }
        setupServiceReceiver();

        // modify layout
        setMainViewBackground(Color.argb(255, 100, 100, 100));
        messageLogDisplay.setText(Util.getRegistrationId(getApplicationContext()));
    }

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
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
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final TextMessage textMessage = new TextMessage("this will be a stroke!");
            TransientContainer transientContainer = new TransientContainer(textMessage);
            AppBackend.sendJSON(gson.toJson(transientContainer));

            final Stroke stroke = new Stroke();
            TransientContainer transientContainer1 = new TransientContainer(stroke);
            AppBackend.sendJSON(gson.toJson(transientContainer1));

            messageLogDisplay.append(" - touch - ");
            return false;
        }
    };

    private void setMainViewBackground(int color) {
        myLayout.setBackgroundColor(color);
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
                    // modify the background
                    if (transientPackage1.backgroundProps != null) {
                        messageLogDisplay.append(String.valueOf(transientPackage1.backgroundProps.getHexColor()));
                        setMainViewBackground(transientPackage1.backgroundProps.getColor());
                    }
                    // Add a text message to the messaging log
                    if (transientPackage1.textMessage != null) {
                        messageLogDisplay.append((transientPackage1.textMessage.message));
                    }
                    if (transientPackage1.stroke != null) {
                        messageLogDisplay.append(("stroke coming in"));
                        messageLogDisplay.append((transientPackage1.stroke.strokePoints).toString());
                    }
                    // Receive a Stroke and display it
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
