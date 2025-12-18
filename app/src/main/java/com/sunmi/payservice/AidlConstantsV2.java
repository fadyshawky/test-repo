package com.sunmi.payservice;

/**
 * Mock Sunmi SDK shim for JVM unit test compilation.
 * Real PayLib AAR provides actual implementations on device.
 */
public final class AidlConstantsV2 {

    public static final class Security {
        public static final int MAC_ALG_ISO_9797_1_MAC_ALG1 = 1;
        public static final int MAC_ALG_ISO_9797_1_MAC_ALG3 = 2;
        public static final int MAC_ALG_ISO_16609_MAC_ALG1 = 3;
        public static final int MAC_ALG_FAST_MODE = 4;
        public static final int MAC_ALG_X9_19 = 5;
        public static final int MAC_ALG_CBC = 6;
        public static final int MAC_ALG_CUP_SM4_MAC_ALG1 = 7;
        public static final int MAC_ALG_FAST_MODE_INTERNATIONAL = 8;
        public static final int MAC_ALG_CBC_INTERNATIONAL = 9;
        public static final int MAC_ALG_HMAC_SHA1 = 10;
        public static final int MAC_ALG_HMAC_SHA256 = 11;
        public static final int MAC_ALG_CMAC = 12;
        public static final int NOTHING_PADDING = 99;

        // Pin/Modes
        public static final int PIN_TYPE_ONLINE = 0;
        public static final int DATA_MODE_ECB = 0;
        public static final int DATA_MODE_CBC = 1;
        public static final int DATA_MODE_CFB = 2;
        public static final int DATA_MODE_OFB = 3;

        // Key Systems / Algos
        public static final int KEY_SYS_ZPK = 0;
        public static final int ALG_3DES = 0;

        // Key Types
        public static final int KEY_TYPE_TMK = 200;
        public static final int KEY_TYPE_PIK = 201;
        public static final int KEY_TYPE_TDK = 202;
        public static final int KEY_TYPE_MAK = 203;
        public static final int KEY_TYPE_KEK = 204;
        public static final int KEY_TYPE_REC = 205;
        public static final int KEY_TYPE_DUPKT_IPEK = 210;
        public static final int KEY_TYPE_DUPKT_BDK = 211;

        // Algorithm types
        public static final int KEY_ALG_TYPE_3DES = 300;
        public static final int KEY_ALG_TYPE_AES = 301;
        public static final int KEY_ALG_TYPE_SM4 = 302;

        // DUKPT variants
        public static final int DUKPT_KEY_TYPE_AES128 = 400;
        public static final int DUKPT_KEY_TYPE_AES192 = 401;
        public static final int DUKPT_KEY_TYPE_AES256 = 402;

        // Hash types
        public static final int HASH_SHA_TYPE_1 = 500;
        public static final int HASH_SHA_TYPE_224 = 501;
        public static final int HASH_SHA_TYPE_256 = 502;
        public static final int HASH_SHA_TYPE_384 = 503;
        public static final int HASH_SHA_TYPE_512 = 504;
        public static final int HASH_SM3_TYPE = 505;

        // Security constants
        public static final int SEC_MKSK = 600;
    }

    public static final class EMV {
        public static final class TLVOpCode {
            public static final int OP_NORMAL = 0;
            public static final int OP_PAYPASS = 1;
            public static final int OP_PAYWAVE = 2;
            public static final int OP_AE = 3;
            public static final int OP_JCB = 4;
            // Alias for OP_NORMAL
            public static final int OP_READ = 0;
        }

        public static final class FlowType {
            public static final int TYPE_EMV_STANDARD = 0x01; // 1 - Standard authorization flow
            public static final int TYPE_EMV_BRIEF = 0x02; // 2 - Simple flow, finished as soon as read card number
            public static final int TYPE_NFC_SKIP_CVM = 0x03; // 3 - Contactless skip CVM verification
            public static final int TYPE_NFC_SPEEDUP = 0x04; // 4 - Contactless speedup flow
        }

        public static final class ScriptType {
            public static final int SCRIPT_71 = 0;
            public static final int SCRIPT_72 = 1;
        }
    }

    public static final class CardType {
        // Common payments
        public static final CardType IC = new CardType(0);
        public static final CardType ICC = new CardType(0);
        public static final CardType NFC = new CardType(1);
        public static final CardType CONTACTLESS = new CardType(1);
        public static final CardType MSR = new CardType(2);
        public static final CardType MAGNETIC = new CardType(2);
        public static final CardType MIFARE = new CardType(10);
        public static final CardType FELICA = new CardType(11);
        // Memory/legacy cards
        public static final CardType AT24C01 = new CardType(101);
        public static final CardType AT24C02 = new CardType(102);
        public static final CardType AT24C04 = new CardType(103);
        public static final CardType AT24C08 = new CardType(104);
        public static final CardType AT24C16 = new CardType(105);
        public static final CardType AT24C32 = new CardType(106);
        public static final CardType AT24C64 = new CardType(107);
        public static final CardType AT24C128 = new CardType(108);
        public static final CardType AT24C256 = new CardType(109);
        public static final CardType AT24C512 = new CardType(110);
        public static final CardType AT88SC1608 = new CardType(120);
        public static final CardType SLE4442 = new CardType(130);
        public static final CardType SLE4428 = new CardType(131);
        public static final CardType CTX512B = new CardType(140);

        private final int value;

        private CardType(int v) {
            this.value = v;
        }

        public int getValue() {
            return value;
        }
    }

    public static final class CardExistStatus {
        public static final int CARD_ABSENT = 0;
        public static final int CARD_PRESENT = 1;
    }

    public static final class PinPadMode {
        public static final String MODE_NORMAL = "N";
        public static final String MODE_MEITUAN = "M";
        public static final String MODE_SILENT = "S";
    }

    public static final class PowerManage {
        public static final int SYS_SHUTDOWN = 0;
        public static final int SYS_REBOOT = 1;
    }

    public static final class SysParam {
        public static final String TAMPER_LOG = "TL";
        public static final String TERM_STATUS = "TS";
        public static final String CLEAR_TAMPER_LOG = "CTL";
        public static final String RESERVED = "RSVD"; // Reserved parameter key
        public static final String CARD_HW = "CHW"; // Card hardware status
        public static final String SAM_COUNT = "SAMC"; // SAM card count
        public static final String NFC_CONFIG = "NFCC"; // NFC configuration
    }

    public static final class SystemUI {
        // Screen mode constants
        public static final int SET_SCREEN_MONOPOLY = 0;
        public static final int CLEAR_SCREEN_MONOPOLY = 1;

        // Status bar constants
        public static final int DISABLE_STATUS_BAR_DROP_DOWN = 0;
        public static final int ENABLE_STATUS_BAR_DROP_DOWN = 1;

        // Navigation bar constants
        public static final int HIDE_NAV_BAR = 0;
        public static final int SHOW_NAV_BAR = 1;

        // Navigation bar item constants
        public static final int HIDE_NAV_ITEM_BACK_KEY = 0x01;
        public static final int HIDE_NAV_ITEM_HOME_KEY = 0x02;
        public static final int HIDE_NAV_ITEM_RECENT_KEY = 0x04;
    }

    public static final class LedLight {
        public static final int BLUE_LIGHT = 0;
        public static final int YELLOW_LIGHT = 1;
        public static final int GREEN_LIGHT = 2;
        public static final int RED_LIGHT = 3;
    }
}
