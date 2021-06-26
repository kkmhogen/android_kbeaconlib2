package com.kkmcn.kbeaconlib2.KBCfgPackage;

public class KBAdvMode {
    //Legacy means traditional mode of Bluetooth 4.0.
    // the maximum broadcast packet size is 31 bytes.
    public final static int Legacy = 0x0;

    //The long range mode refers to the Long Range feature of Bluetooth 5.0.
    // In this mode, the distance of the advertisement will increase by 3 to 4 times.
    // the maximum broadcast packet size is 255 bytes.
    public final static int LongRange = 0x1;

    //The 2MBps mode refers to the 2MBps rate broadcast of Bluetooth 5.0. At this time,
    // the maximum broadcast packet size is 255 bytes.
    public final static int K2Mbps = 0x2;
}
