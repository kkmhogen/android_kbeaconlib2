package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

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

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateConfigNum = super.updateConfig(dicts);

        if (dicts.has(JSON_SENSOR_TYPE_ACC_MODEL))
        {
            accModel = (Integer)dicts.get(JSON_SENSOR_TYPE_ACC_MODEL) ;
            nUpdateConfigNum++;
        }

        return nUpdateConfigNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        return super.toJSONObject();
    }

}
