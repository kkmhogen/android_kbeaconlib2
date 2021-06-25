package com.kkmcn.kbeaconlib2.KBAdvPackage;

import com.kkmcn.kbeaconlib2.KBUtility;

public class KBAdvPacketIBeacon extends KBAdvPacketBase{

    private Integer majorID;

    private Integer minorID;

    private String uuid;

    private Integer refTxPower;

    public Integer getMajorID()
    {
        return majorID;
    }

    public Integer getMinorID()
    {
        return minorID;
    }

    public String getUuid()
    {
        return uuid;
    }

    public Integer getRefTxPower() {
        return refTxPower;
    }


    public int getAdvType()
    {
        return KBAdvType.IBeacon;
    }

    public boolean parseAdvPacket(byte[] iBeaconData)
    {
        super.parseAdvPacket(iBeaconData);

        if (iBeaconData.length < 22){
            return false;
        }
        //get uuid
        String strUUID = "";
        for (int i = 2; i < 18; i++) {
            strUUID = strUUID + String.format("%02x", iBeaconData[i]);
        }
        uuid = KBUtility.FormatHexUUID2User(strUUID);

        //get major id
        int nMajorID = (iBeaconData[18] & 0xFF);
        nMajorID = (nMajorID << 8) +  (iBeaconData[19] & 0xFF);
        majorID = nMajorID;

        //get minor id
        int nMinorID = (iBeaconData[20] & 0xFF);
        minorID = (nMinorID << 8) +  (iBeaconData[21] & 0xFF);

        //get referance power
        refTxPower = (int)iBeaconData[22];

        return true;
    }
}
