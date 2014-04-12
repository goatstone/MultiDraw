package com.goatstone.multidraw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.goatstone.multidraw.MultiDraw;

/**
 * Created by goat on 4/10/14.
 */
public class BrushColorSelectDialog extends DialogFragment {
    private int a, r, g, b;
    private int brushSize = 20;
    private final CharSequence[] colorSelections = {"Red", "Green", "Blue", "Gray", "Black",
            "Small Brush", "Medium Brush", "Large Brush", "XLarge Brush"};
    private LinearLayout layout;

    public BrushColorSelectDialog(LinearLayout layout) {
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
                    case 0: //R
                        r = 255;
                        g = 10;
                        b = 10;
                        break;
                    case 1: // G
                        r = 10;
                        g = 250;
                        b = 10;
                        break;
                    case 2: // B
                        r = 10;
                        g = 10;
                        b = 250;
                        break;
                    case 3: // G
                        r = 150;
                        g = 150;
                        b = 150;
                        break;
                    case 4: // B
                        r = 0;
                        g = 0;
                        b = 0;
                        break;
                    case 5: // S
                        brushSize = 10;
                        break;
                    case 6: // M
                        brushSize = 50;
                        break;
                    case 7: // L
                        brushSize = 100;
                        break;
                    case 8: // XL
                        brushSize = 300;
                        break;
                    default:
                        r = 10;
                        g = 10;
                        b = 10;
                        break;
                }
                MultiDraw.brushSize = brushSize;
                MultiDraw.red = r;
                MultiDraw.green = g;
                MultiDraw.blue = b;
            }

        });

        return builder.create();
    }
}
