package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import java.util.ArrayList;

public class KBRecordDataRsp extends Object
{
    public static final long INVALID_DATA_RECORD_POS = 4294967295L;

    public Long readDataNextPos;

    public ArrayList<KBRecordBase> readDataRspList;

    public int sensorType;

    public KBRecordDataRsp()
    {
        readDataNextPos = INVALID_DATA_RECORD_POS;
    }
}

