package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBException;

import java.util.HashMap;

public class KBCfgTrigger extends KBCfgBase {
    //trigger adv time
    public static final int DEFAULT_TRIGGER_ADV_TIME = 30;
    public static final int MIN_TRIGGER_ADV_TIME = 2;
    public static final int MAX_TRIGGER_ADV_TIME = 7200;

    //temperature sensor
    public static final int KBTriggerConditionDefaultHumidityAbove = 80;
    public static final int KBTriggerConditionDefaultHumidityBelow = 20;
    public static final int MIN_HUMIDITY_VALUE = 1;
    public static final int MAX_HUMIDITY_VALUE = 99;

    //humidity sensor
    public static final int KBTriggerConditionDefaultTemperatureAbove = 60;
    public static final int KBTriggerConditionDefaultTemperatureBelow = -10;
    public static final int MAX_TEMPERATURE_VALUE = 1000;
    public static final int MIN_TEMPERATURE_VALUE = -50;

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

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public void setTriggerAction(Integer nTriggerAction) throws KBException{
        int nTriggerActionMask = KBTriggerAction.ActionOff | KBTriggerAction.Advertisement
                | KBTriggerAction.Alert | KBTriggerAction.Record
                | KBTriggerAction.Vibration | KBTriggerAction.Report2App;
        if (nTriggerAction == 0 || (nTriggerAction & nTriggerActionMask) > 0) {
            this.triggerAction = nTriggerAction;
        }else{
            throw new KBException(KBException.KBEvtCfgInputInvalid, "trigger action invalid");
        }
    }

    public void setTriggerAdvChangeMode(Integer nAdvChangeMode){
        triggerAdvChangeMode = nAdvChangeMode;
    }

    public void setTriggerIndex(Integer triggerIndex) {
        this.triggerIndex = triggerIndex;
    }

    public void setTriggerAdvSlot(Integer triggerSlot) throws KBException {
        this.triggerAdvSlot = triggerSlot;
    }

    public void setTriggerPara(Integer triggerPara) {
        this.triggerPara = triggerPara;
    }

    public void setTriggerAdvTime(Integer triggerAdvTime) throws KBException{
        if (triggerAdvTime >= MIN_TRIGGER_ADV_TIME && triggerAdvTime <= MAX_TRIGGER_ADV_TIME) {
            this.triggerAdvTime = triggerAdvTime;
        }else{
            throw new KBException(KBException.KBEvtCfgInputInvalid, "trigger adv time invalid");
        }
    }



    public int updateConfig(HashMap<String, Object> dicts)
    {
        int nUpdateParaNum = super.updateConfig(dicts);
        Integer nTempValue = null;

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_INDEX);
        if (nTempValue != null)
        {
            triggerIndex = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_TYPE);
        if (nTempValue != null)
        {
            triggerType = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_ACTION);
        if (nTempValue != null)
        {
            triggerAction = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_ADV_CHANGE_MODE);
        if (nTempValue != null)
        {
            triggerAdvChangeMode = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_ADV_SLOT);
        if (nTempValue != null)
        {
            triggerAdvSlot = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_PARA);
        if (nTempValue != null)
        {
            triggerPara = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer)dicts.get(JSON_FIELD_TRIGGER_ADV_TIME);
        if (nTempValue != null)
        {
            triggerAdvTime = nTempValue;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object>cfgDicts = new HashMap<String, Object>(4);
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

        return cfgDicts;
    }

}