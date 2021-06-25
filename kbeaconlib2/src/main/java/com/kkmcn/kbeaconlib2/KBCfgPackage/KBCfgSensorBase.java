package com.kkmcn.kbeaconlib2.KBCfgPackage;

import java.util.HashMap;

public class KBCfgSensorBase extends KBCfgBase{
    public static final String JSON_SENSOR_TYPE = "srType";

    //sensor type
    protected Integer sensorType;

    public void setSensorType(Integer sensorType) {
        this.sensorType = sensorType;
    }

    public Integer getSensorType() {
        return sensorType;
    }

    public int updateConfig(HashMap<String,Object> dicts)
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        obj = dicts.get(JSON_SENSOR_TYPE);
        if (obj != null)
        {
            sensorType = (Integer) obj;
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public HashMap<String, Object> toDictionary() {
        HashMap<String, Object> configDicts = super.toDictionary();
        if (sensorType != null)
        {
            configDicts.put(JSON_SENSOR_TYPE, sensorType);
        }

        return configDicts;
    }
}
