package com.kkmcn.kbeaconlib2.KBAdvPackage;

import com.kkmcn.kbeaconlib2.KBUtility;

import java.math.BigDecimal;

public class KBAdvPacketSensor extends KBAdvPacketBase{

    private final  static int SENSOR_MASK_VOLTAGE = 0x1;
    private final  static int SENSOR_MASK_TEMP = 0x2;
    private final  static int SENSOR_MASK_HUME = 0x4;
    private final  static int SENSOR_MASK_ACC_AIX = 0x8;

    private KBAccSensorValue accSensor;

    private Float temperature;

    private Float humidity;

    private Integer version;

    private Integer batteryLevel;

    public int getAdvType()
    {
        return KBAdvType.Sensor;
    }

    public KBAccSensorValue getAccSensor()
    {
        return accSensor;
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
        return version;
    }

    public Integer getBatteryLevel()
    {
        return batteryLevel;
    }

    public boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);

        int nSrvIndex = 1; //skip adv type

        version = (beaconData[nSrvIndex++] & 0xFF);

        int nSensorMask = (beaconData[nSrvIndex++] & 0xFF);
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
            nAccValue += (beaconData[nSrvIndex++] & 0xFF);
            accSensor.yAis = nAccValue;

            nAccValue = (short)((beaconData[nSrvIndex++] & 0xFF) << 8);
            nAccValue += (beaconData[nSrvIndex] & 0xFF);
            accSensor.zAis = nAccValue;
        }else{
            accSensor = null;
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
