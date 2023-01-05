package com.kkmcn.kbeaconlib2.KBCfgPackage;

import java.util.HashMap;

public class KBCfgSensorAcc extends KBCfgSensorBase{
    public static final String JSON_SENSOR_TYPE_ACC_MODEL = "model";

    //Acc type
    private Integer accModel;

    public KBCfgSensorAcc()
    {
        super();

        sensorType =KBSensorType.AccMotion;
    }

    public Integer getSensorType()
    {
        return sensorType;
    }

    public Integer getAccModel()
    {
        return accModel;
    }

    public int updateConfig(HashMap<String,Object> dicts)
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        obj = dicts.get(JSON_SENSOR_TYPE_ACC_MODEL);
        if (obj != null)
        {
            accModel = (Integer)obj ;
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> configDicts = super.toDictionary();

        return configDicts;
    }

}
