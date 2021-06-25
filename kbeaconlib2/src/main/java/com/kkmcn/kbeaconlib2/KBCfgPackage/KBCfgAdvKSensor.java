package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;

import java.util.HashMap;

public class KBCfgAdvKSensor extends KBCfgAdvBase{
    public static final String JSON_FIELD_SENSOR_HUMIDITY = "ht";
    public static final String JSON_FIELD_SENSOR_AXIS= "axis";

    private Boolean htSensorInclude;
    private Boolean axisSensorInclude;

    public KBCfgAdvKSensor()
    {
        super();
        advType = KBAdvType.Sensor;
    }

    public void setAxisSensorInclude(Boolean axisInclude) {
        this.axisSensorInclude = axisInclude;
    }

    public Boolean isAxisSensorEnable() {
        return axisSensorInclude;
    }

    public void setHtSensorInclude(Boolean htSensorInclude) {
        this.htSensorInclude = htSensorInclude;
    }

    public Boolean isHtSensorInclude() {
        return htSensorInclude;
    }

    public int updateConfig(HashMap<String,Object>dicts)
    {
        int nUpdateConfigNum = super.updateConfig(dicts);
        Object obj;

        obj = dicts.get(JSON_FIELD_SENSOR_HUMIDITY);
        if (obj != null)
        {
            htSensorInclude = ((Integer) obj > 0);
            nUpdateConfigNum++;
        }

        obj = dicts.get(JSON_FIELD_SENSOR_AXIS);
        if (obj != null)
        {
            axisSensorInclude = ((Integer) obj > 0);
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> configDicts = super.toDictionary();

        if (htSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_HUMIDITY, htSensorInclude ? 1 : 0);
        }

        if (axisSensorInclude != null)
        {
            configDicts.put(JSON_FIELD_SENSOR_AXIS, axisSensorInclude ? 1: 0);
        }

        return configDicts;
    }
}
