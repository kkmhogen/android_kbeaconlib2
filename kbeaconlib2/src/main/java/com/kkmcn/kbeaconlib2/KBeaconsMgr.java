package com.kkmcn.kbeaconlib2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("MissingPermission")
public class KBeaconsMgr {
    private final static String TAG = "beacon.KBeaconsMgr";


    public static final int SCAN_MODE_BALANCED = 1;
    public static final int SCAN_MODE_LOW_LATENCY = 2;
    public static final int SCAN_MODE_LOW_POWER = 0;
    public static final int SCAN_MODE_OPPORTUNISTIC = -1;

    public final static int SCAN_ERROR_NO_PERMISSION = 0x1;
    public final static int SCAN_ERROR_BLE_NOT_ENABLE = 0x2;
    public final static int SCAN_ERROR_UNKNOWN = 0x3;

    public final static int BLEStatePowerOn = 0;
    public final static int BLEStatePowerOff = 1;
    public final static int BLEStateUnknown = 2;



    public KBeaconMgrDelegate delegate;

    private ScanSettings.Builder scanSetting = null;

    private String scanNameFilter;
    private int advTypeFilter;
    private String advMacFilter;
    private boolean nameFilterIgnoreCase;

    private Integer scanMinRssiFilter = -100;

    private int mScanMode = ScanSettings.SCAN_MODE_BALANCED;
    // false: legacy + extended PHY dual scan when hardware supports; true: legacy only
    private boolean mLegacyOnlyScan = false;

    private final Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    //all scaned beacons, include kkm beacon and other beacons
    private HashMap<String, KBeacon> mCbAllBeacons;

    //all kkm beacons
    private HashMap<String, KBeacon> mCbKBeacons;

    //the beacons that need notify to UI
    private HashMap<String, KBeacon> mCBNtfBeacons;

    private IntentFilter mIntentFilter;

    private NPhoneScancallback mLegacyScanCallback;
    private NPhoneScancallback mExtendedScanCallback;

    private boolean mIsLegacyScanning;
    private boolean mIsExtendedScanning;

    @SuppressLint("StaticFieldLeak")
    private static KBeaconsMgr sharedStaticBeaconMgr = null;

    private final int MSG_NEW_DEVICE_FOUND = 201;
    private final int MSG_DELAY_ADV_REPORT = 202;
    private final int MAX_DELAY_TIMEOUT_INTERVAL = 300;

    public interface KBeaconMgrDelegate {
        void onBeaconDiscovered(KBeacon[] beacons);

        void onCentralBleStateChang(int nNewState);

        void onScanFailed(int errorCode);
    }

    public static KBeaconsMgr sharedBeaconManager(Context ctx)
    {
        if (sharedStaticBeaconMgr == null)
        {
            sharedStaticBeaconMgr = new KBeaconsMgr(ctx);
            if (!sharedStaticBeaconMgr.initialize()){
                sharedStaticBeaconMgr = null;
            }
        }

        return sharedStaticBeaconMgr;
    }

    //remove the single beacon manager instance
    public static void clearBeaconManager() {
        if (sharedStaticBeaconMgr != null)
        {
            sharedStaticBeaconMgr.clearBeacons();
            if (sharedStaticBeaconMgr.mReceiver != null) {
                sharedStaticBeaconMgr.mContext.unregisterReceiver(sharedStaticBeaconMgr.mReceiver);
                sharedStaticBeaconMgr.mReceiver = null;
            }
            sharedStaticBeaconMgr.delegate = null;
            sharedStaticBeaconMgr = null;
        }
    }


    private KBeaconsMgr(Context c){
        mContext = c;
    }

    private boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        mIntentFilter = makeGattUpdateIntentFilter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires explicit export flag; NOT_EXPORTED is enough for system broadcasts
            mContext.registerReceiver(mReceiver, mIntentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            mContext.registerReceiver(mReceiver, mIntentFilter);
        }
        mCbAllBeacons = new HashMap<>(100);

        mCbKBeacons = new HashMap<>(50);

        mCBNtfBeacons = new HashMap<>(10);

        mLegacyScanCallback = new NPhoneScancallback("legacy");
        mExtendedScanCallback = new NPhoneScancallback("extended");

        return true;
    }

    public BluetoothManager getBluetoothManager()
    {
        return mBluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter()
    {
        return mBluetoothAdapter;
    }

    public HashMap Beacons()
    {
        return mCbKBeacons;
    }

    public void removeBeacon(String strMacAddress)
    {
        KBeacon beacon = mCbKBeacons.get(strMacAddress);
        if (beacon != null){
            beacon.disconnect();
        }
        mCbKBeacons.remove(strMacAddress);
        mCbAllBeacons.remove(strMacAddress);
        mCBNtfBeacons.remove(strMacAddress);
    }

    public void clearBeacons() {
        for (Map.Entry<String, KBeacon> entry : mCbKBeacons.entrySet()) {
            KBeacon kbeacon = entry.getValue();
            kbeacon.disconnect();
        }

        mCbAllBeacons.clear();

        mCbKBeacons.clear();

        mCBNtfBeacons.clear();
    }

    public boolean isBluetoothEnable()
    {
        if (mBluetoothAdapter == null){
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    public void setScanNameFilter(String strFilterName, boolean bCaseIgnore)
    {
        scanNameFilter = strFilterName;
        nameFilterIgnoreCase = bCaseIgnore;
    }

    public void setScanAdvTypeFilter(int advType)
    {
        advTypeFilter = advType;
    }

    public void setScanMacFilter(String strMacAddress)
    {
        advMacFilter = strMacAddress;
    }

    public String getScanMacFilter()
    {
        return advMacFilter;
    }

    public String getScanNameFilter()
    {
        return scanNameFilter;
    }

    public void setScanMinRssiFilter(Integer nMinRssiFilter)
    {
        scanMinRssiFilter = nMinRssiFilter;
    }

    public void setScanMode(int nScanMode)
    {
        if (nScanMode == SCAN_MODE_BALANCED || nScanMode == SCAN_MODE_LOW_LATENCY
        || nScanMode == SCAN_MODE_LOW_POWER || nScanMode == SCAN_MODE_OPPORTUNISTIC) {
            mScanMode = nScanMode;
        }

        if (scanSetting == null) {
            scanSetting = new ScanSettings.Builder().setScanMode(mScanMode);
        }
    }

    /**
     * Control scan PHY coverage.
     * @param legacyOnly true: legacy advertisements only (iBeacon, etc.);
     *                   false: legacy scan plus a second extended-PHY scan when supported.
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void setScanLegacyMode(boolean legacyOnly)
    {
        mLegacyOnlyScan = legacyOnly;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public boolean isLe2MPhySupported()
    {
        if (KBUtility.isMOhone()) {
            return mBluetoothAdapter.isLe2MPhySupported() && mBluetoothAdapter.isLeExtendedAdvertisingSupported();
        }else{
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public boolean isLeCodedPhySupported()
    {
        if (KBUtility.isMOhone()) {
            return mBluetoothAdapter.isLeCodedPhySupported();
        }else{
            return false;
        }
    }

    public void setScanSetting(ScanSettings.Builder scanSetting)
    {
        this.scanSetting = scanSetting;
    }

    public Integer getScanMinRssiFilter()
    {
        return scanMinRssiFilter;
    }

    public KBeacon getBeacon(String strMacAddress)
    {
        return mCbKBeacons.get(strMacAddress);
    }

    @SuppressLint("MissingPermission")
    public int startScanning()
    {
        if (!mBluetoothAdapter.isEnabled())
        {
            Log.e(TAG, "No location permission for scanning.");
            return SCAN_ERROR_BLE_NOT_ENABLE;
        }

        try {
            BluetoothLeScanner scaner = mBluetoothAdapter.getBluetoothLeScanner();
            if (scaner == null) {
                return SCAN_ERROR_UNKNOWN;
            }

            stopScanning();

            List<ScanFilter> filterList = buildScanFilters();

            ScanSettings legacySettings = new ScanSettings.Builder()
                    .setScanMode(mScanMode)
                    .setLegacy(true)
                    .build();
            scaner.startScan(filterList, legacySettings, mLegacyScanCallback);
            mIsLegacyScanning = true;
            Log.i(TAG, "legacy BLE scan started");

            if (!mLegacyOnlyScan && isExtendedPhyScanSupported()) {
                try {
                    ScanSettings extendedSettings = new ScanSettings.Builder()
                            .setScanMode(mScanMode)
                            .setLegacy(false)
                            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                            .build();
                    scaner.startScan(filterList, extendedSettings, mExtendedScanCallback);
                    mIsExtendedScanning = true;
                    Log.i(TAG, "extended PHY BLE scan started");
                } catch (RuntimeException extendedExcp) {
                    Log.w(TAG, "extended PHY scan not started, legacy scan continues", extendedExcp);
                }
            }

            Log.i(TAG, "BLE scan started (legacy=" + mIsLegacyScanning + ", extended=" + mIsExtendedScanning + ")");
        }catch (RuntimeException excp)
        {
            Log.e(TAG, "start scan error", excp);
            return SCAN_ERROR_UNKNOWN;
        }

        return 0;
    }

    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> filterList = new ArrayList<>(4);
        ScanFilter.Builder filter1 = new ScanFilter.Builder().setServiceUuid(KBUtility.PARCE_UUID_EDDYSTONE);
        ScanFilter.Builder filter2 = new ScanFilter.Builder().setServiceUuid(KBUtility.PARCE_UUID_EXT_DATA);
        byte[] iBeaconFilter = {0x02, 0x15};
        ScanFilter.Builder filter3 = new ScanFilter.Builder().setManufacturerData(KBUtility.APPLE_MANUFACTURE_ID,
                iBeaconFilter);
        ScanFilter.Builder filter4 = new ScanFilter.Builder().setManufacturerData(KBUtility.KKM_MANUFACTURE_ID,
                new byte[0]);
        filterList.add(filter1.build());
        filterList.add(filter2.build());
        filterList.add(filter3.build());
        filterList.add(filter4.build());
        return filterList;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private boolean isExtendedPhyScanSupported() {
        if (!KBUtility.isMOhone()) {
            return false;
        }
        return mBluetoothAdapter.isLeCodedPhySupported()
                || (mBluetoothAdapter.isLe2MPhySupported()
                && mBluetoothAdapter.isLeExtendedAdvertisingSupported());
    }

    public boolean isScanning()
    {
        return mIsLegacyScanning || mIsExtendedScanning;
    }

    public void stopScanning()
    {
        BluetoothLeScanner scaner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scaner == null) {
            return;
        }
        if (mIsLegacyScanning) {
            Log.i(TAG, "stop legacy BLE scan");
            scaner.stopScan(mLegacyScanCallback);
            mIsLegacyScanning = false;
        }
        if (mIsExtendedScanning) {
            Log.i(TAG, "stop extended PHY BLE scan");
            scaner.stopScan(mExtendedScanCallback);
            mIsExtendedScanning = false;
        }
    }

    BluetoothManager getBleCentralMgr()
    {
        return mBluetoothManager;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentFilter;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (delegate == null){
                    return;
                }
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_TURNING_ON) {
                    delegate.onCentralBleStateChang(BLEStatePowerOn);
                }
                else if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    delegate.onCentralBleStateChang(BLEStatePowerOff);
                }
                else
                {
                    delegate.onCentralBleStateChang(BLEStateUnknown);
                }
            }
        }
    };

    private class NPhoneScancallback extends ScanCallback
    {
        private final String scanTag;

        NPhoneScancallback(String scanTag) {
            this.scanTag = scanTag;
        }

        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null) {
                onDeviceFound(result);
            }
        }

        public void onBatchScanResults(List< ScanResult > results) {
            if (!results.isEmpty()){
                for(ScanResult rslt: results) {
                    onScanResult(10, rslt);
                }
            }else{
                Log.e(TAG, "Scan [" + scanTag + "] batch found 0 result");
            }
        }

        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan [" + scanTag + "] failed:" + errorCode);
            if ("legacy".equals(scanTag)) {
                mIsLegacyScanning = false;
                if (delegate != null){
                    delegate.onScanFailed(errorCode);
                }
            } else {
                mIsExtendedScanning = false;
            }
        }
    }

    private void onDeviceFound(final ScanResult rslt) {
        int rssi = rslt.getRssi();
        if (rssi > 20 || rssi < -100){
            rssi = -100;
        }
        if (rssi < scanMinRssiFilter) {
            return;
        }

        boolean bFilter = true;
        boolean bNameFilterEnable = true, bMacFilterEnable = true;
        ScanRecord record = rslt.getScanRecord();
        if (scanNameFilter != null && !scanNameFilter.isEmpty() && record != null) {
            String strDevName = record.getDeviceName();
            if (strDevName != null){
                if (nameFilterIgnoreCase)
                {
                    String strDevNameLowCase = strDevName.toLowerCase();
                    String strFilterLowCase = scanNameFilter.toLowerCase();
                    if (strDevNameLowCase.contains(strFilterLowCase)) {
                        bFilter = false;
                    }
                }
                else if (strDevName.contains(scanNameFilter)) {
                    bFilter = false;
                }
            }
        }
        else
        {
            bNameFilterEnable = false;
        }

        if (advMacFilter != null && !advMacFilter.isEmpty())
        {
            String strAddress = rslt.getDevice().getAddress();
            if (strAddress != null){
                String strAddressLowCase = strAddress.toLowerCase().replace(":", "");
                String strFilterLowCase = advMacFilter.toLowerCase().replace(":", "");
                if (strAddressLowCase.contains(strFilterLowCase)) {
                    bFilter = false;
                }
            }
        }else{
            bMacFilterEnable = false;
        }

        if (bNameFilterEnable || bMacFilterEnable) {
            if (bFilter) {
                return;
            }
        }

        Message msg = mMsgHandler.obtainMessage(MSG_NEW_DEVICE_FOUND);
        msg.obj = rslt;
        mMsgHandler.sendMessage(msg);
    }

    private final Handler mMsgHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //start pair scan
                case MSG_NEW_DEVICE_FOUND: {
                    ScanResult rslt = (ScanResult)msg.obj;
                    if (rslt != null) {
                        handleBleScanRslt(rslt);
                    }
                    break;
                }

                case MSG_DELAY_ADV_REPORT:{
                    delayReportAdvTimer();
                    break;
                }

                default:
                    break;
            }

            return true;
        }
    });

    private void delayReportAdvTimer()
    {
        if (!mCBNtfBeacons.isEmpty())
        {
            KBeacon[] beacons = new KBeacon[mCBNtfBeacons.size()];
            mCBNtfBeacons.values().toArray(beacons);
            if (this.delegate != null) {
                this.delegate.onBeaconDiscovered(beacons);
            }
            mCBNtfBeacons.clear();
        }
    }

    private void handleBleScanRslt(final ScanResult rslt) {
        BluetoothDevice device = rslt.getDevice();
        String strMacAddress = device.getAddress();
        int rssi = rslt.getRssi();

        ScanRecord record = rslt.getScanRecord();
        if (record == null) {
            return;
        }

        KBeacon pUnknownBeacon = null;
        boolean bParseAdvData = false;
        if (record.getBytes() != null && record.getBytes().length > 0) {
            pUnknownBeacon = mCbAllBeacons.get(strMacAddress);
            if (pUnknownBeacon == null) {
                pUnknownBeacon = new KBeacon(strMacAddress, mContext);
                pUnknownBeacon.setAdvTypeFilter(advTypeFilter);
                mCbAllBeacons.put(strMacAddress, pUnknownBeacon);
            }
            bParseAdvData = pUnknownBeacon.parseAdvPacket(record, rssi);
        }

        if (bParseAdvData) {
            if (mCbKBeacons.get(strMacAddress) == null) {
                pUnknownBeacon.attach2Device(device);
                mCbKBeacons.put(strMacAddress, pUnknownBeacon);
            }

            //add to notify list
            mCBNtfBeacons.put(strMacAddress, pUnknownBeacon);

            if (!mMsgHandler.hasMessages(MSG_DELAY_ADV_REPORT)) {
                mMsgHandler.sendEmptyMessageDelayed(MSG_DELAY_ADV_REPORT, MAX_DELAY_TIMEOUT_INTERVAL);
            }
        }
    }
}
