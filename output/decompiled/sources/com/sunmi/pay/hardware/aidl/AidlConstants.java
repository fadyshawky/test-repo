package com.sunmi.pay.hardware.aidl;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants.class */
public class AidlConstants {

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$CardExistStatus.class */
    public static class CardExistStatus {
        public static final int CARD_ABSENT = 1;
        public static final int CARD_PRESENT = 2;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$CertType.class */
    public static class CertType {
        public static final int IDCARD = 536911872;
        public static final int ARMYCARD = 536911873;
        public static final int PASSPORT = 536911874;
        public static final int ARRIVALCARD = 536911875;
        public static final int TEMPIDCARD = 536911876;
        public static final int OTHERCARD = 536911877;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV.class */
    public static class EMV {
        public static final int FORCE_ONLINE = 0;
        public static final int NO_ONLINE = 1;
        public static final int EXIST_ALL_NOT = -1;
        public static final int EXIST_ALL = 0;
        public static final int EXIST_CAPK_NOT = 1;
        public static final int EXIST_AID_NOT = 2;
        public static final int EMV_RESULT_FINISHED = 36864;
        public static final int EMV_RESULT_TERMINATION = 36865;
        public static final int EMV_ERROR_PINBLOCK = 36866;
        public static final int EMV_UNSUPPORTED_TRANS = 36867;

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$AID.class */
        public static class AID {
            public static final int ACTION_AID_ADD = 0;
            public static final int ACTION_AID_DEL = 1;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$CAPK.class */
        public static class CAPK {
            public static final int ACTION_CAPK_ADD = 0;
            public static final int ACTION_CAPK_DEL = 1;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$ClearDataOpCode.class */
        public static class ClearDataOpCode {
            public static final int OP_CLEAR_DATA_ALL = 0;
            public static final int OP_CLEAR_DATA_TERMINAL = 1;
            public static final int OP_CLEAR_DATA_CARD = 2;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$FlowType.class */
        public static class FlowType {
            public static final int TYPE_EMV_STANDARD = 1;
            public static final int TYPE_EMV_BRIEF = 2;
            public static final int TYPE_NFC_SKIP_CVM = 3;
            public static final int TYPE_NFC_SPEEDUP = 4;
            public static final int TYPE_NFC_SPEEDUP_FULL = 5;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$KernelType.class */
        public static class KernelType {
            public static final int EMV = 0;
            public static final int QPBOC = 1;
            public static final int PAYPASS = 2;
            public static final int PAYWAVE = 3;
            public static final int AE = 4;
            public static final int DISCOVER = 5;
            public static final int JCB = 6;
            public static final int FLASH = 7;
            public static final int MIR = 8;
            public static final int MCCS = 9;
            public static final int RUPAY = 10;
            public static final int PAGO = 11;
            public static final int EFTPOS = 12;
            public static final int SAMSUNGPAY = 13;
            public static final int CPACE = 15;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$OnlineResult.class */
        public static class OnlineResult {
            public static final int ONLINE_APPROVAL = 0;
            public static final int ONLINE_DECLINE = 1;
            public static final int ONLINE_FAIL = 2;
            public static final int ONLINE_APPROVAL_2_TAP = 3;
            public static final int ONLINE_DECLINE_2_TAP = 4;
            public static final int ONLINE_FAIL_2_TAP = 5;
            public static final int OFFLINE_DECLINE = 6;
            public static final int ONLINE_FAIL_FULL_ONLINE = 7;
            public static final int ONLINE_SUCCESS_NO_SCRIPT = 8;
            public static final int ONLINE_FAIL_ONLINEPIN = 9;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$ParamType.class */
        public static class ParamType {
            public static final int DEFAULT = 0;
            public static final int CONTACT = 1;
            public static final int CONTACTLESS = 2;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$PinPad.class */
        public static class PinPad {
            public static final int EMV_PINPAD_TIMEOUT = 6001;
            public static final int EMV_PINPAD_CANCEL = 6002;
            public static final int EMV_PINPAD_CONNFIRM = 6003;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$TLVOpCode.class */
        public static class TLVOpCode {
            public static final int OP_NORMAL = 0;
            public static final int OP_PAYPASS = 1;
            public static final int OP_PAYWAVE = 2;
            public static final int OP_MIR = 3;
            public static final int OP_PAGO = 4;
            public static final int OP_JCB = 5;
            public static final int OP_PURE = 6;
            public static final int OP_AE = 7;
            public static final int OP_FLASH = 8;
            public static final int OP_DPAS = 9;
            public static final int OP_RUPAY = 10;
            public static final int OP_EFTPOS = 11;
            public static final int OP_CT_DPAS = 12;
            public static final int OP_CPACE = 13;
            public static final int OP_AID_RELEVANT = 101;
            public static final int OP_ADD_SELF_DEFINE_TAG = 102;
            public static final int OP_DEL_SELF_DEFINE_TAG = 103;
            public static final int OP_ENCRYPT_PAN = 104;
            public static final int OP_SET_TAG_EMPTY = 105;
            public static final int OP_SET_TAG_NOT_PRESENT = 106;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$EMV$TransResult.class */
        public static class TransResult {
            public static final int SUCCESS = 0;
            public static final int OFFLINE_APPROVAL = 1;
            public static final int OFFLINE_DECLINE = 2;
            public static final int RESERVE = 3;
            public static final int TRY_AGAIN = 4;
            public static final int ONLINE_APPROVAL = 5;
            public static final int ONLINE_DECLINE = 6;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$KBBeepMode.class */
    public static class KBBeepMode {
        public static final String MODE_ON = "ON";
        public static final String MODE_OFF = "OFF";
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$LedLight.class */
    public static class LedLight {
        public static final int RED_LIGHT = 1;
        public static final int GREEN_LIGHT = 2;
        public static final int YELLOW_LIGHT = 3;
        public static final int BLUE_LIGHT = 4;
        public static final int WHITE_LIGHT = 5;
        public static final int CORNER_RED_LIGHT = 6;
        public static final int CORNER_GREEN_LIGHT = 7;
        public static final int CORNER_BLUE_LIGHT = 8;
        public static final int INDICATOR_YELLOW_LIGHT = 9;
        public static final int CHARGE_RED_LIGHT = 10;
        public static final int CHARGE_GREEN_LIGHT = 11;
        public static final int CHARGE_BLUE_LIGHT = 12;
        public static final int CORNER_WHITE_LIGHT = 13;
        public static final int CAMERA_LIGHT = 14;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$PinBlockFormat.class */
    public static class PinBlockFormat {
        public static final int SEC_PIN_BLK_ISO_FMT0 = 0;
        public static final int SEC_PIN_BLK_ISO_FMT1 = 1;
        public static final int SEC_PIN_BLK_ISO_FMT2 = 2;
        public static final int SEC_PIN_BLK_ISO_FMT3 = 3;
        public static final int SEC_PIN_BLK_EPS = 4;
        public static final int SEC_PIN_BLK_IBM_3621 = 5;
        public static final int SEC_PIN_BLK_IBM_3624 = 6;
        public static final int SEC_PIN_BLK_ISO_FMT4 = 7;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$PinPadMode.class */
    public static class PinPadMode {
        public static final String MODE_NORMAL = "Normal";
        public static final String MODE_MEITUAN = "MeiTuan";
        public static final String MODE_SILENT = "Silent";
        public static final String MODE_LEDOFF = "LedOff";
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$PowerManage.class */
    public static class PowerManage {
        public static final int SYS_POWER_SLEEP = 1;
        public static final int SYS_SHUTDOWN = 2;
        public static final int SYS_REBOOT = 3;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$PrinterStatus.class */
    public static class PrinterStatus {
        public static final int IDLE = 1;
        public static final int PRINTING = 2;
        public static final int PAPERLESS = 3;
        public static final int OVERTEMPERATURE = 4;
        public static final int LOW_BATTERY_VOLTAGE = 5;
        public static final int PRI_CAP_OPEN = 6;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$ReadCard.class */
    public static class ReadCard {
        public static final int ERR_TRACK_SUCCESS = 0;
        public static final int ERR_TRACK_NO_DATA = -1;
        public static final int ERR_TRACK_PARITY_CHECK = -2;
        public static final int ERR_TRACK_LRC_CHECK = -3;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$Security.class */
    public static class Security {
        public static final int KEY_TYPE_KEK = 1;
        public static final int KEY_TYPE_TMK = 2;
        public static final int KEY_TYPE_PIK = 3;
        public static final int KEY_TYPE_MAK = 4;
        public static final int KEY_TYPE_TDK = 5;
        public static final int KEY_TYPE_REC = 6;
        public static final int KEY_TYPE_DUPKT_BDK = 7;
        public static final int KEY_TYPE_DUPKT_IPEK = 8;
        public static final int KEY_TYPE_KBPK = 9;
        public static final int KEY_TYPE_TADK = 10;
        public static final int KEY_TYPE_RSA_KPK = 11;
        public static final int KEY_TYPE_RSA_KEK = 12;
        public static final int KEY_ALG_TYPE_3DES = 1;
        public static final int KEY_ALG_TYPE_AES = 2;
        public static final int KEY_ALG_TYPE_SM4 = 3;
        public static final int MAC_ALG_ISO_9797_1_MAC_ALG1 = 1001;
        public static final int MAC_ALG_ISO_9797_1_MAC_ALG3 = 1003;
        public static final int MAC_ALG_ISO_16609_MAC_ALG1 = 2000;
        public static final int MAC_ALG_FAST_MODE = 3000;
        public static final int MAC_ALG_X9_19 = 3001;
        public static final int MAC_ALG_CBC = 3002;
        public static final int MAC_ALG_CUP_SM4_MAC_ALG1 = 3003;
        public static final int MAC_ALG_CUP_SM4_MAC_ALG2 = 3004;
        public static final int MAC_ALG_X9_19_DEA = 3005;
        public static final int MAC_ALG_HMAC_SHA1 = 3006;
        public static final int MAC_ALG_HMAC_SHA256 = 3007;
        public static final int MAC_ALG_CMAC = 3008;
        public static final int MAC_ALG_FAST_MODE_INTERNATIONAL = 30000;
        public static final int MAC_ALG_CBC_INTERNATIONAL = 30001;
        public static final int SEC_STATUS_MONITOR_ZERO = 1;
        public static final int SEC_STATUS_MONITOR_ONE = 2;
        public static final int SEC_STATUS_MONITOR_TWO = 4;
        public static final int SEC_STATUS_MONITOR_THREE = 8;
        public static final int AUTH_TYPE_DEBUGMODE = 1;
        public static final String DEBUGMODE_YES = "Yes";
        public static final String DEBUGMODE_NO = "No";
        public static final int SEC_CTRL_GETKCV = 0;
        public static final int SEC_CTRL_DUKPT_ADD_KSN = 1;
        public static final int SEC_CTRL_DUKPT_GET_KSN = 2;
        public static final int SEC_CTRL_ERASE_KEY = 3;
        public static final int SEC_MKSK = 0;
        public static final int SEC_DUKPT = 1;
        public static final int SEC_RSA_KEY = 2;
        public static final int SEC_SM2_KEY = 3;
        public static final int SEC_ECC_KEY = 4;
        public static final int SEC_DEVICE_CERT = 5;
        public static final int SEC_MKSK_NOLOST = 6;
        public static final int SEC_RSA_KEY_NOLOST = 7;
        public static final int SEC_ECC_KEY_NOLOST = 8;
        public static final int SEC_CERT_NOLOST = 9;
        public static final int SEC_SM2_KEY_NOLOST = 10;
        public static final int DATA_MODE_ECB = 0;
        public static final int DATA_MODE_CBC = 1;
        public static final int DATA_MODE_OFB = 2;
        public static final int DATA_MODE_CFB = 3;
        public static final int DUKPT_KEY_SELECT_KEY_PIN = 0;
        public static final int DUKPT_KEY_SELECT_KEY_MAC_BOTH = 1;
        public static final int DUKPT_KEY_SELECT_KEY_MAC_RSP = 2;
        public static final int DUKPT_KEY_SELECT_KEY_DATA_BOTH = 3;
        public static final int DUKPT_KEY_SELECT_KEY_DATA_RSP = 4;
        public static final int DUKPT_KEY_SELECT_KEY_MAC_GEN = 5;
        public static final int DUKPT_KEY_SELECT_KEY_DATA_ENC = 6;
        public static final int DUKPT_KEY_SELECT_KEY_KEY_ENC_KEY = 7;
        public static final int DUKPT_KEY_SELECT_KEY_DERIVATION = 8;
        public static final int DUKPT_KEY_SELECT_KEY_DERIVATION_INIT = 9;
        public static final int DUKPT_KEY_TYPE_2TDEA = 1;
        public static final int DUKPT_KEY_TYPE_3TDEA = 2;
        public static final int DUKPT_KEY_TYPE_AES128 = 3;
        public static final int DUKPT_KEY_TYPE_AES192 = 4;
        public static final int DUKPT_KEY_TYPE_AES256 = 5;
        public static final int HASH_SHA_TYPE_1 = 0;
        public static final int HASH_SHA_TYPE_224 = 1;
        public static final int HASH_SHA_TYPE_256 = 2;
        public static final int HASH_SHA_TYPE_384 = 3;
        public static final int HASH_SHA_TYPE_512 = 4;
        public static final int HASH_SM3_TYPE = 5;
        public static final String RSA_TRANSFORMATION_1 = "RSA/None/NoPadding";
        public static final String RSA_TRANSFORMATION_2 = "RSA/None/PKCS1Padding";
        public static final String RSA_TRANSFORMATION_3 = "RSA/ECB/NoPadding";
        public static final String RSA_TRANSFORMATION_4 = "RSA/ECB/PKCS1Padding";
        public static final String RSA_TRANSFORMATION_5 = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
        public static final String RSA_TRANSFORMATION_6 = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
        public static final String RSA_TRANSFORMATION_7 = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
        public static final String RSA_SIGN_ALG_1 = "NONEwithRSA";
        public static final String RSA_SIGN_ALG_2 = "MD5withRSA";
        public static final String RSA_SIGN_ALG_3 = "SHA1withRSA";
        public static final String RSA_SIGN_ALG_4 = "SHA256withRSA";
        public static final String RSA_SIGN_ALG_5 = "SHA512withRSA";
        public static final int SEC_ECDH_STEP1_MODE = 2;
        public static final int SEC_ECDH_STEP2_MODE = 3;
        public static final int SEC_OAEP_MODE = 5;
        public static final String SEC_ECC_PARAM_P256 = "P-256";
        public static final String SEC_ECC_PARAM_P384 = "P-384";
        public static final String SEC_ECC_PARAM_P521 = "P-521";
        public static final int ECC_KEYTYPE_PK = 0;
        public static final int ECC_KEYTYPE_SK = 1;
        public static final int KCV_MODE_NOCHK = 0;
        public static final int KCV_MODE_CHK0 = 1;
        public static final int KCV_MODE_CHKFIX = 2;
        public static final int KCV_MODE_CHKMAC = 3;
        public static final int KCV_MODE_CHKCMAC = 4;
        public static final int KCV_MODE_CHKFIX_16 = 5;
        public static final int KCV_MODE_CHK_BUF = 6;
        public static final int KCV_MODE_CHKCMAC_BUF = 7;
        public static final int KEY_CTRL_PANPARA = 65;
        public static final int KEY_CTRL_AUTHPARA = 66;
        public static final int KEY_CTRL_APACSMAC = 67;
        public static final int KEY_VARIANT_XOR = 57345;
        public static final int NOTHING_PADDING = 0;
        public static final int PKCS1_PADDING = 1;
        public static final int PKCS7_PADDING = 2;
        public static final int PKCS5_PADDING = 3;
        public static final int PKCS1_OAEP_PADDING = 4;
        public static final int PKCS1_V1_5_SHA512 = 5;
        public static final int PADDING_OAEP_SHA1 = 6;
        public static final int INJECT_OAEP_MODE = 5;
        public static final int INJECT_PKCS1_MODE = 6;
        public static final int INJECT_DERIVER_OWF2 = 128;
        public static final int INJECT_DERIVER_OWF3 = 129;
        public static final int INJECT_DERIVER_GOWF = 130;
        public static final int INJECT_DERIVER_ENC = 131;
        public static final int CERT_GENERATE_RSA2048_E65537_PVK_PUK = 130;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$SysParam.class */
    public static class SysParam {
        public static final String BASE_VERSION = "BASE_VER";
        public static final String MSR2_FW_VER = "MSR2_FW_VER";
        public static final String HARDWARE_VERSION = "HardwareVersion";
        public static final String FIRMWARE_VERSION = "FirmwareVersion";
        public static final String SM_VERSION = "SMVersion";
        public static final String ETC_FIRM_VERSION = "ETCFirmVersion";
        public static final String BootVersion = "BootVersion";
        public static final String CFG_FILE_VERSION = "CfgFileVersion";
        public static final String FW_VERSION = "FWVersion";
        public static final String SN = "SN";
        public static final String CSN = "CSN";
        public static final String PN = "PN";
        public static final String TUSN = "TUSN";
        public static final String DEVICE_CODE = "DeviceCode";
        public static final String DEVICE_MODEL = "DeviceModel";
        public static final String RESERVED = "Reserved";
        public static final String PINPAD_MODE = "PinPadMode";
        public static final String PCD_PARAM_A = "PCD_PARAM_A";
        public static final String PCD_PARAM_B = "PCD_PARAM_B";
        public static final String PCD_PARAM_C = "PCD_PARAM_C";
        public static final String PUSH_CFG_FILE = "PushCfgFile";
        public static final String SUPPORT_ETC = "SupportETC";
        public static final String TUSN_KEY_KCV = "TusnKeyKcv";
        public static final String SEC_MODE = "SecMode";
        public static final String PCD_IFM_VERSION = "PCD_IFMVersion";
        public static final String KB_BEEP_MODE = "KBBeepMode";
        public static final String SAM_COUNT = "SAM";
        public static final String SM_TYPE = "SMTYPE";
        public static final String FLASH_SIZE = "FLASH";
        public static final String CARD_HW = "CARD_HW";
        public static final String NFC_CONFIG = "NFC";
        public static final String NFC_FW_VER = "NFC_FW_VER";
        public static final String IFM_LIB_VERSION = "IfmLibVersion";
        public static final String MSR_VERSION = "MsrVersion";
        public static final String POSAPI_VERSION = "posapiVersion";
        public static final String RTC_BAT_VOL_DET = "RTCBATVOLDET";
        public static final String SRED = "sred";
        public static final String PCI_PTS_VERSION = "PCIPTSVersion";
        public static final String RNIB_VERSION = "RNIBVersion";
        public static final String KB_BRIGHTNESS = "KBBrightness";
        public static final String DEV_FINGER_STATUS = "devfingerstatus";
        public static final String SELF_CHECK_STATUS = "SelfCheckStatus";
        public static final String EMV_VERSION = "EMVVersion";
        public static final String PAYPASS_VERSION = "PaypassVersion";
        public static final String PAYWAVE_VERSION = "PaywaveVersion";
        public static final String QPBOC_VERSION = "QPBOCVersion";
        public static final String ENTRY_VERSION = "EntryVersion";
        public static final String MIR_VERSION = "MirVersion";
        public static final String JCB_VERSION = "JCBVersion";
        public static final String PAGO_VERSION = "PAGOVersion";
        public static final String PURE_VERSION = "PUREVersion";
        public static final String AE_VERSION = "AEVersion";
        public static final String FLASH_VERSION = "FLASHVersion";
        public static final String DPAS_VERSION = "DPASVersion";
        public static final String APEMV_VERSION = "APEMVVersion";
        public static final String EFTPOS_VERSION = "EFTPOSVersion";
        public static final String EMVBASE_VERSION = "EMVBaseVersion";
        public static final String KD_VERSION = "KDVersion";
        public static final String RUPAY_VERSION = "RUPAYVersion";
        public static final String SAMSUNGPAY_VERSION = "SAMSUNGPAYVersion";
        public static final String CPACE_VERSION = "CPACEVersion";
        public static final String EMV_KERNEL_CHECKSUM = "EmvKernelCheckSum";
        public static final String PURE_RELEASE_DATE = "PUREReleaseDate";
        public static final String EFTPOS_RELEASE_DATE = "EFTPOSReleaseDate";
        public static final String EMV_RELEASE_DATE = "EMVReleaseDate";
        public static final String PAYPASS_RELEASE_DATE = "PaypassReleaseDate";
        public static final String PAYWAVE_RELEASE_DATE = "PaywaveReleaseDate";
        public static final String QPBOC_RELEASE_DATE = "QPBOCReleaseDate";
        public static final String ENTRY_RELEASE_DATE = "EntryReleaseDate";
        public static final String MIR_RELEASE_DATE = "MirReleaseDate";
        public static final String JCB_RELEASE_DATE = "JCBReleaseDate";
        public static final String PAGO_RELEASE_DATE = "PAGOReleaseDate";
        public static final String AE_RELEASE_DATE = "AEReleaseDate";
        public static final String FLASH_RELEASE_DATE = "FLASHReleaseDate";
        public static final String DPAS_RELEASE_DATE = "DPASReleaseDate";
        public static final String EMVBASE_RELEASE_DATE = "EMVBaseReleaseDate";
        public static final String KD_RELEASE_DATE = "KDReleaseDate";
        public static final String RUPAY_RELEASE_DATE = "RUPAYReleaseDate";
        public static final String SAMSUNGPAY_RELEASE_DATE = "SAMSUNGPAYReleaseDate";
        public static final String CPACE_RELEASE_DATE = "CPACEReleaseDate";
        public static final String TAMPER_LOG = "TamperLog";
        public static final String CLEAR_TAMPER_LOG = "ClearTamperLog";
        public static final String TERM_STATUS = "TermStatus";
        public static final String CLEAR_TAMPER = "ClearTamper";
        public static final String DEBUG_MODE = "DebugMode";
        public static final String EMV_MASK = "EmvMask";
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$SystemUI.class */
    public static class SystemUI {
        public static final int SET_SCREEN_MONOPOLY = 1;
        public static final int CLEAR_SCREEN_MONOPOLY = -1;
        public static final int DISABLE_STATUS_BAR_DROP_DOWN = 1;
        public static final int ENABLE_STATUS_BAR_DROP_DOWN = 0;
        public static final int HIDE_NAV_BAR = 0;
        public static final int SHOW_NAV_BAR = 1;
        public static final int HIDE_NAV_ITEM_BACK_KEY = 4194304;
        public static final int HIDE_NAV_ITEM_HOME_KEY = 2097152;
        public static final int HIDE_NAV_ITEM_RECENT_KEY = 16777216;
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/AidlConstants$CardType.class */
    public enum CardType {
        MAGNETIC(1, "MAG"),
        NFC(4, "SCCL0"),
        IC(2, "SCC0"),
        PSAM0(16, "SAM0"),
        SAM1(64, "SAM1"),
        MIFARE(8, "Mifare"),
        FELICA(32, "Felica"),
        MIFARE_PLUS(Security.INJECT_DERIVER_OWF2, "MPLUS"),
        MIFARE_DESFIRE(256, "Desfire"),
        AT24C01(512, "24C01"),
        AT24C02(1024, "24C02"),
        AT24C04(2048, "24C04"),
        AT24C08(4096, "24C08"),
        AT24C16(8192, "24C16"),
        AT24C32(16384, "24C32"),
        AT24C64(32768, "24C64"),
        AT24C128(65536, "24C128"),
        AT24C256(131072, "24C256"),
        AT24C512(262144, "24C512"),
        SLE4442(524288, "4442"),
        SLE4428(1048576, "4428"),
        AT88SC1608(SystemUI.HIDE_NAV_ITEM_HOME_KEY, "1608"),
        CTX512B(SystemUI.HIDE_NAV_ITEM_BACK_KEY, "CTX512"),
        SAM2(8388608, "SAM2"),
        SAM3(SystemUI.HIDE_NAV_ITEM_RECENT_KEY, "SAM3"),
        SRI(33554432, "SRI"),
        SAM4(67108864, "SAM4"),
        SAM5(134217728, "SAM5"),
        ISO15693(268435456, "15693"),
        INNOVATRON(536870912, "INNOVATRON");

        private final int value;
        private final String deviceId;

        CardType(int value, String text) {
            this.value = value;
            this.deviceId = text;
        }

        public int getValue() {
            return this.value;
        }

        public String getMessage() {
            return this.deviceId;
        }

        public static String getDeviceId(int value) {
            for (CardType card : values()) {
                if (card.value == value) {
                    return card.deviceId;
                }
            }
            return "";
        }
    }
}
