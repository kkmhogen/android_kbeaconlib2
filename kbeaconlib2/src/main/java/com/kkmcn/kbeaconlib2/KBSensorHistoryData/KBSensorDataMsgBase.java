package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTrigger;
import com.kkmcn.kbeaconlib2.KBErrorCause;
import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBeacon;


public abstract class KBSensorDataMsgBase extends KBCfgTrigger {
    public static final int READ_RECORD_REVERSE_ORDER = 1;
    public static final int READ_RECORD_ORDER = 0;
    public static final int READ_RECORD_NEW_RECORD = 2;

    private static final int MSG_READ_SENSOR_INFO_REQ = 1;

    private static final int MSG_READ_SENSOR_INFO_RSP = 1;

    private static final int MSG_READ_SENSOR_DATA_REQ = 2;

    private static final int MSG_READ_SENSOR_DATA_RSP = 2;

    private static final int MSG_CLR_SENSOR_DATA_REQ = 3;



    public static final long INVALID_DATA_RECORD_POS = 4294967295L;

    protected ReadSensorCallback mReadSensorCallback;

    public interface ReadSensorCallback {
        void onReadComplete(boolean bConfigSuccess, Object obj, KBException error);
    }

    abstract public int getSensorDataType();

    abstract public Object parseSensorDataResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp);

    abstract public Object parseSensorInfoResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp);

    //////////////////////////////////////////////////////////////////////////////////////////////////

    public byte[] makeReadSensorDataReq(long nReadRcdNo, int nReadOrder, int nMaxRecordNum)
    {
        byte[] byMsgReq = new byte[7];
        int nIndex = 0;

        //read pos
        byMsgReq[nIndex++] = (byte)((nReadRcdNo >> 24) & 0xFF);
        byMsgReq[nIndex++] = (byte)((nReadRcdNo >> 16) & 0xFF);
        byMsgReq[nIndex++] = (byte)((nReadRcdNo >> 8) & 0xFF);
        byMsgReq[nIndex++] = (byte)(nReadRcdNo  & 0xFF);

        //read num
        byMsgReq[nIndex++] = (byte)((nMaxRecordNum >> 8) & 0xFF);
        byMsgReq[nIndex++] = (byte)(nMaxRecordNum & 0xFF);

        //read direction
        byMsgReq[nIndex] = (byte)nReadOrder;

        return byMsgReq;
    }

    public void readSensorDataInfo(final KBeacon beacon, ReadSensorCallback readCallback) {
        byte [] bySensorInfoReq = new byte[2];

        bySensorInfoReq[0] = MSG_READ_SENSOR_INFO_REQ;
        bySensorInfoReq[1] = (byte)getSensorDataType();;
        mReadSensorCallback = readCallback;

        //send message
        beacon.sendSensorRequest(bySensorInfoReq, new KBeacon.ReadSensorCallback() {
            @Override
            public void onReadComplete(boolean bReadResult, byte[] readPara, KBException except) {
                KBException error = null;
                boolean ret = false;
                Object readInfoRsp = null;

                if (bReadResult) {
                    //tag
                    if (readPara.length > 2 && readPara[0] == MSG_READ_SENSOR_INFO_RSP) {
                        readInfoRsp = parseSensorInfoResponse(beacon, 2, readPara);
                    }
                    if (readInfoRsp == null){
                        error = new KBException(KBErrorCause.KBErrorParseSensorInfoResponseFailed, "parse sensor info response failed");
                    }else {
                        ret = true;
                    }
                }else{
                    error = except;
                    ret = false;
                }

                if (mReadSensorCallback != null){
                    ReadSensorCallback tempCallback = mReadSensorCallback;
                    mReadSensorCallback = null;
                    tempCallback.onReadComplete(ret, readInfoRsp, error);
                }
            }
        });
    }

    public void readSensorRecord(final KBeacon beacon, long nReadRcdNo, int nReadOrder, int nMaxRecordNum, final ReadSensorCallback readCallback) {
        byte[] byMakeReadSensorDataReq = makeReadSensorDataReq(nReadRcdNo, nReadOrder, nMaxRecordNum);
        byte[] bySensorDataReq = new byte[byMakeReadSensorDataReq.length + 2];
        System.arraycopy(byMakeReadSensorDataReq, 0, bySensorDataReq, 2, byMakeReadSensorDataReq.length);
        bySensorDataReq[0] = MSG_READ_SENSOR_DATA_REQ;
        bySensorDataReq[1] = (byte)getSensorDataType();

        //send message
        mReadSensorCallback = readCallback;
        beacon.sendSensorRequest(bySensorDataReq, new KBeacon.ReadSensorCallback() {
            @Override
                public void onReadComplete(boolean bReadResult, byte[] responsePara, KBException except) {
                KBException error = null;
                boolean ret = false;
                Object readInfoRsp = null;

                if (bReadResult) {
                    //tag
                    if (responsePara.length > 2 && responsePara[0] == MSG_READ_SENSOR_DATA_RSP) {
                        //data length
                        int nReadIndex = 1;
                        short nDataLen = ByteConvert.bytesToShort(responsePara, nReadIndex);
                        nReadIndex += 2;

                        //data content
                        if (nDataLen == responsePara.length - 3) {
                            readInfoRsp = parseSensorDataResponse(beacon, 3, responsePara);
                        }
                    }

                    if (readInfoRsp == null){
                        error = new KBException(KBErrorCause.KBErrorParseSensorDataResponseFailed, "parse sensor data response failed");
                    }else {
                        ret = true;
                    }
                } else {
                    error = except;
                    ret = false;
                }

                if (mReadSensorCallback != null){
                    ReadSensorCallback tempCallback = mReadSensorCallback;
                    mReadSensorCallback = null;
                    tempCallback.onReadComplete(ret, readInfoRsp, error);
                }
            }
        });
    }

    public void clearSensorRecord(final KBeacon beacon,final ReadSensorCallback readCallback) {
        byte [] bySensorInfoReq = new byte[2];
        bySensorInfoReq[0] = MSG_CLR_SENSOR_DATA_REQ;
        bySensorInfoReq[1] = (byte)getSensorDataType();;
        mReadSensorCallback = readCallback;

        //send message
        beacon.sendSensorRequest(bySensorInfoReq, new KBeacon.ReadSensorCallback() {
            @Override
            public void onReadComplete(boolean bReadResult, byte[] readPara, KBException error) {
                ReadSensorCallback tempCallback = mReadSensorCallback;
                mReadSensorCallback = null;
                tempCallback.onReadComplete(bReadResult, null, error);
            }
        });
    }
}
