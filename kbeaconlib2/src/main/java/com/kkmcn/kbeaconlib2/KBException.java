package com.kkmcn.kbeaconlib2;

public class KBException extends Exception {
    public final static int KBEvtCfgBusy = 0x1;
    public final static int KBEvtCfgFailed = 0x2;
    public final static int KBEvtCfgTimeout = 0x3;
    public final static int KBEvtCfgInputInvalid = 0x4;
    public final static int KBEvtCfgReadNull = 0x5;
    public final static int KBEvtCfgStateError = 0x6;
    public final static int KBEvtCfgNoParameters = 0x7;
    public final static int KBEvtCfgNotSupport = 0x8;

    public int errorCode;
    public int subErrorCode;

    public KBException(int nErrorCode, String strErrorDesc) {
        super(strErrorDesc);
        errorCode = nErrorCode;
        subErrorCode = 0;
    }

    public KBException(int nErrorCode, int nSubErrorCode, String strErrorDesc) {
        super(strErrorDesc);
        errorCode = nErrorCode;
        subErrorCode = 0;
        subErrorCode = nSubErrorCode;
    }
}