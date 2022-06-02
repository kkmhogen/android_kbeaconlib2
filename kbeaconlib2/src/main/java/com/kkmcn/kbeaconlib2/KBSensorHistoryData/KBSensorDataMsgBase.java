package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTrigger;
import com.kkmcn.kbeaconlib2.KBErrorCode;
import com.kkmcn.kbeaconlib2.KBException;
import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.UTCTime;


public abstract class KBSensorDataMsgBase extends KBCfgTrigger {
    private static final int MSG_READ_SENSOR_INFO_REQ = 1;

    private static final int MSG_READ_SENSOR_INFO_RSP = 1;

    private static final int MSG_READ_SENSOR_DATA_REQ = 2;

    private static final int MSG_READ_SENSOR_DATA_RSP = 2;

    private static final int MSG_CLR_SENSOR_DATA_REQ = 3;

    public static final long INVALID_DATA_RECORD_POS = 4294967295L;

    protected ReadSensorCallback mReadSensorCallback;

    protected long mUtcOffset;

    public interface ReadSensorCallback {
        void onReadComplete(boolean bConfigSuccess, Object obj, KBException error);
    }

    public class ReadSensorInfoRsp extends Object
    {
        public Integer totalRecordNumber;

        public Integer unreadRecordNumber;

        public Long readInfoUtcSeconds;
    };

    abstract public int getSensorDataType();

    abstract public Object parseSensorDataResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp);

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
                        error = new KBException(KBErrorCode.ParseSensorInfoResponseFailed, "parse sensor info response failed");
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

    public Object parseSensorInfoResponse(final KBeacon beacon, int nDataPtr, byte[] sensorDataRsp)
    {
        if (sensorDataRsp.length -  nDataPtr < 8)
        {
            return null;
        }

        ReadSensorInfoRsp infoRsp = new ReadSensorInfoRsp();

        //total record number
        infoRsp.totalRecordNumber = (int) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr);
        nDataPtr+=4;

        //total record number
        infoRsp.unreadRecordNumber = (int) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr);
        nDataPtr+=4;

        //utc offset
        infoRsp.readInfoUtcSeconds = (Long) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr );
        mUtcOffset = UTCTime.getUTCTimeSeconds() - infoRsp.readInfoUtcSeconds;
        nDataPtr += 4;

        return infoRsp;
    }

    public void readSensorRecord(final KBeacon beacon, long nReadRcdNo, int nReadOption, int nMaxRecordNum, final ReadSensorCallback readCallback) {
        byte[] byMakeReadSensorDataReq = makeReadSensorDataReq(nReadRcdNo, nReadOption, nMaxRecordNum);
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
                        error = new KBException(KBErrorCode.ParseSensorDataResponseFailed, "parse sensor data response failed");
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
