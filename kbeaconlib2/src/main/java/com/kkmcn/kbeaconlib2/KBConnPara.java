package com.kkmcn.kbeaconlib2;

public class KBConnPara {
    //sync the UTC time to device while setup connection
    public boolean syncUtcTime = true;

    //read common parameters while setup connection
    public boolean readCommPara = true;

    //read slot advertisement parameters while setup connection
    public boolean readSlotPara = true;

    //read sensor trigger parameters while setup connection
    public boolean readTriggerPara = true;

    //read sensor parameters while setup connection
    public boolean readSensorPara = false;

    public KBConnPara()
    {
    }
}
