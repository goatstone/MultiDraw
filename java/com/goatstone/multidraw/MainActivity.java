package com.goatstone.multidraw;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static TextView messageLogDisplay;
    private static MainResultReceiver mainResultReceiver;
    private Button redButton, greenButton, blueButton, clearButton;
    private Gson gson = new Gson();
    private LinearLayout myLayout;
    private EditText editText;
    private CustomDrawableView customDrawableView;
    private ColorSelectDialog colorSelectDialog;
    private LogViewDialog logViewDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup layout
        setContentView(R.layout.main);
        myLayout = (LinearLayout) findViewById(R.id.layout1);
        myLayout.setOnTouchListener(onTouchListener);
        messageLogDisplay = (TextView) findViewById(R.id.display);
        messageLogDisplay.setText("init");
//        redButton = (Button) findViewById(R.id.red);
//        greenButton = (Button) findViewById(R.id.green);
//        blueButton = (Button) findViewById(R.id.blue);
//        clearButton = (Button) findViewById(R.id.clear);
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

        customDrawableView = new CustomDrawableView(getApplicationContext());
        addContentView(customDrawableView, new ViewGroup.LayoutParams(550, 900));

        colorSelectDialog = new ColorSelectDialog(myLayout);
        colorSelectDialog.setArguments(getIntent().getExtras());

        logViewDialog = new LogViewDialog(messageLogDisplay);
        logViewDialog.setArguments(getIntent().getExtras());

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
//                redButton.requestFocus();
            }
            return handled;
        }
    };

    private List<int[]> currentStokes;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Log.i(AppUtil.getTagName(), String.valueOf(event.getAction()));


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    currentStokes = new ArrayList<int[]>();
                    currentStokes.add(new int[]{(int) event.getX(), (int) event.getY()});
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    currentStokes.add(new int[]{(int) event.getX(), (int) event.getY()});
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Log.i(AppUtil.getTagName(), "xx" + currentStokes.size());

                    final Stroke stroke = new Stroke();
                    stroke.strokePoints = currentStokes;

                    TransientContainer transientContainer = new TransientContainer(stroke);

                    if (transientContainer.stroke != null) {
                        Log.i(AppUtil.getTagName(), "stroke has been made and set!!!!!!! !!!!!!");

                        // Make the transientContainer into a string to send it to the backend.
                        String gsonString = gson.toJson(transientContainer);
                        AppBackend.sendJSON(gsonString);
                        Log.i(AppUtil.getTagName(), gsonString);

                        // convert back to Object TransientContainer
                        TransientContainer newTransientContainer = gson.fromJson(gsonString, TransientContainer.class);
                        Log.i(AppUtil.getTagName(), String.valueOf(transientContainer.stroke.strokePoints.size()));


                        // Add the transientContainer.stroke to the customDrawableView.
//                        customDrawableView.addToStrokePoints((ArrayList<int[]>) newTransientContainer.stroke.strokePoints);
//                        customDrawableView.invalidate();

                    }
                    break;
                }
            }
            return true;
        }
    };

    public void setMainViewBackground(int color) {
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
                        messageLogDisplay.append(("stroke coming in !!!!!!! !!!!!!"));
                        //messageLogDisplay.append((transientPackage1.stroke.strokePoints).toString());
                        customDrawableView.addToStrokePoints((ArrayList<int[]>) transientPackage1.stroke.strokePoints);
//                        customDrawableView.addToStrokePoints((ArrayList<int[]>) transientContainer.stroke.strokePoints);
                        customDrawableView.invalidate();

//                        TransientContainer newTransientContainer = gson.fromJson(gsonString, TransientContainer.class);
                        Log.i(AppUtil.getTagName(), String.valueOf(transientPackage1.stroke.strokePoints.size()));

                        // Add the transientContainer.stroke to the customDrawableView.
                        customDrawableView.addToStrokePoints((ArrayList<int[]>) transientPackage1.stroke.strokePoints);
                        customDrawableView.invalidate();


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

        Util.checkPlayServices(getApplicationContext(), this);
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
        switch (item.getItemId()) {
            case R.id.action_background:
                Log.i(AppUtil.getTagName(), "background - - - -");
                colorSelectDialog.show(getFragmentManager(), "DrawLab");
                return true;
            case R.id.action_log:
                Log.i(AppUtil.getTagName(), "log - - - -");
                logViewDialog.show(getFragmentManager(), "MultiDraw : log");
//                sizeSelectDialog.show(getFragmentManager(), "DrawLab");
                return true;
            case R.id.action_message:
                Log.i(AppUtil.getTagName(), "message - - - -");
//                sizeSelectDialog.show(getFragmentManager(), "DrawLab");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
    }

}
