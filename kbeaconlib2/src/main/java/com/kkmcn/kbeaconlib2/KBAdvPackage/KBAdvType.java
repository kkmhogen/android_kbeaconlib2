package com.kkmcn.kbeaconlib2.KBAdvPackage;

public class KBAdvType
{
    public final static int AdvNull = 0x0;
    public final static int Sensor = 0x01;
    public final static int EddyUID = 0x2;
    public final static int EddyTLM = 0x3;
    public final static int EddyURL = 0x4;
    public final static int IBeacon = 0x5;
    public final static int System = 0x6;
    public final static int KBAdvTypeMAXValue = 0x6;

    public final static String SensorString  = "KSensor";
    public final static String EddyUIDString  = "UID";
    public final static String EddyTLMString = "TLM";
    public final static String EddyURLString = "URL";
    public final static String IBeaconString = "iBeacon";
    public final static String SystemString = "System";
    public final static String InvalidString = "Disabled";

    public static String getAdvTypeString(int nAdvType){
        String strAdv = "";
        switch (nAdvType)
        {
            case AdvNull:
                strAdv = InvalidString;
                break;
            case Sensor:
                strAdv = SensorString;
                break;
            case EddyUID:
                strAdv = EddyUIDString;
                break;
            case EddyTLM:
                strAdv = EddyTLMString;
                break;
            case EddyURL:
                strAdv = EddyURLString;
                break;
            case IBeacon:
                strAdv = IBeaconString;
                break;
            case System:
                strAdv = SystemString;
                break;

            default:
                strAdv = "Unknown";
        }
        return strAdv;
    }
}