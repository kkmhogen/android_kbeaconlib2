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

    public Boolean isAdvTriggerOnly(){
        return advTriggerOnly;
    }

    public Integer getTxPower(){return txPower;}

    public Integer getAdvMode(){return advMode;}

    protected KBCfgAdvBase()
    {
    }

    //slot index about advertisement
    public void setSlotIndex(Integer nSlotIndex)
    {
        slotIndex = nSlotIndex;
    }


    //please reference KBAdvMode for more detail
    //some device may only support KBAdvMode.Legacy
    public boolean setAdvMode(Integer nAdvMode)
    {
        if (nAdvMode != KBAdvMode.Legacy
                && nAdvMode != KBAdvMode.LongRange
                && nAdvMode != KBAdvMode.K2Mbps) {
            return false;
        }else{
            advMode = nAdvMode;
            return true;
        }
    }

    //if enabled, this slot does not broadcast by default, and it only broadcasts when the Trigger event is triggered.
    public void setAdvTriggerOnly(Boolean triggerAdvOnly)
    {
        advTriggerOnly = triggerAdvOnly;
    }

    //set adv period, the unit is ms
    public boolean setAdvPeriod(Float nAdvPeriod)
    {
        if (nAdvPeriod >= MIN_ADV_PERIOD) {
            advPeriod = nAdvPeriod;
            return true;
        } else {
            return false;
        }
    }

    //set KBeacon tx power
    public boolean setTxPower(Integer nTxPower)
    {
        if (nTxPower >= KBAdvTxPower.RADIO_MIN_TXPOWER && nTxPower <= KBAdvTxPower.RADIO_MAX_TXPOWER) {
            txPower = nTxPower;
            return true;
        } else {
            return false;
        }
    }

    // Warning: if the app set the KBeacon to un-connectable, the app cannot connect to the device.
    //When the device enters the unconnectable mode, you can enter it in the following ways:
    //1. If the Button Trigger is not enabled, you can press the button of the device and the device will enter the connectable broadcast for 30 seconds.
    //2. When the device is powered on again, the device will enter the connectable broadcast within 30 seconds after it is powered on.
    public void setAdvConnectable(Boolean nConnectable)
    {
        advConnectable = nConnectable;
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
