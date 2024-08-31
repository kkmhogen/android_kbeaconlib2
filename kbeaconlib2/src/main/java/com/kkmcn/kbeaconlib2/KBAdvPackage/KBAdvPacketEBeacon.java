package com.kkmcn.kbeaconlib2.KBAdvPackage;

import com.kkmcn.kbeaconlib2.ByteConvert;
import com.kkmcn.kbeaconlib2.KBUtility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;

public class KBAdvPacketEBeacon extends KBAdvPacketBase{

    private Long utcSecCount;

    private String uuid;

    private Integer refTxPower = -59;

    private String password;

    public String getUuid()
    {
        return uuid;
    }

    public Integer getRefTxPower() {
        return refTxPower;
    }

    public Long getUtcSecCount() {
        return utcSecCount;
    }

    public int getAdvType()
    {
        return KBAdvType.EBeacon;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean parseAdvPacket(byte[] eBeaconData)
    {
        super.parseAdvPacket(eBeaconData);
        int nStartIndex = 0;
		
		if (eBeaconData.length < 23){
            return false;
        }
		
        //0x03: MD5 + AES ECB
        int advType = eBeaconData[nStartIndex++];
        int length = eBeaconData[nStartIndex++];
        if (eBeaconData.length - nStartIndex != length){
            return false;
        }

        byte[] encryptedUUID = new byte[16];
        System.arraycopy(eBeaconData,nStartIndex,encryptedUUID,0,16);
        nStartIndex += 16;

        byte[] utcData = new byte[4];
        System.arraycopy(eBeaconData,nStartIndex,utcData,0,4);
        utcSecCount = ByteConvert.bytesToUint(eBeaconData,nStartIndex);
        nStartIndex += 4;

        //get reference power
        refTxPower = (int)eBeaconData[nStartIndex];

        byte[] md5KeyData = new byte[26];
        byte[] pwdBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] dataPwd = new byte[16];
        System.arraycopy(pwdBytes,0,dataPwd,0,pwdBytes.length);
        String mac = getMac();
        byte[] dataMac = KBUtility.fromHex2Bytes(mac, Pattern.compile(":"));

        System.arraycopy(dataPwd,0,md5KeyData,0,16);
        System.arraycopy(dataMac,0,md5KeyData,16,6);
        System.arraycopy(utcData,0,md5KeyData,22,4);

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(md5KeyData);
            byte[] aesKey = md.digest();
            byte[] decrypted = Crypter.decrypt(aesKey,encryptedUUID);

            StringBuilder strUUID = new StringBuilder();
            for (byte b : decrypted) {
                strUUID.append(String.format("%02x", b));
            }
            uuid = KBUtility.FormatHexUUID2User(strUUID.toString());
        }catch (Exception exception) {
            return false;
        }

        return true;
    }
}
