package com.kkmcn.kbeaconlib2.KBCfgPackage;

public class KBTriggerAction {
    //action option
    public static final int ActionOff = 0x0;    //disable trigger
    public static final int Advertisement = 0x1;    //start advertisement when trigger event happened
    public static final int Alert = 0x2;  //start beep led flash when trigger event happened
    public static final int Record = 0x4;
    public static final int Vibration = 0x8;
    public static final int Report2App = 0x10;
}
