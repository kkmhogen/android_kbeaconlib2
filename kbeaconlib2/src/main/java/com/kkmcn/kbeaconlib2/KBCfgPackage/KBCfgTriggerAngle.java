package com.kkmcn.kbeaconlib2.KBCfgPackage;

import org.json.JSONException;
import org.json.JSONObject;

public class KBCfgTriggerAngle  extends KBCfgTrigger{

    public static final String JSON_FIELD_TRIGGER_REPEAT_PRD = "rptPrd";
    public static final String JSON_FIELD_TRIGGER_ABOVEANGLE = "aAng";
    public static final int MAX_TRIGGER_RPT_TIME = 255;
    public static final int MIN_TRIGGER_RPT_TIME = 0;
    public static final int MAX_TRIGGER_ANGLE = 90;
    public static final int MIN_TRIGGER_ANGLE = -90;

    //when beacon detects that the tilt angle is less or above then the threshold, it will trigger an event.
    //If it is not recovered after reportInterval, it will trigger event again
    //the unit is minute
    protected Integer reportInterval;

    //When the Beacon tilt angle >= aboveAngle threshold, a trigger event is sent.
    protected Integer aboveAngle;

    public Integer getReportingInterval() {
        return reportInterval;
    }

    public void setReportingInterval(Integer rptPrd) {
        this.reportInterval = rptPrd;
    }

    public Integer getAboveAngle() {
        return aboveAngle;
    }

    public void setAboveAngle(Integer aboveAngle) {
        this.aboveAngle = aboveAngle;
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateParaNum = super.updateConfig(dicts);

        if (dicts.has(JSON_FIELD_TRIGGER_REPEAT_PRD))
        {
            reportInterval = dicts.getInt(JSON_FIELD_TRIGGER_REPEAT_PRD);
            nUpdateParaNum++;
        }
        if (dicts.has(JSON_FIELD_TRIGGER_ABOVEANGLE))
        {
            aboveAngle = dicts.getInt(JSON_FIELD_TRIGGER_ABOVEANGLE);
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();

        if (reportInterval != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_REPEAT_PRD, reportInterval);
        }
        if (aboveAngle != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_ABOVEANGLE, aboveAngle);
        }

        return cfgDicts;
    }
}
