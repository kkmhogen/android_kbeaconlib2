package com.kkmcn.kbeaconlib2.KBAdvPackage;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBUtility;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;

public class KBAdvPacketSensor extends KBAdvPacketBase{

    private final  static int SENSOR_MASK_VOLTAGE = 0x1;
    private final  static int SENSOR_MASK_TEMP = 0x2;
    private final  static int SENSOR_MASK_HUME = 0x4;
    private final  static int SENSOR_MASK_ACC_AIX = 0x8;
    private final  static int SENSOR_MASK_ALARM = 0x10;
    private final  static int SENSOR_MASK_PIR = 0x20;
    private final  static int SENSOR_MASK_LUX = 0x40;
    private final  static int SENSOR_MASK_VOC = 0x80;
    private final  static int SENSOR_MASK_CO2 = 0x200;
    private final  static int SENSOR_MASK_RECORD_NUM = 0x400;

    private final static int ENCRYPT_SENSOR_TYPE = 0x06;

    private KBAccSensorValue accSensor;

    private Integer alarmStatus;

    private Integer pirIndication;

    private Float temperature;

    private Float humidity;

    private Integer batteryLevel;

    private Integer luxValue;

    private Integer vocElapseSec;
    private Integer voc;
    private Integer nox;

    private Integer co2ElapseSec;
    private Integer co2;

    private Integer newTHRecordNum;

    private Long utcSecCount;

    private String password;

    private boolean isEncryptAdv;

    public int getAdvType()
    {
        return KBAdvType.Sensor;
    }

    public KBAccSensorValue getAccSensor()
    {
        return accSensor;
    }

    public Integer getAlarmStatus()
    {
        return alarmStatus;
    }

    public Float getTemperature()
    {
        return temperature;
    }

    public Float getHumidity()
    {
        return humidity;
    }

    public Integer getVersion()
    {
        return 0;
    }

    public Integer getBatteryLevel()
    {
        return batteryLevel;
    }

    public Integer getPirIndication() {
        return pirIndication;
    }

    public Integer getLuxValue() {
        return luxValue;
    }

    public Integer getVoc() {
        return voc;
    }

    public Integer getNox() {
        return nox;
    }

    public Integer getCo2() {
        return co2;
    }

    public Long getUtcSecCount() {
        return utcSecCount;
    }

    public boolean isEncryptAdv() {
        return isEncryptAdv;
    }

    public Integer getNewTHRecordNum() {
        return newTHRecordNum;
    }

    public Integer getCo2ElapseSec() {
        return co2ElapseSec;
    }

    public Integer getVocElapseSec() {
        return vocElapseSec;
    }

    void setPassword(String password) {
        this.password = password;
    }

    private byte[] decryptMD5Data(int nStartIndex, byte[] beaconData)
    {
        byte[] encryptedSensorData = new byte[16];
        System.arraycopy(beaconData, nStartIndex, encryptedSensorData,0,16);
        nStartIndex += 16;

        byte[] utcData = new byte[4];
        System.arraycopy(beaconData, nStartIndex, utcData,0,4);
        utcSecCount = ByteConvert.bytesToUint(beaconData, nStartIndex);

        byte[] md5KeyData = new byte[26];
        byte[] pwdBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] dataPwd = new byte[16];
        System.arraycopy(pwdBytes,0,dataPwd,0,pwdBytes.length);
        String mac = getMac();
        byte[] dataMac = KBUtility.fromHex2Bytes(mac, Pattern.compile(":"));

        System.arraycopy(dataPwd,0,md5KeyData,0,16);
        System.arraycopy(dataMac,0,md5KeyData,16,6);
        System.arraycopy(utcData,0,md5KeyData,22,4);

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(md5KeyData);
            byte[] aesKey = md.digest();
            return Crypter.decrypt(aesKey, encryptedSensorData);
        }catch (Exception exception) {
            return null;
        }

    }

    boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);
        byte[] plainData;
        int nSensorMask;

        int nSrvIndex = 0;
        byte advType = beaconData[nSrvIndex++];

        //get sensor mask
        int sensorMaskHigh = ((beaconData[nSrvIndex++] & 0xFF) << 8);
        nSensorMask = sensorMaskHigh + (beaconData[nSrvIndex++] & 0xFF);

        //decrypt content
        if (ENCRYPT_SENSOR_TYPE == advType)
        {
            plainData = decryptMD5Data(nSrvIndex, beaconData);
            isEncryptAdv = true;
        }
        else
        {
            int dataLen = beaconData.length - nSrvIndex;
            plainData = new byte[dataLen];
            System.arraycopy( beaconData, nSrvIndex, plainData,0,dataLen);
            isEncryptAdv = false;
        }

        return parseSensorData(nSensorMask, plainData);
    }

    private boolean parseSensorData(int nSensorMask, byte[] beaconData)
    {
        int nSrvIndex = 0;
        if (beaconData == null){
            return false;
        }

        if ((nSensorMask & SENSOR_MASK_VOLTAGE) > 0)
        {
            if (nSrvIndex > (beaconData.length - 2))
            {
                return false;
            }
            int nBatteryLvs = (beaconData[nSrvIndex++] & 0xFF);
            nBatteryLvs = (nBatteryLvs << 8);
            nBatteryLvs += (beaconData[nSrvIndex++] & 0xFF);
            batteryLevel= nBatteryLvs;
        }else{
            batteryLevel = null;
        }

        if ((nSensorMask & SENSOR_MASK_TEMP) > 0)
        {
            if (nSrvIndex > (beaconData.length - 2))
            {
                return false;
            }

            Byte tempHigh = beaconData[nSrvIndex++];
            Byte tempLow = beaconData[nSrvIndex++];
            temperature = KBUtility.signedBytes2Float(tempHigh, tempLow);
        }else{
            temperature = null;
        }

        if ((nSensorMask & SENSOR_MASK_HUME) > 0)
        {
            if (nSrvIndex > (beaconData.length - 2))
            {
                return false;
            }

            Byte humHigh = beaconData[nSrvIndex++];
            Byte humLow = beaconData[nSrvIndex++];
            humidity = KBUtility.signedBytes2Float(humHigh, humLow);
            if (humidity < 0) {
                if(temperature != null){
                  temperature = (-1 - humidity)*100 + temperature;
                }
                humidity = null;
            }
        }else{
            humidity = null;
        }

        if ((nSensorMask & SENSOR_MASK_ACC_AIX) > 0)
        {
            if (nSrvIndex > (beaconData.length - 6))
            {
                return false;
            }

            accSensor = new KBAccSensorValue();
            short nAccValue = (short)((beaconData[nSrvIndex++] & 0xFF) << 8);
            nAccValue += (short)(beaconData[nSrvIndex++] & 0xFF);
            accSensor.xAis = nAccValue;

            nAccValue = (short)((beaconData[nSrvIndex++] & 0xFF) << 8);
            nAccValue += (short)(beaconData[nSrvIndex++] & 0xFF);
            accSensor.yAis = nAccValue;

            nAccValue = (short)((beaconData[nSrvIndex++] & 0xFF) << 8);
            nAccValue += (short)(beaconData[nSrvIndex++] & 0xFF);
            accSensor.zAis = nAccValue;
        }else{
            accSensor = null;
        }

        if ((nSensorMask & SENSOR_MASK_ALARM) > 0) {
            if (nSrvIndex > (beaconData.length - 1)) {
                return false;
            }
            alarmStatus = (int)beaconData[nSrvIndex++];
        }else{
            alarmStatus = null;
        }

        if ((nSensorMask & SENSOR_MASK_PIR) > 0) {
            if (nSrvIndex > (beaconData.length - 1)) {
                return false;
            }
            pirIndication = (int)beaconData[nSrvIndex++];
        }else{
            pirIndication = null;
        }

        //get lux value
        if ((nSensorMask & SENSOR_MASK_LUX) > 0) {
            if (nSrvIndex > (beaconData.length - 2)) {
                return false;
            }
            luxValue = ((beaconData[nSrvIndex++] & 0xFF) << 8);
            luxValue += (beaconData[nSrvIndex++] & 0xFF);
        }else{
            luxValue = null;
        }

        //get voc value
        if ((nSensorMask & SENSOR_MASK_VOC) > 0) {
            if (nSrvIndex > (beaconData.length - 5)) {
                return false;
            }

            vocElapseSec = (beaconData[nSrvIndex++] & 0xFF) * 10;
            voc = ((beaconData[nSrvIndex++] & 0xFF) << 8);
            voc += (beaconData[nSrvIndex++] & 0xFF);

            nox = ((beaconData[nSrvIndex++] & 0xFF) << 8);
            nox += (beaconData[nSrvIndex++] & 0xFF);
        }else{
            voc = null;
            nox = null;
        }

        //get co2 value
        if ((nSensorMask & SENSOR_MASK_CO2) > 0) {
            if (nSrvIndex > (beaconData.length - 3)) {
                return false;
            }

            co2ElapseSec = (beaconData[nSrvIndex++] & 0xFF) * 10;
            co2 = ((beaconData[nSrvIndex++] & 0xFF) << 8);
            co2 += (beaconData[nSrvIndex++] & 0xFF);
        }else{
            co2 = null;
        }

        //record number
        if ((nSensorMask & SENSOR_MASK_RECORD_NUM) > 0) {
            if (nSrvIndex > (beaconData.length - 3)) {
                return false;
            }

            byte countMask = beaconData[nSrvIndex++];
            newTHRecordNum = ((beaconData[nSrvIndex++] & 0xFF) << 8);
            newTHRecordNum += (beaconData[nSrvIndex++] & 0xFF);
        }else{
            newTHRecordNum = null;
        }

        return true;
    }

    public static Integer shortToInteger(Short s){
        if(s < 0){
            return 65535+1+s;
        }else{
            return new BigDecimal(s).intValue();
        }
    }
}
