package com.goatstone.multidraw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.goatstone.multidraw.AppBackend;
import com.goatstone.multidraw.trans.TextMessage;
import com.goatstone.multidraw.trans.TransientContainer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by goat on 4/10/14.
 */
public class TextMessageDialog extends DialogFragment {

    private String[] intArr = new String[5];
    private String longStr = "Lorum Ipsum dolor etclfkjxasd  xasd fkasj fdsa lfkjxasd  xasd fkasj fdsa lfkjxasd  xasd fkasj fdsa lfkj\n";
    private TextView textView;
    private EditText editText;
    private Gson gson = new Gson();
    private DialogFragment textMessageDialog;
    private List<String> messageList = new ArrayList<String>();

    public TextMessageDialog() {
        Arrays.fill(intArr, longStr);
        textMessageDialog = this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Log.i(AppUtil.getTagName(), "on create");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LinearLayout ll = new LinearLayout(getActivity());
        final ScrollView scrollView = new ScrollView(getActivity());
        final Button button = new Button(getActivity());
        textView = new TextView(getActivity());
        textView.setTextSize(20.0f);

        editText = new EditText(getActivity());
        editText.setMinimumWidth(300);
        editText.setBackgroundColor(Color.argb(255, 200, 200, 255));
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        editText.setOnEditorActionListener(onEditorActionListener);

        button.setText("Send A Message");

        for (String s : messageList) {
            textView.append(s + "\n");
        }

        scrollView.setMinimumWidth(100);
        scrollView.addView(textView);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(editText);
//        ll.addView(button);
        ll.addView(scrollView);

        builder.setTitle("  Messages ");
        builder.setMessage("Send a message, review the messages.");
        builder.setView(ll);

        return builder.create();
    }


    public TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;

            if (actionId == EditorInfo.IME_ACTION_DONE && !v.getText().equals("")) {
//                Log.i(AppUtil.getTagName(), "onEditorActionListener  2 2 2 2 2 2 ");

                //handled = true; // setting to true leaves the keyboard open on Samsung Tab
                final TextMessage textMessage = new TextMessage(v.getText().toString());
                TransientContainer transientContainer = new TransientContainer(textMessage);
                AppBackend.sendJSON(gson.toJson(transientContainer));
                v.setText("");
                textMessageDialog.dismiss();
            }
            return handled;
        }
    };

    public void setLog(String message) {
        messageList.add(message);
    }
}
