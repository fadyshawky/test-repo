package com.neo.neopayplus.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.MainActivity;
import com.neo.neopayplus.R;
import com.neo.neopayplus.emv.TLV;
import com.neo.neopayplus.emv.TLVUtil;

import java.util.Map;
import java.util.HashMap;

public class DataViewActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView mTvAmountDisplay;
    private TextView mTvTransactionData;
    private ScrollView mScrollView;
    private Button mBtnNewPayment;
    private Button mBtnHome;
    
    private String mAmount; // Piasters for SDK
    private String mAmountDisplay; // Pounds for display
    private String mPin;
    private String mStatus;
    private String mTransactionId;
    private String mCardNo;
    private String mEmvTlvData;
    private String mBackendRequestJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        
        // Get data from previous screen
        mAmount = getIntent().getStringExtra("amount"); // Piasters for SDK
        mAmountDisplay = getIntent().getStringExtra("amountDisplay"); // Pounds for display
        mPin = getIntent().getStringExtra("pin");
        mStatus = getIntent().getStringExtra("status");
        mTransactionId = getIntent().getStringExtra("transactionId");
        mCardNo = getIntent().getStringExtra("cardNo");
        mEmvTlvData = getIntent().getStringExtra("emvTlvData");
        mBackendRequestJson = getIntent().getStringExtra("backendRequestJson");
        
        if (mAmount == null) mAmount = "10000"; // Default 100 EGP in piasters
        if (mAmountDisplay == null) mAmountDisplay = "100"; // Default 100 EGP
        if (mPin == null) mPin = "1234";
        if (mStatus == null) mStatus = "success";
        if (mTransactionId == null) mTransactionId = "TXN" + System.currentTimeMillis();
        if (mCardNo == null) mCardNo = "**** **** **** 1234";
        if (mEmvTlvData == null) mEmvTlvData = "No EMV data available";
        if (mBackendRequestJson == null) mBackendRequestJson = "No backend request data available";
        
        initView();
        displayTransactionData();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction Data");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvAmountDisplay = findViewById(R.id.tv_amount_display);
        mTvTransactionData = findViewById(R.id.tv_transaction_data);
        mScrollView = findViewById(R.id.scroll_view);
        mBtnNewPayment = findViewById(R.id.btn_new_payment);
        mBtnHome = findViewById(R.id.btn_home);
        
        mBtnNewPayment.setOnClickListener(this);
        mBtnHome.setOnClickListener(this);
        
        updateAmountDisplay();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_new_payment) {
            // Start new payment flow
            Intent intent = new Intent(this, com.neo.neopayplus.amount.AmountInputActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.btn_home) {
            // Go to home screen
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void updateAmountDisplay() {
        long amountInPounds = Long.parseLong(mAmountDisplay);
        mTvAmountDisplay.setText(String.format("%d EGP", amountInPounds));
    }

    private void displayTransactionData() {
        StringBuilder data = new StringBuilder();
        
        // Transaction Summary
        data.append("=== TRANSACTION SUMMARY ===\n\n");
        data.append("Transaction ID: ").append(mTransactionId).append("\n");
        data.append("Amount: ").append(mAmountDisplay).append(" EGP\n");
        data.append("Amount (Piasters): ").append(mAmount).append(" piasters\n");
        data.append("Card Number: ").append(mCardNo != null ? mCardNo : "Not available").append("\n");
        data.append("Status: ").append(mStatus.toUpperCase()).append("\n");
        data.append("Timestamp: ").append(new java.util.Date().toString()).append("\n\n");
        
        // Parse real EMV TLV data
        Map<String, String> parsedEmvData = parseEmvTlvData();
        
        // Real EMV TLV Data
        data.append("=== REAL EMV TLV DATA ===\n\n");
        if (mEmvTlvData != null && !mEmvTlvData.equals("No EMV data available") && !mEmvTlvData.equals("Error extracting EMV data")) {
            data.append("Raw TLV Data:\n").append(mEmvTlvData).append("\n\n");
        } else {
            data.append("No real EMV data available (card not detected)\n\n");
        }
        
        // Parsed EMV Data
        data.append("=== PARSED EMV DATA ===\n\n");
        if (!parsedEmvData.isEmpty()) {
            for (Map.Entry<String, String> entry : parsedEmvData.entrySet()) {
                String tag = entry.getKey();
                String value = entry.getValue();
                String description = getTagDescription(tag);
                data.append(tag).append(": ").append(value).append(" (").append(description).append(")\n");
            }
        } else {
            data.append("No EMV data available for parsing\n");
        }
        
        // Apple Pay Information
        data.append("\n=== APPLE PAY INFORMATION ===\n\n");
        if (mCardNo != null && mCardNo.contains("Apple Pay")) {
            data.append("Payment Method: Apple Pay/Contactless\n");
            data.append("Card Number: ").append(mCardNo).append("\n");
            data.append("Note: This is a tokenized transaction (DPAN)\n");
        } else {
            data.append("Payment Method: EMV Card\n");
            data.append("Card Number: ").append(mCardNo != null ? mCardNo : "Not available").append("\n");
        }
        
        // Backend Payload with real data
        data.append("\n=== BACKEND PAYLOAD ===\n\n");
        data.append(generateBackendPayload(parsedEmvData));
        
        // Exact JSON sent to backend API
        data.append("\n=== EXACT REQUEST JSON SENT TO BACKEND ===\n\n");
        data.append(mBackendRequestJson != null ? mBackendRequestJson : "No request data available");
        
        mTvTransactionData.setText(data.toString());
    }
    
    /**
     * Parse the real EMV TLV data into a map
     */
    private Map<String, String> parseEmvTlvData() {
        Map<String, String> emvData = new HashMap<>();
        
        if (mEmvTlvData == null || mEmvTlvData.equals("No EMV data available") || mEmvTlvData.equals("Error extracting EMV data")) {
            return emvData;
        }
        
        try {
            // Split by lines and parse each tag:value pair
            String[] lines = mEmvTlvData.split("\n");
            for (String line : lines) {
                if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    if (parts.length == 2) {
                        String tag = parts[0].trim();
                        String value = parts[1].trim();
                        emvData.put(tag, value);
                    }
                }
            }
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError("DataViewActivity", e);
        }
        
        return emvData;
    }
    
    /**
     * Get human-readable description for EMV tags
     */
    private String getTagDescription(String tag) {
        switch (tag) {
            case "9F26": return "Application Cryptogram";
            case "9F27": return "Cryptogram Information Data";
            case "9F10": return "Issuer Application Data";
            case "9F36": return "Application Transaction Counter";
            case "9F37": return "Unpredictable Number";
            case "95": return "Terminal Verification Results";
            case "9A": return "Transaction Date";
            case "9C": return "Transaction Type";
            case "9F02": return "Amount, Authorised";
            case "5F2A": return "Transaction Currency Code";
            case "9F1A": return "Terminal Country Code";
            case "82": return "Application Interchange Profile";
            case "9F33": return "Terminal Capabilities";
            case "9F35": return "Terminal Type";
            case "9F1E": return "Interface Device Serial Number";
            case "9F09": return "Application Version Number";
            case "9F34": return "CVM Results";
            case "9F41": return "Transaction Sequence Counter";
            case "9F53": return "Transaction Category Code";
            case "9F21": return "Transaction Time";
            case "9F4E": return "Merchant Name and Location";
            case "9F15": return "Merchant Category Code";
            case "9F16": return "Merchant Identifier";
            case "9F4C": return "ICC Dynamic Number";
            case "9F4D": return "Log Entry";
            case "9F40": return "Additional Terminal Capabilities";
            case "9F4A": return "Transaction Type";
            case "9F4B": return "Signed Data Format";
            case "9F4F": return "Log Format";
            case "9F50": return "ICC Public Key Certificate";
            case "9F51": return "ICC Public Key Exponent";
            case "9F52": return "ICC Public Key Remainder";
            case "9F54": return "DS Summary 1";
            case "9F55": return "DS Summary 2";
            case "9F56": return "Issuer Public Key Certificate";
            case "9F57": return "Issuer Public Key Exponent";
            case "9F58": return "Issuer Public Key Remainder";
            case "9F59": return "Issuer Public Key Certificate";
            case "9F5A": return "Issuer Public Key Exponent";
            case "9F5B": return "Issuer Public Key Remainder";
            case "9F5C": return "ICC Public Key Certificate";
            case "9F5D": return "ICC Public Key Exponent";
            case "9F5E": return "ICC Public Key Remainder";
            case "9F5F": return "ICC Public Key Certificate";
            case "9F60": return "ICC Public Key Exponent";
            case "9F61": return "ICC Public Key Remainder";
            case "9F62": return "ICC Public Key Certificate";
            case "9F63": return "ICC Public Key Exponent";
            case "9F64": return "ICC Public Key Remainder";
            case "9F65": return "ICC Public Key Certificate";
            case "9F66": return "ICC Public Key Exponent";
            case "9F67": return "ICC Public Key Remainder";
            case "9F68": return "ICC Public Key Certificate";
            case "9F69": return "ICC Public Key Exponent";
            case "9F6A": return "ICC Public Key Remainder";
            case "9F6B": return "ICC Public Key Certificate";
            case "9F6C": return "ICC Public Key Exponent";
            case "9F6D": return "ICC Public Key Remainder";
            case "9F6E": return "Third Party Data";
            case "9F6F": return "ICC Public Key Exponent";
            case "9F70": return "ICC Public Key Remainder";
            case "9F71": return "ICC Public Key Certificate";
            case "9F72": return "ICC Public Key Exponent";
            case "9F73": return "ICC Public Key Remainder";
            case "9F74": return "ICC Public Key Certificate";
            case "9F75": return "ICC Public Key Exponent";
            case "9F76": return "ICC Public Key Remainder";
            case "9F77": return "ICC Public Key Certificate";
            case "9F78": return "ICC Public Key Exponent";
            case "9F79": return "ICC Public Key Remainder";
            case "9F7A": return "ICC Public Key Certificate";
            case "9F7B": return "ICC Public Key Exponent";
            case "9F7C": return "ICC Public Key Remainder";
            case "9F7D": return "ICC Public Key Certificate";
            case "9F7E": return "ICC Public Key Exponent";
            case "9F7F": return "ICC Public Key Remainder";
            case "9F80": return "ICC Public Key Certificate";
            case "9F81": return "ICC Public Key Exponent";
            case "9F82": return "ICC Public Key Remainder";
            case "9F83": return "ICC Public Key Certificate";
            case "9F84": return "Dedicated File (DF) Name (AID)";
            case "9F85": return "ICC Public Key Exponent";
            case "9F86": return "ICC Public Key Remainder";
            case "9F87": return "ICC Public Key Certificate";
            case "9F88": return "ICC Public Key Exponent";
            case "9F89": return "ICC Public Key Remainder";
            case "9F8A": return "ICC Public Key Certificate";
            case "9F8B": return "ICC Public Key Exponent";
            case "9F8C": return "ICC Public Key Remainder";
            case "9F8D": return "ICC Public Key Certificate";
            case "9F8E": return "ICC Public Key Exponent";
            case "9F8F": return "ICC Public Key Remainder";
            case "9F90": return "ICC Public Key Certificate";
            case "9F91": return "ICC Public Key Exponent";
            case "9F92": return "ICC Public Key Remainder";
            case "9F93": return "ICC Public Key Certificate";
            case "9F94": return "ICC Public Key Exponent";
            case "9F95": return "ICC Public Key Remainder";
            case "9F96": return "ICC Public Key Certificate";
            case "9F97": return "ICC Public Key Exponent";
            case "9F98": return "ICC Public Key Remainder";
            case "9F99": return "ICC Public Key Certificate";
            case "9F9A": return "ICC Public Key Exponent";
            case "9F9B": return "ICC Public Key Remainder";
            case "9F9C": return "ICC Public Key Certificate";
            case "9F9D": return "ICC Public Key Exponent";
            case "9F9E": return "ICC Public Key Remainder";
            case "9F9F": return "ICC Public Key Certificate";
            case "9FA0": return "ICC Public Key Exponent";
            case "9FA1": return "ICC Public Key Remainder";
            case "9FA2": return "ICC Public Key Certificate";
            case "9FA3": return "ICC Public Key Exponent";
            case "9FA4": return "ICC Public Key Remainder";
            case "9FA5": return "ICC Public Key Certificate";
            case "9FA6": return "ICC Public Key Exponent";
            case "9FA7": return "ICC Public Key Remainder";
            case "9FA8": return "ICC Public Key Certificate";
            case "9FA9": return "ICC Public Key Exponent";
            case "9FAA": return "ICC Public Key Remainder";
            case "9FAB": return "ICC Public Key Certificate";
            case "9FAC": return "ICC Public Key Exponent";
            case "9FAD": return "ICC Public Key Remainder";
            case "9FAE": return "ICC Public Key Certificate";
            case "9FAF": return "ICC Public Key Exponent";
            case "9FB0": return "ICC Public Key Remainder";
            case "9FB1": return "ICC Public Key Certificate";
            case "9FB2": return "ICC Public Key Exponent";
            case "9FB3": return "ICC Public Key Remainder";
            case "9FB4": return "ICC Public Key Certificate";
            case "9FB5": return "ICC Public Key Exponent";
            case "9FB6": return "ICC Public Key Remainder";
            case "9FB7": return "ICC Public Key Certificate";
            case "9FB8": return "ICC Public Key Exponent";
            case "9FB9": return "ICC Public Key Remainder";
            case "9FBA": return "ICC Public Key Certificate";
            case "9FBB": return "ICC Public Key Exponent";
            case "9FBC": return "ICC Public Key Remainder";
            case "9FBD": return "ICC Public Key Certificate";
            case "9FBE": return "ICC Public Key Exponent";
            case "9FBF": return "ICC Public Key Remainder";
            case "9FC0": return "ICC Public Key Certificate";
            case "9FC1": return "ICC Public Key Exponent";
            case "9FC2": return "ICC Public Key Remainder";
            case "9FC3": return "ICC Public Key Certificate";
            case "9FC4": return "ICC Public Key Exponent";
            case "9FC5": return "ICC Public Key Remainder";
            case "9FC6": return "ICC Public Key Certificate";
            case "9FC7": return "ICC Public Key Exponent";
            case "9FC8": return "ICC Public Key Remainder";
            case "9FC9": return "ICC Public Key Certificate";
            case "9FCA": return "ICC Public Key Exponent";
            case "9FCB": return "ICC Public Key Remainder";
            case "9FCC": return "ICC Public Key Certificate";
            case "9FCD": return "ICC Public Key Exponent";
            case "9FCE": return "ICC Public Key Remainder";
            case "9FCF": return "ICC Public Key Certificate";
            case "9FD0": return "ICC Public Key Exponent";
            case "9FD1": return "ICC Public Key Remainder";
            case "9FD2": return "ICC Public Key Certificate";
            case "9FD3": return "ICC Public Key Exponent";
            case "9FD4": return "ICC Public Key Remainder";
            case "9FD5": return "ICC Public Key Certificate";
            case "9FD6": return "ICC Public Key Exponent";
            case "9FD7": return "ICC Public Key Remainder";
            case "9FD8": return "ICC Public Key Certificate";
            case "9FD9": return "ICC Public Key Exponent";
            case "9FDA": return "ICC Public Key Remainder";
            case "9FDB": return "ICC Public Key Certificate";
            case "9FDC": return "ICC Public Key Exponent";
            case "9FDD": return "ICC Public Key Remainder";
            case "9FDE": return "ICC Public Key Certificate";
            case "9FDF": return "ICC Public Key Exponent";
            case "9FE0": return "ICC Public Key Remainder";
            case "9FE1": return "ICC Public Key Certificate";
            case "9FE2": return "ICC Public Key Exponent";
            case "9FE3": return "ICC Public Key Remainder";
            case "9FE4": return "ICC Public Key Certificate";
            case "9FE5": return "ICC Public Key Exponent";
            case "9FE6": return "ICC Public Key Remainder";
            case "9FE7": return "ICC Public Key Certificate";
            case "9FE8": return "ICC Public Key Exponent";
            case "9FE9": return "ICC Public Key Remainder";
            case "9FEA": return "ICC Public Key Certificate";
            case "9FEB": return "ICC Public Key Exponent";
            case "9FEC": return "ICC Public Key Remainder";
            case "9FED": return "ICC Public Key Certificate";
            case "9FEE": return "ICC Public Key Exponent";
            case "9FEF": return "ICC Public Key Remainder";
            case "9FF0": return "ICC Public Key Certificate";
            case "9FF1": return "ICC Public Key Exponent";
            case "9FF2": return "ICC Public Key Remainder";
            case "9FF3": return "ICC Public Key Certificate";
            case "9FF4": return "ICC Public Key Exponent";
            case "9FF5": return "ICC Public Key Remainder";
            case "9FF6": return "ICC Public Key Certificate";
            case "9FF7": return "ICC Public Key Exponent";
            case "9FF8": return "ICC Public Key Remainder";
            case "9FF9": return "ICC Public Key Certificate";
            case "9FFA": return "ICC Public Key Exponent";
            case "9FFB": return "ICC Public Key Remainder";
            case "9FFC": return "ICC Public Key Certificate";
            case "9FFD": return "ICC Public Key Exponent";
            case "9FFE": return "ICC Public Key Remainder";
            case "9FFF": return "ICC Public Key Exponent";
            case "5F34": return "PAN Sequence Number";
            case "50": return "Application Label";
            case "9F12": return "Application Preferred Name";
            case "9F39": return "POS Entry Mode";
            default: return "Unknown Tag";
        }
    }
    
    /**
     * Generate backend payload with real EMV data
     */
    private String generateBackendPayload(Map<String, String> emvData) {
        StringBuilder payload = new StringBuilder();
        payload.append("{\n");
        payload.append("  \"transactionId\": \"").append(mTransactionId).append("\",\n");
        payload.append("  \"amount\": ").append(mAmount).append(",\n");
        payload.append("  \"amountDisplay\": ").append(mAmountDisplay).append(",\n");
        payload.append("  \"currency\": \"EGP\",\n");
        payload.append("  \"status\": \"").append(mStatus).append("\",\n");
        payload.append("  \"timestamp\": \"").append(new java.util.Date().toString()).append("\",\n");
        payload.append("  \"cardNumber\": \"").append(mCardNo != null ? mCardNo : "").append("\",\n");
        payload.append("  \"emvData\": {\n");
        
        // Add essential EMV tags with real data
        String[] essentialTags = {
            "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
            "82", "9F1A", "9F03", "9F33", "9F34", "9F35", "9F1E", "84", "9F09", "9F41",
            "5F34", "50", "9F12", "9F6E", "9F4E", "9F15", "9F39"
        };
        
        boolean first = true;
        for (String tag : essentialTags) {
            if (emvData.containsKey(tag)) {
                if (!first) payload.append(",\n");
                payload.append("    \"").append(tag).append("\": \"").append(emvData.get(tag)).append("\"");
                first = false;
            }
        }
        
        // Add any other tags that were extracted
        for (Map.Entry<String, String> entry : emvData.entrySet()) {
            String tag = entry.getKey();
            boolean isEssential = false;
            for (String essentialTag : essentialTags) {
                if (tag.equals(essentialTag)) {
                    isEssential = true;
                    break;
                }
            }
            if (!isEssential) {
                if (!first) payload.append(",\n");
                payload.append("    \"").append(tag).append("\": \"").append(entry.getValue()).append("\"");
                first = false;
            }
        }
        
        payload.append("\n  }\n");
        payload.append("}\n");
        
        return payload.toString();
    }

    private String generateRandomHex(int length) {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < length; i++) {
            hex.append(Integer.toHexString((int) (Math.random() * 16)));
        }
        return hex.toString().toUpperCase();
    }
}
