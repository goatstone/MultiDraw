package com.goatstone.multidraw;


    import android.app.Activity;
    import android.app.AlertDialog;
    import android.app.Dialog;
    import android.app.DialogFragment;
    import android.content.DialogInterface;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.text.Layout;
    import android.util.Log;
    import android.widget.LinearLayout;

    import com.google.android.gms.plus.model.people.Person;

/**
 * Created by goat on 4/8/14.
 */
public class ColorSelectDialog extends DialogFragment {
//        private DrawLab drawLab;
        private int a, r, g, b;
        private final CharSequence[] colorSelections = {"Red", "Green", "Blue", "Gray", "Black"};
        private LinearLayout layout;

        public ColorSelectDialog(  LinearLayout layout ) {
//            this.drawLab = drawLab;
//             layout.setBackgroundColor(Color.argb(255, 120,120,120));
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
                    layout.setBackgroundColor(Color.argb(255, r, g, b));

                    //drawLab.setColor(Color.argb(a, r, g, b));
                }
            });

            return builder.create();
        }
}
