package com.kkmcn.kbeaconlib2.KBCfgPackage;

import android.annotation.SuppressLint;

import com.kkmcn.kbeaconlib2.UTCTime;

public class KBTimeRange {
    public byte localStartHour;
    public byte localStartMinute;
    public byte localEndHour;
    public byte localEndMinute;

    public KBTimeRange()
    {
        localStartHour = localStartMinute = localEndHour = localEndMinute = 0;
    }

    public KBTimeRange(byte startHour, byte startMinute, byte endHour, byte endMinute)
    {
        localStartHour = startHour;
        localStartMinute = startMinute;
        localEndHour = endHour;
        localEndMinute = endMinute;
    }

    public KBTimeRange(Integer intTime)
    {
        fromUTCInteger(intTime);
    }

    public void fromUTCInteger(Integer intTime)
    {
        byte startHour = (byte)((intTime >> 24) & 0xFF);
        byte startMinute = (byte)((intTime >> 16) & 0xFF);
        byte endHour = (byte)((intTime >> 8) & 0xFF);
        byte endMinute = (byte)(intTime & 0xFF);

        if (startHour == 0 && startMinute == 0 && endHour == 0 && endMinute == 0) {
            localStartHour = 0;
            localStartMinute = 0;
            localEndHour = 0;
            localEndMinute = 0;
        } else {
            UTCTime localStart = UTCTime.getLocalTimeFromUTC(startHour, startMinute, 0);
            UTCTime localEnd = UTCTime.getLocalTimeFromUTC(endHour, endMinute, 0);
            localStartHour = (byte) localStart.mHours;
            localStartMinute = (byte) localStart.mMinutes;
            localEndHour = (byte) localEnd.mHours;
            localEndMinute = (byte) localEnd.mMinutes;
        }
    }

    public Integer toUTCInteger()
    {
        if (!isTimeRangeValid())
        {
            return null;
        }

        if (isTimeRangeDisable())
        {
            return 0;
        }

        Integer utcSecond = 0;
        UTCTime utcStart = UTCTime.getUTCFromLocalTime((int)localStartHour, (int) localStartMinute, 0);
        utcSecond = utcStart.mHours;
        utcSecond = (utcSecond << 8);
        utcSecond += (byte) utcStart.mMinutes;
        utcSecond = (utcSecond << 8);

        UTCTime utcStop = UTCTime.getUTCFromLocalTime((int) localEndHour, (int) localEndMinute, 0);
        utcSecond += (byte) utcStop.mHours;
        utcSecond = (utcSecond << 8);
        utcSecond += (byte) utcStop.mMinutes;

        return utcSecond;
    }

    public boolean isTimeRangeValid()
    {
        if (localStartHour > 24 || localStartMinute > 59 || localEndHour > 24 || localEndMinute > 59)
        {
            return false;
        }

        return true;
    }

    public boolean isTimeRangeDisable()
    {
        if (localStartHour == 0 && localStartMinute == 0 && localEndHour == 0 && localEndMinute == 0)
        {
            return true;
        }
        return false;
    }

    public void setTimeRangeDisable()
    {
        localStartHour = localStartMinute = localEndHour = localEndMinute = 0;
    }

    @SuppressLint("DefaultLocale")
    public String toString()
    {
        return String.format("%02d:%02d ~ %02d:%02d", localStartHour,
                localStartMinute,
                localEndHour,
                localEndMinute);
    }
}
