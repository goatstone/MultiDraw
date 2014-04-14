package com.goatstone.multidraw;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.goatstone.multidraw.dialogs.BrushColorSelectDialog;
import com.goatstone.multidraw.dialogs.TextMessageDialog;
import com.goatstone.multidraw.trans.Stroke;
import com.goatstone.multidraw.trans.TransientContainer;
import com.google.gson.Gson;


public class MainActivity extends ActionBarActivity {

    private Gson gson = new Gson();
    private static MainResultReceiver mainResultReceiver;
    private LinearLayout linearLayout;
    private CustomDrawableView customDrawableView;
    // dialogs
    private ColorSelectDialog colorSelectDialog;
    private BrushColorSelectDialog brushColorSelectDialog;
    private TextMessageDialog textMessageDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup layout
        setContentView(R.layout.main);
        linearLayout = (LinearLayout) findViewById(R.id.layout1);
        linearLayout.setOnTouchListener(onTouchListener);

        // set up registration / serviceReceiver
        if ((Util.getRegistrationId(getApplicationContext())).isEmpty()) {
            Util.registerInBackground(getApplicationContext());
        }
        setupServiceReceiver();

        colorSelectDialog = new ColorSelectDialog(linearLayout);
        colorSelectDialog.setArguments(getIntent().getExtras());

        brushColorSelectDialog = new BrushColorSelectDialog(linearLayout);
        textMessageDialog = new TextMessageDialog();

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

    private void setupServiceReceiver() {

        mainResultReceiver = new MainResultReceiver(new Handler());
        mainResultReceiver.setReceiver(new MainResultReceiver.Receiver() {

            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {

                    // Expect to find the Sting with key 'data'
                    String mainBundleString = resultData.getString("data");
                    if (mainBundleString == null) return;

                    TransientContainer transientPackage1 = gson.fromJson(mainBundleString, TransientContainer.class);

                    // Add a text message to the messaging log
                    if (transientPackage1.textMessage != null) {
                        Toast.makeText(getApplicationContext(),
                                "Message Received : " + transientPackage1.textMessage.message, Toast.LENGTH_SHORT).show();
                        textMessageDialog.setLog(transientPackage1.textMessage.message);
                    }
                    if (transientPackage1.stroke != null) {
                        Log.i(AppUtil.getTagName(), String.valueOf("stroke coming in" + transientPackage1.stroke.strokePoints.size()));
                        MultiDraw.strokes.add(MultiDraw.strokes.size(), transientPackage1.stroke);
                        customDrawableView.invalidate();
                    }
                } // END OK
            }
        });
    }

    public static ResultReceiver getMainResultReceiver() {
        return mainResultReceiver;
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
        //The action bar will  automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
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
                textMessageDialog.show(getFragmentManager(), "MultiDraw : text message");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
