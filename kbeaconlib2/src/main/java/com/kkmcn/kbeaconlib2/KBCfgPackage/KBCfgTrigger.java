package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KBCfgTrigger extends KBCfgBase {
    //trigger adv time
    public static final int DEFAULT_TRIGGER_ADV_TIME = 30;
    public static final int MIN_TRIGGER_ADV_TIME = 2;
    public static final int MAX_TRIGGER_ADV_TIME = 7200;
    public static final float DEFAULT_TRIGGER_ADV_PERIOD = 400.0f;
    public static final int DEFAULT_TRIGGER_ADV_POWER = 0;

    //temperature sensor
    public static final int KBTriggerConditionDefaultHumidityAbove = 80;
    public static final int KBTriggerConditionDefaultHumidityBelow = 20;
    public static final int MIN_HUMIDITY_VALUE = 1;
    public static final int MAX_HUMIDITY_VALUE = 99;

    public static final int KBTriggerConditionDefaultHumidityRptInterval = 300;
    public static final int MIN_HUMIDITY_RPT_INTERVAL = 3;
    public static final int MAX_HUMIDITY_RPT_INTERVAL = 36000;

    //humidity sensor
    public static final int KBTriggerConditionDefaultTemperatureAbove = 60;
    public static final int KBTriggerConditionDefaultTemperatureBelow = -10;
    public static final int MAX_TEMPERATURE_VALUE = 1000;
    public static final int MIN_TEMPERATURE_VALUE = -50;

    //PIR  detected interval
    public static final int KBTriggerConditionPIRRepeatRptInterval = 30;
    public static final int MAX_PIR_REPORT_INTERVAL_VALUE = 3600;
    public static final int MIN_PIR_REPORT_INTERVAL_VALUE = 5;

    //Light level
    public static final int MAX_LIGHT_LEVEL_VALUE = 65535;
    public static final int MIN_LIGHT_LEVEL_VALUE = 1;

    //motion
    public static final int DEFAULT_MOTION_SENSITIVITY = 0x2;   //default motion sensitive
    public static final int MAX_MOTION_SENSITIVITY = 126;
    public static final int MIN_MOTION_SENSITIVITY = 1;

    public static final String JSON_FIELD_TRIGGER_INDEX = "trIdx";
    public static final String JSON_FIELD_TRIGGER_TYPE = "trType";
    public static final String JSON_FIELD_TRIGGER_ACTION = "trAct";
    public static final String JSON_FIELD_TRIGGER_PARA = "trPara";
    public static final String JSON_FIELD_TRIGGER_ADV_CHANGE_MODE = "trAChg";
    public static final String JSON_FIELD_TRIGGER_ADV_SLOT = "slot";
    public static final String JSON_FIELD_TRIGGER_ADV_TIME = "trATm";
    public static final String JSON_FIELD_TRIGGER_ADV_PERIOD = "trAPrd";
    public static final String JSON_FIELD_TRIGGER_ADV_POWER = "trAPwr";

    protected Integer triggerIndex;

    protected Integer triggerType;

    //trigger action, advertise, report app or alert
    protected Integer triggerAction;

    //trigger para
    protected Integer triggerPara;

    //trigger advMode
    protected Integer triggerAdvChangeMode;

    //trigger advertise slot
    protected Integer triggerAdvSlot;

    //trigger advertise time
    protected Integer triggerAdvTime;

    //trigger advertise period
    protected Float triggerAdvPeriod;

    //trigger advertise tx power
    protected Integer triggerAdvTxPower;

    public KBCfgTrigger()
    {
        triggerType = KBTriggerType.TriggerNull;
        triggerIndex = 0;
    }

    public KBCfgTrigger(int nTriggerIndex, int nTriggerType)
    {
        triggerIndex = nTriggerIndex;
        triggerType = nTriggerType;
    }


    public Integer getTriggerIndex(){return triggerIndex;}

    public Integer getTriggerType()
    {
        return triggerType;
    }

    public Integer getTriggerAction()
    {
        return triggerAction;
    }

    public Integer getTriggerAdvChgMode(){return triggerAdvChangeMode;}

    public Integer getTriggerAdvSlot()
    {
        return triggerAdvSlot;
    }

    public Integer getTriggerAdvTime()
    {
        return triggerAdvTime;
    }

    public Integer getTriggerPara() {
        return triggerPara;
    }

    public Float getTriggerAdvPeriod() {
        return triggerAdvPeriod;
    }

    public Integer getTriggerAdvTxPower() {
        return triggerAdvTxPower;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    //trigger No.
    public void setTriggerIndex(Integer triggerIndex) {
        this.triggerIndex = triggerIndex;
    }

    //trigger action, it can be advertisement, vibration, report to app. ,etc;
    public boolean setTriggerAction(Integer nTriggerAction){
        int nTriggerActionMask = KBTriggerAction.ActionOff | KBTriggerAction.Advertisement
                | KBTriggerAction.Alert | KBTriggerAction.Record
                | KBTriggerAction.Vibration | KBTriggerAction.Report2App;
        if (nTriggerAction == 0 || (nTriggerAction & nTriggerActionMask) > 0) {
            this.triggerAction = nTriggerAction;
            return true;
        }else{
            return false;
        }
    }

    //When we set multiple triggers to the same slot broadcast, we can set nAdvChangeMode to 0x01
    // in order to distinguish different triggers based on broadcast content
    //if nAdvChangeMode set to 0x01, the trigger advertisement content of UUID will change by UUID + trigger type.
    public void setTriggerAdvChangeMode(Integer nAdvChangeMode){
        triggerAdvChangeMode = nAdvChangeMode;
    }

    //The broadcast slot when the trigger occurs
    public void setTriggerAdvSlot(Integer triggerSlot) {
        this.triggerAdvSlot = triggerSlot;
    }

    /*Trigger parameters : different trigger types have different corresponding parameter ranges,
      For Motion Trigger: This parameter indicates the sensitivity of sensor detection, the range is 2~126, unit is 16mg
      For Humidity trigger: This parameter indicates the humidity threshold. unit is 1%
      For Temperature trigger: This parameter indicates the temperature threshold. unit is 1 Celsius
     */
    public void setTriggerPara(Integer triggerPara) {
        this.triggerPara = triggerPara;
    }

    //Trigger advertisement duration
    public boolean setTriggerAdvTime(Integer triggerAdvTime) {
        if (triggerAdvTime >= MIN_TRIGGER_ADV_TIME && triggerAdvTime <= MAX_TRIGGER_ADV_TIME) {
            this.triggerAdvTime = triggerAdvTime;
            return true;
        }else{
            return false;
        }
    }

    //set trigger adv period, the unit is ms
    public boolean setTriggerAdvPeriod(Float nAdvPeriod)
    {
        if (nAdvPeriod >= KBCfgAdvBase.MIN_ADV_PERIOD) {
            triggerAdvPeriod = nAdvPeriod;
            return true;
        } else {
            return false;
        }
    }

    //set trigger adv tx power
    public boolean setTriggerTxPower(Integer nTxPower)
    {
        if (nTxPower >= KBAdvTxPower.RADIO_MIN_TXPOWER && nTxPower <= KBAdvTxPower.RADIO_MAX_TXPOWER) {
            triggerAdvTxPower = nTxPower;
            return true;
        } else {
            return false;
        }
    }

    public int updateConfig(JSONObject dicts) throws JSONException
    {
        int nUpdateParaNum = super.updateConfig(dicts);
        Integer nTempValue = null;

        if (dicts.has(JSON_FIELD_TRIGGER_INDEX))
        {
            triggerIndex = dicts.getInt(JSON_FIELD_TRIGGER_INDEX);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_TYPE))
        {
            triggerType = dicts.getInt(JSON_FIELD_TRIGGER_TYPE);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_ACTION))
        {
            triggerAction = dicts.getInt(JSON_FIELD_TRIGGER_ACTION);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_ADV_CHANGE_MODE))
        {
            triggerAdvChangeMode = dicts.getInt(JSON_FIELD_TRIGGER_ADV_CHANGE_MODE);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_ADV_SLOT))
        {
            triggerAdvSlot = dicts.getInt(JSON_FIELD_TRIGGER_ADV_SLOT);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_PARA))
        {
            triggerPara = dicts.getInt(JSON_FIELD_TRIGGER_PARA);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_ADV_TIME))
        {
            triggerAdvTime = dicts.getInt(JSON_FIELD_TRIGGER_ADV_TIME);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_ADV_POWER)) {
            triggerAdvTxPower = dicts.getInt(JSON_FIELD_TRIGGER_ADV_POWER);
            nUpdateParaNum++;
        }

        if (dicts.has(JSON_FIELD_TRIGGER_ADV_PERIOD)) {
            triggerAdvPeriod = parseFloat(dicts.get(JSON_FIELD_TRIGGER_ADV_PERIOD));;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject cfgDicts = super.toJSONObject();
        if (triggerIndex != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_INDEX, triggerIndex);
        }

        if (triggerType != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_TYPE, triggerType);
        }

        if (triggerAction != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_ACTION, triggerAction);
        }

        if (triggerPara != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_PARA, triggerPara);
        }

        if (triggerAdvChangeMode != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_ADV_CHANGE_MODE, triggerAdvChangeMode);
        }

        if (triggerAdvSlot != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_ADV_SLOT, triggerAdvSlot);
        }

        if (triggerAdvTime != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_ADV_TIME, triggerAdvTime);
        }

        if (triggerAdvTxPower != null)
        {
            cfgDicts.put(JSON_FIELD_TRIGGER_ADV_POWER, triggerAdvTxPower);
        }

        if (triggerAdvPeriod != null) {
            cfgDicts.put(JSON_FIELD_TRIGGER_ADV_PERIOD, triggerAdvPeriod);
        }

        return cfgDicts;
    }

}
