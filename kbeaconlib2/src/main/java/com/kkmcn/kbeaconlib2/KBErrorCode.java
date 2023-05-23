package com.kkmcn.kbeaconlib2;

public class KBErrorCode
{
    public static final int CfgBusy = 0x1;
    public static final int CfgFailed = 0x2;
    public static final int CfgTimeout = 0x3;
    public static final int CfgInputInvalid = 0x4;
    public static final int CfgReadNull = 0x5;
    public static final int CfgStateError = 0x6;
    public static final int CfgNotSupport = 0x8;
    public static final int CfgJSONError = 0x9;
    public static final int CfgParseSensorMsgFailed = 0x10;

    public static final int ParseSensorInfoResponseFailed = 0x501;
    public static final int ParseSensorDataResponseFailed = 0x502;

    public static final int CfgSubErrorAuthNotSupport = 0x102;

    //the input parameters was invalid
    public static final int CfgSubErrorInputParaInvalid = 0x103;

    //the device does not support this feature
    public static final int CfgSubErrorFeatureUnSupport = 0x104;

    //parse json message failed
    public static final int CfgSubErrorParseJsonFail = 0x105;

    //Some required parameters do not exist
    public static final int CfgSubErrorParaNotExist = 0x106;

    //Command execute failed
    public static final int CfgSubErrorCmdExeFailed = 0x107;

    //the advertisement slot triggered by Trigger does not exist
    public static final int CfgSubErrorSlotParaNotExist = 0x108;

    //the advertisement slot was used by trigger, and not allowed remove,
    //please remove the trigger first
    public static final int CfgSubErrorSlotUsedByTrigger = 0x109;

    //This type advertisement can only be single instance
    public static final int CfgSubErrorAdvTypeDuplicate = 0x110;

    //the trigger was already enable
    public static final int CfgSubErrorTriggerTypeDuplicate = 0x111;

    //the request record No does not exist
    public static final int CfgSubErrorRecordNotExist = 0x131;

    //enable sensor failed
    public static final int CfgSubErrorEnableSensorFailed = 0x135;
}
