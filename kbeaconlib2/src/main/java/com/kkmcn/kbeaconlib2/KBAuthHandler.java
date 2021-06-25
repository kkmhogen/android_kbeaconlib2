package com.kkmcn.kbeaconlib2;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class KBAuthHandler {

    public final static int Failed = 1;
    public final static int Success = 0;

    private final static int MTU_SIZE_HEAD = 0x3;
    private final static int BLE4_MTU_SIZE = 23;


    private final static String LOG_TAG = "KBAuthHandler";
    private final static int AUTH_PHASE1_APP = 0x1;
    private final static int AUTH_PHASE2_DEV = 0x2;
    private final static int AUTH_MIN_MTU_ALOGRIM_PH1 = 11;
    private final static int AUTH_MIN_MTU_SIMP_ALOGRIM_PH2 = 12;
    private final static int AUTH_RETURN_FAIL = 0xF1;

    private final static int AUTH_PASSWORD_LEN = 16;
    private final static int AUTH_FACTOR_ID_1 = 0xA9;
    private final static int AUTH_FACTOR_ID_2 = 0xB1;

    private byte mAuthPhase1AppRandom[];
    private byte mAuthDeviceMac[];
    private String mPassword;
    private Integer mtuSize;
    private KBConnPara mConnPara;

    public KBAuthDelegate delegate;

    public interface KBAuthDelegate {
         void authStateChange(int authRslt);

         void writeAuthData(byte[] data);
    }

    public KBAuthHandler(KBAuthDelegate authDelegate) {
        mtuSize = BLE4_MTU_SIZE - MTU_SIZE_HEAD;
        delegate = authDelegate;
    }

    void setConnPara(KBConnPara mConnPara) {
        this.mConnPara = mConnPara;
    }

    public Integer getMtuSize()
    {
        return mtuSize;
    }

    //send md5 requet
    boolean authSendMd5Request(String macAddress, String password) {
        String strMacAddress = macAddress.replace(":", "");

        byte macData[] = KBUtility.hexStringToBytes(strMacAddress);
        if (macData.length != 6) {
            Log.e(LOG_TAG, "mac address or password length failed");
            return false;
        }
        mAuthDeviceMac = macData;
        if (password.length() < 8 || password.length() > 16) {
            Log.e(LOG_TAG, "Password length failed");
            return false;
        }
        mPassword = password;

        byte authRequest[];
        if (mConnPara != null && mConnPara.syncUtcTime)
        {
            authRequest = new byte[10];
            int nIndex = 6;

            long utcTime = UTCTime.getUTCTimeSeconds();
            authRequest[nIndex++] = (byte)((utcTime >> 24) & 0xFF);
            authRequest[nIndex++] = (byte)((utcTime >> 16) & 0xFF);
            authRequest[nIndex++] = (byte)((utcTime >> 8) & 0xFF);
            authRequest[nIndex++] = (byte)(utcTime & 0xFF);
        }else{
            authRequest = new byte[6];
        }

        authRequest[0] = 0x13;
        authRequest[1] = AUTH_PHASE1_APP;

        Random r = new Random(System.currentTimeMillis());
        int nRandom = r.nextInt(0xFFFFFFF);
        mAuthPhase1AppRandom = new byte[4];
        authRequest[2] = mAuthPhase1AppRandom[0] = (byte) ((nRandom >> 24) & 0xFF);
        authRequest[3] = mAuthPhase1AppRandom[1] = (byte) ((nRandom >> 16) & 0xFF);
        authRequest[4] = mAuthPhase1AppRandom[2] = (byte) ((nRandom >> 8) & 0xFF);
        authRequest[5] = mAuthPhase1AppRandom[3] = (byte) (nRandom & 0xFF);

        //set data;
        delegate.writeAuthData(authRequest);
        return true;
    }

    void authHandleResponse(byte[] byRcvNtfValue) {
        if (byRcvNtfValue.length < 1) {
            Log.e(LOG_TAG, "receive auth data length error");
            delegate.authStateChange(Failed);
        }

        if (byRcvNtfValue[0] == AUTH_PHASE1_APP || byRcvNtfValue[0] == AUTH_MIN_MTU_ALOGRIM_PH1) {
            byte[] byPhase1Data = new byte[byRcvNtfValue.length - 1];
            System.arraycopy(byRcvNtfValue, 1, byPhase1Data, 0, byRcvNtfValue.length - 1);
            if (!authHandlePhase1Response(byPhase1Data, byRcvNtfValue[0] == AUTH_MIN_MTU_ALOGRIM_PH1)) {
                Log.e(LOG_TAG, "app auth with device failed:" + mAuthDeviceMac);
                delegate.authStateChange(Failed);
            }else{
                Log.e(LOG_TAG, "app auth phase1 success:" + mAuthDeviceMac);
            }
        } else if (byRcvNtfValue[0] == AUTH_PHASE2_DEV) {
            if (byRcvNtfValue.length >= 2) {
                mtuSize = (byRcvNtfValue[1] & 0xFF) - MTU_SIZE_HEAD;
            }
            Log.e(LOG_TAG, "app auth phase2 success:" + mAuthDeviceMac);
            this.delegate.authStateChange(Success);
        }else if ((byRcvNtfValue[0] & 0xFF) == AUTH_RETURN_FAIL){
            delegate.authStateChange(Failed);
        }
    }

    boolean authHandlePhase1Response(byte[] byRcvNtfValue, boolean isShortMtu) {
        byte nFactorID[] = new byte[2];
        nFactorID[0] = (byte) (AUTH_FACTOR_ID_1 & 0xFF);
        nFactorID[1] = (byte) (AUTH_FACTOR_ID_2 & 0xFF);

        ByteBuffer auth1AppMd5Data = ByteBuffer.allocate(12 + mPassword.length());
        byte[] byAuth1AppMd5Result;
        ByteBuffer auth2DevMd5Data = ByteBuffer.allocate(12 + mPassword.length());
        byte[] byAuth2DevMd5Result;

        //check input valid
        byte[] byAuth2DevRandom = new byte[4];
        byte[] byAuth1DevResult;
        if (isShortMtu)
        {
            if (byRcvNtfValue.length < 12) {
                return false;
            }
            byAuth1DevResult = new byte[8];
        }else{
            if (byRcvNtfValue.length < 20) {
                return false;
            }
            byAuth1DevResult = new byte[16];

        }
        System.arraycopy(byRcvNtfValue, 0, byAuth2DevRandom, 0, byAuth2DevRandom.length);
        System.arraycopy(byRcvNtfValue, 4, byAuth1DevResult, 0, byAuth1DevResult.length);


        if (mPassword == null) {
            Log.e(LOG_TAG, "not found password");
            return false;
        }
        byte[] nsPasswordData = null;
        try {
            nsPasswordData = mPassword.getBytes(StandardCharsets.UTF_8);

            //verify phase1 app auth value
            byte[] byBleMacAddress = new byte[6];
            for (int i = 0; i < 6; i++){
                byBleMacAddress[i] = mAuthDeviceMac[5-i];
            }
            auth1AppMd5Data.put(byBleMacAddress);
            auth1AppMd5Data.put(nFactorID);
            auth1AppMd5Data.put(mAuthPhase1AppRandom);
            auth1AppMd5Data.put(nsPasswordData);
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] byAuth1AppInput = auth1AppMd5Data.array();
            md.update(byAuth1AppInput);
            byAuth1AppMd5Result = md.digest();
            if (isShortMtu)
            {
                byte[]byShortMd5Result = new byte[8];
                for (int i = 0; i < 8; i++)
                {
                    byShortMd5Result[i] = (byte)((byAuth1AppMd5Result[i] ^ byAuth1AppMd5Result[8+i]) & 0xFF);
                }

                if (!Arrays.equals(byShortMd5Result, byAuth1DevResult))
                {
                    return false;
                }
            }else{
                if (!Arrays.equals(byAuth1AppMd5Result, byAuth1DevResult))
                {
                    return false;
                }
            }

            //get phase2 device md5 value
            auth2DevMd5Data.put(byBleMacAddress);
            auth2DevMd5Data.put(nFactorID);
            auth2DevMd5Data.put(byAuth2DevRandom);
            auth2DevMd5Data.put(nsPasswordData);
            md.reset();
            md.update(auth2DevMd5Data.array());
            byAuth2DevMd5Result = md.digest();

            //send auth2 md5 response
            byte authRequest[];
            if (isShortMtu)
            {
                authRequest = new byte[10];
                authRequest[0] = 0x13;
                authRequest[1] = AUTH_MIN_MTU_SIMP_ALOGRIM_PH2;
                for (int i = 0; i < 8; i++)
                {
                    authRequest[i+2] = (byte)((byAuth2DevMd5Result[i] ^ byAuth2DevMd5Result[i + 8]) & 0xFF);
                }
            }
            else
            {
                authRequest = new byte[18];
                authRequest[0] = 0x13;
                authRequest[1] = AUTH_PHASE2_DEV;
                for (int i = 0; i < 16; i++)
                {
                    authRequest[i+2] = byAuth2DevMd5Result[i];
                }
            }


            delegate.writeAuthData(authRequest);
        } catch (Exception excpt) {
            excpt.printStackTrace();
            return false;
        }

        return true;
    }
}
