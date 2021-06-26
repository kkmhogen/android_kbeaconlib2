package com.kkmcn.kbeaconlib2;

//connection event
public class KBConnectionEvent {
    //connect to device success
    public final static int ConnSuccess = 0;

    //connect to device timeout
    public final static int ConnTimeout = 1;

    //connection exception
    public final static int ConnException = 2;

    //the app can not identify the device
    public final static int ConnServiceNotSupport = 4;

    //manual disconnect with device
    public final static int ConnManualDisconnecting = 5;

    //password error
    public final static int ConnAuthFail= 6;
}
