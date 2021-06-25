package com.kkmcn.kbeaconlib2.KBAdvPackage;

public class KBAdvPacketBase {

    public  final static int INVALID_BATTERY_PERCNET = 0xFF;

    private Integer rssi;

    private Integer connectable;

    private Long lastReceiveTime;

    public Integer getRssi()
    {
        return rssi;
    }

    public Long getLastReceiveTime()
    {
        return lastReceiveTime;
    }

    public boolean isConnectable()
    {
        if (connectable != null){
            return connectable == 1;
        }else{
            return false;
        }
    }

    public boolean parseAdvPacket(byte[] data)
    {
        lastReceiveTime = System.currentTimeMillis();

        return true;
    }

    void updateBasicInfo(int nRssi)
    {
        rssi = nRssi;
    }

    public int getAdvType()
    {
        return KBAdvType.AdvNull;
    }
}
