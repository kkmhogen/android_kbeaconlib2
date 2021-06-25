package com.kkmcn.kbeaconlib2;

public class KBConnPara {
    public boolean syncUtcTime;
    public boolean readCommPara;
    public boolean readSlotPara;
    public boolean readTriggerPara;
    public boolean readSensorPara;

    public KBConnPara()
    {
        syncUtcTime = true;
        readCommPara = true;
        readSlotPara = true;
        readTriggerPara = true;
        readSensorPara = false;
    }
}
