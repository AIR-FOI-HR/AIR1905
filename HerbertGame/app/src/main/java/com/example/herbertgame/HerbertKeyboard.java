package com.example.herbertgame;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

public class HerbertKeyboard extends LinearLayout implements View.OnClickListener {

    public HerbertKeyboard(Context context) {
        this(context, null, 0);
    }

    public HerbertKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HerbertKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Button mButton_s;
    private Button mButton_l;
    private Button mButton_r;
    private Button mButton_a;
    private Button mButton_X;
    private Button mButton_Y;
    private Button mButton_plus;
    private Button mButton_minus;
    private Button mButton_colon;
    private Button mButton_star;
    private Button mButton_o_bracket;
    private Button mButton_c_bracket;
    private Button mButton_slash;
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private Button mButtonDelete;
    private Button mButtonEnter;

    SparseArray<String> keyValues = new SparseArray<>();

    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.herbert_keyboard, this, true);
        mButton_s = findViewById(R.id.button_s);
        mButton_l = findViewById(R.id.button_l);
        mButton_r = findViewById(R.id.button_r);
        mButton_a = findViewById(R.id.button_a);
        mButton_X = findViewById(R.id.button_X);
        mButton_Y = findViewById(R.id.button_Y);
        mButton_plus = findViewById(R.id.button_plus);
        mButton_minus = findViewById(R.id.button_minus);
        mButton_star = findViewById(R.id.button_star);
        mButton_slash = findViewById(R.id.button_slash);
        mButton_colon = findViewById(R.id.button_colon);
        mButton_o_bracket = findViewById(R.id.button_open_bracket);
        mButton_c_bracket = findViewById(R.id.button_closed_bracket);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);
        mButton4 = findViewById(R.id.button_4);
        mButton5 = findViewById(R.id.button_5);
        mButton6 = findViewById(R.id.button_6);
        mButton7 = findViewById(R.id.button_7);
        mButton8 = findViewById(R.id.button_8);
        mButton9 = findViewById(R.id.button_9);
        mButton0 = findViewById(R.id.button_0);
        mButtonDelete = findViewById(R.id.button_delete);
        mButtonEnter = findViewById(R.id.button_enter);

        // set button click listeners
        mButton_s.setOnClickListener(this);
        mButton_l.setOnClickListener(this);
        mButton_r.setOnClickListener(this);
        mButton_a.setOnClickListener(this);
        mButton_X.setOnClickListener(this);
        mButton_Y.setOnClickListener(this);
        mButton_plus.setOnClickListener(this);
        mButton_minus.setOnClickListener(this);
        mButton_star.setOnClickListener(this);
        mButton_slash.setOnClickListener(this);
        mButton_colon.setOnClickListener(this);
        mButton_o_bracket.setOnClickListener(this);
        mButton_c_bracket.setOnClickListener(this);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);
        mButtonEnter.setOnClickListener(this);

        // map buttons IDs to input strings
        keyValues.put(R.id.button_s, "s");
        keyValues.put(R.id.button_l, "l");
        keyValues.put(R.id.button_r, "r");
        keyValues.put(R.id.button_a, "a");
        keyValues.put(R.id.button_X, "X");
        keyValues.put(R.id.button_Y, "Y");
        keyValues.put(R.id.button_plus, "+");
        keyValues.put(R.id.button_minus, "-");
        keyValues.put(R.id.button_star, "*");
        keyValues.put(R.id.button_slash, "/");
        keyValues.put(R.id.button_colon, ":");
        keyValues.put(R.id.button_open_bracket, "(");
        keyValues.put(R.id.button_closed_bracket, ")");
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
        keyValues.put(R.id.button_enter, "\n");
    }

    @Override
    public void onClick(View v) {

        if (inputConnection == null) return;

        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
    }

    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }
}
