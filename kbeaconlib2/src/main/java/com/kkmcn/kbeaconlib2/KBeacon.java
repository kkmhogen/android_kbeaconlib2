package com.kkmcn.kbeaconlib2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanRecord;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketBase;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketHandler;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvBase;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvEddyTLM;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvKSensor;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvSystem;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgBase;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgCommon;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgHandler;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorBase;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTrigger;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgType;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBTriggerType;
import com.kkmcn.kbeaconlib2.KBSensorHistoryData.KBRecordDataHandler;
import com.kkmcn.kbeaconlib2.KBSensorHistoryData.KBSensorMsgType;
import com.kkmcn.kbeaconlib2.KBSensorHistoryData.KBRecordInfoRsp;
import com.kkmcn.kbeaconlib2.KBSensorHistoryData.KBRecordDataRsp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class KBeacon implements KBAuthHandler.KBAuthDelegate{
    private static final String LOG_TAG = "KBeacon";

    private ConnStateDelegate delegate;

    //advertisement
    private String mac;
    private int rssi;
    private String name;
    private KBConnState state; //connection state

    public final static int MIN_JSON_MSG_LEN = 7;

    //adv and config manager
    private final static int MSG_CONNECT_TIMEOUT = 201;
    private final static int MSG_ACTION_TIME_OUT = 202;
    private final static int MSG_SYS_CONNECTION_EVT = 203;
    private final static int MSG_SERVICES_DISCOVERD = 204;
    private final static int MSG_START_AUTHENTICATION = 205;
    private final static int MSG_BEACON_DATA_RECEIVED = 206;
    private final static int MSG_CLOSE_CONNECTION_TIMEOUT = 207;
    private final static int MSG_START_REQUST_MAX_MTU = 208;
    private final static int MSG_NTF_CONNECT_SUCCESS = 209;
    private final static int MSG_NTF_IND_ENABLE = 210;
    private final static int MSG_BEACON_INDICATION_RECEIVED = 211;
    private final static int MSG_NTF_SUBSCRIBE_INDICATION_CMP = 212;

    private final static int  MSG_START_EXECUTE_NEXT_MSG = 213;

    //timer
    private final static int MAX_READ_CFG_TIMEOUT = 15*1000;

    private final static int MAX_WRITE_CFG_TIMEOUT = 15*1000;

    private final static int DATA_TYPE_AUTH = 0x1;
    private final static int DATA_TYPE_JSON = 0x2;

    //frame tag
    private final static int PDU_TAG_START = 0x0;
    private final static int PDU_TAG_MIDDLE = 0x1;
    private final static int  PDU_TAG_END = 0x2;
    private final static int PDU_TAG_SINGLE = 0x3;
    private final static int MSG_PDU_HEAD_LEN = 0x3;

    private final static int DATA_ACK_HEAD_LEN = 6;

    //down json data
    private final static int  CENT_PERP_TX_JSON_DATA =  2;
    private final static int  PERP_CENT_TX_JSON_ACK  = 2;

    private final static int  CENT_PERP_TX_HEX_DATA = 0;
    private final static int  PERP_CENT_TX_HEX_ACK  = 0;

    private final static int  PERP_CENT_DATA_RPT = 3;
    private final static int  CENT_PERP_DATA_RPT_ACK  = 3;

    private final static int  PERP_CENT_HEX_DATA_RPT = 5;
    private final static int  CENT_PERP_HEX_DATA_RPT_ACK  = 5;

    private final static int  BEACON_ACK_SUCCESS = 0x0;
    private final static int  BEACON_ACK_EXPECT_NEXT = 0x4;
    private final static int  BEACON_ACK_CAUSE_CMD_RCV = 0x5;
    private final static int  BEACON_ACK_EXE_CMD_CMP = 0x6;
    private final static int MAX_MTU_SIZE = 251;
    private final static int MAX_BUFFER_DATA_SIZE = 1024;

    private KBAuthHandler mAuthHandler;
    private KBConnPara mConnPara;

    private int mCloseReason;
    private KBAdvPacketHandler mAdvPacketMgr;
    private KBRecordDataHandler mSensorRecordsMgr;
    private KBCfgHandler mCfgMgr;
    private String mPassword;
    private BluetoothDevice mBleDevice;
    private Context mContext;
    private final BluetoothGattCallback mGattCallback;
    private BluetoothGatt mGattConnection;

    //indication bluetooth device is busy
    private boolean mActionDoing;

    private HashMap<Integer, NotifyDataDelegate> notifyData2ClassMap;
    private NotifyDataDelegate mToAddedSubscribeInstance = null;
    private Integer mToAddedTriggerType = 0;

    private enum ActionType
    {
        ACTION_IDLE,
        ACTION_WRITE_CFG,
        ACTION_WRITE_CMD,
        ACTION_INIT_READ_CFG,
        ACTION_USR_READ_CFG,
        ACTION_SENSOR_READ_INFO,
        ACTION_SENSOR_READ_RECORD,
        ACTION_SENSOR_COMMAND,
        ACTION_ENABLE_NTF,
        ACTION_DISABLE_NTF
    };

    private class ActionCommand
    {
        ActionType actionType;
        Object actionCallback;
        byte[] downDataBuff;
        int downDataType;
        int actionTimeout;

        int receiveDataLen;
        private byte[] receiveData;
        ArrayList<KBCfgBase> toBeCfgData;

        public ActionCommand(ActionType type, int timeout)
        {
            actionType = type;
            actionTimeout = timeout;

            actionCallback = null;
            downDataBuff = null;
            toBeCfgData = null;
            receiveDataLen = 0;
            receiveData = new byte[MAX_BUFFER_DATA_SIZE];
        }
    };

    //command msg buffer list
    ArrayList<ActionCommand> mActionList = new ArrayList<>(5);

    public interface ConnStateDelegate {
        void onConnStateChange(KBeacon beacon, KBConnState state, int nReason);
    }

    public interface NotifyDataDelegate {
        void onNotifyDataReceived(KBeacon beacon, int nEventType, byte[] sensorData);
    }

    public interface ActionCallback {
        void onActionComplete(boolean bConfigSuccess, KBException error);
    }

    public interface ReadConfigCallback {
        void onReadComplete(boolean bConfigSuccess, JSONObject readPara, KBException error);
    }

    public interface ReadSensorInfoCallback {
        void onReadComplete(boolean bReadResult, KBRecordInfoRsp readPara, KBException error);
    }

    public interface ReadSensorRspCallback {
        void onReadComplete(boolean bReadResult, KBRecordDataRsp readPara, KBException error);
    }

    public interface SensorCommandCallback {
        void onCommandComplete(boolean bReadResult, Object readPara, KBException error);
    }

    public KBeacon(String strMacAddress, Context ctx)
    {
        mac = strMacAddress;
        state = KBConnState.Disconnected;
        mAdvPacketMgr = new KBAdvPacketHandler();
        mSensorRecordsMgr = new KBRecordDataHandler();
        mCfgMgr = new KBCfgHandler();
        mContext = ctx;
        mGattCallback = new KBeaconGattCallback();
        mAuthHandler = new KBAuthHandler(this);
        notifyData2ClassMap = new HashMap<>(10);
        mConnPara = new KBConnPara();
    }

    void setAdvTypeFilter(int nAdvTypeFilter)
    {
        mAdvPacketMgr.setAdvTypeFilter(nAdvTypeFilter);
    }

    public void attach2Device(BluetoothDevice bleDevice, KBeaconsMgr beaconMgr)
    {
        mBleDevice = bleDevice;
    }

    //get ble device
    public BluetoothDevice getBleDevice() 
    {
        return mBleDevice;
    }

    //get mac address
    public String getMac()
    {
        return mac;
    }

    public KBRecordDataHandler getSensorRecordsMgr()
    {
        return mSensorRecordsMgr;
    }

    //get rssi
    public Integer getRssi()
    {
        return rssi;
    }

    public String getName()
    {
        return name;
    }

    public Integer getBatteryPercent()
    {
        return mAdvPacketMgr.getBatteryPercent();
    }


    public KBConnState getState()
    {
        return state;
    }

    public Integer maxTxPower()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getMaxTxPower();
        }else{
            return null;
        }
    }

    public Integer minTxPower()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getMinTxPower();
        }else{
            return null;
        }
    }

    public String model()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getModel();
        }else{
            return null;
        }
    }

    //hardware version
    public String hardwareVersion()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getHardwareVersion();
        }else{
            return null;
        }
    }

    //firmware version
    public String version()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getVersion();
        }else{
            return null;
        }
    }

    public Integer capability()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getBasicCapability();
        }else{
            return null;
        }
    }

    public Integer triggerCapability()
    {
        KBCfgCommon commCfg = (KBCfgCommon)mCfgMgr.getCfgComm();
        if (commCfg != null){
            return commCfg.getTrigCapability();
        }else{
            return null;
        }
    }

    //get all scanned advertisement packets
    public KBAdvPacketBase[] allAdvPackets()
    {
        return mAdvPacketMgr.advPackets();
    }

    //get specified advertisement packet that scanned
    public KBAdvPacketBase getAdvPacketByType(int nAdvType)
    {
        return mAdvPacketMgr.getAdvPacket(nAdvType);
    }

    //remove all scanned packet
    public void removeAdvPacket()
    {
        this.mAdvPacketMgr.removeAdvPacket();
    }

    //connect to device with default parameters
    //When the app is connected to the KBeacon device, the app can specify which the configuration parameters to be read,
    //the app will read common parameters, advertisement parameters, trigger parameters by default
    public boolean connect(String password, int timeout, ConnStateDelegate connectCallback)
    {
        KBConnPara connPara = new KBConnPara();
        return connectEnhanced(password, timeout, connPara, connectCallback);
    }

    //connect to device with specified parameters
    //When the app is connected to the KBeacon device, the app can specify which the configuration parameters to be read,
    //The parameter that can be read include: common parameters, advertisement parameters, trigger parameters, and sensor parameters
    public boolean connectEnhanced(String password, int timeout, KBConnPara connPara, ConnStateDelegate connectCallback)
    {
        if (state == KBConnState.Disconnected && password.length() <= 16 && password.length() >= 8)
        {
            delegate = connectCallback;
            mGattConnection = mBleDevice.connectGatt(mContext, false, mGattCallback);
            Log.v(LOG_TAG, "start connect to device " + mac);

            mPassword = password;
            state = KBConnState.Connecting;

            //cancel action timer
            mActionDoing = false;
            mMsgHandler.removeMessages(MSG_ACTION_TIME_OUT);
            mActionList.clear();
            clearBufferConfig();

            //cancel connect timer
            if (connPara != null) {
                mConnPara = connPara;
            }
            mAuthHandler.setConnPara(mConnPara);
            mMsgHandler.removeMessages(MSG_CONNECT_TIMEOUT);
            mMsgHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, timeout);

            //notify connecting
            if (delegate != null) {
                this.delegate.onConnStateChange(this, KBConnState.Connecting, 0);
            }
            return true;
        }
        else
        {
            //notify connecting
            Log.e(LOG_TAG, "input parameters false");
            return false;
        }
    }

    //check if device was connected
    public boolean isConnected()
    {
        return state == KBConnState.Connected;
    }

    public void disconnect()
    {
        if (state != KBConnState.Disconnected
                && state != KBConnState.Disconnecting) {
            this.closeBeacon(KBConnectionEvent.ConnManualDisconnecting);
        }
    }

    //get common parameters that already read from device, if the SDK does not have common parameters, it wil return null
    //The app can specify whether to read common parameters when connecting.
    // The common parameters include the capability information of the device, as well as some other public parameters.
    public KBCfgCommon getCommonCfg()
    {
        return mCfgMgr.getCfgComm();
    }

    //get all trigger configuration parameters that read from device
    public ArrayList<KBCfgTrigger> getTriggerCfgList()
    {
        return mCfgMgr.getTriggerCfgList();
    }

    //get trigger configuration by trigger index
    public KBCfgTrigger getTriggerCfgByIndex(int nTriggerIndex)
    {
        ArrayList<KBCfgTrigger> triggerList = mCfgMgr.getTriggerCfgList();
        for (KBCfgTrigger trigger : triggerList)
        {
            if (trigger.getTriggerIndex() != null && trigger.getTriggerIndex() == nTriggerIndex)
            {
                return trigger;
            }
        }

        return null;
    }

    //get trigger configuration by advertisement slot index
    public ArrayList<KBCfgTrigger> getSlotTriggerCfgList(int nAdvSlotIndex)
    {
        return mCfgMgr.getSlotTriggerCfgList(nAdvSlotIndex);
    }

    //get all slot configuration
    public ArrayList<KBCfgAdvBase> getSlotCfgList()
    {
        return mCfgMgr.getSlotCfgList();
    }

    //get advertisement configuration by advertisement type
    public ArrayList<KBCfgAdvBase> getSlotCfgByAdvType(int nAdvType)
    {
        return mCfgMgr.getDeviceSlotsCfgByType(nAdvType);
    }

    //get advertisement configuration by slot ID
    public KBCfgAdvBase getSlotCfg(int nSlotIndex)
    {
        return mCfgMgr.getDeviceSlotCfg(nSlotIndex);
    }

    //get trigger configuration by trigger type
    public KBCfgTrigger getTriggerCfg(int nTriggerType)
    {
        return mCfgMgr.getDeviceTriggerCfg(nTriggerType);
    }

    //get all sensor configuration
    public ArrayList<KBCfgSensorBase> getSensorCfgList()
    {
        return mCfgMgr.getSensorCfgList();
    }

    //get sensor configuration by sensor type
    public KBCfgSensorBase getSensorCfg(int nSensorType)
    {
        return mCfgMgr.getDeviceSensorCfg(nSensorType);
    }

    //get eddy TLM advertisement configuration
    public KBCfgAdvEddyTLM getEddyTLMAdvCfg()
    {
        ArrayList<KBCfgAdvBase> sensorList = mCfgMgr.getDeviceSlotsCfgByType(KBAdvType.EddyTLM);
        if (sensorList == null){
            return null;
        }else{
            return (KBCfgAdvEddyTLM)sensorList.get(0);
        }
    }

    //get system advertisement configuration
    public KBCfgAdvSystem getSystemAdvCfg()
    {
        ArrayList<KBCfgAdvBase> advList = mCfgMgr.getDeviceSlotsCfgByType(KBAdvType.System);
        if (advList == null){
            return null;
        }else{
            return (KBCfgAdvSystem)advList.get(0);
        }
    }

    //get KSensor advertisement configuration
    public KBCfgAdvKSensor getKSensorAdvCfg()
    {
        ArrayList<KBCfgAdvBase> sensorList = mCfgMgr.getDeviceSlotsCfgByType(KBAdvType.Sensor);
        if (sensorList == null){
            return null;
        }else{
            return (KBCfgAdvKSensor)sensorList.get(0);
        }
    }

    //clear all read parameters
    public void clearBufferConfig(){
        mCfgMgr.clearBufferConfig();
    }

    //update connection event delegation
    public void setConnStateDelegate(ConnStateDelegate connectCallback)
    {
        delegate = connectCallback;
    }

    //check if device support trigger notification
    public boolean isSupportSensorDataNotification()
    {
        if (getCharacteristicByID(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID) != null)
        {
            return true;
        }
        return false;
    }

    //subscribe trigger notification
    public void subscribeSensorDataNotify(Integer nTriggerEventType,
                                          NotifyDataDelegate notifyDataCallback,
                                          ActionCallback callback)
    {
        try {
            if (!isSupportSensorDataNotification()) {
                if (callback != null) {
                    callback.onActionComplete(false, new KBException(KBErrorCode.CfgNotSupport, "device not support subscription"));
                }
                return;
            }

            if (this.notifyData2ClassMap.size() == 0)
            {
                if (state != KBConnState.Connected)
                {
                    if (callback != null) {
                        callback.onActionComplete(false, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
                    }
                    return;
                }

                //save callback
                mToAddedSubscribeInstance = notifyDataCallback;
                if (nTriggerEventType == null){
                    nTriggerEventType = KBTriggerType.TriggerNull;
                }
                mToAddedTriggerType = nTriggerEventType;

                //add action
                ActionCommand action = new ActionCommand(ActionType.ACTION_ENABLE_NTF, 3000);
                action.actionCallback = callback;
                mActionList.add(action);

                //write data
                executeNextAction();
            } else {
                this.notifyData2ClassMap.put(nTriggerEventType, notifyDataCallback);
                if (callback != null) {
                    callback.onActionComplete(true, null);
                }
            }
        }catch (Exception excpt)
        {
            excpt.printStackTrace();
        }
    }

    //check if app already subscribe the trigger notification
    public boolean isSensorDataSubscribe(Integer triggerType)
    {
        return notifyData2ClassMap.get(triggerType) != null;
    }

    //remove subscribed trigger notification
    public void removeSubscribeSensorDataNotify(Integer nTriggerType, ActionCallback callback)
    {
        try {
            if (!isSupportSensorDataNotification()) {
                if (callback != null) {
                    callback.onActionComplete(false, new KBException(KBErrorCode.CfgNotSupport, "Device does not support subscription"));
                }
                return;
            }

            if (nTriggerType == null){
                nTriggerType = KBTriggerType.TriggerNull;
            }

            if (this.notifyData2ClassMap.get(nTriggerType) == null)
            {
                if (callback != null) {
                    callback.onActionComplete(true, null);
                }
                return;
            }

            if (this.notifyData2ClassMap.size() == 1)
            {
                if (state != KBConnState.Connected)
                {
                    if (callback != null) {
                        callback.onActionComplete(false, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
                    }
                    return;
                }

                //save callback
                mToAddedSubscribeInstance = null;
                mToAddedTriggerType = 0;

                //add action
                ActionCommand action = new ActionCommand(ActionType.ACTION_DISABLE_NTF, 3000);
                action.actionCallback = callback;
                mActionList.add(action);

                //execute next action
                executeNextAction();
            } else {
                this.notifyData2ClassMap.remove(nTriggerType);
                if (callback != null) {
                    callback.onActionComplete(true, null);
                }
            }
        }catch (Exception excpt)
        {
            excpt.printStackTrace();
        }
    }

    public ConnStateDelegate getConnStateDelegate()
    {
        return delegate;
    }

    public void checkClearGattBuffer(int status)
    {
        if (status == 133 || status == BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED)
        {
            Log.e(LOG_TAG, "remove device gatt catch:" + mac);

            try {
                Method localMethod = mGattConnection.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    localMethod.invoke(mGattConnection, new Object[0]);
                }
            } catch (Exception localException) {
                Log.e(LOG_TAG, "An exception occured while refreshing device");
            }
        }
    }

    //send command parameters to device
    public void sendCommand(JSONObject cmdPara, ActionCallback callback)
    {
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        //save callback
        String strJsonCfgData = KBCfgHandler.cmdParaToJsonString(cmdPara);
        if (strJsonCfgData.length() == 0) {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters invalid"));
            }
            return;
        }

        //create action object
        ActionCommand command = new ActionCommand(ActionType.ACTION_WRITE_CMD, MAX_WRITE_CFG_TIMEOUT);
        command.downDataBuff = strJsonCfgData.getBytes(StandardCharsets.UTF_8);
        command.downDataType = CENT_PERP_TX_JSON_DATA;
        command.actionCallback = callback;
        mActionList.add(command);

        //write data
        executeNextAction();
    }

    //create cfg object from JSON
    public ArrayList<KBCfgBase> createCfgObjectsFromJsonObject(JSONObject jsonObj)
    {
        return KBCfgHandler.createCfgObjectsFromJsonObject(jsonObj);
    }

    //read config by raw json message
    public void readConfig(JSONObject readMsg, final ReadConfigCallback callback)
    {
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        String strJsonCfgData = KBCfgHandler.cmdParaToJsonString(readMsg);
        if (strJsonCfgData.length() < MIN_JSON_MSG_LEN)
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters invalid"));
            }
        }

        startReadBeaconParameters(ActionType.ACTION_USR_READ_CFG, strJsonCfgData, callback);
    }

    //read slot common parameters from device
    //this function will force app to read parameters again from device
    public void readCommonConfig(final ReadConfigCallback callback)
    {
        JSONObject readCommMsg = new JSONObject();
        try
        {
            readCommMsg.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
            readCommMsg.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.CommonPara);
            readConfig(readCommMsg, callback);
        } catch (JSONException except)
        {
            except.printStackTrace();
            callback.onReadComplete(false, null,
                    new KBException(KBErrorCode.CfgJSONError,
                            "create JSON object failed"));
        }
    }

    //read slot adv parameters from device
    //this function will force app to read parameters again from device
    public void readSlotConfig(int nSlotIndex, final ReadConfigCallback callback)
    {
        JSONObject readCfgReq = new JSONObject();
        try
        {
            readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
            readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.AdvPara);
            readCfgReq.put(KBCfgAdvBase.JSON_FIELD_SLOT, nSlotIndex);
            readConfig(readCfgReq, callback);
        } catch (JSONException except)
        {
            except.printStackTrace();
            callback.onReadComplete(false, null,
                    new KBException(KBErrorCode.CfgJSONError,
                            "create JSON object failed"));
        }
    }

    //read trigger parameters from device
    //this function will force app to read trigger parameters again from device
    public void readTriggerConfig(int nTriggerType, final ReadConfigCallback callback)
    {
        JSONObject readCfgReq = new JSONObject();
        try
        {
            readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
            readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.TriggerPara);
            readCfgReq.put(KBCfgTrigger.JSON_FIELD_TRIGGER_TYPE, nTriggerType);
            readConfig(readCfgReq, callback);
        } catch (JSONException except)
        {
            except.printStackTrace();
            callback.onReadComplete(false, null,
                    new KBException(KBErrorCode.CfgJSONError,
                    "create JSON object failed"));
        }
    }

    //read sensor parameters from device
    //this function will force app to read sensor parameters again from device
    public void readSensorConfig(int nSensorType, final ReadConfigCallback callback)
    {
        JSONObject readCfgReq = new JSONObject();
        try
        {
            readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
            readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.SensorPara);
            readCfgReq.put(KBCfgSensorBase.JSON_SENSOR_TYPE, nSensorType);
            readConfig(readCfgReq, callback);
        } catch (JSONException except)
        {
            except.printStackTrace();
            callback.onReadComplete(false, null,
                    new KBException(KBErrorCode.CfgJSONError,
                            "create JSON object failed"));
        }
    }

    //modify single config
    public void modifyConfig(KBCfgBase cfgTrigger, ActionCallback callback)
    {
        ArrayList<KBCfgBase> cfgList = new ArrayList<>(1);
        cfgList.add(cfgTrigger);
        modifyConfig(cfgList,callback);
    }

    //modify config list
    public void modifyConfig(ArrayList<KBCfgBase> cfgList, ActionCallback callback)
    {
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgStateError, "device is not in connected"));
            }
            return;
        }

        //check if input is valid
        if (!mCfgMgr.checkConfigValid(cfgList)){
            Log.e(LOG_TAG, "verify configuration data invalid");
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters invalid"));
            }
            return;
        }

        //get configruation json
        String strJsonCfgData = null;
        try
        {
            strJsonCfgData = KBCfgHandler.objectsToJsonString(cfgList);
        }catch (JSONException except){
            except.printStackTrace();
        }
        if (strJsonCfgData == null || strJsonCfgData.length() == 0){
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters to json failed"));
            }
            return;
        }

        //create action object
        ActionCommand command = new ActionCommand(ActionType.ACTION_WRITE_CFG, MAX_WRITE_CFG_TIMEOUT);
        command.downDataBuff = strJsonCfgData.getBytes(StandardCharsets.UTF_8);
        command.downDataType = CENT_PERP_TX_JSON_DATA;
        command.actionCallback = callback;
        command.toBeCfgData = cfgList;
        mActionList.add(command);

        //write data
        executeNextAction();
    }

    //send sensor message request to device
    private void sendHexMessage(byte[] msgReq, ActionType actionType, Object callback)
    {
        //create action object
        ActionCommand command = new ActionCommand(actionType, MAX_READ_CFG_TIMEOUT);
        command.downDataBuff = msgReq;
        command.downDataType = CENT_PERP_TX_HEX_DATA;
        command.actionCallback = callback;
        mActionList.add(command);

        //write data
        executeNextAction();
    }


    @SuppressLint("MissingPermission")
    private void handleCentralBLEEvent(int status, int nNewState)
    {
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            if (state == KBConnState.Connecting && nNewState == BluetoothGatt.STATE_CONNECTED)
            {
                mGattConnection.discoverServices();
            }
        }
        else
        {
            if (state == KBConnState.Disconnecting)
            {
                clearGattResource(mCloseReason);

                checkClearGattBuffer(status);
            }
            else if (state == KBConnState.Connecting || state == KBConnState.Connected)
            {
                if (nNewState == BluetoothGatt.STATE_DISCONNECTED)
                {
                    state = KBConnState.Disconnecting;
                    clearGattResource(KBConnectionEvent.ConnException);
                    checkClearGattBuffer(status);
                }
                this.closeBeacon(KBConnectionEvent.ConnException);
            }
        }
    }

    private void handleBeaconEnableSubscribeComplete()
    {
		if (mActionList.size() == 0)
		{
			return;
		}
        ActionCommand action = cancelActionTimer();

        if (mToAddedSubscribeInstance != null)
        {
            this.notifyData2ClassMap.put(mToAddedTriggerType, mToAddedSubscribeInstance);
            mToAddedSubscribeInstance = null;
        }
        else
        {
            this.notifyData2ClassMap.clear();
        }

        if (action.actionCallback != null) {
            ActionCallback tmpAction = (ActionCallback)action.actionCallback;
            tmpAction.onActionComplete(true, null);
        }

        mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
    }

    private void connectingTimeout()
    {
        this.closeBeacon(KBConnectionEvent.ConnTimeout);
    }

    //connect device timeout
    private void actionTimeout() {
        ActionCommand action = mActionList.remove(0);
        mActionDoing = false;

        if (action.actionType == ActionType.ACTION_INIT_READ_CFG) {
            closeBeacon(KBConnectionEvent.ConnTimeout);
        } else {
            if (action.actionCallback == null) {
                return;
            }

            if (action.actionType == ActionType.ACTION_USR_READ_CFG) {
                ((ReadConfigCallback) action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgTimeout,
                        "Read parameters from device timeout"));
            } else if (action.actionType == ActionType.ACTION_WRITE_CFG) {
                ((ActionCallback) action.actionCallback).onActionComplete(false, new KBException(KBErrorCode.CfgTimeout,
                        "Write parameters to device timeout"));
            } else if (action.actionType == ActionType.ACTION_WRITE_CMD) {
                ((ActionCallback) action.actionCallback).onActionComplete(false, new KBException(KBErrorCode.CfgTimeout,
                        "Write command to device timeout"));
            } else if (action.actionType == ActionType.ACTION_SENSOR_READ_INFO) {
                ((ReadSensorInfoCallback) action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgTimeout,
                        "Read sensor from device timeout"));
            } else if (action.actionType == ActionType.ACTION_SENSOR_READ_RECORD) {
                ((ReadSensorInfoCallback) action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgTimeout,
                        "Read sensor from device timeout"));
            }else if (action.actionType == ActionType.ACTION_SENSOR_COMMAND) {
                ((SensorCommandCallback) action.actionCallback).onCommandComplete(false, null, new KBException(KBErrorCode.CfgTimeout,
                        "Read sensor from device timeout"));
            }else if (action.actionType == ActionType.ACTION_ENABLE_NTF) {
                ((ActionCallback) action.actionCallback).onActionComplete(false, new KBException(KBErrorCode.CfgTimeout,
                        "Enable notification timeout"));
            }
        }
    }

    @Override
    public void authStateChange(int authRslt)
    {
        if (authRslt == KBAuthHandler.Failed)
        {
            this.closeBeacon(KBConnectionEvent.ConnAuthFail);
        }
        else if (authRslt == KBAuthHandler.Success)
        {
            this.cancelActionTimer();

            if (state == KBConnState.Connecting) {
                //common para
                int firstReadRoundSubType = 0;
                int secondRoundReadSubType = 0;
                int readCfgTypeNum = 0;

                if (mConnPara.readCommPara){
                    firstReadRoundSubType = (firstReadRoundSubType | KBCfgType.CommonPara);
                    readCfgTypeNum = readCfgTypeNum + 1;
                }

                //slot adv para
                if (mConnPara.readSlotPara){
                    firstReadRoundSubType = (firstReadRoundSubType | KBCfgType.AdvPara);
                    readCfgTypeNum = readCfgTypeNum + 1;
                }

                //trigger para
                if (mConnPara.readTriggerPara){
                    if (readCfgTypeNum < 2)
                    {
                        firstReadRoundSubType = (firstReadRoundSubType | KBCfgType.TriggerPara);
                    }
                    else
                    {
                        secondRoundReadSubType = (secondRoundReadSubType | KBCfgType.TriggerPara);
                    }
                }

                //sensor para
                if (mConnPara.readSensorPara){
                    if (readCfgTypeNum < 2)
                    {
                        firstReadRoundSubType = (firstReadRoundSubType | KBCfgType.SensorPara);
                    }
                    else
                    {
                        secondRoundReadSubType = (secondRoundReadSubType | KBCfgType.SensorPara);
                    }
                }

                JSONObject readCfgReq = new JSONObject();
                try {
                    if (firstReadRoundSubType + secondRoundReadSubType > 0) {
                        if (firstReadRoundSubType > 0) {
                            readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
                            readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, firstReadRoundSubType);
                            String strJsonCfgData = KBCfgHandler.cmdParaToJsonString(readCfgReq);
                            startReadBeaconParameters(ActionType.ACTION_INIT_READ_CFG, strJsonCfgData, null);
                        }

                        if (secondRoundReadSubType > 0) {
                            readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
                            readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, secondRoundReadSubType);
                            String strJsonCfgData = KBCfgHandler.cmdParaToJsonString(readCfgReq);
                            startReadBeaconParameters(ActionType.ACTION_INIT_READ_CFG, strJsonCfgData, null);
                        }
                    }else{
                        if (isSupportSensorDataNotification() && notifyData2ClassMap.size() > 0)
                        {
                            startEnableIndication(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID, true);
                        }
                        else
                        {
                            mMsgHandler.removeMessages(MSG_CONNECT_TIMEOUT);
                            state = KBConnState.Connected;
                            mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_CONNECT_SUCCESS, 200);
                        }
                    }
                }
                catch (JSONException except)
                {
                    except.printStackTrace();
                }
            }
        }
    }

    @Override
    public void writeAuthData(byte[] data)
    {
        this.startWriteCfgValue(data);
    }

    @SuppressLint("MissingPermission")
    private void clearGattResource(int nReason)
    {
        if (state == KBConnState.Disconnecting)
        {
            Log.v(LOG_TAG, "clear gatt connection resource");
            state = KBConnState.Disconnected;
            mGattConnection.close();
            if (delegate != null) {
                delegate.onConnStateChange(this, state, nReason);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void closeBeacon(int nReason)
    {
        mCloseReason = nReason;

        this.cancelActionTimer();
        mMsgHandler.removeMessages(MSG_CONNECT_TIMEOUT);
        mMsgHandler.removeMessages(MSG_CLOSE_CONNECTION_TIMEOUT);

        if (state == KBConnState.Connected || state == KBConnState.Connecting)
        {
            state = KBConnState.Disconnecting;
            //cancel connection
            mGattConnection.disconnect();

            mMsgHandler.sendEmptyMessageDelayed(MSG_CLOSE_CONNECTION_TIMEOUT, 7000);

            if (delegate != null) {
                delegate.onConnStateChange(this, state, mCloseReason);
            }
        }
        else
        {
            if (state != KBConnState.Disconnected)
            {
                Log.e(LOG_TAG, "disconnected kbeacon for reason");
                state = KBConnState.Disconnected;
                if (delegate != null){
                    delegate.onConnStateChange(this, state, mCloseReason);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private boolean startEnableNotification(UUID srvUUID, UUID charUUID)
    {
        BluetoothGattCharacteristic characteristic = getCharacteristicByID(srvUUID,
                charUUID);
        if (characteristic == null) {
            Log.e(LOG_TAG, ":startWriteCharatics getCharacteristicByID failed." + charUUID);
            Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, BluetoothGatt.STATE_DISCONNECTING);
            mMsgHandler.sendMessage(msgCentralEvt);
            return false;
        }

        //set enable
        if (!mGattConnection.setCharacteristicNotification(characteristic, true)) {
            Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, BluetoothGatt.STATE_DISCONNECTING);
            mMsgHandler.sendMessage(msgCentralEvt);
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(KBUtility.CHARACTERISTIC_NOTIFICATION_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return mGattConnection.writeDescriptor(descriptor);
    }

    @SuppressLint("MissingPermission")
    private boolean startEnableIndication(UUID srvUUID, UUID charUUID, boolean bEnable)
    {
        BluetoothGattCharacteristic characteristic = getCharacteristicByID(srvUUID,
                charUUID);
        if (characteristic == null) {
            Log.e(LOG_TAG, ":startWriteCharatics getCharacteristicByID failed." + charUUID);
            Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, BluetoothGatt.STATE_DISCONNECTING);
            mMsgHandler.sendMessage(msgCentralEvt);
            return false;
        }

        //set enable
        if (!mGattConnection.setCharacteristicNotification(characteristic, bEnable)) {
            Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, BluetoothGatt.STATE_DISCONNECTING);
            mMsgHandler.sendMessage(msgCentralEvt);
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(KBUtility.CHARACTERISTIC_NOTIFICATION_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        return mGattConnection.writeDescriptor(descriptor);
    }

    private void sendNextCfgData(int nReqDataSeq)
    {
        ActionCommand action = mActionList.get(0);
        if (action.downDataBuff == null)
        {
            return;
        }

        if (nReqDataSeq >= action.downDataBuff.length)
        {
            Log.v(LOG_TAG, "tx config data complete");
            return;
        }

        //get mtu tag
        byte nPduTag = PDU_TAG_START;
        int nMaxTxDataSize = mAuthHandler.getMtuSize() - MSG_PDU_HEAD_LEN;
        int nDataLen = nMaxTxDataSize;
        if (action.downDataBuff.length <= nMaxTxDataSize)
        {
            nPduTag = PDU_TAG_SINGLE;
            nDataLen = action.downDataBuff.length;
        }
        else if (nReqDataSeq == 0)
        {
            nPduTag = PDU_TAG_START;
            nDataLen = nMaxTxDataSize;
        }
        else if (nReqDataSeq + nMaxTxDataSize < action.downDataBuff.length)
        {
            nPduTag = PDU_TAG_MIDDLE;
            nDataLen = nMaxTxDataSize;
        }
        else if (nReqDataSeq + nMaxTxDataSize >= action.downDataBuff.length)
        {
            nPduTag = PDU_TAG_END;
            nDataLen = action.downDataBuff.length - nReqDataSeq;
        }

        //down data head
        byte[] downData = new byte[nDataLen + MSG_PDU_HEAD_LEN];
        downData[0] = (byte)(((action.downDataType << 4) + nPduTag) & 0xFF);
        byte nNetOrderSeq[] = KBUtility.htonbyte((short)nReqDataSeq);
        downData[1] = nNetOrderSeq[0];
        downData[2] = nNetOrderSeq[1];

        //fill data body
        System.arraycopy(action.downDataBuff, nReqDataSeq, downData, 3, nDataLen);

        //send to device
        Log.v(LOG_TAG, "Tx message to device, seq:" + nReqDataSeq + ", len:" + nDataLen);
        startWriteCfgValue(downData);
    }

    private ActionCommand cancelActionTimer()
    {
        ActionCommand action = null;
        mMsgHandler.removeMessages(MSG_ACTION_TIME_OUT);
        if (mActionList.size() > 0) {
            action = mActionList.remove(0);
        }
        mActionDoing = false;
        return action;
    }

    private void executeNextAction()
    {
        if (mActionDoing){
            Log.v(LOG_TAG, "action busy, now wait device enter idle");
            return;
        }

        ActionCommand action = mActionList.get(0);
        if (action.actionType == ActionType.ACTION_ENABLE_NTF){
            startEnableIndication(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID, true);
        }
        else if (action.actionType == ActionType.ACTION_DISABLE_NTF) {
            startEnableIndication(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID, false);
        }else {
            sendNextCfgData(0);
        }

        mActionDoing = true;
        mMsgHandler.sendEmptyMessageDelayed(MSG_ACTION_TIME_OUT, action.actionTimeout);
    }

    private void configHandleDownCmdAck(byte frameType, byte byDataType, byte[]data)
    {
        short nReqDataSeq = KBUtility.htonshort(data[0], data[1]);
        short nAckCause = KBUtility.htonshort(data[4], data[5]);
        //Log.v(LOG_TAG, "Receive device ack:" + nAckCause + ",seq:" + nReqDataSeq);

        if (mActionList.size() == 0){
            Log.e(LOG_TAG, "action state error");
            return;
        }

        if (nAckCause == BEACON_ACK_CAUSE_CMD_RCV)  //beacon has received the command, now start execute
        {
            if (byDataType == PERP_CENT_TX_JSON_ACK || byDataType == PERP_CENT_TX_HEX_ACK)
            {
                ActionCommand actionCommand = mActionList.get(0);
                if (data.length > DATA_ACK_HEAD_LEN) {
                    System.arraycopy(data, DATA_ACK_HEAD_LEN, actionCommand.receiveData, 0, data.length - DATA_ACK_HEAD_LEN);
                    actionCommand.receiveDataLen = (data.length - DATA_ACK_HEAD_LEN);

                    Log.v(LOG_TAG, "beacon has receive command:" + actionCommand.receiveDataLen);

                    //if has next data, send report ack
                    if (byDataType == PERP_CENT_TX_HEX_ACK) {
                        configSendDataRptAck((short) actionCommand.receiveDataLen, (byte) CENT_PERP_HEX_DATA_RPT_ACK, (short) 0);
                    }else{
                        configSendDataRptAck((short) actionCommand.receiveDataLen, (byte) CENT_PERP_DATA_RPT_ACK, (short) 0);
                    }
                }
            }
        }
        else if (nAckCause == BEACON_ACK_SUCCESS)   //write command receive
        {
            ActionType actionType = mActionList.get(0).actionType;

            if (ActionType.ACTION_SENSOR_READ_INFO == actionType
                    || ActionType.ACTION_SENSOR_READ_RECORD == actionType
                    || ActionType.ACTION_SENSOR_COMMAND == actionType
                    || ActionType.ACTION_USR_READ_CFG == actionType
                    || ActionType.ACTION_INIT_READ_CFG == actionType)
            {
                ActionCommand action = mActionList.get(0);
                if (data.length > DATA_ACK_HEAD_LEN) {
                    System.arraycopy(data, DATA_ACK_HEAD_LEN, action.receiveData, 0, data.length - DATA_ACK_HEAD_LEN);
                    action.receiveDataLen = (data.length - DATA_ACK_HEAD_LEN);
                }

                if (byDataType == PERP_CENT_TX_JSON_ACK) {
                    Log.v(LOG_TAG, "receive json ack complete, data len:" + action.receiveDataLen);

                    handleJsonRptDataComplete();
                }
                else if (byDataType == PERP_CENT_TX_HEX_ACK)
                {
                    Log.v(LOG_TAG, "receive hex ack complete, data len:" + action.receiveDataLen);

                    handleHexRptDataComplete();
                }
            }
            else if (ActionType.ACTION_WRITE_CFG == actionType)
            {
                ActionCommand action = cancelActionTimer();

                //update config to local
                if (action.toBeCfgData != null)
                {
                    try {
                        mCfgMgr.updateDeviceConfig(action.toBeCfgData);
                    }catch (JSONException except){
                        except.printStackTrace();
                    }
                }

                //download data complete
                if (action.actionCallback != null) {
                    ((ActionCallback)action.actionCallback).onActionComplete(true, null);
                }

                mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
            }
            else if (ActionType.ACTION_WRITE_CMD == actionType)
            {
                ActionCommand action = cancelActionTimer();

                //download data complete
                if (action.actionCallback != null) {
                    ((ActionCallback)action.actionCallback).onActionComplete(true, null);
                }
                mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
            }
        }
        else if (nAckCause == BEACON_ACK_EXPECT_NEXT)
        {
            if (mActionDoing)
            {
                this.sendNextCfgData(nReqDataSeq);
            }
        }
        else if (nAckCause == BEACON_ACK_EXE_CMD_CMP)
        {
            Log.v(LOG_TAG, "beacon execute command complete");
            mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
        }
        else   //command failed
        {
            Log.e(LOG_TAG, "beacon command execute failed:" +  nAckCause);
            ActionType actionType = mActionList.get(0).actionType;
            ActionCommand action = cancelActionTimer();

            if (ActionType.ACTION_INIT_READ_CFG == actionType)
            {
                closeBeacon(KBConnectionEvent.ConnException);
                return;
            }
            else if (action.actionCallback != null)
            {
                if (ActionType.ACTION_WRITE_CFG == actionType
                        || ActionType.ACTION_WRITE_CMD == actionType) {
                    ((ActionCallback) action.actionCallback).onActionComplete(false, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Write parameters to device failed"));
                } else if (ActionType.ACTION_USR_READ_CFG == actionType) {
                    ((ReadConfigCallback) action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Read parameters from device failed"));
                } else if (ActionType.ACTION_SENSOR_READ_INFO == actionType) {
                    ((ReadSensorInfoCallback) action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Read sensor info from device failed"));
                } else if (ActionType.ACTION_SENSOR_READ_RECORD == actionType) {
                    ((ReadSensorRspCallback) action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Read sensor record from device failed"));
                } else if (ActionType.ACTION_SENSOR_COMMAND == actionType) {
                    ((SensorCommandCallback) action.actionCallback).onCommandComplete(false,
                            null,
                            new KBException(KBErrorCode.CfgFailed,
                                    nAckCause,
                                    "Execute sensor command failed"));
                }
            }

            mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
        }
    }

	private void configHandleReadDataRpt(byte frameType, byte byDataType, byte[]data)
    {
        boolean bRcvDataCmp = false;
        short nDataSeq = KBUtility.htonshort(data[0], data[1]);
        int nDataPayloadLen = data.length - 2;

        if (mActionList.size() == 0){
            Log.e(LOG_TAG, "receive data report in no action state");
            return;
        }
        ActionCommand action = mActionList.get(0);

        //frame start
        if (frameType == PDU_TAG_START)
        {
            //new read configuration
            System.arraycopy(data, 2, action.receiveData, 0, nDataPayloadLen);
            action.receiveDataLen = nDataPayloadLen;

            //send ack
            configSendDataRptAck((short)action.receiveDataLen, byDataType, (short)0);
        }
        else if (frameType == PDU_TAG_MIDDLE)
        {
            if (nDataSeq != action.receiveDataLen || action.receiveDataLen + nDataPayloadLen > MAX_BUFFER_DATA_SIZE)
            {
                Log.e(LOG_TAG, "Middle receive unknown data sequence:" + nDataSeq + ", expect seq:" + action.receiveDataLen);
                configSendDataRptAck((short)action.receiveDataLen, byDataType, (short)0x1);
            }
            else
            {
                System.arraycopy(data, 2, action.receiveData, action.receiveDataLen, nDataPayloadLen);
                action.receiveDataLen += nDataPayloadLen;

                //Log.v(LOG_TAG, "Middle receive data " + action.receiveDataLen);
                configSendDataRptAck((short)action.receiveDataLen, byDataType, (short)0x0);
            }
        }
        else if (frameType == PDU_TAG_END)
        {
            if (nDataSeq != action.receiveDataLen || action.receiveDataLen + nDataPayloadLen > MAX_BUFFER_DATA_SIZE)
            {
                Log.e(LOG_TAG, "End receive unknown data sequence:" + nDataSeq + ", expect seq:" + action.receiveDataLen);
                configSendDataRptAck((short)action.receiveDataLen, byDataType, (short)0x1);
            }
            else
            {
                System.arraycopy(data, 2, action.receiveData, action.receiveDataLen, nDataPayloadLen);
                action.receiveDataLen += nDataPayloadLen;

                //all data receive complete
                configSendDataRptAck((short)action.receiveDataLen, byDataType, (short)0x0);

                bRcvDataCmp = true;
            }
        }
        else if (frameType == PDU_TAG_SINGLE)
        {
            //new read message command
            System.arraycopy(data, 2, action.receiveData, action.receiveDataLen, nDataPayloadLen);
            action.receiveDataLen += nDataPayloadLen;

            configSendDataRptAck((short)action.receiveDataLen, byDataType, (short)0x0);

            bRcvDataCmp = true;
        }

        if (bRcvDataCmp)
        {
            if (byDataType == PERP_CENT_DATA_RPT) {
                Log.v(LOG_TAG, "receive json report complete:" + nDataSeq + ", expect seq:" + action.receiveDataLen);

                handleJsonRptDataComplete();
            }
            else if (byDataType == PERP_CENT_HEX_DATA_RPT)
            {
                Log.v(LOG_TAG, "receive hex report complete:" + nDataSeq + ", expect seq:" + action.receiveDataLen);
                handleHexRptDataComplete();
            }
        }
    }

    private void configSendDataRptAck(short nAckDataSeq, byte dataType, short cause)
    {
        ByteBuffer ackDataBuff = ByteBuffer.allocate(7);

        //ack head
        byte byAckHead = (byte)((dataType << 4) & 0xFF);
        byAckHead += PDU_TAG_SINGLE;
        ackDataBuff.put(byAckHead);

        //ack seq
        byte []ackDataSeq = KBUtility.htonbyte(nAckDataSeq);
        ackDataBuff.put(ackDataSeq);

        //windows
        short window = 1000;
        byte[] ackWindow = KBUtility.htonbyte(window);
        ackDataBuff.put(ackWindow);

        //cause
        byte[] ackCause = KBUtility.htonbyte(cause);
        ackDataBuff.put(ackCause);

        this.startWriteCfgValue(ackDataBuff.array());
    }

    private void handleHexRptDataComplete()
    {
        if (mActionList.size() == 0){
            Log.e(LOG_TAG, "receive hex report in no action state");
            return;
        }
        ActionCommand action = this.cancelActionTimer();

        if (action.actionType == ActionType.ACTION_SENSOR_READ_INFO) {
            KBRecordInfoRsp readRsp = null;
            boolean readSuccess = true;
            KBException exception = null;

            if (action.receiveDataLen > 0) {
                byte[] validData = new byte[action.receiveDataLen];
                System.arraycopy(action.receiveData, 0, validData, 0, action.receiveDataLen);
                readRsp = (KBRecordInfoRsp)mSensorRecordsMgr.parseSensorResponse(validData);
            }

            if (readRsp == null) {
                readSuccess = false;
                exception = new KBException(KBErrorCode.CfgParseSensorMsgFailed, "parse sensor info response failed");
            }
            if (action.actionCallback != null) {
                ((ReadSensorInfoCallback) action.actionCallback).onReadComplete(readSuccess, readRsp, exception);
            }
        }
        else if (action.actionType == ActionType.ACTION_SENSOR_READ_RECORD) {
            KBRecordDataRsp readRsp = null;
            boolean readSuccess = true;
            KBException exception = null;
            if (action.receiveDataLen > 0) {
                byte[] validData = new byte[action.receiveDataLen];
                System.arraycopy(action.receiveData, 0, validData, 0, action.receiveDataLen);
                readRsp = (KBRecordDataRsp)mSensorRecordsMgr.parseSensorResponse(validData);
            }

            if (readRsp == null) {
                readSuccess = false;
                exception = new KBException(KBErrorCode.CfgParseSensorMsgFailed, "parse sensor info response failed");
            }
            if (action.actionCallback != null) {
                ((ReadSensorRspCallback) action.actionCallback).onReadComplete(readSuccess, readRsp, exception);
            }
        } else if (action.actionType == ActionType.ACTION_SENSOR_COMMAND) {
            byte[] validData = null;
            if (action.receiveDataLen > 0) {
                validData = new byte[action.receiveDataLen];
                System.arraycopy(action.receiveData, 0, validData, 0, action.receiveDataLen);
            }

            ((SensorCommandCallback) action.actionCallback).onCommandComplete(true, validData, null);
        }

        mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
    }

    boolean parseAdvPacket(ScanRecord data, int nRssi, String strName)
    {
        name = strName;
        rssi = nRssi;

        return mAdvPacketMgr.parseAdvPacket(data, rssi, strName);
    }

    private void handleJsonRptDataComplete()
    {
        if (mActionList.size() == 0){
            Log.e(LOG_TAG, "receive hex report in no action state");
            return;
        }
        ActionCommand action = cancelActionTimer();
        if (action.receiveDataLen == 0)
        {
            mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
            return;
        }

        byte[] validData = new byte[action.receiveDataLen];
        System.arraycopy(action.receiveData, 0, validData, 0, action.receiveDataLen);
        String jsonString = new String(validData);
        JSONObject mRspJason = null;
        try
        {
            mRspJason = new JSONObject(jsonString);
        }
        catch(JSONException excp)
        {
            Log.e(LOG_TAG, "Parse Jason network command response failed");
        }

        if (mRspJason == null || mRspJason.length() == 0) {
            Log.e(LOG_TAG, "Parse Json response failed");
            if (action.actionType == ActionType.ACTION_INIT_READ_CFG)
            {
                closeBeacon(KBConnectionEvent.ConnException);
            }
            else if (action.actionType == ActionType.ACTION_USR_READ_CFG)
            {
                if (action.actionCallback != null) {
                    ((ReadConfigCallback)action.actionCallback).onReadComplete(false, null, new KBException(KBErrorCode.CfgReadNull,
                            "Read parameters and return null"));
                }

                //next msg
                mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
            }
        } else {
            //check if is connecting
            if (action.actionType == ActionType.ACTION_INIT_READ_CFG) {
                //update configuration
                mCfgMgr.updateDeviceCfgFromJsonObject(mRspJason);

                //check if has no read information
                if (mActionList.size() > 0) {
                    mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
                }else {
                    //change connection state
                    if (isSupportSensorDataNotification() && notifyData2ClassMap.size() > 0) {
                        //enable indication for receive
                        mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_IND_ENABLE, 100);
                    } else {
                        Log.v(LOG_TAG, "read para complete, connect to device(" + mac + ") success");
                        mMsgHandler.removeMessages(MSG_CONNECT_TIMEOUT);
                        state = KBConnState.Connected;
                        mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_CONNECT_SUCCESS, 200);
                    }
                }
            } else if (action.actionType == ActionType.ACTION_USR_READ_CFG) {
                if (action.actionCallback != null) {
                    //update configuration
                    mCfgMgr.updateDeviceCfgFromJsonObject(mRspJason);

                    ((ReadConfigCallback)action.actionCallback).onReadComplete(true, mRspJason, null);
                }

                //next message
                mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
            } else {
                mMsgHandler.sendEmptyMessageDelayed(MSG_START_EXECUTE_NEXT_MSG, 10);
                Log.e(LOG_TAG, "receive data report error");
            }
        }
    }
	
    private boolean startReadBeaconParameters(ActionType readParaType, String readMsgCmd, final ReadConfigCallback callback)
    {
        //create action object
        ActionCommand command = new ActionCommand(readParaType, MAX_READ_CFG_TIMEOUT);
        command.downDataBuff = readMsgCmd.getBytes(StandardCharsets.UTF_8);
        command.downDataType = CENT_PERP_TX_JSON_DATA;
        command.actionCallback = callback;
        mActionList.add(command);

        //write data
        executeNextAction();

        return true;
    }
	
	 //write configruation to beacon
    @SuppressLint("MissingPermission")
    private boolean startWriteCfgValue(byte[] data)
    {
        BluetoothGattCharacteristic characteristic = getCharacteristicByID(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_WRITE_CHAR_UUID);
        if (characteristic == null) {
            Log.e(LOG_TAG, ":startWriteCfgValue get CharacteristicByID failed.");
            return false;
        }

        characteristic.setValue(data);
        if (!mGattConnection.writeCharacteristic(characteristic)) {
            Log.e(LOG_TAG, ":startWriteCfgValue failed, data len:" + data.length);
            return false;
        }

        return true;
    }


    private BluetoothGattCharacteristic getCharacteristicByID(java.util.UUID srvUUID, java.util.UUID charaID) {
        if (mGattConnection == null) {
            Log.e(LOG_TAG, ":mBleGatt is null");
            return null;
        }

        BluetoothGattService service = mGattConnection.getService(srvUUID);
        if (service == null) {
            Log.e(LOG_TAG, ":getCharacteristicByID get services failed." + srvUUID);
            return null;
        }

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charaID);
        if (characteristic == null) {
            Log.e(LOG_TAG, ":getCharacteristicByID get characteristic failed." + charaID);
            return null;
        }

        return characteristic;
    }

    private Handler mMsgHandler = new Handler(new Handler.Callback() {
        @SuppressLint("MissingPermission")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //start pair scan
                case MSG_CONNECT_TIMEOUT: {
                    connectingTimeout();
                    break;
                }

                case MSG_ACTION_TIME_OUT: {
                    actionTimeout();
                    break;
                }

                case MSG_SYS_CONNECTION_EVT: {
                    handleCentralBLEEvent(msg.arg1, msg.arg2);
                    break;
                }

                case MSG_SERVICES_DISCOVERD: {
                    startEnableNotification(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_NTF_CHAR_UUID);
                    break;
                }

                case MSG_NTF_IND_ENABLE: {
                    startEnableIndication(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID, true);
                    break;
                }

                case MSG_START_AUTHENTICATION: {
                    mAuthHandler.authSendMd5Request(mac, mPassword);
                    break;
                }

                case MSG_BEACON_DATA_RECEIVED: {
                    handleBeaconNtfData((byte[]) msg.obj);
                    break;
                }

                case MSG_NTF_SUBSCRIBE_INDICATION_CMP:{
                    handleBeaconEnableSubscribeComplete();
                    break;
                }

                case MSG_BEACON_INDICATION_RECEIVED:{
                    handleBeaconIndData((byte[]) msg.obj);
                    break;
                }

                case MSG_CLOSE_CONNECTION_TIMEOUT:{
                    clearGattResource(mCloseReason);
                    break;
                }

                case MSG_START_REQUST_MAX_MTU:{
                    mGattConnection.requestMtu(MAX_MTU_SIZE);
                    break;
                }

                case MSG_START_EXECUTE_NEXT_MSG:{
                    if (mActionList.size() > 0){
                        executeNextAction();
                    }
                    break;
                }

                case MSG_NTF_CONNECT_SUCCESS:
                {
                    if (delegate != null){
                        if (KBeacon.this.isConnected()) {
                            delegate.onConnStateChange(KBeacon.this, KBConnState.Connected, KBConnectionEvent.ConnSuccess);
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            return true;
        }
    });


    private void handleBeaconIndData(byte[] data)
    {
        int nDataType = ((data[0] & 0xFF) & 0x3F);
        NotifyDataDelegate sensorInstance = null;
        sensorInstance = this.notifyData2ClassMap.get(KBTriggerType.TriggerNull);

        if (sensorInstance == null) {
            sensorInstance = this.notifyData2ClassMap.get(nDataType);
            if (sensorInstance == null) {
                return;
            }
        }

        try {
            byte[] ntfDataBody = new byte[data.length - 1];
            System.arraycopy(data, 1, ntfDataBody, 0, ntfDataBody.length);
            sensorInstance.onNotifyDataReceived(KBeacon.this, nDataType, ntfDataBody);
        }
        catch (Exception excpt)
        {
            excpt.printStackTrace();
        }
    }

    private void handleBeaconNtfData(byte[] data)
    {
        if (data.length < 2)
        {
            return;
        }

        byte byDataType = (byte)((data[0] >> 4) & 0xF);
        byte byFrameType = (byte)(data[0] & 0xF);
        byte[] ntfDataBody = new byte[data.length - 1];
        System.arraycopy(data, 1, ntfDataBody, 0, ntfDataBody.length);

        if (byDataType == DATA_TYPE_AUTH)
        {
            mAuthHandler.authHandleResponse(ntfDataBody);
        }
        else if (byDataType == PERP_CENT_TX_JSON_ACK || byDataType == PERP_CENT_TX_HEX_ACK)
        {
            this.configHandleDownCmdAck(byFrameType, byDataType, ntfDataBody);
        }
        else if (byDataType == PERP_CENT_DATA_RPT || byDataType == PERP_CENT_HEX_DATA_RPT)
        {
            this.configHandleReadDataRpt(byFrameType, byDataType, ntfDataBody);
        }
    }

    public class KBeaconGattCallback extends android.bluetooth.BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice tmpBlePerp = gatt.getDevice();
            if (!mac.equals(tmpBlePerp.getAddress())) {
                return;
            }

            //update connection handle
            mGattConnection = gatt;

            //check if result is success
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(LOG_TAG, mac + "onConnectionStateChange connection fail, error code:" + status);
                Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, status, newState);
                mMsgHandler.sendMessage(msgCentralEvt);
            } else {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e(LOG_TAG, mac + " onConnectionStateChange success");
                    mMsgHandler.sendEmptyMessageDelayed(MSG_START_REQUST_MAX_MTU, 100);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                    Log.e(LOG_TAG, mac + " onConnectionStateChange detected other gatt fail:" + newState );
                    Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, -1, newState);
                    mMsgHandler.sendMessage(msgCentralEvt);
                }else{
                    Log.e(LOG_TAG, mac + " onConnectionStateChange detected unknown state:" + newState );
                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.v(LOG_TAG, "The max mtu size is:" + mtu);
            }

            if (state == KBConnState.Connecting) {
                Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);
                mMsgHandler.sendMessageDelayed(msgCentralEvt, 300);  //delay 200ms for next action
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothDevice tmpBlePerp = gatt.getDevice();
            if (!mac.equals(tmpBlePerp.getAddress())) {
                return;
            }

            mGattConnection = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //delay 100ms for next action
                mMsgHandler.sendEmptyMessageDelayed(MSG_SERVICES_DISCOVERD, 300);
            } else {
                //error
                Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_FAILURE,
                        BluetoothGatt.STATE_DISCONNECTING);
                mMsgHandler.sendMessage(msgCentralEvt);
            }
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //check if success
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Message msgCentralEvt = mMsgHandler.obtainMessage(MSG_SYS_CONNECTION_EVT, BluetoothGatt.GATT_FAILURE,
                        BluetoothGatt.STATE_DISCONNECTING);
                mMsgHandler.sendMessage(msgCentralEvt);
            }else{
                UUID uuid = descriptor.getCharacteristic().getUuid();
                if (uuid.equals(KBUtility.KB_NTF_CHAR_UUID))
                {
                    if (state == KBConnState.Connecting) {
                        mMsgHandler.sendEmptyMessageDelayed(MSG_START_AUTHENTICATION, 100);
                    }
                }
                else if (uuid.equals(KBUtility.KB_IND_CHAR_UUID))
                {
					if (state == KBConnState.Connecting)
					{
                        Log.v(LOG_TAG, "enable indication success, connection setup complete");
                        mMsgHandler.removeMessages(MSG_CONNECT_TIMEOUT);
                        state = KBConnState.Connected;
                        mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_CONNECT_SUCCESS, 300);
                    }
					else
					{                    
						Log.v(LOG_TAG, "enable indication success");
                    	mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_SUBSCRIBE_INDICATION_CMP, 100);
					}
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            String strMac = gatt.getDevice().getAddress();
            if (!strMac.equals(mac)) {
                Log.e(LOG_TAG, "on characteristic failed.");
                return;
            }

            UUID charUuid = characteristic.getUuid();
            if (charUuid.equals(KBUtility.KB_NTF_CHAR_UUID))
            {
                byte[] ntfData = characteristic.getValue();
                Message msg = mMsgHandler.obtainMessage(MSG_BEACON_DATA_RECEIVED, ntfData);
                mMsgHandler.sendMessage(msg);
            }
            else if (charUuid.equals(KBUtility.KB_IND_CHAR_UUID))
            {
                byte[] ntfData = characteristic.getValue();
                Message msg = mMsgHandler.obtainMessage(MSG_BEACON_INDICATION_RECEIVED, ntfData);
                mMsgHandler.sendMessage(msg);
            }
        };
    }


    //read device sensor summary information
    public void readSensorDataInfo(int sensorType, ReadSensorInfoCallback callback)
    {
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        byte [] reqInfoMsg = new byte[2];
        reqInfoMsg[0] = (byte)KBSensorMsgType.MsgReadSensorInfo;
        reqInfoMsg[1] = (byte)sensorType;

        sendHexMessage(reqInfoMsg,
                ActionType.ACTION_SENSOR_READ_INFO,
                callback);
    }

    //read device sensor record
    public void readSensorRecord(int sensorType,
                                 long nReadRcdNo,
                                 int nReadOption,
                                 int nMaxRecordNum,
                                 final ReadSensorRspCallback readCallback)
    {
        if (state != KBConnState.Connected)
        {
            if (readCallback != null) {
                readCallback.onReadComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        byte[] byMakeReadSensorDataReq = mSensorRecordsMgr.makeReadSensorRecordRequest(sensorType,
                nReadRcdNo,
                nReadOption,
                nMaxRecordNum);

        sendHexMessage(byMakeReadSensorDataReq,
                ActionType.ACTION_SENSOR_READ_RECORD,
                readCallback);
    }

    public void clearSensorRecord(int sensorType,
                                  final SensorCommandCallback readCallback)
    {
        byte [] byClearRequest = new byte[2];

        if (state != KBConnState.Connected)
        {
            if (readCallback != null) {
                readCallback.onCommandComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        byClearRequest[0] = (byte)KBSensorMsgType.MsgClearSensorRecord;
        byClearRequest[1] = (byte)sensorType;

        sendHexMessage(byClearRequest,
                ActionType.ACTION_SENSOR_COMMAND,
                readCallback);
    }

    public void sendSensorRawMessage(byte []message, final SensorCommandCallback readCallback)
    {
        if (state != KBConnState.Connected)
        {
            if (readCallback != null) {
                readCallback.onCommandComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        sendHexMessage(message,
                ActionType.ACTION_SENSOR_COMMAND,
                readCallback);
    }
}