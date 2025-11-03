package com.neo.neopayplus.iso;

import android.content.Context;
import android.os.Environment;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.LogUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * ISO8583 Logger
 * 
 * Writes raw ISO8583 frames to disk for debugging
 * Location: /Android/data/com.neo.neopayplus/files/iso_logs/
 * Format: yyyymmdd_hhmmss_MTI.txt
 * 
 * No storage permission needed (app's external files directory)
 */
public class IsoLogger {
    
    private static final String TAG = Constant.TAG;
    private static final String LOG_DIR = "iso_logs";
    private static final String FILE_PREFIX = "iso_";
    private static final int MAX_LOG_FILES = 100; // Keep last 100 log files
    
    /**
     * Save ISO8583 frame to disk
     * 
     * @param isoFrame Raw ISO8583 binary frame
     * @param mti Message Type Indicator (e.g., "0100", "0400")
     * @return File path if successful, null otherwise
     */
    public static String save(byte[] isoFrame, String mti) {
        if (isoFrame == null || isoFrame.length == 0) {
            LogUtil.e(TAG, "⚠️ Empty ISO frame - skipping save");
            return null;
        }
        
        try {
            Context context = MyApplication.app;
            File logDir = getLogDirectory(context);
            
            if (!logDir.exists() && !logDir.mkdirs()) {
                LogUtil.e(TAG, "❌ Failed to create log directory: " + logDir.getAbsolutePath());
                return null;
            }
            
            // Generate filename: yyyymmdd_hhmmss_MTI.txt
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
            String timestamp = dateFormat.format(new Date());
            String filename = FILE_PREFIX + timestamp + "_" + mti + ".txt";
            File logFile = new File(logDir, filename);
            
            // Write ISO frame as hex dump
            FileWriter writer = new FileWriter(logFile);
            writer.write("ISO8583 Frame - MTI: " + mti + "\n");
            writer.write("Timestamp: " + new Date().toString() + "\n");
            writer.write("Length: " + isoFrame.length + " bytes\n");
            writer.write("Hex Dump:\n");
            
            // Write hex dump (16 bytes per line)
            for (int i = 0; i < isoFrame.length; i++) {
                if (i > 0 && i % 16 == 0) {
                    writer.write("\n");
                }
                writer.write(String.format("%02X ", isoFrame[i]));
            }
            writer.write("\n");
            
            // Write ASCII representation (if printable)
            writer.write("\nASCII (if printable):\n");
            for (byte b : isoFrame) {
                char c = (char) (b & 0xFF);
                if (c >= 32 && c < 127) {
                    writer.write(c);
                } else {
                    writer.write(".");
                }
            }
            writer.write("\n");
            
            writer.close();
            
            LogUtil.e(TAG, "✓ ISO8583 frame saved: " + logFile.getAbsolutePath());
            
            // Clean up old log files (keep last MAX_LOG_FILES)
            cleanupOldLogs(logDir);
            
            return logFile.getAbsolutePath();
            
        } catch (IOException e) {
            LogUtil.e(TAG, "❌ Error saving ISO frame: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get last N ISO log entries
     * 
     * @param count Number of entries to retrieve
     * @return List of log file paths (most recent first)
     */
    public static List<String> tail(int count) {
        List<String> logs = new ArrayList<>();
        
        try {
            Context context = MyApplication.app;
            File logDir = getLogDirectory(context);
            
            if (!logDir.exists() || !logDir.isDirectory()) {
                LogUtil.e(TAG, "Log directory does not exist: " + logDir.getAbsolutePath());
                return logs;
            }
            
            File[] files = logDir.listFiles();
            if (files == null || files.length == 0) {
                LogUtil.e(TAG, "No ISO log files found");
                return logs;
            }
            
            // Sort by last modified (newest first)
            java.util.Arrays.sort(files, (f1, f2) -> 
                Long.compare(f2.lastModified(), f1.lastModified())
            );
            
            // Get last N files
            int takeCount = Math.min(count, files.length);
            for (int i = 0; i < takeCount; i++) {
                logs.add(files[i].getAbsolutePath());
            }
            
            LogUtil.e(TAG, "✓ Retrieved " + logs.size() + " ISO log entries");
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error retrieving ISO logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
    
    /**
     * Read log file content
     * 
     * @param filePath Full path to log file
     * @return Log file content, or null if error
     */
    public static String readLog(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LogUtil.e(TAG, "Log file not found: " + filePath);
                return null;
            }
            
            StringBuilder content = new StringBuilder();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            
            return content.toString();
            
        } catch (IOException e) {
            LogUtil.e(TAG, "❌ Error reading log file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get log directory
     * Location: /Android/data/com.neo.neopayplus/files/iso_logs/
     * 
     * @param context Application context
     * @return Log directory File object
     */
    private static File getLogDirectory(Context context) {
        // Use external files directory (no permission needed)
        File filesDir = context.getExternalFilesDir(null);
        if (filesDir == null) {
            // Fallback to internal files directory
            filesDir = context.getFilesDir();
        }
        
        return new File(filesDir, LOG_DIR);
    }
    
    /**
     * Clean up old log files (keep last MAX_LOG_FILES)
     * 
     * @param logDir Log directory
     */
    private static void cleanupOldLogs(File logDir) {
        try {
            File[] files = logDir.listFiles();
            if (files == null || files.length <= MAX_LOG_FILES) {
                return; // No cleanup needed
            }
            
            // Sort by last modified (oldest first)
            java.util.Arrays.sort(files, (f1, f2) -> 
                Long.compare(f1.lastModified(), f2.lastModified())
            );
            
            // Delete oldest files
            int deleteCount = files.length - MAX_LOG_FILES;
            for (int i = 0; i < deleteCount; i++) {
                boolean deleted = files[i].delete();
                if (deleted) {
                    LogUtil.e(TAG, "✓ Deleted old log file: " + files[i].getName());
                }
            }
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error cleaning up old logs: " + e.getMessage());
        }
    }
    
    /**
     * Clear all log files (for testing/admin)
     * 
     * @return Number of files deleted
     */
    public static int clearAll() {
        int deleted = 0;
        
        try {
            Context context = MyApplication.app;
            File logDir = getLogDirectory(context);
            
            if (!logDir.exists() || !logDir.isDirectory()) {
                return 0;
            }
            
            File[] files = logDir.listFiles();
            if (files == null) {
                return 0;
            }
            
            for (File file : files) {
                if (file.delete()) {
                    deleted++;
                }
            }
            
            LogUtil.e(TAG, "✓ Cleared " + deleted + " ISO log files");
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error clearing logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deleted;
    }
}

