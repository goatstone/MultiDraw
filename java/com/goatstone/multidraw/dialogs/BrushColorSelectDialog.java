package com.goatstone.multidraw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.goatstone.multidraw.MultiDraw;

/**
 * Created by goat on 4/10/14.
 */
public class BrushColorSelectDialog extends DialogFragment {
    private int a, r, g, b;
    private final CharSequence[] colorSelections = {"Red", "Green", "Blue", "Gray", "Black"};
    private LinearLayout layout;

    public BrushColorSelectDialog(  LinearLayout layout ) {
        this.layout = layout;
        a = 100;
        g = 250;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Color");
        builder.setItems(colorSelections, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.i("DrawLab", "item: " + item);
                switch (item) {
                    case 0:
                        r = 255;
                        g = 10;
                        b = 10;
                        break;
                    case 1:
                        r = 10;
                        g = 250;
                        b = 10;
                        break;
                    case 2:
                        r = 10;
                        g = 10;
                        b = 250;
                        break;
                    case 3:
                        r = 150;
                        g = 150;
                        b = 150;
                        break;
                    default:
                        r = 10;
                        g = 10;
                        b = 10;
                        break;
                }
                MultiDraw.brushColor = Color.argb(255, r, g, b);
            }
        });

        return builder.create();
    }
}
