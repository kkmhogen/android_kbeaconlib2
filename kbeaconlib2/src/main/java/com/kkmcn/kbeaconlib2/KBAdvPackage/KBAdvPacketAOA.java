package com.kkmcn.kbeaconlib2.KBAdvPackage;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class KBAdvPacketAOA extends KBAdvPacketBase{
    public final static int MIN_ADV_PACKET_LEN = 5;
    public static int AOA_MASK_ACC_AIX = 0x8;
    public final static int CHANNEL37 = 0x1;
    public final static int CHANNEL38 = 0x2;
    public final static int CHANNEL39 = 0x3;
    private Integer batteryPercent;
    private Integer txPower;
    private Integer channel;
    private Double freq;
    private KBAccSensorValue accSensor;

    static private ArrayList<Integer> TXPowerArray;
    static private HashMap<Integer, Double> FreqMap;

    static{
        List<Integer> list = Arrays.asList(0,3,4,-40,-20,-16,-12,-8,-4,-30);
        TXPowerArray = new ArrayList<>(list);

        FreqMap = new HashMap<>();
        FreqMap.put(0x7E, 300.0); FreqMap.put(0x49, 9.0);FreqMap.put(0x43, 3.0);FreqMap.put(0x14, 0.05);
        FreqMap.put(0x6A, 100.0); FreqMap.put(0x48, 8.0);FreqMap.put(0x42, 2.0);FreqMap.put(0x25, 0.02);
        FreqMap.put(0x65, 50.0);  FreqMap.put(0x47, 7.0);FreqMap.put(0x41, 1.0);FreqMap.put(0x2A, 0.01);
        FreqMap.put(0x5E, 30.0);  FreqMap.put(0x46, 6.0);FreqMap.put(0x2, 0.5);
        FreqMap.put(0x54, 20.0);  FreqMap.put(0x45, 5.0);FreqMap.put(0x5, 0.2);
        FreqMap.put(0x4A, 10.0);  FreqMap.put(0x44, 4.0);FreqMap.put(0xA, 0.1);
    }

    public int getAdvType()
    {
        return KBAdvType.AOA;
    }

    public Integer getBatteryPercent()
    {
        return batteryPercent;
    }
    public Integer getTxPower() {
        return txPower;
    }

    public Integer getChannel() {
        return channel;
    }

    public KBAccSensorValue getAccSensor() {
        return accSensor;
    }

    public String getChannelTitle() {
        if (channel == null) return "N/A";
        if (channel == CHANNEL37){
            return "37";
        } else if (channel == CHANNEL38) {
            return "38";
        }else if(channel == CHANNEL39) {
            return "39";
        }else return "N/A";
    }

    public Double getFreq() {
        return freq;
    }

    @SuppressLint("DefaultLocale")
    boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);
        if (beaconData.length < MIN_ADV_PACKET_LEN) {
            return  false;
        }

        int nSrvIndex = 1; //skip device tag
        int sign = (beaconData[nSrvIndex++] & 0xFF);

        if ((sign & AOA_MASK_ACC_AIX) > 0) {
            accSensor = new KBAccSensorValue();
            accSensor.xAis = (short)beaconData[nSrvIndex++];
            accSensor.yAis = (short)beaconData[nSrvIndex++];
            accSensor.zAis = (short)beaconData[nSrvIndex];
            return  true;
        }

        //model
        int firstData = beaconData[nSrvIndex++] & 0xFF;

        //channel
        channel = firstData & 0x7;

        //txPower
        int txPowerKey = ((firstData >> 4) & 0xFF);
        if (txPowerKey < TXPowerArray.size()) {
            txPower = TXPowerArray.get(txPowerKey);
        }

        //second
        int secondData = (beaconData[nSrvIndex++] & 0xFF);

        //battery level
        batteryPercent = ((secondData >> 4) & 0xFF) * 10;

        //third
        int thirdData = (beaconData[nSrvIndex] & 0xFF);

        //battery level
        freq = FreqMap.get(thirdData);
        return true;
    }
}
