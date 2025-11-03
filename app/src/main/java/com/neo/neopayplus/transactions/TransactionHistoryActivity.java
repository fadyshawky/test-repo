package com.neo.neopayplus.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.R;
import com.neo.neopayplus.data.TransactionJournal;
import com.neo.neopayplus.payment.PaymentActivity;
import com.neo.neopayplus.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Transaction History Activity
 * 
 * Displays last transactions for:
 * - Auto-fill RRN in reversal flow
 * - Transaction history lookup
 * - Quick access to reverse a transaction
 */
public class TransactionHistoryActivity extends BaseAppCompatActivity implements View.OnClickListener {
    
    private ListView mTransactionList;
    private TextView mTvEmpty;
    private Button mBtnClose;
    private List<TransactionJournal.TransactionRecord> mTransactions;
    
    private static final int MAX_DISPLAY = 20; // Show last 20 transactions
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        initView();
        loadTransactions();
    }
    
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction History");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTransactionList = findViewById(R.id.transaction_list);
        mTvEmpty = findViewById(R.id.tv_empty);
        mBtnClose = findViewById(R.id.btn_close);
        
        mBtnClose.setOnClickListener(this);
        
        mTransactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransactionJournal.TransactionRecord record = mTransactions.get(position);
                if (record != null && record.rrn != null) {
                    // Return selected RRN to caller (for reversal)
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("rrn", record.rrn);
                    resultIntent.putExtra("amount", record.amount);
                    resultIntent.putExtra("currencyCode", record.currencyCode);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
    
    private void loadTransactions() {
        mTransactions = TransactionJournal.getLastTransactions(MAX_DISPLAY);
        
        if (mTransactions == null || mTransactions.isEmpty()) {
            mTvEmpty.setVisibility(View.VISIBLE);
            mTransactionList.setVisibility(View.GONE);
            mTvEmpty.setText("No transactions found");
            return;
        }
        
        mTvEmpty.setVisibility(View.GONE);
        mTransactionList.setVisibility(View.VISIBLE);
        
        // Create adapter for transaction list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_list_item_2, 
                android.R.id.text1);
        
        // Add transactions to adapter
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.US);
        for (TransactionJournal.TransactionRecord record : mTransactions) {
            String rrn = record.rrn != null ? record.rrn : "N/A";
            String amount = record.amount != null ? record.amount : "0";
            String status = record.status != null ? record.status : "UNKNOWN";
            String dateTime = dateFormat.format(new Date(record.timestamp));
            
            String displayText = String.format(Locale.US, "%s | %s | %s | %s", 
                    dateTime, rrn, amount, status);
            adapter.add(displayText);
        }
        
        mTransactionList.setAdapter(adapter);
        LogUtil.e(Constant.TAG, "Loaded " + mTransactions.size() + " transactions");
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            finish();
        }
    }
}

