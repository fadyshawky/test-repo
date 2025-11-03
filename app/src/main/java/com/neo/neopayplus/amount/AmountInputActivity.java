package com.neo.neopayplus.amount;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.R;
import com.neo.neopayplus.processing.ProcessingActivity;
import com.neo.neopayplus.utils.LogUtil;

public class AmountInputActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView mTvAmountDisplay;
    private Button[] mNumberButtons = new Button[10];
    private Button mBtnClear;
    private Button mBtnBackspace;
    private Button mBtnNext;
    
    private StringBuilder mAmountInput = new StringBuilder();
    private static final int MAX_AMOUNT_LENGTH = 8; // Max 8 digits for amount

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_input);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Enter Amount");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvAmountDisplay = findViewById(R.id.tv_amount_display);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnClear = findViewById(R.id.btn_clear);
        mBtnBackspace = findViewById(R.id.btn_backspace);
        
        // Initialize number buttons
        int[] buttonIds = {
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        };
        
        for (int i = 0; i < buttonIds.length; i++) {
            mNumberButtons[i] = findViewById(buttonIds[i]);
            final int digit = i;
            mNumberButtons[i].setOnClickListener(v -> onNumberClick(digit));
        }
        
        mBtnClear.setOnClickListener(this);
        mBtnBackspace.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        
        updateAmountDisplay();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_clear) {
            onClearClick();
        } else if (v.getId() == R.id.btn_backspace) {
            onBackspaceClick();
        } else if (v.getId() == R.id.btn_next) {
            onNextClick();
        }
    }

    private void onNumberClick(int digit) {
        if (mAmountInput.length() < MAX_AMOUNT_LENGTH) {
            mAmountInput.append(digit);
            updateAmountDisplay();
        }
    }

    private void onClearClick() {
        mAmountInput.setLength(0);
        updateAmountDisplay();
    }

    private void onBackspaceClick() {
        if (mAmountInput.length() > 0) {
            mAmountInput.setLength(mAmountInput.length() - 1);
            updateAmountDisplay();
        }
    }

    private void onNextClick() {
        if (mAmountInput.length() == 0) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String amountInPounds = mAmountInput.toString();
        long poundsValue = Long.parseLong(amountInPounds);
        
        if (poundsValue <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Convert pounds to piasters (1 pound = 100 piasters)
        long amountInPiasters = poundsValue * 100;
        String amountInPiastersStr = String.valueOf(amountInPiasters);
        
        LogUtil.e(Constant.TAG, "Amount entered: " + poundsValue + " EGP (" + amountInPiasters + " piasters)");
        
        // Navigate directly to card detection (ProcessingActivity)
        Intent intent = new Intent(this, ProcessingActivity.class);
        intent.putExtra("amount", amountInPiastersStr); // Pass piasters to SDK
        intent.putExtra("amountDisplay", amountInPounds); // Pass pounds for display
        startActivity(intent);
    }

    private void updateAmountDisplay() {
        if (mAmountInput.length() == 0) {
            mTvAmountDisplay.setText("0 EGP");
        } else {
            long amountInPounds = Long.parseLong(mAmountInput.toString());
            mTvAmountDisplay.setText(String.format("%d EGP", amountInPounds));
        }
    }
}