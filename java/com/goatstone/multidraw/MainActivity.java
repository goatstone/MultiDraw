package com.goatstone.multidraw;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goatstone.multidraw.dialogs.BrushColorSelectDialog;
import com.goatstone.multidraw.trans.BackgroundProps;
import com.goatstone.multidraw.trans.Stroke;
import com.goatstone.multidraw.trans.TextMessage;
import com.goatstone.multidraw.trans.TransientContainer;
import com.google.gson.Gson;


public class MainActivity extends ActionBarActivity {

    private static TextView messageLogDisplay;
    private static MainResultReceiver mainResultReceiver;
    private Gson gson = new Gson();
    private LinearLayout linearLayout;
    private EditText editText;
    private CustomDrawableView customDrawableView;
    private ColorSelectDialog colorSelectDialog;
    private LogViewDialog logViewDialog;
    private BrushColorSelectDialog brushColorSelectDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup layout
        setContentView(R.layout.main);
        linearLayout = (LinearLayout) findViewById(R.id.layout1);
        linearLayout.setOnTouchListener(onTouchListener);
        messageLogDisplay = (TextView) findViewById(R.id.display);
        messageLogDisplay.setText("init");
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

        colorSelectDialog = new ColorSelectDialog(linearLayout);
        colorSelectDialog.setArguments(getIntent().getExtras());

        logViewDialog = new LogViewDialog(messageLogDisplay);
        logViewDialog.setArguments(getIntent().getExtras());

        brushColorSelectDialog = new BrushColorSelectDialog(linearLayout);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        customDrawableView = new CustomDrawableView(getApplicationContext());
        int drawAreaWidth = 580;
        int drawAreaHeight = 850;

        int screenMatchRatio = (int) Math.floor(metrics.densityDpi / 160);
        MultiDraw.screenMatchRatio = (int) Math.floor(metrics.densityDpi / 160);

//        Log.i(AppUtil.getTagName(), "density: " + String.valueOf(metrics.densityDpi));
//        Log.i(AppUtil.getTagName(), "screenMatchRatio: " + (Math.floor(metrics.densityDpi / 160)));
//        Log.i(AppUtil.getTagName(), "MultiDraw.screenMatchRatio : " + MultiDraw.screenMatchRatio);

        customDrawableView.setX(10);
        customDrawableView.setY(10);

        addContentView(customDrawableView, new ViewGroup.LayoutParams(drawAreaWidth * screenMatchRatio, drawAreaHeight * screenMatchRatio));
        customDrawableView.invalidate();
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
            }
            return handled;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        // create a stroke that will be sent to the backend in JSON form
        private Stroke currentStroke = new Stroke();

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {

                    final int x = (int) event.getX() / MultiDraw.screenMatchRatio;
                    final int y = (int) event.getY() / MultiDraw.screenMatchRatio;

                    currentStroke = new Stroke();
                    currentStroke.color = MultiDraw.getGhostBrushColor();
                    currentStroke.brushSize = MultiDraw.brushSize;
                    currentStroke.strokePoints.add(new int[]{x, y});
                    MultiDraw.strokes.add(currentStroke);
                    customDrawableView.invalidate();

                    break;
                }
                case MotionEvent.ACTION_MOVE: {

                    final int x = (int) event.getX() / MultiDraw.screenMatchRatio;
                    final int y = (int) event.getY() / MultiDraw.screenMatchRatio;

                    currentStroke.strokePoints.add(new int[]{x, y});
                    customDrawableView.invalidate();

                    break;
                }
                case MotionEvent.ACTION_UP: {

                    currentStroke.color = MultiDraw.getBrushColor();

                    // Make the transientContainer into a string to send it to the backend.
                    String gsonString = gson.toJson(new TransientContainer(currentStroke));

                    // send JSON to the backend
                    AppBackend.sendJSON(gsonString);

                    break;
                }
            }
            return true;
        }
    };

    public void setMainViewBackground(int color) {
        linearLayout.setBackgroundColor(color);
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
                        Log.i(AppUtil.getTagName(), String.valueOf("stroke coming in" + transientPackage1.stroke.strokePoints.size()));
                        MultiDraw.strokes.add(MultiDraw.strokes.size(), transientPackage1.stroke);
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
//            case R.id.action_background:
//                Log.i(AppUtil.getTagName(), "background - - - -");
//                colorSelectDialog.show(getFragmentManager(), "DrawLab");
//                return true;
            case R.id.action_brush:
                brushColorSelectDialog.show(getFragmentManager(), "MultiDraw: color");
                return true;
            case R.id.action_clear:
                Log.i(AppUtil.getTagName(), "action_clear : - - - -");
                MultiDraw.clearStrokeList();
                customDrawableView.invalidate();
                return true;
            case R.id.action_message:
                Log.i(AppUtil.getTagName(), "message - - - -");
//                sizeSelectDialog.show(getFragmentManager(), "DrawLab");
//BrushColorSelectDialog
                return true;
            case R.id.action_log:
                Log.i(AppUtil.getTagName(), "log - - - -");
                logViewDialog.show(getFragmentManager(), "MultiDraw : log");
//                sizeSelectDialog.show(getFragmentManager(), "DrawLab");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
