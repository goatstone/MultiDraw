package com.goatstone.multidraw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;


/**
 * Created by goat on 4/10/14.
 */
public class TextMessageDialog extends DialogFragment {
    private final CharSequence[] colorSelections = {"Send Text:", "Clear Text Message", "Clear Text Log"};
    private Gson gson = new Gson();

    public TextMessageDialog() {
    }

    public boolean setLog(String message) {

        return true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText input = new EditText(getActivity());
        input.setBackgroundColor(Color.argb(255, 255, 0, 0));

        final android.widget.TextView text = new TextView(getActivity());
        text.setText("hello");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Send Message");
//        builder.setView(input);
//        builder.setMessage("hello builder");
//        builder.setView(text);
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//                if (!input.getText().toString().equals("")) {
//                    String value = "Message sent: " + input.getText().toString().trim();
//                    Toast.makeText(getActivity().getApplicationContext(), value, Toast.LENGTH_SHORT).show();
//                    final TextMessage textMessage = new TextMessage(input.getText().toString().trim());
//                    TransientContainer transientContainer = new TransientContainer(textMessage);
//                    AppBackend.sendJSON(gson.toJson(transientContainer));
//                    input.setText("");
//                }
//
//            }
//        });
        builder.setItems(colorSelections, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.i("DrawLab", "item: " + item);
                switch (item) {
                    case 0: // Send
                        break;
                    case 1: // Clear Message
                        break;
                    case 2: // Clear Log
                        break;
                    default:
                        break;
                }
//                MultiDraw.brushSize = brushSize;
//                String gsonString = gson.toJson(new TransientContainer(currentStroke));

                // send JSON to the backend
//                AppBackend.sendJSON(gsonString);

            }

        });
//        builder.setIt
//        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        final EditText input = new EditText(this);
//        alert.setView(input);
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                String value = input.getText().toString().trim();
//                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                dialog.cancel();
//            }
//        });
//        alert.show();
//    }
        return builder.create();
    }
}
