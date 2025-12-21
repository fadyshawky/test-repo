package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ISO 8583 Socket Client
 * 
 * Implements ISO 8583 communication over TCP socket per MsgSpec v341.
 * Message format: Header (ADR + CB + TPDU) + Application Data (MTI + Bitmap +
 * Data Elements) + CRC
 */
public class Iso8583SocketClient {

    private static final String TAG = Constant.TAG;

    private static final int DEFAULT_CONNECT_TIMEOUT = 30000; // 30 seconds
    private static final int DEFAULT_READ_TIMEOUT = 30000; // 30 seconds

    private String host;
    private int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ExecutorService executorService;

    /**
     * Create ISO 8583 socket client
     * 
     * @param host Host address (IP or hostname)
     * @param port Port number
     */
    public Iso8583SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Connect to the ISO 8583 server
     * 
     * @throws IOException if connection fails
     */
    public synchronized void connect() throws IOException {
        if (socket != null && socket.isConnected()) {
            LogUtil.e(TAG, "Socket already connected");
            return;
        }

        LogUtil.e(TAG, "=== Connecting to ISO 8583 server ===");
        LogUtil.e(TAG, "  Host: " + host);
        LogUtil.e(TAG, "  Port: " + port);

        try {
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(host, port), DEFAULT_CONNECT_TIMEOUT);
            socket.setSoTimeout(DEFAULT_READ_TIMEOUT);
            socket.setTcpNoDelay(true); // Disable Nagle's algorithm for low latency

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            LogUtil.e(TAG, "✓ Connected to ISO 8583 server");
        } catch (IOException e) {
            LogUtil.e(TAG, "✗ Failed to connect to ISO 8583 server: " + e.getMessage());
            disconnect();
            throw e;
        }
    }

    /**
     * Send ISO 8583 message and receive response
     * 
     * @param message   ISO 8583 message bytes (with header, application data, and
     *                  CRC)
     * @param timeoutMs Timeout in milliseconds
     * @return Response message bytes
     * @throws IOException if communication fails
     */
    public byte[] sendAndReceive(byte[] message, long timeoutMs) throws IOException {
        if (socket == null || !socket.isConnected()) {
            throw new IOException("Socket not connected");
        }

        LogUtil.e(TAG, "=== Sending ISO 8583 message ===");
        LogUtil.e(TAG, "  Message length: " + message.length + " bytes");
        LogUtil.e(TAG, "  Timeout: " + timeoutMs + " ms");

        // Send message
        outputStream.write(message);
        outputStream.flush();
        LogUtil.e(TAG, "✓ Message sent");

        // Read response
        // First, read header to determine message length
        // For ISO 8583, we need to read the TPDU header (5 bytes) first
        // Then read the application data length
        // For simplicity, we'll read in chunks until we have a complete message

        byte[] header = new byte[7]; // ADR (1) + CB (1) + TPDU (5)
        int headerBytesRead = 0;
        long startTime = System.currentTimeMillis();

        while (headerBytesRead < header.length) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                throw new SocketTimeoutException("Timeout reading response header");
            }

            int bytesRead = inputStream.read(header, headerBytesRead, header.length - headerBytesRead);
            if (bytesRead == -1) {
                throw new IOException("Connection closed by server");
            }
            headerBytesRead += bytesRead;
        }

        LogUtil.e(TAG, "✓ Response header received: " + bytesToHex(header));

        // Parse TPDU to get application data length
        // TPDU is at bytes 2-6 (index 2-6)
        // For now, we'll read the application data
        // The application data starts with MTI (4 bytes) + Bitmap (8 bytes) + Data
        // Elements

        // Read MTI (4 bytes) and Bitmap (8 bytes) to determine data length
        byte[] mtiAndBitmap = new byte[12];
        int mtiBytesRead = 0;
        while (mtiBytesRead < mtiAndBitmap.length) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                throw new SocketTimeoutException("Timeout reading MTI and Bitmap");
            }

            int bytesRead = inputStream.read(mtiAndBitmap, mtiBytesRead, mtiAndBitmap.length - mtiBytesRead);
            if (bytesRead == -1) {
                throw new IOException("Connection closed by server");
            }
            mtiBytesRead += bytesRead;
        }

        // Parse bitmap to determine which fields are present
        // Bitmap is at bytes 4-11 (after MTI)
        byte[] bitmap = new byte[8];
        System.arraycopy(mtiAndBitmap, 4, bitmap, 0, 8);

        // Calculate exact message length by parsing bitmap and data elements
        int dataElementsLength = calculateDataElementsLength(bitmap);
        int applicationDataLength = 4 + 8 + dataElementsLength; // MTI (4) + Bitmap (8) + Data Elements
        int totalMessageLength = 7 + applicationDataLength + 2; // Header (7) + Application Data + CRC (2)

        LogUtil.e(TAG, "  Calculated message length: " + totalMessageLength + " bytes");
        LogUtil.e(TAG, "    Header: 7 bytes");
        LogUtil.e(TAG, "    Application Data: " + applicationDataLength + " bytes (MTI: 4, Bitmap: 8, Data Elements: "
                + dataElementsLength + ")");
        LogUtil.e(TAG, "    CRC: 2 bytes");

        // Read remaining data elements and CRC
        int remainingBytes = dataElementsLength + 2; // Data Elements + CRC
        byte[] remainingData = new byte[remainingBytes];
        int remainingBytesRead = 0;

        while (remainingBytesRead < remainingBytes) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                throw new SocketTimeoutException("Timeout reading response data");
            }

            int bytesRead = inputStream.read(remainingData, remainingBytesRead, remainingBytes - remainingBytesRead);
            if (bytesRead == -1) {
                throw new IOException("Connection closed by server");
            }
            remainingBytesRead += bytesRead;
        }

        // Build complete response message
        byte[] response = new byte[totalMessageLength];
        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(mtiAndBitmap, 0, response, header.length, mtiAndBitmap.length);
        System.arraycopy(remainingData, 0, response, header.length + mtiAndBitmap.length, remainingData.length);

        LogUtil.e(TAG, "✓ Response received: " + response.length + " bytes");

        return response;
    }

    /**
     * Disconnect from the server
     */
    public synchronized void disconnect() {
        if (socket != null) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                socket.close();
                LogUtil.e(TAG, "✓ Disconnected from ISO 8583 server");
            } catch (IOException e) {
                LogUtil.e(TAG, "Error disconnecting: " + e.getMessage());
            } finally {
                socket = null;
                inputStream = null;
                outputStream = null;
            }
        }
    }

    /**
     * Check if connected
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Close the client and release resources
     */
    public void close() {
        disconnect();
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Calculate the total length of data elements based on bitmap
     * 
     * @param bitmap Primary bitmap (8 bytes = 64 bits)
     * @return Total length of all data elements in bytes
     */
    private int calculateDataElementsLength(byte[] bitmap) {
        int totalLength = 0;

        // Parse bitmap to determine which fields are present
        for (int i = 0; i < 8; i++) {
            byte bitmapByte = bitmap[i];
            for (int j = 0; j < 8; j++) {
                int fieldNumber = i * 8 + j + 1; // Fields are 1-indexed
                boolean isPresent = ((bitmapByte >> (7 - j)) & 0x01) == 1;

                if (isPresent && fieldNumber > 1) { // Skip field 1 (secondary bitmap)
                    totalLength += getFieldLength(fieldNumber);
                }
            }
        }

        return totalLength;
    }

    /**
     * Get the length of a specific ISO 8583 field
     * Based on MsgSpec v341 field definitions
     * 
     * @param fieldNumber Field number (1-64)
     * @return Length of the field in bytes
     */
    private int getFieldLength(int fieldNumber) {
        // Common fields in response (0110):
        // DE37: RRN - 12 digits (12 bytes ASCII)
        // DE38: Auth Code - 6 digits (6 bytes ASCII)
        // DE39: Response Code - 2 digits (2 bytes ASCII)

        switch (fieldNumber) {
            case 37: // RRN (Retrieval Reference Number) - 12 digits
                return 12;
            case 38: // Auth Code (Authorization Identification Response) - 6 digits
                return 6;
            case 39: // Response Code - 2 digits
                return 2;
            default:
                // For unknown fields, return 0 (they shouldn't be in response)
                // In production, you'd have a complete field definition table
                LogUtil.e(TAG, "⚠️ Unknown field " + fieldNumber + " in bitmap - assuming 0 length");
                return 0;
        }
    }

    /**
     * Convert bytes to hex string for logging
     */
    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }
}
