package com.kkmcn.kbeaconlib2.KBCfgPackage;

import android.util.Log;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class KBCfgHandler {
    private final static String LOG_TAG = "KBCfgHandler";
    private final static String ADV_OBJ_PARAS = "advObj";
    private final static String TRIGGER_OBJ_PARAS = "trObj";
    private final static String SENSOR_OBJ_PARAS = "srObj";

    //configuration read from device
    private KBCfgCommon kbDeviceAdvCommonPara;
    private ArrayList<KBCfgAdvBase> kbDeviceCfgAdvSlotLists;
    private ArrayList<KBCfgTrigger> kbDeviceCfgTriggerLists;
    private ArrayList<KBCfgSensorBase> kbDeviceCfgSensorLists;

    //object creation factory
    private final static HashMap<String, Class> kbCfgAdvObjects;
    private final static HashMap<String, Class> kbCfgTriggerObjects;
    private final static HashMap<String, Class> kbCfgSensorObjects;

    static
    {
        kbCfgAdvObjects = new HashMap<>(5);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.AdvNull), KBCfgAdvNull.class);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.Sensor), KBCfgAdvKSensor.class);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.EddyUID), KBCfgAdvEddyUID.class);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.EddyTLM), KBCfgAdvEddyTLM.class);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.EddyURL), KBCfgAdvEddyURL.class);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.IBeacon), KBCfgAdvIBeacon.class);
        kbCfgAdvObjects.put(String.valueOf(KBAdvType.System), KBCfgAdvSystem.class);

        kbCfgTriggerObjects = new HashMap<>(10);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.AccMotion), KBCfgTriggerMotion.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.TriggerNull), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.BtnLongPress), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.BtnSingleClick), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.BtnDoubleClick), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.BtnTripleClick), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.Cutoff), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.HTTempAbove), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.HTTempBelow), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.HTHumidityBelow), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.HTHumidityAbove), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.HTHumidityPeriodically), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.PIRBodyInfraredDetected), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.LightLUXAbove), KBCfgTrigger.class);
        kbCfgTriggerObjects.put(String.valueOf(KBTriggerType.LightLUXBelow), KBCfgTrigger.class);

        kbCfgSensorObjects = new HashMap<>(5);
        kbCfgSensorObjects.put(String.valueOf(KBSensorType.HTHumidity), KBCfgSensorHT.class);
        kbCfgSensorObjects.put(String.valueOf(KBSensorType.Cutoff), KBCfgSensorBase.class);
        kbCfgSensorObjects.put(String.valueOf(KBSensorType.PIR), KBCfgSensorBase.class);
        kbCfgSensorObjects.put(String.valueOf(KBSensorType.AccMotion), KBCfgSensorAcc.class);
        kbCfgSensorObjects.put(String.valueOf(KBSensorType.Light), KBCfgSensorLight.class);
    }

    public final KBCfgAdvBase getDeviceSlotCfg(int nSlotIndex)
    {
        for (KBCfgAdvBase slotCfg: kbDeviceCfgAdvSlotLists)
        {
            Integer slotIndex = slotCfg.getSlotIndex();
            if (slotIndex != null && slotIndex == nSlotIndex)
            {
                return slotCfg;
            }
        }
        return null;
    }

    public void clearBufferConfig()
    {
        kbDeviceAdvCommonPara = null;
        kbDeviceCfgAdvSlotLists.clear();
        kbDeviceCfgSensorLists.clear();
        kbDeviceCfgTriggerLists.clear();
    }

    public final KBCfgCommon getCfgComm()
    {
        return kbDeviceAdvCommonPara;
    }

    public ArrayList<KBCfgAdvBase> getSlotCfgList(){
        return kbDeviceCfgAdvSlotLists;
    }

    public ArrayList<KBCfgSensorBase> getSensorCfgList()
    {
        return kbDeviceCfgSensorLists;
    }

    public ArrayList<KBCfgTrigger> getTriggerCfgList()
    {
        return kbDeviceCfgTriggerLists;
    }

    public final ArrayList<KBCfgTrigger> getSlotTriggerCfgList(int nSlotIndex)
    {
        ArrayList<KBCfgTrigger> cfgList = null;
        for (KBCfgTrigger triggerCfg: kbDeviceCfgTriggerLists)
        {
            Integer triggerSlotIndex = triggerCfg.getTriggerAdvSlot();
            if (triggerSlotIndex != null && triggerSlotIndex == nSlotIndex)
            {
                if (cfgList == null) {
                    cfgList = new ArrayList<>(2);
                }
                cfgList.add(triggerCfg);
            }
        }
        return cfgList;
    }

    public final ArrayList<KBCfgAdvBase> getDeviceSlotsCfgByType(int nAdvType)
    {
        ArrayList<KBCfgAdvBase> cfgList = null;
        for (KBCfgAdvBase slotCfg: kbDeviceCfgAdvSlotLists)
        {
            Integer advType = slotCfg.getAdvType();
            if (advType != null && advType == nAdvType)
            {
                if (cfgList == null){
                    cfgList = new ArrayList<>(2);
                }
                cfgList.add(slotCfg);
            }
        }

        return cfgList;
    }

    public final KBCfgTrigger getDeviceTriggerCfg(int nTriggerType)
    {
        for (KBCfgTrigger triggerCfg: kbDeviceCfgTriggerLists)
        {
            Integer triggerType = triggerCfg.getTriggerType();
            if (triggerType != null && triggerType == nTriggerType)
            {
                return triggerCfg;
            }
        }
        return null;
    }

    public final KBCfgSensorBase getDeviceSensorCfg(int nSensorType)
    {
        for (KBCfgSensorBase sensorCfg: kbDeviceCfgSensorLists)
        {
            Integer sensorType = sensorCfg.getSensorType();
            if (sensorType != null && nSensorType == sensorType)
            {
                return sensorCfg;
            }
        }
        return null;
    }

    public final ArrayList<KBCfgTrigger> getDeviceTriggerCfgPara()
    {
        return kbDeviceCfgTriggerLists;
    }

    public KBCfgHandler()
    {
        kbDeviceCfgAdvSlotLists = new ArrayList<>(5);
        kbDeviceCfgTriggerLists = new ArrayList<>(5);
        kbDeviceCfgSensorLists = new ArrayList<>(5);
    }

    private KBCfgAdvBase getDeviceAdvSlotObj(int nSlotIndex)
    {
        for (KBCfgAdvBase obj : kbDeviceCfgAdvSlotLists)
        {
            if (obj.getSlotIndex() == nSlotIndex)
            {
                return obj;
            }
        }

        return null;
    }

    public void addTriggerClass(int nTriggerType, Class trgClass)
    {
        kbCfgTriggerObjects.put(String.valueOf(nTriggerType), trgClass);
    }

    public void addAdvClass(int nAdvType, Class advClass)
    {
        kbCfgAdvObjects.put(String.valueOf(nAdvType), advClass);
    }

    public void addSensorClass(int nSensorType, Class advClass)
    {
        kbCfgSensorObjects.put(String.valueOf(nSensorType), advClass);
    }

    public static KBCfgAdvBase createCfgAdvObject(int nAdvType)
    {
        try {
            Class classObj = kbCfgAdvObjects.get(String.valueOf(nAdvType));
            if (classObj != null){
                return (KBCfgAdvBase) classObj.newInstance();
            }
        } catch (Exception excpt) {
            excpt.printStackTrace();
            Log.e(LOG_TAG, "create adv para object failed:" + nAdvType);
        }

        return null;
    }

    public static KBCfgTrigger createCfgTriggerObject(int nTriggerType)
    {
        try {
            Class classObj = kbCfgTriggerObjects.get(String.valueOf(nTriggerType));
            if (classObj != null){
                KBCfgTrigger cfgTrigger = (KBCfgTrigger) classObj.newInstance();
                cfgTrigger.setTriggerType(nTriggerType);
                return cfgTrigger;
            }
        } catch (Exception excpt) {
            excpt.printStackTrace();
            Log.e(LOG_TAG, "create trigger object failed:" + nTriggerType);
        }

        return null;
    }

    public static KBCfgSensorBase createCfgSensorObject(int nSensorType)
    {
        try {
            Class classObj = kbCfgSensorObjects.get(String.valueOf(nSensorType));
            if (classObj != null){
                 KBCfgSensorBase sensorPara = (KBCfgSensorBase)classObj.newInstance();
                sensorPara.setSensorType(nSensorType);
                return sensorPara;
            }
        } catch (Exception excpt) {
            excpt.printStackTrace();
            Log.e(LOG_TAG, "create sensor object failed:" + nSensorType);
        }

        return null;
    }

    private KBCfgTrigger getDeviceTriggerObj(int nTriggerIndex)
    {
        for (KBCfgTrigger obj : kbDeviceCfgTriggerLists)
        {
            if (obj.getTriggerIndex() == nTriggerIndex)
            {
                return obj;
            }
        }

        return null;
    }

    private KBCfgAdvBase updateDeviceCfgAdvObjFromParas(HashMap<String, Object> advPara)
    {
        Integer advType = (Integer) advPara.get(KBCfgAdvBase.JSON_FIELD_BEACON_TYPE);
        Integer slotIndex = (Integer) advPara.get(KBCfgAdvBase.JSON_FIELD_SLOT);
        if (slotIndex == null){
            Log.e(LOG_TAG, "update device configuration failed during slot index is null");
            return null;
        }

        KBCfgAdvBase deviceAdvObj = getDeviceAdvSlotObj(slotIndex);
        if (deviceAdvObj != null && Objects.equals(advType, deviceAdvObj.getAdvType())) {
           deviceAdvObj.updateConfig(advPara);
        }else {
            KBCfgAdvBase tempAdv = createCfgAdvObject(advType);
            if (tempAdv == null) {
                Log.e(LOG_TAG, "update device create adv para failed, adv type:" + advType);
                return null;
            }
            tempAdv.updateConfig(advPara);
            Log.v(LOG_TAG, "add new adv object(slot:" + slotIndex +
                    ", type:" + advType + ") to device config buffer");

            if (deviceAdvObj != null) {
                kbDeviceCfgAdvSlotLists.remove(deviceAdvObj);
            }
            kbDeviceCfgAdvSlotLists.add(tempAdv);
            deviceAdvObj = tempAdv;
        }

        return deviceAdvObj;
    }

    private KBCfgTrigger updateDeviceCfgTriggerFromParas(HashMap<String, Object> triggerPara)
    {
        Integer triggerIdx = (Integer) triggerPara.get(KBCfgTrigger.JSON_FIELD_TRIGGER_INDEX);
        Integer triggerType = (Integer) triggerPara.get(KBCfgTrigger.JSON_FIELD_TRIGGER_TYPE);
        if (triggerIdx == null || triggerType == null){
            Log.e(LOG_TAG, "update device configuration failed during slot index is null");
            return null;
        }

        KBCfgTrigger deviceTriggerObj = getDeviceTriggerObj(triggerIdx);
        if (deviceTriggerObj != null && deviceTriggerObj.getTriggerType().equals(triggerType)) {
            deviceTriggerObj.updateConfig(triggerPara);
        }else {
            KBCfgTrigger tempTrigger = createCfgTriggerObject(triggerType);
            if (tempTrigger == null) {
                Log.e(LOG_TAG, "update device create adv para failed, trigger type:" + triggerType);
                return null;
            }
            tempTrigger.updateConfig(triggerPara);
            Log.v(LOG_TAG, "add new trigger object(index:" + triggerIdx +
                    ", type:" + triggerType + ") to device config buffer");

            if (deviceTriggerObj != null) {
                kbDeviceCfgTriggerLists.remove(deviceTriggerObj);
            }
            kbDeviceCfgTriggerLists.add(tempTrigger);
            deviceTriggerObj = tempTrigger;
        }

        return deviceTriggerObj;
    }

    private KBCfgSensorBase updateDeviceCfgSensorFromParas(HashMap<String, Object> sensorPara)
    {
        Integer sensorType = (Integer) sensorPara.get(KBCfgSensorBase.JSON_SENSOR_TYPE);
        if (sensorType == null){
            Log.e(LOG_TAG, "update device configuration failed during sensor type is null");
            return null;
        }

        KBCfgSensorBase deviceSensorObj = getDeviceSensorCfg(sensorType);
        if (deviceSensorObj == null) {
            deviceSensorObj = createCfgSensorObject(sensorType);
            if (deviceSensorObj == null){
                Log.e(LOG_TAG, "update device create sensor object failed, trigger type:" + sensorType);
                return null;
            }
            kbDeviceCfgSensorLists.add(deviceSensorObj);
            Log.v(LOG_TAG, "add new sensor object(type:" + sensorType + ") to device config buffer");
        }
        deviceSensorObj.updateConfig(sensorPara);

        return deviceSensorObj;
    }

    public static ArrayList<KBCfgBase> createCfgObjectsFromJsonObject(JSONObject jsonObj)
    {
        KBCfgHandler cfgHandler = new KBCfgHandler();
        cfgHandler.updateDeviceCfgFromJsonObject(jsonObj);

        ArrayList<KBCfgBase> cfgList = new ArrayList<>(5);
        if (cfgHandler.kbDeviceAdvCommonPara != null){
            cfgList.add(cfgHandler.kbDeviceAdvCommonPara);
        }

        cfgList.addAll(cfgHandler.kbDeviceCfgAdvSlotLists);

        cfgList.addAll(cfgHandler.kbDeviceCfgTriggerLists);

        cfgList.addAll(cfgHandler.kbDeviceCfgSensorLists);

        return cfgList;
    }

    public boolean checkConfigValid(ArrayList<KBCfgBase> cfgArray)
    {
        for (KBCfgBase cfgObj: cfgArray){
            if (cfgObj instanceof KBCfgAdvBase){
                KBCfgAdvBase advObj = (KBCfgAdvBase) cfgObj;
                if (advObj.getSlotIndex() == null){
                    Log.e(LOG_TAG, "the configuration of slot index is null.");
                    return false;
                }

                if (kbDeviceAdvCommonPara != null)
                {
                    //check tx power
                    Integer txPower = advObj.getTxPower();
                    if (txPower != null && (txPower < kbDeviceAdvCommonPara.getMinTxPower()
                            || txPower > kbDeviceAdvCommonPara.getMaxTxPower())){
                        Log.e(LOG_TAG, "the tx power is out of device capability");
                        return false;
                    }

                    //check the device adv mode
                    Integer advMode = advObj.getAdvMode();
                    if (advMode != null) {
                        if (advMode == KBAdvMode.K2Mbps && !kbDeviceAdvCommonPara.isSupportBLE2MBps()) {
                            Log.e(LOG_TAG, "the tx power is not support 2MBPS adv");
                            return false;
                        }
                        if (advMode == KBAdvMode.LongRange && !kbDeviceAdvCommonPara.isSupportBLELongRangeAdv()) {
                            Log.e(LOG_TAG, "the tx power is not support long range adv");
                            return false;
                        }
                    }
                }
            }

            //check the trigger
            if (cfgObj instanceof KBCfgTrigger){
                KBCfgTrigger advObj = (KBCfgTrigger) cfgObj;
                if ((advObj.getTriggerAction() & KBTriggerAction.Advertisement) > 0){
                    if (advObj.getTriggerAdvSlot() == null){
                        Log.e(LOG_TAG, "trigger adv slot is null");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void updateDeviceCfgFromJsonObject(JSONObject jsonObj){
        initUpdateDeviceCfgFromJsonObject(jsonObj, false);
    }

    //create adv objects from JSON string
    private void initUpdateDeviceCfgFromJsonObject(JSONObject jsonObj, boolean bInit)
    {
        try {
            HashMap<String, Object> advParas = new HashMap<>(10);
            KBCfgBase.JsonObject2HashMap(jsonObj, advParas);

            //adv common
            if (bInit || kbDeviceAdvCommonPara == null){
                kbDeviceAdvCommonPara = new KBCfgCommon();
            }
            kbDeviceAdvCommonPara.updateConfig(advParas);

            if (bInit){
                kbDeviceCfgAdvSlotLists.clear();
                kbDeviceCfgTriggerLists.clear();
                kbDeviceCfgSensorLists.clear();
            }

            //update adv paras
            if (advParas.containsKey(ADV_OBJ_PARAS)) {
                JSONArray advArrays = (JSONArray) advParas.get(ADV_OBJ_PARAS);
                if (advArrays != null) {
                    for (int i = 0; i < advArrays.length(); i++) {
                        JSONObject jsonObject = advArrays.getJSONObject(i);
                        HashMap<String, Object> slotPara = new HashMap<>(10);
                        KBCfgBase.JsonObject2HashMap(jsonObject, slotPara);
                        updateDeviceCfgAdvObjFromParas(slotPara);
                    }
                }
            }

            //trigger objects
            if (advParas.containsKey(TRIGGER_OBJ_PARAS)) {
                JSONArray advArrays = (JSONArray) advParas.get(TRIGGER_OBJ_PARAS);
                if (advArrays != null) {
                    for (int i = 0; i < advArrays.length(); i++) {
                        JSONObject jsonObject = advArrays.getJSONObject(i);
                        HashMap<String, Object> triggerPara = new HashMap<>(10);
                        KBCfgBase.JsonObject2HashMap(jsonObject, triggerPara);
                        updateDeviceCfgTriggerFromParas(triggerPara);
                    }
                }
            }

            //sensor objects
            if (advParas.containsKey(SENSOR_OBJ_PARAS)) {
                JSONArray advArrays = (JSONArray) advParas.get(SENSOR_OBJ_PARAS);
                if (advArrays != null) {
                    for (int i = 0; i < advArrays.length(); i++) {
                        JSONObject jsonObject = advArrays.getJSONObject(i);
                        HashMap<String, Object> triggerPara = new HashMap<>(10);
                        KBCfgBase.JsonObject2HashMap(jsonObject, triggerPara);

                        updateDeviceCfgSensorFromParas(triggerPara);
                    }
                }
            }
        }
        catch (JSONException excp)
        {
            Log.e(LOG_TAG, "Parse Jason config string failed");
        }
    }

    //update configruation
    public void updateDeviceConfig(ArrayList<KBCfgBase> newCfgArray)
    {
        for (KBCfgBase obj: newCfgArray)
        {
            HashMap<String, Object> updatePara = obj.toDictionary();
            if (updatePara == null || updatePara.size() == 0)
            {
                Log.e(LOG_TAG, "config data is null");
                continue;
            }

            //check if is common para
            if (obj instanceof KBCfgCommon) {
                if (kbDeviceAdvCommonPara == null){
                    kbDeviceAdvCommonPara = new KBCfgCommon();
                }
                kbDeviceAdvCommonPara.updateConfig(updatePara);
            }

            //check if adv para
            if (obj instanceof KBCfgAdvBase)
            {
                KBCfgAdvBase advBase = (KBCfgAdvBase)obj;
                updateDeviceCfgAdvObjFromParas(advBase.toDictionary());
            }

            //check if trigger para
            if (obj instanceof KBCfgTrigger) {
                KBCfgTrigger triggerBase = (KBCfgTrigger)obj;
                updateDeviceCfgTriggerFromParas(triggerBase.toDictionary());
            }

            //check if trigger para
            if (obj instanceof KBCfgSensorBase) {
                KBCfgSensorBase sensorBase = (KBCfgSensorBase)obj;
                updateDeviceCfgSensorFromParas(sensorBase.toDictionary());
            }
        }
    }

    //translate object to json string for download to beacon
    public static String objectsToJsonString(ArrayList<KBCfgBase> cfgObjects)
    {
        JSONObject jsonMsgObject = new JSONObject();
        JSONArray jsonAdvParaArray = new JSONArray();
        JSONArray jsonTriggerParaArray = new JSONArray();
        JSONArray jsonSensorParaArray = new JSONArray();

        for (KBCfgBase obj: cfgObjects)
        {
            HashMap<String, Object> updatePara = obj.toDictionary();
            if (updatePara == null || updatePara.size() == 0)
            {
                Log.e(LOG_TAG, "config data is null");
                continue;
            }

            //add common object
            if (obj instanceof KBCfgCommon) {
                KBCfgBase.HashMap2JsonObject(obj.toDictionary(), jsonMsgObject);
            }
            else if (obj instanceof KBCfgAdvBase)
            {
                //add adv cfg object
                KBCfgAdvBase advBase = (KBCfgAdvBase)obj;
                JSONObject jsonObject = advBase.toJsonObject();
                jsonAdvParaArray.put(jsonObject);
            }
            else if (obj instanceof KBCfgTrigger) {
                //add trigger cfg object
                KBCfgTrigger triggerBase = (KBCfgTrigger)obj;
                JSONObject jsonObject = triggerBase.toJsonObject();
                jsonTriggerParaArray.put(jsonObject);
            }
            else if (obj instanceof KBCfgSensorBase) {
                //add trigger cfg object
                KBCfgSensorBase sensorBase = (KBCfgSensorBase)obj;
                JSONObject jsonObject = sensorBase.toJsonObject();
                jsonSensorParaArray.put(jsonObject);
            }
        }

        try {
            if (jsonAdvParaArray.length() > 0) {
                jsonMsgObject.put(ADV_OBJ_PARAS, jsonAdvParaArray);
            }
            if (jsonTriggerParaArray.length() > 0) {
                jsonMsgObject.put(TRIGGER_OBJ_PARAS, jsonTriggerParaArray);
            }
            if (jsonSensorParaArray.length() > 0) {
                jsonMsgObject.put(SENSOR_OBJ_PARAS, jsonSensorParaArray);
            }
            jsonMsgObject.put("msg", "cfg");

            return jsonMsgObject.toString().replace("\\", "");
        }
        catch (JSONException excpt)
        {
            excpt.printStackTrace();
        }

        return null;
    }

    //parse command para to string
    public static String cmdParaToJsonString(HashMap<String, Object> paraDicts)
    {
        if (paraDicts != null)
        {
            JSONObject jsonObj = new JSONObject();
            KBCfgBase.HashMap2JsonObject(paraDicts, jsonObj);
            if (jsonObj.length() > 0)
            {
                return jsonObj.toString();
            }
        }

        return null;
    }
}
