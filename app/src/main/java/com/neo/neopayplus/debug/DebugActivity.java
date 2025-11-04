package com.neo.neopayplus.debug;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.R;
import com.neo.neopayplus.iso.IsoLogger;
import com.neo.neopayplus.utils.LogUtil;

import java.util.List;

/**
 * Debug Activity
 * 
 * Displays latest ISO8583 logs for debugging
 * Shows last 10 ISO frames saved to disk
 */
public class DebugActivity extends BaseAppCompatActivity {
    
    private TextView mTvLogs;
    private ScrollView mScrollView;
    
    private static final int MAX_LOGS = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        initView();
        loadIsoLogs();
    }
    
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ISO8583 Debug Logs");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvLogs = findViewById(R.id.tv_logs);
        mScrollView = findViewById(R.id.scroll_view);
    }
    
    private void loadIsoLogs() {
        try {
            // Get last N ISO log entries
            List<String> logFiles = IsoLogger.tail(MAX_LOGS);
            
            if (logFiles == null || logFiles.isEmpty()) {
                mTvLogs.setText("No ISO8583 log files found.\n\n" +
                    "Location: /Android/data/com.neo.neopayplus/files/iso_logs/\n\n" +
                    "Logs are created when you:\n" +
                    "- Process a sale transaction (0100)\n" +
                    "- Process a reversal transaction (0400)");
                return;
            }
            
            StringBuilder logs = new StringBuilder();
            logs.append("=== ISO8583 Debug Logs ===\n");
            logs.append("Showing last " + logFiles.size() + " log files\n\n");
            
            // Read each log file
            for (int i = 0; i < logFiles.size(); i++) {
                String filePath = logFiles.get(i);
                String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                
                logs.append("--- Log " + (i + 1) + ": " + filename + " ---\n");
                
                String content = IsoLogger.readLog(filePath);
                if (content != null) {
                    logs.append(content);
                } else {
                    logs.append("(Unable to read log file)\n");
                }
                
                logs.append("\n\n");
            }
            
            mTvLogs.setText(logs.toString());
            
            // Scroll to top
            mScrollView.post(() -> mScrollView.fullScroll(ScrollView.FOCUS_UP));
            
            LogUtil.e(Constant.TAG, "✓ Loaded " + logFiles.size() + " ISO log entries");
            
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "Loading ISO logs", e);
            mTvLogs.setText("Error loading ISO logs: " + e.getMessage());
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload logs when activity resumes (in case new logs were created)
        loadIsoLogs();
    }
}

