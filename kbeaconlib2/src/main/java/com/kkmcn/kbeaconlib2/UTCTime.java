package com.kkmcn.kbeaconlib2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UTCTime
{
    public int mYear;
    public int mMonth;
    public int mDays;
    public int mHours;
    public int mMinutes;
    public int mSeconds;

    public static long getUTCTimeSeconds() {

        //long currentTimeMillis ()-Returns the current time in milliseconds.
        long millis = System.currentTimeMillis();
        long seconds = millis / 1000;

        return seconds;
    }

    public static UTCTime getLocalTimeFromUTC(int hour, int minute, int second)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String utcTime = formatter.format(date);
        utcTime += String.format(" %02d:%02d", hour, minute, second);

        Date utcDate = null;
        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        UTCTime localTranslateTime = new UTCTime();
        if (locatlDate != null) {
            String[] strNow3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(locatlDate).toString().split(":");
            localTranslateTime.mHours = Integer.parseInt(strNow3[0]);            //获取时（24小时制）
            localTranslateTime.mMinutes = Integer.parseInt(strNow3[1]);            //获取分
        }
        return localTranslateTime;
    }

    public static UTCTime getUTCFromLocalTime(int hour, int minute, int second)
    {
        StringBuffer UTCTimeBuffer = new StringBuffer();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), hour, minute, second);

        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        UTCTime utcTime = new UTCTime();
        utcTime.mYear = cal.get(Calendar.YEAR);
        utcTime.mMonth = cal.get(Calendar.MONTH);
        utcTime.mDays = cal.get(Calendar.DAY_OF_MONTH);
        utcTime.mHours = cal.get(Calendar.HOUR_OF_DAY);
        utcTime.mMinutes = cal.get(Calendar.MINUTE);
        utcTime.mSeconds = cal.get(Calendar.SECOND);

        return utcTime;
    }
};