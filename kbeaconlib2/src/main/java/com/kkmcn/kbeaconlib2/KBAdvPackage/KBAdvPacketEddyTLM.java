package com.kkmcn.kbeaconlib2.KBAdvPackage;

import com.kkmcn.kbeaconlib2.KBUtility;

public class KBAdvPacketEddyTLM extends KBAdvPacketBase{
    public class TLMElapseTime
    {
        public int days;
        public int hours;
        public int minutes;
        public int second;
    };

    private final static int DAYS_SECONDS = 3600*24;

    private Integer batteryLevel;

    private Float temperature;

    private Integer advCount;

    private Integer secCount;

    private Integer tlmType;

    public int getAdvType()
    {
        return KBAdvType.EddyTLM;
    }

    public Integer getBatteryLevel()
    {
        return batteryLevel;
    }

    public  Integer getAdvCount()
    {
        return advCount;
    }

    public Integer getSecCount()
    {
        return secCount;
    }

    public TLMElapseTime getElapseTime()
    {
        TLMElapseTime elapseTime = new TLMElapseTime();

        elapseTime.days = secCount / DAYS_SECONDS;
        long nRemainsSec = secCount % DAYS_SECONDS;
        elapseTime.hours = (int)(nRemainsSec / 3600);
        nRemainsSec = nRemainsSec % 3600;
        elapseTime.minutes = (int)(nRemainsSec / 60);
        elapseTime.second = (int)(nRemainsSec % 60);

        return elapseTime;
    }

    public Float getTemperature(){
        return temperature;
    }


    public Integer getTlmType()
    {
        return tlmType;
    }

    public boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);
        int nSrvIndex = 1;  //skip adv type

        //skip version
        tlmType = (beaconData[nSrvIndex++] & 0xFF);

        //battery
        int nBatteryLevel = (beaconData[nSrvIndex++] & 0xFF);
        nBatteryLevel = (nBatteryLevel << 8);
        nBatteryLevel += (beaconData[nSrvIndex++] & 0xFF);
        batteryLevel = nBatteryLevel;

        //temputure
        Byte tempHigh = beaconData[nSrvIndex++];
        Byte tempLow = beaconData[nSrvIndex++];
        temperature = KBUtility.signedBytes2Float(tempHigh, tempLow);

        //adv count
        int nAdvCount = (beaconData[nSrvIndex++] & 0xFF);
        for (int i = 0; i < 3; i++)
        {
            nAdvCount = (nAdvCount << 8);
            nAdvCount += (beaconData[nSrvIndex] & 0xFF);
            nSrvIndex++;
        }
        advCount = nAdvCount;

        //adv count
        int nSecCount = (beaconData[nSrvIndex++] & 0xFF);
        for (int i = 0; i < 3; i++)
        {
            nSecCount = (nSecCount << 8);
            nSecCount += (beaconData[nSrvIndex] & 0xFF);
            nSrvIndex++;
        }
        secCount = nSecCount/10;

        return true;
    }
}
