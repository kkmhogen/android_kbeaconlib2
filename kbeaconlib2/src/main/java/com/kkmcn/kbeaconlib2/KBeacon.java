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

import androidx.core.app.ActivityCompat;

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

    //timer
    private final static int MAX_READ_CFG_TIMEOUT = 15*1000;

    private final static int MAX_WRITE_CFG_TIMEOUT = 15*1000;

    //action status
    private final static int ACTION_IDLE = 0x0;
    private final static int ACTION_WRITE_CFG = 0x1;
    private final static int ACTION_WRITE_CMD = 0x2;
    private final static int ACTION_INIT_READ_CFG = 0x3;
    private final static int ACTION_USR_READ_CFG = 0x4;
    private final static int ACTION_READ_SENSOR = 0x5;
    private final static int ACTION_ENABLE_NTF = 0x6;

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

    private ActionCallback mWriteCfgCallback;
    private ActionCallback mWriteCmdCallback;
    private ActionCallback mEnableSubscribeNotifyCallback;
    private ReadConfigCallback mReadCfgCallback;
    private ReadSensorCallback mReadSensorCallback;
    private byte[] mByDownloadDatas;
    private byte mByDownDataType;

    private byte[] mReceiveData;
    private int mReceiveDataLen;
    private ArrayList<KBCfgBase> mToBeCfgData;
    private KBAuthHandler mAuthHandler;
    private KBConnPara mConnPara;
    private int mNextInitReadCfgSubtype;

    private int mCloseReason;
    private KBAdvPacketHandler mAdvPacketMgr;
    private KBCfgHandler mCfgMgr;
    private String mPassword;
    private BluetoothDevice mBleDevice;
    private Context mContext;
    private final BluetoothGattCallback mGattCallback;
    private BluetoothGatt mGattConnection;
    private int mActionStatus;

    private HashMap<Integer, NotifyDataDelegate> notifyData2ClassMap;
    private NotifyDataDelegate mToAddedSubscribeInstance = null;
    private Integer mToAddedTriggerType = 0;

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

    public interface ReadSensorCallback {
        void onReadComplete(boolean bReadResult, byte[] readPara, KBException error);
    }

    public KBeacon(String strMacAddress, Context ctx)
    {
        mac = strMacAddress;
        state = KBConnState.Disconnected;
        mAdvPacketMgr = new KBAdvPacketHandler();
        mCfgMgr = new KBCfgHandler();
        mContext = ctx;
        mGattCallback = new KBeaconGattCallback();
        mAuthHandler = new KBAuthHandler(this);
        mReceiveData = new byte[MAX_BUFFER_DATA_SIZE];
        mReceiveDataLen = 0;
        notifyData2ClassMap = new HashMap<>(10);
        mConnPara = new KBConnPara();
    }

    void setAdvTypeFilter(int nAdvTypeFilter)
    {
        mAdvPacketMgr.setAdvTypeFilter(nAdvTypeFilter);
    }

    void attach2Device(BluetoothDevice bleDevice, KBeaconsMgr beaconMgr)
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            mGattConnection = mBleDevice.connectGatt(mContext, false, mGattCallback);
            Log.v(LOG_TAG, "start connect to device " + mac);

            mPassword = password;
            state = KBConnState.Connecting;

            //cancel action timer
            this.cancelActionTimer();
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
            Log.e(LOG_TAG, "input paramaters false");
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
        if (sensorList.size() == 0){
            return null;
        }else{
            return (KBCfgAdvEddyTLM)sensorList.get(0);
        }
    }

    //get system advertisement configuration
    public KBCfgAdvSystem getSystemAdvCfg()
    {
        ArrayList<KBCfgAdvBase> advList = mCfgMgr.getDeviceSlotsCfgByType(KBAdvType.System);
        if (advList.size() == 0){
            return null;
        }else{
            return (KBCfgAdvSystem)advList.get(0);
        }
    }

    //get KSensor advertisement configuration
    public KBCfgAdvKSensor getKSensorAdvCfg()
    {
        ArrayList<KBCfgAdvBase> sensorList = mCfgMgr.getDeviceSlotsCfgByType(KBAdvType.Sensor);
        if (sensorList.size() == 0){
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
                if (mActionStatus != ACTION_IDLE)
                {
                    if (callback != null) {
                        callback.onActionComplete(false, new KBException(KBErrorCode.CfgBusy, "Device was busy"));
                    }
                    return;
                }
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
                mEnableSubscribeNotifyCallback = callback;
                if (startEnableIndication(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID, true))
                {
                    startNewAction(ACTION_ENABLE_NTF, 3000);
                }else{
                    if (callback != null) {
                        callback.onActionComplete(false, new KBException(KBErrorCode.CfgFailed, "Enable notification failed"));
                    }
                }
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

    //remove subscribed trigger notificaiton
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
                if (mActionStatus != ACTION_IDLE)
                {
                    if (callback != null) {
                        callback.onActionComplete(false, new KBException(KBErrorCode.CfgBusy, "Device was busy"));
                    }
                    return;
                }
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
                mEnableSubscribeNotifyCallback = callback;
                if (startEnableIndication(KBUtility.KB_CFG_SERVICE_UUID, KBUtility.KB_IND_CHAR_UUID, false))
                {
                    startNewAction(ACTION_ENABLE_NTF, 3000);
                }
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
    public void sendCommand(HashMap<String,Object>cmdPara, ActionCallback callback)
    {
        if (mActionStatus != ACTION_IDLE)
        {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgBusy, "Device was busy"));
            }
            return;
        }
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        //save callback
        String strJsonCfgData = KBCfgHandler.cmdParaToJsonString(cmdPara);
        if (strJsonCfgData == null || strJsonCfgData.length() == 0) {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters invalid"));
            }
            return;
        }
        mToBeCfgData = null;
        mWriteCmdCallback = callback;

        mByDownloadDatas = strJsonCfgData.getBytes(StandardCharsets.UTF_8);
        mByDownDataType = CENT_PERP_TX_JSON_DATA;
        startNewAction(ACTION_WRITE_CMD, MAX_READ_CFG_TIMEOUT);
        sendNextCfgData(0);
    }



    //create cfg object from JSON
    public ArrayList<KBCfgBase> createCfgObjectsFromJsonObject(JSONObject jsonObj)
    {
        return KBCfgHandler.createCfgObjectsFromJsonObject(jsonObj);
    }

    //read config by raw json message
    public void readConfig(HashMap<String,Object>readPara, final ReadConfigCallback callback)
    {
        if (mActionStatus != ACTION_IDLE)
        {
            callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgBusy, "Device was busy"));
            return;
        }
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        if (configReadBeaconParamaters(readPara, ACTION_USR_READ_CFG))
        {
            mReadCfgCallback = callback;
        }
        else
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters invalid"));
            }
        }
    }

    //read slot common parameters from device
    //this function will force app to read parameters again from device
    public void readCommonConfig(final ReadConfigCallback callback)
    {
        HashMap<String, Object> readCfgReq = new HashMap<>(10);
        readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
        readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.CommonPara);
        readConfig(readCfgReq, callback);
    }

    //read slot adv parameters from device
    //this function will force app to read parameters again from device
    public void readSlotConfig(int nSlotIndex, final ReadConfigCallback callback)
    {
        HashMap<String, Object> readCfgReq = new HashMap<>(10);
        readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
        readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.AdvPara);
        readCfgReq.put(KBCfgAdvBase.JSON_FIELD_SLOT, nSlotIndex);
        readConfig(readCfgReq, callback);
    }

    //read trigger parameters from device
    //this function will force app to read trigger parameters again from device
    public void readTriggerConfig(int nTriggerType, final ReadConfigCallback callback)
    {
        HashMap<String,Object> readCfgReq = new HashMap<>(5);
        readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
        readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.TriggerPara);
        readCfgReq.put(KBCfgTrigger.JSON_FIELD_TRIGGER_TYPE, nTriggerType);
        readConfig(readCfgReq, callback);
    }

    //read sensor parameters from device
    //this function will force app to read sensor parameters again from device
    public void readSensorConfig(int nSensorType, final ReadConfigCallback callback)
    {
        HashMap<String,Object> readCfgReq = new HashMap<>(5);
        readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
        readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, KBCfgType.SensorPara);
        readCfgReq.put(KBCfgSensorBase.JSON_SENSOR_TYPE, nSensorType);

        readConfig(readCfgReq, callback);
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
        if (mActionStatus != ACTION_IDLE)
        {
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgBusy, "Device was busy"));
            }
            return;
        }
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
        String strJsonCfgData = KBCfgHandler.objectsToJsonString(cfgList);
        if (strJsonCfgData == null || strJsonCfgData.length() == 0){
            if (callback != null) {
                callback.onActionComplete(false, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters to json failed"));
            }
            return;
        }

        //save data
        mByDownloadDatas = strJsonCfgData.getBytes(StandardCharsets.UTF_8);
        mByDownDataType = CENT_PERP_TX_JSON_DATA;
        mWriteCfgCallback = callback;
        mToBeCfgData = cfgList;

        //write data
        startNewAction(ACTION_WRITE_CFG, MAX_WRITE_CFG_TIMEOUT);
        sendNextCfgData(0);
    }

    //send sensor message request to device
    public void sendSensorRequest(byte[] msgReq, ReadSensorCallback callback)
    {
        if (mActionStatus != ACTION_IDLE)
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgBusy, "Device was busy"));
            }
            return;
        }
        if (state != KBConnState.Connected)
        {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgStateError, "Device was disconnected"));
            }
            return;
        }

        if (msgReq == null || msgReq.length == 0) {
            if (callback != null) {
                callback.onReadComplete(false, null, new KBException(KBErrorCode.CfgInputInvalid, "Input parameters invalid"));
            }
            return;
        }

        mReadSensorCallback = callback;
        mByDownloadDatas = msgReq;
        mReceiveDataLen = 0;
        mByDownDataType = CENT_PERP_TX_HEX_DATA;
        startNewAction(ACTION_READ_SENSOR, MAX_READ_CFG_TIMEOUT);
        sendNextCfgData(0);
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
        cancelActionTimer();

        if (mToAddedSubscribeInstance != null)
        {
            this.notifyData2ClassMap.put(mToAddedTriggerType, mToAddedSubscribeInstance);
            mToAddedSubscribeInstance = null;

            if (mEnableSubscribeNotifyCallback != null) {
                ActionCallback tmpAction = mEnableSubscribeNotifyCallback;
                mEnableSubscribeNotifyCallback = null;
                tmpAction.onActionComplete(true, null);
            }
        }
        else
        {
            this.notifyData2ClassMap.clear();
            if (mEnableSubscribeNotifyCallback != null) {
                ActionCallback tmpAction = mEnableSubscribeNotifyCallback;
                mEnableSubscribeNotifyCallback = null;
                tmpAction.onActionComplete(true, null);
            }
        }
    }

    private void connectingTimeout()
    {
        this.closeBeacon(KBConnectionEvent.ConnTimeout);
    }

    private void cancelActionTimer()
    {
        mMsgHandler.removeMessages(MSG_ACTION_TIME_OUT);
        mActionStatus = ACTION_IDLE;
    }

    private boolean startNewAction(int nNewAction, int timeout)
    {
        if (mActionStatus != ACTION_IDLE)
        {
            return false;
        }

        mActionStatus = nNewAction;
        if (timeout > 0)
        {
            mMsgHandler.sendEmptyMessageDelayed(MSG_ACTION_TIME_OUT, timeout);
        }

        return true;
    }

    //connect device timeout
    private void actionTimeout()
    {
        if (mActionStatus == ACTION_INIT_READ_CFG)
        {
            mActionStatus = ACTION_IDLE;
            closeBeacon(KBConnectionEvent.ConnTimeout);
        }
        else if (mActionStatus == ACTION_USR_READ_CFG)
        {
            mActionStatus = ACTION_IDLE;
            if (mReadCfgCallback != null){
                ReadConfigCallback tempCallback = mReadCfgCallback;
                mReadCfgCallback = null;
                tempCallback.onReadComplete(false, null, new KBException(KBErrorCode.CfgTimeout,
                        "Read parameters from device timeout"));
            }
        }
        else if (mActionStatus == ACTION_WRITE_CFG)
        {
            mActionStatus = ACTION_IDLE;
            if (mWriteCfgCallback != null)
            {
                ActionCallback tmpAction = mWriteCfgCallback;
                mWriteCfgCallback = null;
                tmpAction.onActionComplete(false, new KBException(KBErrorCode.CfgTimeout,
                        "Write parameters to device timeout"));
            }
        }
        else if (mActionStatus == ACTION_WRITE_CMD)
        {
            mActionStatus = ACTION_IDLE;
            if (mWriteCmdCallback != null)
            {
                ActionCallback tmpAction = mWriteCfgCallback;
                mWriteCfgCallback = null;
                tmpAction.onActionComplete(false, new KBException(KBErrorCode.CfgTimeout,
                        "Write command to device timeout"));
            }
        }
        else if (mActionStatus == ACTION_READ_SENSOR)
        {
            mActionStatus = ACTION_IDLE;
            if (mReadSensorCallback != null)
            {
                ReadSensorCallback tempCallback = mReadSensorCallback;
                mReadSensorCallback = null;
                tempCallback.onReadComplete(false, null, new KBException(KBErrorCode.CfgTimeout,
                        "Read parameters from device timeout"));
            }
        }
        else if (mActionStatus == ACTION_ENABLE_NTF)
        {
            mActionStatus = ACTION_IDLE;
            if (mEnableSubscribeNotifyCallback != null)
            {
                ActionCallback tmpAction = mEnableSubscribeNotifyCallback;
                mEnableSubscribeNotifyCallback = null;
                tmpAction.onActionComplete(false, new KBException(KBErrorCode.CfgTimeout,
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
                int readCfgTypeNum = 0;
                mNextInitReadCfgSubtype = 0;
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
                        mNextInitReadCfgSubtype = (mNextInitReadCfgSubtype | KBCfgType.TriggerPara);
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
                        mNextInitReadCfgSubtype = (mNextInitReadCfgSubtype | KBCfgType.SensorPara);
                    }
                }

                if (firstReadRoundSubType > 0) {
                    HashMap<String, Object> readCfgReq = new HashMap<>(10);
                    readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
                    readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, firstReadRoundSubType);
                    configReadBeaconParamaters(readCfgReq, ACTION_INIT_READ_CFG);
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
        if (mByDownloadDatas == null)
        {
            return;
        }

        if (nReqDataSeq >= mByDownloadDatas.length)
        {
            Log.v(LOG_TAG, "tx config data complete");
            return;
        }

        //get mtu tag
        byte nPduTag = PDU_TAG_START;
        int nMaxTxDataSize = mAuthHandler.getMtuSize() - MSG_PDU_HEAD_LEN;
        int nDataLen = nMaxTxDataSize;
        if (mByDownloadDatas.length <= nMaxTxDataSize)
        {
            nPduTag = PDU_TAG_SINGLE;
            nDataLen = mByDownloadDatas.length;
        }
        else if (nReqDataSeq == 0)
        {
            nPduTag = PDU_TAG_START;
            nDataLen = nMaxTxDataSize;
        }
        else if (nReqDataSeq + nMaxTxDataSize < mByDownloadDatas.length)
        {
            nPduTag = PDU_TAG_MIDDLE;
            nDataLen = nMaxTxDataSize;
        }
        else if (nReqDataSeq + nMaxTxDataSize >= mByDownloadDatas.length)
        {
            nPduTag = PDU_TAG_END;
            nDataLen = mByDownloadDatas.length - nReqDataSeq;
        }

        //down data head
        byte[] downData = new byte[nDataLen + MSG_PDU_HEAD_LEN];
        downData[0] = (byte)(((mByDownDataType << 4) + nPduTag) & 0xFF);
        byte nNetOrderSeq[] = KBUtility.htonbyte((short)nReqDataSeq);
        downData[1] = nNetOrderSeq[0];
        downData[2] = nNetOrderSeq[1];

        //fill data body
        System.arraycopy(mByDownloadDatas, nReqDataSeq, downData, 3, nDataLen);

        //send to device
        Log.v(LOG_TAG, "tx data seq:" + nReqDataSeq);
        startWriteCfgValue(downData);
    }

    private void configHandleDownCmdAck(byte frameType, byte byDataType, byte[]data)
    {
        short nReqDataSeq = KBUtility.htonshort(data[0], data[1]);
        short nAckCause = KBUtility.htonshort(data[4], data[5]);

        if (nAckCause == BEACON_ACK_CAUSE_CMD_RCV)  //beacon has received the command, now start execute
        {
            if (byDataType == PERP_CENT_TX_JSON_ACK || byDataType == PERP_CENT_TX_HEX_ACK)
            {
                if (data.length > DATA_ACK_HEAD_LEN) {
                    System.arraycopy(data, DATA_ACK_HEAD_LEN, mReceiveData, 0, data.length - DATA_ACK_HEAD_LEN);
                    mReceiveDataLen = (data.length - DATA_ACK_HEAD_LEN);

                    Log.v(LOG_TAG, "beacon has receive command:" + mReceiveDataLen);

                    //if has next data, send report ack
                    if (byDataType == PERP_CENT_TX_HEX_ACK) {
                        configSendDataRptAck((short) mReceiveDataLen, (byte) CENT_PERP_HEX_DATA_RPT_ACK, (short) 0);
                    }else{
                        configSendDataRptAck((short) mReceiveDataLen, (byte) CENT_PERP_DATA_RPT_ACK, (short) 0);
                    }
                }
            }
        }
        else if (nAckCause == BEACON_ACK_SUCCESS)   //write command receive
        {
            if (ACTION_READ_SENSOR == mActionStatus
                || ACTION_USR_READ_CFG == mActionStatus
                || ACTION_INIT_READ_CFG == mActionStatus)
            {
                if (data.length > DATA_ACK_HEAD_LEN) {
                    System.arraycopy(data, DATA_ACK_HEAD_LEN, mReceiveData, 0, data.length - DATA_ACK_HEAD_LEN);
                    mReceiveDataLen = (data.length - DATA_ACK_HEAD_LEN);
                }

                if (byDataType == PERP_CENT_TX_JSON_ACK) {
                    handleJsonRptDataComplete();
                }
                else if (byDataType == PERP_CENT_TX_HEX_ACK)
                {
                    handleHexRptDataComplete();
                }
            }
            else if (ACTION_WRITE_CFG == mActionStatus)
            {
                cancelActionTimer();

                //update config to local
                if (mToBeCfgData != null)
                {
                    mCfgMgr.updateDeviceConfig(mToBeCfgData);
                    mToBeCfgData = null;
                }

                //download data complete
                if (mWriteCfgCallback != null) {
                    ActionCallback tmpAction = mWriteCfgCallback;
                    mWriteCfgCallback = null;

                    tmpAction.onActionComplete(true, null);
                }
            }
            else if (ACTION_WRITE_CMD == mActionStatus)
            {
                cancelActionTimer();

                //download data complete
                if (mWriteCmdCallback != null) {
                    ActionCallback tmpAction = mWriteCmdCallback;
                    mWriteCmdCallback = null;
                    tmpAction.onActionComplete(true, null);
                }
            }
        }
        else if (nAckCause == BEACON_ACK_EXPECT_NEXT)
        {
            if (ACTION_IDLE != mActionStatus)
            {
                this.sendNextCfgData(nReqDataSeq);
            }
        }
        else if (nAckCause == BEACON_ACK_EXE_CMD_CMP)
        {
            Log.v(LOG_TAG, "beacon execute command complete");
        }
        else   //command failed
        {
            Log.e(LOG_TAG, "beacon command execute failed:" +  nAckCause);

            if (ACTION_INIT_READ_CFG == mActionStatus) {
                cancelActionTimer();

                closeBeacon(KBConnectionEvent.ConnException);
            }
            else if (ACTION_WRITE_CFG == mActionStatus)
            {
                cancelActionTimer();
                mToBeCfgData = null;

                if (mWriteCfgCallback != null) {
                    ActionCallback tmpAction = mWriteCfgCallback;
                    mWriteCfgCallback = null;
                    tmpAction.onActionComplete(false, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Write parameters to device failed"));
                }
            }
            else if (ACTION_WRITE_CMD == mActionStatus)
            {
                cancelActionTimer();

                if (mWriteCmdCallback != null) {
                    ActionCallback tmpAction = mWriteCmdCallback;
                    mWriteCmdCallback = null;
                    tmpAction.onActionComplete(false, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Write command to device failed"));
                }
            }
            else if (ACTION_USR_READ_CFG == mActionStatus) {
                cancelActionTimer();

                //read config data failed
                if (mReadCfgCallback != null) {
                    ReadConfigCallback tempCallback = mReadCfgCallback;
                    mReadCfgCallback = null;
                    tempCallback.onReadComplete(false, null, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Read parameters from device failed"));
                }
            }
            else if (ACTION_READ_SENSOR == mActionStatus) {
                cancelActionTimer();

                //read config data failed
                if (mReadSensorCallback != null) {
                    ReadSensorCallback tempCallback = mReadSensorCallback;
                    mReadSensorCallback = null;
                    Log.v(LOG_TAG, "beacon sensor read execute failed");
                    tempCallback.onReadComplete(false, null, new KBException(KBErrorCode.CfgFailed,
                            nAckCause,
                            "Read parameters from device failed"));
                }
            }
        }
    }

	private void configHandleReadDataRpt(byte frameType, byte byDataType, byte[]data)
    {
        boolean bRcvDataCmp = false;
        short nDataSeq = KBUtility.htonshort(data[0], data[1]);
        int nDataPayloadLen = data.length - 2;
        //frame start
        if (frameType == PDU_TAG_START)
        {
            //new read configruation
            System.arraycopy(data, 2, mReceiveData, 0, nDataPayloadLen);
            mReceiveDataLen = nDataPayloadLen;

            //send ack
            configSendDataRptAck((short)mReceiveDataLen, byDataType, (short)0);
        }
        else if (frameType == PDU_TAG_MIDDLE)
        {
            if (nDataSeq != mReceiveDataLen || mReceiveDataLen + nDataPayloadLen > MAX_BUFFER_DATA_SIZE)
            {
                Log.v(LOG_TAG, "Middle receive unknown data sequence:" + nDataSeq + ", expect seq:" + mReceiveDataLen);
                configSendDataRptAck((short)mReceiveDataLen, byDataType, (short)0x1);
            }
            else
            {
                System.arraycopy(data, 2, mReceiveData, mReceiveDataLen, nDataPayloadLen);
                mReceiveDataLen += nDataPayloadLen;

                configSendDataRptAck((short)mReceiveDataLen, byDataType, (short)0x0);
            }
        }
        else if (frameType == PDU_TAG_END)
        {
            if (nDataSeq != mReceiveDataLen || mReceiveDataLen + nDataPayloadLen > MAX_BUFFER_DATA_SIZE)
            {
                Log.v(LOG_TAG, "End receive unknown data sequence:" + nDataSeq + ", expect seq:" + mReceiveDataLen);
                configSendDataRptAck((short)mReceiveDataLen, byDataType, (short)0x1);
            }
            else
            {
                System.arraycopy(data, 2, mReceiveData, mReceiveDataLen, nDataPayloadLen);
                mReceiveDataLen += nDataPayloadLen;

                //configSendDataRptAck((short)mReceiveDataLen, byDataType, (short)0x0);
                bRcvDataCmp = true;
            }
        }
        else if (frameType == PDU_TAG_SINGLE)
        {
            //new read message command
            System.arraycopy(data, 2, mReceiveData, mReceiveDataLen, nDataPayloadLen);
            mReceiveDataLen += nDataPayloadLen;

            //configSendDataRptAck((short)mReceiveDataLen, byDataType, (short)0x0);
            bRcvDataCmp = true;
        }

        if (bRcvDataCmp)
        {
            Log.v(LOG_TAG, "receive report data complete:" + nDataSeq + ", expect seq:" + mReceiveDataLen);

            if (byDataType == PERP_CENT_DATA_RPT) {
                handleJsonRptDataComplete();
            }
            else if (byDataType == PERP_CENT_HEX_DATA_RPT)
            {
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
        if (mActionStatus == ACTION_READ_SENSOR) {
            this.cancelActionTimer();

            byte[] validData = null;
            if (mReceiveDataLen > 0) {
                validData = new byte[mReceiveDataLen];
                System.arraycopy(mReceiveData, 0, validData, 0, mReceiveDataLen);
                if (validData[0] == 0x10) {
                    printBleLogMessage(validData);
                    return;
                }
            }

            if (mReadSensorCallback != null) {

                ReadSensorCallback tempCallback = mReadSensorCallback;
                mReadSensorCallback = null;
                tempCallback.onReadComplete(true, validData, null);
            }
        }
    }

    private void printBleLogMessage(byte[] logMessage)
    {
        byte[] byStrMessage = new byte[logMessage.length -1];
        System.arraycopy(logMessage, 1, byStrMessage, 0, byStrMessage.length);

        String jstrLogString = new String(byStrMessage);
        Log.e(LOG_TAG, jstrLogString);
    }

    boolean parseAdvPacket(ScanRecord data, int nRssi, String strName)
    {
        name = strName;
        rssi = nRssi;

        return mAdvPacketMgr.parseAdvPacket(data, rssi, strName);
    }

    private void handleJsonRptDataComplete()
    {
        if (mReceiveDataLen == 0)
        {
            return;
        }

        byte[] validData = new byte[mReceiveDataLen];
        System.arraycopy(mReceiveData, 0, validData, 0, mReceiveDataLen);
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
            if (mActionStatus == ACTION_INIT_READ_CFG) {
                closeBeacon(KBConnectionEvent.ConnException);
            } else if (mActionStatus == ACTION_USR_READ_CFG) {
                this.cancelActionTimer();
                if (mReadCfgCallback != null) {
                    ReadConfigCallback tempCallback = mReadCfgCallback;
                    mReadCfgCallback = null;

                    tempCallback.onReadComplete(false, null, new KBException(KBErrorCode.CfgReadNull,
                            "Read parameters and return null"));
                }
            }
        } else {
            //check if is connecting
            if (mActionStatus == ACTION_INIT_READ_CFG) {
                this.cancelActionTimer();

                //invalid connection timer
                mMsgHandler.removeMessages(MSG_CONNECT_TIMEOUT);

                //update configuration
                mCfgMgr.updateDeviceCfgFromJsonObject(mRspJason);

                //check if has no read information
                if (mNextInitReadCfgSubtype != 0) {
                    HashMap<String, Object> readCfgReq = new HashMap<>(10);
                    readCfgReq.put(KBCfgBase.JSON_MSG_TYPE_KEY, KBCfgBase.JSON_MSG_TYPE_GET_PARA);
                    readCfgReq.put(KBCfgBase.JSON_FIELD_SUBTYPE, mNextInitReadCfgSubtype);
                    mNextInitReadCfgSubtype = 0;
                    configReadBeaconParamaters(readCfgReq, ACTION_INIT_READ_CFG);
                }else {
                    //change connection state
                    if (isSupportSensorDataNotification() && notifyData2ClassMap.size() > 0) {
                        //enable indication for receive
                        mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_IND_ENABLE, 100);
                    } else {
                        Log.v(LOG_TAG, "read para complete, connect to device(" + mac + ") success");
                        state = KBConnState.Connected;
                        mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_CONNECT_SUCCESS, 200);
                    }
                }
            } else if (mActionStatus == ACTION_USR_READ_CFG) {
                this.cancelActionTimer();

                if (mReadCfgCallback != null) {
                    ReadConfigCallback tempCallback = mReadCfgCallback;
                    mReadCfgCallback = null;

                    //update configuration
                    mCfgMgr.updateDeviceCfgFromJsonObject(mRspJason);

                    tempCallback.onReadComplete(true, mRspJason, null);
                }
            } else {
                this.cancelActionTimer();

                Log.e(LOG_TAG, "receive data report error");
            }
        }
    }
	
    private boolean configReadBeaconParamaters(HashMap<String, Object> readCfgReq, int nActionType)
    {
        if (ACTION_IDLE != mActionStatus)
        {
            Log.e(LOG_TAG, "last action command not complete");
            return false;
        }

        String strJsonCfgData = KBCfgBase.HashMap2JsonString(readCfgReq);
        if (strJsonCfgData == null || strJsonCfgData.length() == 0)
        {
            return false;
        }

        mByDownloadDatas = strJsonCfgData.getBytes(StandardCharsets.UTF_8);
        mByDownDataType = CENT_PERP_TX_JSON_DATA;
        mReceiveDataLen = 0;
        startNewAction(nActionType, MAX_READ_CFG_TIMEOUT);

        sendNextCfgData(0);

        return true;
    }
	
	 //write configuration to beacon
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
                        state = KBConnState.Connected;
                        mMsgHandler.sendEmptyMessageDelayed(MSG_NTF_CONNECT_SUCCESS, 300);
                    }
					else
					{                    
						Log.v(LOG_TAG, "enable indication success, connection setup complete");
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
                mMsgHandler.sendMessageDelayed(msg, 50);
            }
            else if (charUuid.equals(KBUtility.KB_IND_CHAR_UUID))
            {
                byte[] ntfData = characteristic.getValue();
                Message msg = mMsgHandler.obtainMessage(MSG_BEACON_INDICATION_RECEIVED, ntfData);
                mMsgHandler.sendMessage(msg);
            }
        };
    }
}