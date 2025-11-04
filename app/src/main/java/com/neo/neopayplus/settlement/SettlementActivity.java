package com.neo.neopayplus.settlement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.R;
import com.neo.neopayplus.api.SettlementApiFactory;
import com.neo.neopayplus.api.SettlementApiService;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Settlement Activity
 * 
 * Handles batch upload of transactions for settlement processing.
 * Uploads transaction batches to backend via POST /v1/settlement/upload.
 */
public class SettlementActivity extends BaseAppCompatActivity implements View.OnClickListener {
    
    private TextView mTvStatus;
    private TextView mTvBatchInfo;
    private TextView mTvResults;
    private Button mBtnUpload;
    private Button mBtnRefresh;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;
    
    private SettlementApiService mSettlementApi;
    
    // Mock transaction data for testing (in production, this would come from local database)
    private List<SettlementApiService.SettlementTransaction> mMockTransactions;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        initView();
        
        mSettlementApi = SettlementApiFactory.getInstance();
        
        // Initialize mock transactions for testing
        // TODO: In production, load transactions from local database/journal
        initMockTransactions();
    }
    
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settlement Upload");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvStatus = findViewById(R.id.tv_status);
        mTvBatchInfo = findViewById(R.id.tv_batch_info);
        mTvResults = findViewById(R.id.tv_results);
        mBtnUpload = findViewById(R.id.btn_upload);
        mBtnRefresh = findViewById(R.id.btn_refresh);
        mProgressBar = findViewById(R.id.progress_bar);
        mScrollView = findViewById(R.id.scroll_view);
        
        mBtnUpload.setOnClickListener(this);
        mBtnRefresh.setOnClickListener(this);
        
        updateStatus("Ready to upload settlement batch");
        updateBatchInfo();
    }
    
    private void initMockTransactions() {
        mMockTransactions = new ArrayList<>();
        
        // Add mock transactions for testing
        // TODO: Replace with actual transactions from local database
        for (int i = 1; i <= 5; i++) {
            SettlementApiService.SettlementTransaction tx = new SettlementApiService.SettlementTransaction();
            tx.rrn = String.format(Locale.US, "RRN%08d", i);
            tx.authCode = String.format(Locale.US, "AUTH%06d", i);
            tx.pan = "****1234";
            tx.amount = String.valueOf(100 * i * 100); // i * 100 EGP in piasters
            tx.currencyCode = PaymentConfig.getCurrencyCode();
            tx.transactionType = "00"; // Purchase
            tx.date = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());
            tx.time = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
            tx.field55 = ""; // Mock Field 55 (empty for now)
            tx.responseCode = "00"; // Approved
            tx.status = "APPROVED";
            mMockTransactions.add(tx);
        }
        
        LogUtil.e(Constant.TAG, "Initialized " + mMockTransactions.size() + " mock transactions for settlement");
    }
    
    private void updateStatus(String status) {
        mTvStatus.setText(status);
        LogUtil.e(Constant.TAG, "Settlement Status: " + status);
    }
    
    private void updateBatchInfo() {
        int transactionCount = mMockTransactions != null ? mMockTransactions.size() : 0;
        String batchDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        String batchTime = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
        
        StringBuilder info = new StringBuilder();
        info.append("Terminal ID: ").append(PaymentConfig.getTerminalId()).append("\n");
        info.append("Batch Date: ").append(batchDate).append("\n");
        info.append("Batch Time: ").append(batchTime).append("\n");
        info.append("Transaction Count: ").append(transactionCount).append("\n");
        
        if (transactionCount > 0) {
            long totalAmount = 0;
            for (SettlementApiService.SettlementTransaction tx : mMockTransactions) {
                try {
                    totalAmount += Long.parseLong(tx.amount);
                } catch (NumberFormatException e) {
                    // Skip invalid amounts
                }
            }
            double totalInMainUnit = totalAmount / 100.0; // Convert piasters to pounds
            info.append("Total Amount: ").append(String.format(Locale.US, "%.2f", totalInMainUnit))
                .append(" ").append(PaymentConfig.CURRENCY_NAME).append("\n");
        }
        
        mTvBatchInfo.setText(info.toString());
    }
    
    private void updateResults(String results) {
        mTvResults.setText(results);
        // Scroll to bottom to show latest results
        mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_DOWN));
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_upload) {
            uploadBatch();
        } else if (v.getId() == R.id.btn_refresh) {
            refreshTransactions();
        }
    }
    
    private void uploadBatch() {
        if (mMockTransactions == null || mMockTransactions.isEmpty()) {
            updateStatus("No transactions to upload");
            showToast("No transactions available for settlement");
            return;
        }
        
        if (!mSettlementApi.isAvailable()) {
            updateStatus("Settlement API not available");
            showToast("Settlement API service is not configured");
            return;
        }
        
        // Disable upload button during upload
        mBtnUpload.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        updateStatus("Uploading settlement batch...");
        
        // Build batch upload request
        SettlementApiService.BatchUploadRequest request = new SettlementApiService.BatchUploadRequest();
        request.terminalId = PaymentConfig.getTerminalId();
        request.batchDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        request.batchTime = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
        request.transactions = new ArrayList<>(mMockTransactions);
        
        LogUtil.e(Constant.TAG, "Starting settlement batch upload...");
        LogUtil.e(Constant.TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(Constant.TAG, "  Batch Date: " + request.batchDate);
        LogUtil.e(Constant.TAG, "  Transaction Count: " + request.transactions.size());
        
        // Upload batch
        mSettlementApi.uploadBatch(request, new SettlementApiService.BatchUploadCallback() {
            @Override
            public void onBatchUploadComplete(SettlementApiService.BatchUploadResponse response) {
                runOnUiThread(() -> {
                    mBtnUpload.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    
                    if (response.success) {
                        updateStatus("Batch upload successful");
                        
                        StringBuilder results = new StringBuilder();
                        results.append("Batch Upload Results:\n\n");
                        results.append("Batch ID: ").append(response.batchId).append("\n");
                        results.append("Total Transactions: ").append(response.totalCount).append("\n");
                        results.append("Accepted: ").append(response.acceptedCount).append("\n");
                        results.append("Rejected: ").append(response.rejectedCount).append("\n\n");
                        
                        if (response.acceptedRrns != null && !response.acceptedRrns.isEmpty()) {
                            results.append("Accepted RRNs:\n");
                            for (String rrn : response.acceptedRrns) {
                                results.append("  • ").append(rrn).append("\n");
                            }
                            results.append("\n");
                        }
                        
                        if (response.rejectedRrns != null && !response.rejectedRrns.isEmpty()) {
                            results.append("Rejected RRNs:\n");
                            for (String rrn : response.rejectedRrns) {
                                results.append("  • ").append(rrn).append("\n");
                            }
                            results.append("\n");
                        }
                        
                        results.append("Message: ").append(response.message);
                        updateResults(results.toString());
                        
                        showToast("Batch upload successful");
                        LogUtil.e(Constant.TAG, "✓ Settlement batch upload successful: " + response.batchId);
                    } else {
                        updateStatus("Batch upload failed");
                        updateResults("Error: " + response.message);
                        showToast("Batch upload failed: " + response.message);
                        LogUtil.e(Constant.TAG, "❌ Settlement batch upload failed: " + response.message);
                    }
                });
            }
            
            @Override
            public void onBatchUploadError(Throwable error) {
                runOnUiThread(() -> {
                    mBtnUpload.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    
                    updateStatus("Batch upload failed");
                    updateResults("Error: " + error.getMessage());
                    showToast("Batch upload failed: " + error.getMessage());
                    com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "Settlement batch upload", error);
                });
            }
        });
    }
    
    private void refreshTransactions() {
        // TODO: In production, refresh transactions from local database/journal
        updateStatus("Refreshing transactions...");
        
        // Simulate refresh delay
        mBtnRefresh.postDelayed(() -> {
            initMockTransactions();
            updateBatchInfo();
            updateStatus("Transactions refreshed");
            showToast("Transactions refreshed");
        }, 500);
    }
}

