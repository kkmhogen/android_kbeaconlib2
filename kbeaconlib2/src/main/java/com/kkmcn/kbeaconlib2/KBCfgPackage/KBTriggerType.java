package com.kkmcn.kbeaconlib2.KBCfgPackage;

public class KBTriggerType {
    public static final int TriggerNull= 0;

    //motion sensor trigger
    public static final int AccMotion = 1;

    //push button trigger
    public static final int BtnLongPress = 3;   //long press
    public static final int BtnSingleClick = 4;   //single tap
    public static final int BtnDoubleClick = 5;   //double tap
    public static final int BtnTripleClick = 6;   //triple tap

    //temp and humidity trigger
    public static final int HTTempAbove = 8;   //temperature above
    public static final int HTTempBelow = 9;   //temperature below
    public static final int HTHumidityAbove = 10;   //humidity above
    public static final int HTHumidityBelow = 11;   //humidity below
    public static final int HTRealTimeReport = 12;   //report the measure data to app realtime when connected

}
