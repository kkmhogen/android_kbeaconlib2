package com.kkmcn.kbeaconlib2.KBSensorHistoryData;

import android.util.Log;
import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBSensorType;
import com.kkmcn.kbeaconlib2.UTCTime;

import java.util.ArrayList;
import java.util.HashMap;


public class KBRecordDataHandler extends Object {


    public long mUtcOffset;

    private static final String LOG_TAG = "KBSensorDataHandler";

    //object creation factory
    private final static HashMap<String, Class> kbSensorParserObjects;

    public KBRecordDataHandler()
    {
        mUtcOffset = 0;
    }

    static {
        kbSensorParserObjects = new HashMap<>(5);
        kbSensorParserObjects.put(String.valueOf(KBSensorType.VOC), KBRecordVOC.class);
        kbSensorParserObjects.put(String.valueOf(KBSensorType.CO2), KBRecordCO2.class);
        kbSensorParserObjects.put(String.valueOf(KBSensorType.PIR), KBRecordPIR.class);
        kbSensorParserObjects.put(String.valueOf(KBSensorType.HTHumidity), KBRecordHumidity.class);
        kbSensorParserObjects.put(String.valueOf(KBSensorType.Cutoff), KBRecordCutoff.class);
        kbSensorParserObjects.put(String.valueOf(KBSensorType.Light), KBRecordLight.class);
    }

    public byte[] makeReadSensorRecordRequest(int sensorType, long nReadRcdNo, int nReadOrder, int nMaxRecordNum)
    {
        byte[] byMsgReq = new byte[10];
        int nIndex = 0;

        byMsgReq[nIndex++] = (byte)KBSensorMsgType.MsgReadSensorRecord;
        byMsgReq[nIndex++] = (byte)sensorType;

        //read pos
        byMsgReq[nIndex++] = (byte)((nReadRcdNo >> 24) & 0xFF);
        byMsgReq[nIndex++] = (byte)((nReadRcdNo >> 16) & 0xFF);
        byMsgReq[nIndex++] = (byte)((nReadRcdNo >> 8) & 0xFF);
        byMsgReq[nIndex++] = (byte)(nReadRcdNo  & 0xFF);

        //read num
        byMsgReq[nIndex++] = (byte)((nMaxRecordNum >> 8) & 0xFF);
        byMsgReq[nIndex++] = (byte)(nMaxRecordNum & 0xFF);

        //read direction
        byMsgReq[nIndex++] = (byte)nReadOrder;

        //high speed (connection interval to 30 ms, unit is 1.25ms)
        byMsgReq[nIndex] = (byte)24;

        return byMsgReq;
    }

    public Object parseSensorResponse(byte[] sensorDataRsp)
    {
        if (sensorDataRsp.length <= 1)
        {
            return null;
        }

        byte msgType = sensorDataRsp[0];
        if (msgType == KBSensorMsgType.MsgReadSensorInfo)
        {
            return parseSensorInfoResponse(1, sensorDataRsp);
        }
        else if (msgType == KBSensorMsgType.MsgReadSensorRecord)
        {
            int readIndex = 1;
            if (sensorDataRsp.length <= 3)
            {
                return null;
            }

            //check message length valid
            int msgLength = ByteConvert.bytesTo2Int(sensorDataRsp, readIndex);
            if (msgLength != sensorDataRsp.length - 3)
            {
                return null;
            }
            readIndex += 2;

            return parseSensorDataResponse(readIndex, sensorDataRsp);
        }
        else
        {
            return null;
        }
    }

    private Object parseSensorInfoResponse(int nDataPtr, byte[] sensorDataRsp)
    {
        if (sensorDataRsp.length -  nDataPtr < 13)
        {
            return null;
        }

        KBRecordInfoRsp infoRsp = new KBRecordInfoRsp();

        infoRsp.sensorType = (int)sensorDataRsp[nDataPtr];
        nDataPtr++;

        //total record number
        infoRsp.totalRecordNumber = (int) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr);
        nDataPtr+=4;

        //new record number
        infoRsp.unreadRecordNumber = (int) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr);
        nDataPtr+=4;

        //utc offset
        infoRsp.readInfoUtcSeconds = (Long) ByteConvert.bytesTo4Long(sensorDataRsp, nDataPtr );
        mUtcOffset = UTCTime.getUTCTimeSeconds() - infoRsp.readInfoUtcSeconds;
        nDataPtr += 4;

        return infoRsp;
    }

    private Object parseSensorDataResponse(int nDataPtr, byte[] sensorDataRsp)
    {
        int nReadIndex = nDataPtr;
        if (sensorDataRsp.length - nReadIndex < 5)
        {
            return null;
        }

        //sensor type
        int nSensorType = sensorDataRsp[nReadIndex++];
        KBRecordBase record = createSensorRecordObject(nSensorType);
        if (record == null)
        {
            return null;
        }

        //next read data pos
        KBRecordDataRsp readDataRsp = new KBRecordDataRsp();
        readDataRsp.sensorType = nSensorType;
        readDataRsp.readDataNextPos = ByteConvert.bytesTo4Long(sensorDataRsp, nReadIndex);
        nReadIndex += 4;

        //check payload length
        int nPayLoad = (sensorDataRsp.length - nReadIndex);
        if (nPayLoad % record.getRecordLen() != 0)
        {
            return null;
        }

        //read record
        readDataRsp.readDataRspList = new ArrayList<>(30);
        int nTotalRecordNum= nPayLoad / record.getRecordLen();
        int nRecordPtr = nReadIndex;
        for (int i = 0; i < nTotalRecordNum; i++)
        {
            KBRecordBase nextRecord = createSensorRecordObject(nSensorType);
            if (nextRecord == null) {
                return null;
            }

            //utc time
            nextRecord.parseSensorDataResponse(mUtcOffset, nRecordPtr, sensorDataRsp);
            readDataRsp.readDataRspList.add(nextRecord);

            nRecordPtr += nextRecord.getRecordLen();
        }

        return readDataRsp;
    }

    private static KBRecordBase createSensorRecordObject(int nSensorType)
    {
        try {
            Class classObj = kbSensorParserObjects.get(String.valueOf(nSensorType));
            if (classObj != null){
                KBRecordBase sensorRecord = (KBRecordBase) classObj.newInstance();
                return sensorRecord;
            }
        } catch (Exception excpt) {
            excpt.printStackTrace();
            Log.e(LOG_TAG, "create sensor object failed:" + nSensorType);
        }

        return null;
    }

    public void addSensorClass(int nAdvType, Class advClass)
    {
        kbSensorParserObjects.put(String.valueOf(nAdvType), advClass);
    }
}
