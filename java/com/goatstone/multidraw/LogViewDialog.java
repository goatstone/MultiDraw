package com.goatstone.multidraw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by goat on 4/8/14.
 */
public class LogViewDialog extends DialogFragment {
    private int a;
    //    private final CharSequence[] colorSelections = {"Red", "Green", "Blue", "Gray", "Black"};
//    private LinearLayout layout;
    TextView textView;
    private final CharSequence[] colorSelections = {"Close", "Clear"};

    public LogViewDialog(TextView messageLogDisplay) {
        this.textView = messageLogDisplay;
        a = 100;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(": Log Viewer : ");
        builder.setItems(colorSelections, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.i("DrawLab", "item: " + item);
                switch (item) {
                    case 0:
                        Log.i(AppUtil.getTagName(), "Log Dialog");
                        break;
                    case 1:
                        Log.i(AppUtil.getTagName(), "Log Clear");
                        textView.setText("");

                        break;
                }
            }
        });
        return builder.create();
    }
}
