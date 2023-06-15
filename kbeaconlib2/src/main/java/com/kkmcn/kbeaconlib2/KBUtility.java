package com.kkmcn.kbeaconlib2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class KBUtility {
    public static final int APPLE_MANUFACTURE_ID = 0x004C;

    public static final int KKM_MANUFACTURE_ID = 0x0A53;

    public static final ParcelUuid PARCE_UUID_EDDYSTONE = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805f9b34fb");

    public static final ParcelUuid PARCE_UUID_EXT_DATA = ParcelUuid.fromString("00002080-0000-1000-8000-00805f9b34fb");

    public static final UUID KB_CFG_SERVICE_UUID = UUID.fromString("0000FEA0-0000-1000-8000-00805f9b34fb");
    public static final UUID KB_WRITE_CHAR_UUID = UUID.fromString("0000FEA1-0000-1000-8000-00805f9b34fb");
    public static final UUID KB_NTF_CHAR_UUID = UUID.fromString("0000FEA2-0000-1000-8000-00805f9b34fb");
    public static final UUID KB_IND_CHAR_UUID = UUID.fromString("0000FEA3-0000-1000-8000-00805f9b34fb");

    public static final UUID CHARACTERISTIC_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static byte[] hexStringToBytes(String hexString){
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        char []hexCharacter = hexString.toCharArray();
        for (int i = 0; i < hexCharacter.length; i++){
            if (-1 == charToByte(hexCharacter[i])){
                return null;
            }
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static short htonshort(byte nFirst, byte nSecond)
    {
        short nOutput = (short)((nFirst & 0xFF) << 8);
        nOutput += (short)(nSecond & 0xFF);

        return nOutput;
    }

    public static int htonint(byte byte0, byte byte1, byte byte2, byte byte3)
    {
        int nOutput = (int)((byte0 & 0xFF) << 24);
        nOutput += (int)((byte1 & 0xFF) << 16);
        nOutput += (int)((byte2 & 0xFF) << 8);
        nOutput += (byte3 & 0xFF);
        return nOutput;
    }

    public static byte[] htonbyte(short nInput)
    {
        byte[] outputData = new byte[2];
        outputData[1] = (byte)(nInput & 0xFF);
        outputData[0] = (byte)((nInput & 0xFF00) >> 8);
        return outputData;
    }

    public static int htonl(int nInput)
    {
        int nOutput = ((nInput & 0xFF) << 24);
        nOutput += ((nInput  & 0xFF00) << 8);
        nOutput += ((nInput  & 0xFF0000) >> 8);
        nOutput += ((nInput & 0xFF000000) >> 24);
        return nOutput;
    }

    public static boolean isMPhone() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isMOhone() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isHexString(String hexString)
    {
        String pattern = "([0-9A-Fa-f]{2})+";
        String pattern2 = "^0X|^0x([0-9A-Fa-f]{2})+";
        if (!Pattern.matches(pattern, hexString))
        {
            if (!Pattern.matches(pattern2, hexString))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isNumber(String string) {
        if (string == null)
            return false;
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(string).matches();
    }

    public static boolean isUUIDString(String hexString)
    {
        String pattern = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}";
        return Pattern.matches(pattern, hexString);
    }

    public static String byteBuffer2String(ByteBuffer buffer)
    {
        CharBuffer charBuffer = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String FormatHexUUID2User(String strUUID)
    {
        strUUID = strUUID.toUpperCase().replace("0X", "");
        if (strUUID.length() != 32)
        {
            return "";
        }

        String strUserUUID;
        strUserUUID = strUUID.substring(0, 8);
        strUserUUID += "-";

        strUserUUID += strUUID.substring(8, 12);
        strUserUUID += "-";

        strUserUUID += strUUID.substring(12, 16);
        strUserUUID += "-";

        strUserUUID += strUUID.substring(16, 20);
        strUserUUID += "-";

        strUserUUID += strUUID.substring(20);

        return strUserUUID;
    }

    public static <K, V> Map<K, V> castMap(Object obj, Class<K> clazz1, Class<V> clazz2)
    {
        Map<K, V> result = new HashMap<>();
        if(obj instanceof Map<?, ?>)
        {
            for (Map.Entry<?,?> o : ((Map<?, ?>)obj).entrySet())
            {
                result.put(clazz1.cast(o.getKey()), clazz2.cast(o.getValue()));
            }
            return result;
        }

        return null;
    }

    public static <T> List<T> castList(Object obj, Class<T> clazz)
    {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    public static float signedBytes2Float(byte byHeight, byte byLow)
    {
        int combine = ((byHeight & 0xFF) << 8) + (byLow & 0xFF);
        if (combine >= 0x8000)
        {
            combine = combine - 0x10000;
        }

        float fResult = (float)(combine / 256.0);
        BigDecimal bigTemp = new BigDecimal(fResult);
        return bigTemp.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
