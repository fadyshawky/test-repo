package com.neo.neopayplus.pin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.R;
import com.neo.neopayplus.processing.ProcessingActivity;

public class PinInputActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView mTvPinDisplay;
    private Button[] mPinButtons = new Button[10];
    private Button mBtnPinClear;
    private Button mBtnPinBackspace;
    private Button mBtnNext;
    
    private StringBuilder mPinInput = new StringBuilder();
    private static final int MAX_PIN_LENGTH = 6;
    private String mAmount; // Piasters for SDK
    private String mAmountDisplay; // Pounds for display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_input);
        
        // Get amount from previous screen
        mAmount = getIntent().getStringExtra("amount"); // Piasters for SDK
        mAmountDisplay = getIntent().getStringExtra("amountDisplay"); // Pounds for display
        if (mAmount == null) {
            mAmount = "10000"; // Default amount in piasters (100 EGP)
        }
        if (mAmountDisplay == null) {
            mAmountDisplay = "100"; // Default amount in pounds
        }
        
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Enter PIN");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvPinDisplay = findViewById(R.id.tv_pin_display);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnPinClear = findViewById(R.id.btn_pin_clear);
        mBtnPinBackspace = findViewById(R.id.btn_pin_backspace);
        
        // Initialize PIN buttons
        int[] buttonIds = {
            R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
            R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };
        
        for (int i = 0; i < buttonIds.length; i++) {
            mPinButtons[i] = findViewById(buttonIds[i]);
            final int digit = i;
            mPinButtons[i].setOnClickListener(v -> onPinDigitClick(digit));
        }
        
        mBtnPinClear.setOnClickListener(this);
        mBtnPinBackspace.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        
        updatePinDisplay();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pin_clear) {
            onPinClearClick();
        } else if (v.getId() == R.id.btn_pin_backspace) {
            onPinBackspaceClick();
        } else if (v.getId() == R.id.btn_next) {
            onNextClick();
        }
    }

    private void onPinDigitClick(int digit) {
        if (mPinInput.length() < MAX_PIN_LENGTH) {
            mPinInput.append(digit);
            updatePinDisplay();
        }
    }

    private void onPinClearClick() {
        mPinInput.setLength(0);
        updatePinDisplay();
    }

    private void onPinBackspaceClick() {
        if (mPinInput.length() > 0) {
            mPinInput.setLength(mPinInput.length() - 1);
            updatePinDisplay();
        }
    }

    private void onNextClick() {
        if (mPinInput.length() == 0) {
            Toast.makeText(this, "Please enter a PIN", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Return PIN to caller (ProcessingActivity) via result
        Intent result = new Intent();
        result.putExtra("pin", mPinInput.toString());
        result.putExtra("amount", mAmount);
        result.putExtra("amountDisplay", mAmountDisplay);
        setResult(RESULT_OK, result);
        finish();
    }


    private void updatePinDisplay() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < MAX_PIN_LENGTH; i++) {
            if (i < mPinInput.length()) {
                display.append("•");
            } else {
                display.append("_");
            }
        }
        mTvPinDisplay.setText(display.toString());
    }
}
