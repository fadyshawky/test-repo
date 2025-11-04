package com.neo.neopayplus.emv;

import android.text.TextUtils;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.tuple.Tuple;
import com.neo.neopayplus.tuple.TupleUtil;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TLV Utility Class
 * 
 * Unified EMV TLV parser with amount extraction support.
 * This is the single source of truth for EMV TLV parsing in the POS.
 */
public final class TLVUtil {

    private TLVUtil() {
        throw new AssertionError("Create instance of TLVUtil is prohibited");
    }

    /**
     * 将16进制字符串转换为TLV对象列表
     *
     * @param hexStr Hex格式的TLV数据
     * @return TLV数据List
     */
    public static List<TLV> buildTLVList(String hexStr) {
        List<TLV> list = new ArrayList<>();
        int position = 0;

        while (position != hexStr.length()) {
            Tuple<String, Integer> tupleTag = getTag(hexStr, position);
            if (TextUtils.isEmpty(tupleTag.a) || "00".equals(tupleTag.a)) {
                break;
            }
            Tuple<Integer, Integer> tupleLen = getLength(hexStr, tupleTag.b);
            Tuple<String, Integer> tupleValue = getValue(hexStr, tupleLen.b, tupleLen.a);
//            Log.e("TLV-buildTLVList", tupleTag.a + ":" + tupleValue.a);
            list.add(new TLV(tupleTag.a, tupleLen.a, tupleValue.a));
            position = tupleValue.b;
        }
        return list;
    }

    /**
     * 将16进制字符串转换为TLV对象MAP<br/>
     * TLV文档连接参照：http://wenku.baidu.com/view/b31b26a13186bceb18e8bb53.html?re=view&qq-pf-to=pcqq.c2c
     *
     * @param hexStr Hex格式TLV数据
     * @return TLV数据Map
     */
    public static Map<String, TLV> buildTLVMap(String hexStr) {
        Map<String, TLV> map = new LinkedHashMap<>();
        if (TextUtils.isEmpty(hexStr) || hexStr.length() % 2 != 0) return map;
        int position = 0;
        while (position < hexStr.length()) {
            Tuple<String, Integer> tupleTag = getTag(hexStr, position);
            if (TextUtils.isEmpty(tupleTag.a) || "00".equals(tupleTag.a)) {
                break;
            }
            Tuple<Integer, Integer> tupleLen = getLength(hexStr, tupleTag.b);
            Tuple<String, Integer> tupleValue = getValue(hexStr, tupleLen.b, tupleLen.a);
//            Log.e("TLV-buildTLVMap", tupleTag.a + ":" + tupleValue.a);
            map.put(tupleTag.a, new TLV(tupleTag.a, tupleLen.a, tupleValue.a));
            position = tupleValue.b;
        }
        return map;
    }

    /**
     * 将字节数组转换为TLV对象列表
     *
     * @param hexByte byte数据格式的TLV数据
     * @return TLV数据List
     */
    public static List<TLV> buildTLVList(byte[] hexByte) {
        String hexString = ByteUtil.bytes2HexStr(hexByte);
        return buildTLVList(hexString);
    }

    /**
     * 将字节数组转换为TLV对象MAP
     *
     * @param hexByte byte数据格式的TLV数据
     * @return TLV数据Map
     */
    public static Map<String, TLV> buildTLVMap(byte[] hexByte) {
        String hexString = ByteUtil.bytes2HexStr(hexByte);
        return buildTLVMap(hexString);
    }

    /**
     * 获取Tag及更新后的游标位置
     */
    private static Tuple<String, Integer> getTag(String hexString, int position) {
        String tag = "";
        try {
            String byte1 = hexString.substring(position, position + 2);
            String byte2 = hexString.substring(position + 2, position + 4);
            int b1 = Integer.parseInt(byte1, 16);
            int b2 = Integer.parseInt(byte2, 16);
            // b5~b1如果全为1，则说明这个tag下面还有一个子字节，PBOC/EMV里的tag最多占两个字节
            if ((b1 & 0x1F) == 0x1F) {
                // 除tag标签首字节外，tag中其他字节最高位为：1-表示后续还有字节；0-表示为最后一个字节。
                if ((b2 & 0x80) == 0x80) {
                    tag = hexString.substring(position, position + 6);// 3Bytes的tag
                } else {
                    tag = hexString.substring(position, position + 4);// 2Bytes的tag
                }
            } else {
                tag = hexString.substring(position, position + 2);// 1Bytes的tag
            }
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "TLVUtil", e);
        }
        return TupleUtil.tuple(tag.toUpperCase(), position + tag.length());
    }

    /**
     * 获取Length及游标更新后的游标位置
     */
    private static Tuple<Integer, Integer> getLength(final String hexStr, final int position) {
        int index = position;
        String hexLen = hexStr.substring(index, index + 2);
        index += 2;
        int byte1 = Integer.parseInt(hexLen, 16);
        // Length域的编码比较简单,最多有四个字节, 
        // 如果第一个字节的最高位b8为0, b7~b1的值就是value域的长度. 
        // 如果b8为1, b7~b1的值指示了下面有几个子字节. 下面子字节的值就是value域的长度.
        if ((byte1 & 0x80) != 0) {// 最左侧的bit位为1
            int subLen = byte1 & 0x7F;
            hexLen = hexStr.substring(index, index + subLen * 2);
            index += subLen * 2;
        }
        return TupleUtil.tuple(Integer.parseInt(hexLen, 16), index);
    }

    /**
     * 获取Value及游标更新后的游标位置
     */
    private static Tuple<String, Integer> getValue(final String hexStr, final int position, final int len) {
        String value = "";
        try {
            value = hexStr.substring(position, position + len * 2);
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "TLVUtil", e);
        }
        return TupleUtil.tuple(value.toUpperCase(), position + len * 2);
    }

    /***
     * 将TLV转换成16进制字符串
     */
    public static String revertToHexStr(TLV tlv) {
        StringBuilder sb = new StringBuilder();
        sb.append(tlv.getTag());
        sb.append(TLVValueLengthToHexString(tlv.getLength()));
        sb.append(tlv.getValue());
        return sb.toString();
    }

    /**
     * 将TLV数据反转成字节数组
     */
    public static byte[] revertToBytes(TLV tlv) {
        String hex = revertToHexStr(tlv);
        return ByteUtil.hexStr2Bytes(hex);
    }

    /**
     * 将TLV中数据长度转化成16进制字符串
     */
    public static String TLVValueLengthToHexString(int length) {
        if (length < 0) {
            throw new RuntimeException("不符要求的长度");
        }
        if (length <= 0x7f) {
            return String.format("%02x", length);
        } else if (length <= 0xff) {
            return "81" + String.format("%02x", length);
        } else if (length <= 0xffff) {
            return "82" + String.format("%04x", length);
        } else if (length <= 0xffffff) {
            return "83" + String.format("%06x", length);
        } else {
            throw new RuntimeException("TLV 长度最多4个字节");
        }
    }
    
    // ==================== EMV AMOUNT PARSING ====================
    
    /**
     * EMV tag for Amount, Authorised (9F02)
     */
    private static final String TAG_AMOUNT = "9F02";
    
    /**
     * EMV tag for Amount, Other (9F03) - used for cashback
     */
    private static final String TAG_AMOUNT_OTHER = "9F03";
    
    /**
     * EMV tag for Transaction Currency Code (5F2A)
     */
    private static final String TAG_CURRENCY_CODE = "5F2A";
    
    /**
     * Parse amount from Field 55 (9F02 tag) in MINOR currency units
     * This matches PayActivity's parseAmount9F02Minor() signature for compatibility.
     * 
     * @param tlv55Hex EMV Field 55 TLV data in hex string format
     * @return Amount in smallest currency unit (e.g., piasters for EGP), or -1 if not found
     */
    public static long parseAmount9F02Minor(String tlv55Hex) {
        if (TextUtils.isEmpty(tlv55Hex)) {
            LogUtil.e(Constant.TAG, "⚠️ Field 55 is empty - cannot extract amount");
            return -1;
        }
        
        try {
            Map<String, TLV> tlvMap = buildTLVMap(tlv55Hex);
            TLV amountTag = tlvMap.get(TAG_AMOUNT);
            
            if (amountTag == null || TextUtils.isEmpty(amountTag.getValue())) {
                LogUtil.e(Constant.TAG, "⚠️ Tag 9F02 (Amount) not found in Field 55");
                return -1;
            }
            
            String amountHex = amountTag.getValue();
            
            // Amount in 9F02 is BCD (Binary-Coded Decimal), 6 bytes = 12 hex digits
            // Example: "000000000100" = 100 piasters = 1.00 EGP
            if (amountHex.length() < 12) {
                LogUtil.e(Constant.TAG, "⚠️ Invalid amount length in 9F02: " + amountHex.length() + " (expected 12)");
                return -1;
            }
            
            // Parse BCD: each 2 hex digits = 1 decimal digit
            // "000000000100" -> "000000000100" (12 digits) = 100 piasters
            return bcdToLong(amountHex);
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error extracting amount from Field 55: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "TLVUtil", e);
            return -1;
        }
    }
    
    /**
     * Convert BCD (Binary-Coded Decimal) hex string to long value
     * 
     * @param bcdHex BCD hex string (e.g., "000000000100" = 100)
     * @return Long value
     */
    public static long bcdToLong(String bcdHex) {
        if (TextUtils.isEmpty(bcdHex)) {
            return 0;
        }
        try {
            // BCD: each 2 hex digits represents one decimal digit
            // "000000000100" = 000000000100 = 100 (in decimal)
            return Long.parseLong(bcdHex, 16);
        } catch (NumberFormatException e) {
            LogUtil.e(Constant.TAG, "❌ Error parsing BCD: " + bcdHex);
            return -1;
        }
    }
    
    /**
     * Extract currency code (5F2A) from EMV Field 55
     * 
     * @param field55 EMV Field 55 TLV data in hex string format
     * @return Currency code as hex string (e.g., "0818" for EGP), or null if not found
     */
    public static String extractCurrencyCode(String field55) {
        if (TextUtils.isEmpty(field55)) {
            return null;
        }
        
        try {
            Map<String, TLV> tlvMap = buildTLVMap(field55);
            TLV currencyTag = tlvMap.get(TAG_CURRENCY_CODE);
            
            if (currencyTag == null || TextUtils.isEmpty(currencyTag.getValue())) {
                return null;
            }
            
            return currencyTag.getValue();
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error extracting currency code from Field 55: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract other amount (9F03) from EMV Field 55 (used for cashback)
     * 
     * @param field55 EMV Field 55 TLV data in hex string format
     * @return Amount in smallest currency unit, or -1 if not found
     */
    public static long extractOtherAmount(String field55) {
        if (TextUtils.isEmpty(field55)) {
            return -1;
        }
        
        try {
            Map<String, TLV> tlvMap = buildTLVMap(field55);
            TLV amountOtherTag = tlvMap.get(TAG_AMOUNT_OTHER);
            
            if (amountOtherTag == null || TextUtils.isEmpty(amountOtherTag.getValue())) {
                return -1; // 9F03 is optional (only present for cashback transactions)
            }
            
            String amountHex = amountOtherTag.getValue();
            if (amountHex.length() < 12) {
                return -1;
            }
            
            return bcdToLong(amountHex);
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error extracting other amount from Field 55: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Extract amount (9F02) from EMV Field 55 and convert to main currency unit
     * 
     * @param field55 EMV Field 55 TLV data in hex string format
     * @param currencyExponent Currency exponent (typically 2 for EGP, meaning 100 smallest units = 1 main unit)
     * @return Amount in main currency unit (e.g., pounds for EGP), or -1.0 if not found
     */
    public static double parseAmount9F02InMainUnit(String field55, int currencyExponent) {
        long amountInSmallestUnit = parseAmount9F02Minor(field55);
        if (amountInSmallestUnit < 0) {
            return -1.0;
        }
        
        return amountInSmallestUnit / Math.pow(10, currencyExponent);
    }
    
    /**
     * Extract amount (9F02) from EMV Field 55 and convert to main currency unit (default 2 decimal places)
     * 
     * @param field55 EMV Field 55 TLV data in hex string format
     * @return Amount in main currency unit (e.g., pounds for EGP), or -1.0 if not found
     */
    public static double parseAmount9F02InMainUnit(String field55) {
        return parseAmount9F02InMainUnit(field55, 2); // Default: 2 decimal places (e.g., EGP)
    }

}