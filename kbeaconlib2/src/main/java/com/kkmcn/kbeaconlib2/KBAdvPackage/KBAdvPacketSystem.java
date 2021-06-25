package com.kkmcn.kbeaconlib2.KBAdvPackage;
import android.annotation.SuppressLint;
import com.kkmcn.kbeaconlib2.KBUtility;

public class KBAdvPacketSystem extends KBAdvPacketBase{

    public final static int MIN_ADV_PACKET_LEN = 11;

    private Integer batteryPercent;

    private String firmwareVersion;

    private Integer model;

    private Integer tag;

    private String macAddress;

    public int getAdvType()
    {
        return KBAdvType.System;
    }

    public String getVersion()
    {
        return firmwareVersion;
    }

    public Integer getBatteryPercent()
    {
        return batteryPercent;
    }

    public Integer getModel(){return model;}

    public String getMacAddress(){return macAddress;}

    @SuppressLint("DefaultLocale")
    public boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);

        int nSrvIndex = 0; //skip device tag
        tag = (beaconData[nSrvIndex++] & 0xFF);

        //model
        model = (Integer)(beaconData[nSrvIndex++] & 0xFF);

        //battery level
        batteryPercent = (beaconData[nSrvIndex++] & 0xFF);

        //mac address
        byte[] byAddress = new byte[6];
        System.arraycopy(beaconData, nSrvIndex, byAddress, 0, 6);
        macAddress = KBUtility.bytesToHexString(byAddress);
        nSrvIndex += 6;

        //firmware version
        firmwareVersion = String.format("%d.%d",
                (beaconData[nSrvIndex] & 0xFF),(beaconData[nSrvIndex+1] & 0xFF));
        nSrvIndex += 2;

        return true;
    }
}
