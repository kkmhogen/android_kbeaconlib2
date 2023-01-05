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

    //cutoff alarm
    public static final int Cutoff = 7;   //cut off

    //temp and humidity trigger
    public static final int HTTempAbove = 8;   //temperature above
    public static final int HTTempBelow = 9;   //temperature below
    public static final int HTHumidityAbove = 10;   //humidity above
    public static final int HTHumidityBelow = 11;   //humidity below

    public static final int HTHumidityPeriodically= 12;   //Periodically report

    //PIR sensor
    public static final int PIRBodyInfraredDetected = 13;

    //Light lux level
    public static final int LightLUXAbove = 14;   //light lux above
    public static final int LightLUXBelow = 15;   //light lux below
}
