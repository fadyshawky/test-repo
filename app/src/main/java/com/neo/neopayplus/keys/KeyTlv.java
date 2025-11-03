package com.neo.neopayplus.keys;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Key TLV Parser
 * 
 * Parses DE62 TLV payload containing session keys (TPK/TAK) from host sign-on response.
 * 
 * TLV Tags:
 * - DF01: Key set version
 * - DF10: TPK (encrypted under TMK)
 * - DF11: TPK KCV (6 hex characters)
 * - DF12: TAK (encrypted under TMK)
 * - DF13: TAK KCV (6 hex characters)
 */
public final class KeyTlv {
    
    public static final class SessionKeys {
        public String keySetVersion; // DF01
        public byte[] tpkEnc;        // DF10 (encrypted under TMK)
        public String tpkKcv;        // DF11 (6 hex characters)
        public byte[] takEnc;        // DF12 (encrypted under TMK)
        public String takKcv;        // DF13 (6 hex characters)
    }
    
    /**
     * Parse session keys from DE62 TLV payload
     * 
     * @param de62 DE62 TLV bytes from sign-on response (0810)
     * @return Parsed session keys
     * @throws IllegalStateException if required keys are missing
     */
    public static SessionKeys parseKeySetFromDE62(byte[] de62) {
        if (de62 == null || de62.length == 0) {
            throw new IllegalStateException("DE62 is null or empty");
        }
        
        SessionKeys s = new SessionKeys();
        int i = 0;
        
        while (i < de62.length) {
            // Tag: 2 bytes (DFxx format)
            if (i + 2 > de62.length) break;
            
            int tag1 = de62[i++] & 0xFF;
            int tag2 = de62[i++] & 0xFF;
            int tag = (tag1 << 8) | tag2;
            
            // Length: 1 byte (assume compact TLV; adapt if host uses 2-byte length)
            if (i >= de62.length) break;
            int len = de62[i++] & 0xFF;
            
            // Extended length (if first byte is 0x81 = 1-byte length, 0x82 = 2-byte length)
            if (len == 0x81 && i < de62.length) {
                len = de62[i++] & 0xFF;
            } else if (len == 0x82 && i + 1 < de62.length) {
                len = ((de62[i++] & 0xFF) << 8) | (de62[i++] & 0xFF);
            }
            
            if (i + len > de62.length) break;
            
            byte[] value = Arrays.copyOfRange(de62, i, i + len);
            i += len;
            
            switch (tag) {
                case 0xDF01: // Key set version
                    s.keySetVersion = new String(value, StandardCharsets.US_ASCII);
                    break;
                case 0xDF10: // TPK (encrypted)
                    s.tpkEnc = value;
                    break;
                case 0xDF11: // TPK KCV
                    s.tpkKcv = new String(value, StandardCharsets.US_ASCII);
                    break;
                case 0xDF12: // TAK (encrypted)
                    s.takEnc = value;
                    break;
                case 0xDF13: // TAK KCV
                    s.takKcv = new String(value, StandardCharsets.US_ASCII);
                    break;
                default:
                    // Ignore unknown tags
                    break;
            }
        }
        
        // Validate required fields
        if (s.tpkEnc == null || s.takEnc == null || s.tpkKcv == null || s.takKcv == null) {
            throw new IllegalStateException("Missing required key TLVs in DE62. " +
                "Found: TPK=" + (s.tpkEnc != null) + ", TAK=" + (s.takEnc != null) +
                ", TPK_KCV=" + (s.tpkKcv != null) + ", TAK_KCV=" + (s.takKcv != null));
        }
        
        return s;
    }
}

