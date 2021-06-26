package com.kkmcn.kbeaconlib2;

public class KBException extends Exception {
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