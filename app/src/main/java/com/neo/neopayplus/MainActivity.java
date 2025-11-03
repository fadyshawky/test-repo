package com.neo.neopayplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.amount.AmountInputActivity;
import com.neo.neopayplus.settlement.SettlementActivity;

public class MainActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("NeoPayPlus");

        findViewById(R.id.card_view_card).setOnClickListener(this);
        findViewById(R.id.btnDebug).setOnClickListener(this);
        findViewById(R.id.btnSettle).setOnClickListener(this);
        findViewById(R.id.btnReverse).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.app.isConnectPaySDK()) {
            MyApplication.app.bindPaySDKService();
        }
    }

    @Override
    public void onClick(View v) {
        if (!MyApplication.app.isConnectPaySDK()) {
            MyApplication.app.bindPaySDKService();
            showToast(R.string.connect_loading);
            return;
        }
        final int id = v.getId();
        switch (id) {
            case R.id.card_view_card:
                openActivity(AmountInputActivity.class);
                break;
            case R.id.btnDebug:
                // Open DebugActivity to view ISO8583 logs
                openActivity(com.neo.neopayplus.debug.DebugActivity.class);
                break;
            case R.id.btnSettle:
                openActivity(SettlementActivity.class);
                break;
            case R.id.btnReverse:
                // Navigate to PayActivity in reversal mode
                Intent reversalIntent = new Intent(this, com.neo.neopayplus.payment.PaymentActivity.class);
                reversalIntent.putExtra("mode", "reversal");
                startActivity(reversalIntent);
                break;
        }
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
