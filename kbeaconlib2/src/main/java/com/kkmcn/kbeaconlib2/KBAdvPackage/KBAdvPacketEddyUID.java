package com.kkmcn.kbeaconlib2.KBAdvPackage;

public class KBAdvPacketEddyUID extends KBAdvPacketBase{

    private String nid;
    private String sid;

    private Integer refTxPower;

    public Integer getRefTxPower()
    {
        return refTxPower;
    }

    public String getNid()
    {
        return nid;
    }

    public String getSid()
    {
        return sid;
    }

    public int getAdvType()
    {
        return KBAdvType.EddyUID;
    }

    public boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);
        int nSrvIndex = 1;  //skip adv type

        refTxPower = (int)beaconData[nSrvIndex++];

        nid = String.format("0x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
                beaconData[nSrvIndex++], beaconData[nSrvIndex++], beaconData[nSrvIndex++], beaconData[nSrvIndex++],
                beaconData[nSrvIndex++], beaconData[nSrvIndex++], beaconData[nSrvIndex++], beaconData[nSrvIndex++],
                beaconData[nSrvIndex++], beaconData[nSrvIndex++]);

        sid = String.format("0x%02x%02x%02x%02x%02x%02x",
                beaconData[nSrvIndex++], beaconData[nSrvIndex++], beaconData[nSrvIndex++], beaconData[nSrvIndex++],
                beaconData[nSrvIndex++], beaconData[nSrvIndex]);

        return true;
    }
}
