package com.neo.neopayplus.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.R;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.LogUtil;
import com.sunmi.pay.hardware.aidlv2.bean.AidV2;
import com.sunmi.pay.hardware.aidlv2.bean.CapkV2;

public class EMVTestSaveAidCapkActivity extends BaseAppCompatActivity {
    private static final String TAG = Constant.TAG;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_aid_capk);
        initView();
    }

    private void initView() {
        initToolbarBringBack("Save AID/CAPK");
        findViewById(R.id.test).setOnClickListener(v -> {
            testSaveAidCapk();
        });
    }

    /**
     * Load AIDs and CAPKs from API service instead of hardcoded test data
     */
    private void testSaveAidCapk() {
        // Load AIDs and CAPKs from API service
        com.neo.neopayplus.api.EmvConfigApiService apiService = 
            com.neo.neopayplus.api.EmvConfigApiFactory.getInstance();
        
        if (!apiService.isAvailable()) {
            android.widget.Toast.makeText(this, "EMV Config API service not available", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        android.widget.Toast.makeText(this, "Loading AIDs and CAPKs from API...", android.widget.Toast.LENGTH_SHORT).show();
        
        apiService.loadEmvConfiguration(new com.neo.neopayplus.api.EmvConfigApiService.EmvConfigCallback() {
            @Override
            public void onConfigLoaded(com.neo.neopayplus.api.EmvConfigApiService.EmvConfigResponse response) {
                if (response.success) {
                    runOnUiThread(() -> {
                        try {
                            saveAidsFromApiResponse(response.aids);
                            saveCapksFromApiResponse(response.capks);
                            android.widget.Toast.makeText(EMVTestSaveAidCapkActivity.this, 
                                "Loaded " + response.aids.size() + " AIDs and " + response.capks.size() + " CAPKs", 
                                android.widget.Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            LogUtil.e(TAG, "Error saving AIDs/CAPKs: " + e.getMessage());
                            android.widget.Toast.makeText(EMVTestSaveAidCapkActivity.this, 
                                "Error saving: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(EMVTestSaveAidCapkActivity.this, 
                            "Failed to load configuration", android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onConfigError(Throwable error) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(EMVTestSaveAidCapkActivity.this, 
                        "Error: " + error.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    /**
     * Save AIDs from API response
     */
    private void saveAidsFromApiResponse(java.util.List<com.neo.neopayplus.api.EmvConfigApiService.AidConfig> aidConfigs) throws RemoteException {
        if (aidConfigs == null || aidConfigs.isEmpty()) {
            LogUtil.e(TAG, "No AIDs to save");
            return;
        }
        
        LogUtil.e(TAG, "Saving " + aidConfigs.size() + " AIDs from API...");
        
        int successCount = 0;
        for (com.neo.neopayplus.api.EmvConfigApiService.AidConfig aidConfig : aidConfigs) {
            try {
                AidV2 aid = new AidV2();
                aid.aid = ByteUtil.hexStr2Bytes(aidConfig.aidHex);
                aid.selFlag = (byte) aidConfig.selFlag;
                // PayLib v2.0.32 requires version field
                aid.version = aidConfig.version != null && !aidConfig.version.isEmpty() 
                    ? ByteUtil.hexStr2Bytes(aidConfig.version) 
                    : ByteUtil.hexStr2Bytes("008C"); // Default version if not provided
                aid.TACDefault = ByteUtil.hexStr2Bytes(aidConfig.tacDefault);
                aid.TACDenial = ByteUtil.hexStr2Bytes(aidConfig.tacDenial);
                aid.TACOnline = ByteUtil.hexStr2Bytes(aidConfig.tacOnline);
                aid.threshold = ByteUtil.hexStr2Bytes(aidConfig.threshold);
                aid.floorLimit = ByteUtil.hexStr2Bytes(aidConfig.floorLimit);
                aid.targetPer = (byte) aidConfig.targetPer;
                aid.maxTargetPer = (byte) aidConfig.maxTargetPer;
                
                int code = MyApplication.app.emvOptV2.addAid(aid);
                if (code == 0) {
                    successCount++;
                    LogUtil.e(TAG, "✓ Saved AID: " + aidConfig.aidHex);
                } else {
                    LogUtil.e(TAG, "AID save result: " + code + " for " + aidConfig.aidHex + " (may already exist)");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Error saving AID " + aidConfig.aidHex + ": " + e.getMessage());
            }
        }
        
        LogUtil.e(TAG, "Saved " + successCount + "/" + aidConfigs.size() + " AIDs");
    }
    
    /**
     * Save CAPKs from API response
     */
    private void saveCapksFromApiResponse(java.util.List<com.neo.neopayplus.api.EmvConfigApiService.CapkConfig> capkConfigs) throws RemoteException {
        if (capkConfigs == null || capkConfigs.isEmpty()) {
            LogUtil.e(TAG, "No CAPKs to save");
            return;
        }
        
        LogUtil.e(TAG, "Saving " + capkConfigs.size() + " CAPKs from API...");
        
        int successCount = 0;
        for (com.neo.neopayplus.api.EmvConfigApiService.CapkConfig capkConfig : capkConfigs) {
            try {
                CapkV2 capk = new CapkV2();
                capk.rid = ByteUtil.hexStr2Bytes(capkConfig.ridHex);
                capk.index = ByteUtil.hexStr2Byte(capkConfig.indexHex);
                capk.modul = ByteUtil.hexStr2Bytes(capkConfig.modulusHex);
                capk.exponent = ByteUtil.hexStr2Bytes(capkConfig.exponentHex);
                if (capkConfig.hashIndHex != null && !capkConfig.hashIndHex.isEmpty()) {
                    capk.hashInd = ByteUtil.hexStr2Byte(capkConfig.hashIndHex);
                }
                if (capkConfig.arithIndHex != null && !capkConfig.arithIndHex.isEmpty()) {
                    capk.arithInd = ByteUtil.hexStr2Byte(capkConfig.arithIndHex);
                }
                
                int code = MyApplication.app.emvOptV2.addCapk(capk);
                if (code == 0) {
                    successCount++;
                    LogUtil.e(TAG, "✓ Saved CAPK: " + capkConfig.ridHex + " index " + capkConfig.indexHex);
                } else {
                    LogUtil.e(TAG, "CAPK save result: " + code + " for " + capkConfig.ridHex + " (may already exist)");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Error saving CAPK " + capkConfig.ridHex + ": " + e.getMessage());
            }
        }
        
        LogUtil.e(TAG, "Saved " + successCount + "/" + capkConfigs.size() + " CAPKs");
    }
}
