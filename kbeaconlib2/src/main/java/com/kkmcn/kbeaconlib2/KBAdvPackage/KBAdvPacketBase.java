package com.kkmcn.kbeaconlib2.KBAdvPackage;

public class KBAdvPacketBase {

    public  final static int INVALID_BATTERY_PERCNET = 0xFF;

    private Integer rssi;

    private Integer connectable;

    private Long lastReceiveTime;

    private String mac;

    public int getAdvType()
    {
        return KBAdvType.AdvNull;
    }

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

    public String getMac() {
        return mac;
    }

    void setMac(String mac) {
        this.mac = mac;
    }

    boolean parseAdvPacket(byte[] data)
    {
        lastReceiveTime = System.currentTimeMillis();
        return true;
    }

    void updateBasicInfo(int nRssi, String macAddress)
    {
        rssi = nRssi;
        mac = macAddress;
    }
}
