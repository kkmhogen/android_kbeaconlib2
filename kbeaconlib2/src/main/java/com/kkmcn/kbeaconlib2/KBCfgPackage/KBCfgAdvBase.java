package com.kkmcn.kbeaconlib2.KBCfgPackage;

import com.kkmcn.kbeaconlib2.KBException;

import java.util.HashMap;

public class KBCfgAdvBase extends KBCfgBase{
    public final static Float DEFAULT_ADV_PERIOD = 1000.0f;
    public final static Float MIN_ADV_PERIOD = 100.0f;
    public final static Float MAX_ADV_PERIOD = 20000.0f;

    public final static Integer INVALID_SLOT_INDEX = 0xff;

    public final static Integer DEFAULT_TX_POWER = 0;

    public final static Boolean DEFAULT_ADV_CONNECTABLE = true;

    public final static Boolean ADV_TRIGGER_MODE_ONLY = false;
    public final static Boolean ADV_ALWAYS = true;

    public final static Integer DEFAULT_ADV_MODE = KBAdvMode.Legacy;

    public final static String  JSON_FIELD_SLOT = "slot";
    public final static String  JSON_FIELD_TX_PWR = "txPwr";
    public final static String  JSON_FIELD_ADV_PERIOD = "advPrd";
    public final static String  JSON_FIELD_BEACON_TYPE = "type";
    public final static String  JSON_FIELD_ADV_TRIGGER_ONLY= "trAdv";
    public final static String  JSON_FIELD_ADV_CONNECTABLE = "conn";
    public final static String  JSON_FIELD_ADV_MODE = "mode";

    protected Integer slotIndex;

    protected Integer txPower;

    protected Float advPeriod;

    protected Integer advType; //beacon type (iBeacon, Eddy TLM/UID/ etc.,)

    protected Boolean advConnectable; //is beacon can be connectable

    protected Integer advMode;       //advertisement mode

    protected Boolean advTriggerOnly;

    public Integer getSlotIndex(){return slotIndex;}

    public Integer getAdvType()
    {
        return advType;
    }

    public Float getAdvPeriod()
    {
        return advPeriod;
    }

    public Boolean isAdvConnectable()
    {
        return advConnectable;
    }

    public Integer getTxPower(){return txPower;}

    public Integer getAdvMode(){return advMode;}

    protected KBCfgAdvBase()
    {
    }

    public Boolean isAdvTriggerOnly(){
        return advTriggerOnly;
    }

    public void setAdvTriggerOnly(Boolean triggerAdvOnly)
    {
        advTriggerOnly = triggerAdvOnly;
    }

    public void setSlotIndex(Integer nSlotIndex) throws KBException
    {
        slotIndex = nSlotIndex;
    }

    //set adv type
    /*
    public void setAdvType(Integer nAdvType) throws KBException
    {
        if (nAdvType > KBAdvType.KBAdvTypeMAXValue){
            throw new KBException(KBException.KBEvtCfgInputInvalid, "adv type invalid");
        }else{
            advType = nAdvType;
        }
    }
    */

    //set adv period
    public void setAdvPeriod(Float nAdvPeriod) throws KBException
    {
        if ((nAdvPeriod <= MAX_ADV_PERIOD && nAdvPeriod >= MIN_ADV_PERIOD)) {
            advPeriod = nAdvPeriod;
        } else {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "adv period invalid");
        }
    }

    //set KBeacon tx power
    public void setTxPower(Integer nTxPower) throws KBException
    {
        if (nTxPower >= KBAdvTxPower.RADIO_TXPOWER_MIN_TXPOWER && nTxPower <= KBAdvTxPower.RADIO_TXPOWER_MAX_TXPOWER) {
            txPower = nTxPower;
        } else {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "invalid tx power data");
        }
    }

    public void setAdvConnectable(Boolean nConnectable)
    {
        advConnectable = nConnectable;
    }

    public void setAdvMode(Integer nAdvMode) throws  KBException
    {
        if (nAdvMode != KBAdvMode.Legacy
                && nAdvMode != KBAdvMode.LongRange
                && nAdvMode != KBAdvMode.K2Mbps) {
            throw new KBException(KBException.KBEvtCfgInputInvalid, "invalid advertise mode");
        }else{
            advMode = nAdvMode;
        }
    }

    public int updateConfig(HashMap<String, Object> dicts) {
        int nUpdateParaNum = super.updateConfig(dicts);

        Integer nTempValue = (Integer) dicts.get(JSON_FIELD_SLOT);
        if (nTempValue != null) {
            slotIndex = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_TX_PWR);
        if (nTempValue != null) {
            txPower = nTempValue;
            nUpdateParaNum++;
        }

        Float nTempFloat = parseFloat(dicts.get(JSON_FIELD_ADV_PERIOD));
        if (nTempFloat != null) {
            advPeriod = nTempFloat;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_BEACON_TYPE);
        if (nTempValue != null) {
            advType = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_ADV_CONNECTABLE);
        if (nTempValue != null) {
            advConnectable = nTempValue > 0;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_ADV_MODE);
        if (nTempValue != null) {
            advMode = nTempValue;
            nUpdateParaNum++;
        }

        nTempValue = (Integer) dicts.get(JSON_FIELD_ADV_TRIGGER_ONLY);
        if (nTempValue != null) {
            advTriggerOnly = nTempValue > 0;
            nUpdateParaNum++;
        }

        return nUpdateParaNum;
    }

    public HashMap<String, Object> toDictionary()
    {
        HashMap<String, Object> configDicts = super.toDictionary();
        if (slotIndex != null) {
            configDicts.put(JSON_FIELD_SLOT, slotIndex);
        }

        if (txPower != null) {
            configDicts.put(JSON_FIELD_TX_PWR, txPower);
        }

        if (advPeriod != null) {
            configDicts.put(JSON_FIELD_ADV_PERIOD, advPeriod);
        }

        if (advType != null) {
            configDicts.put(JSON_FIELD_BEACON_TYPE, advType);
        }

        if (advConnectable != null) {
            configDicts.put(JSON_FIELD_ADV_CONNECTABLE, advConnectable? 1: 0);
        }

        if (advMode != null) {
            configDicts.put(JSON_FIELD_ADV_MODE, advMode);
        }

        if (advTriggerOnly != null){
            configDicts.put(JSON_FIELD_ADV_TRIGGER_ONLY, advTriggerOnly ? 1 : 0);
        }

        return configDicts;
    }

}
