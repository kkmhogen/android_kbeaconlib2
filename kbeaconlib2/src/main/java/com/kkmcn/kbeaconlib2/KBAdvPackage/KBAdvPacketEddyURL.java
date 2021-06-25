package com.kkmcn.kbeaconlib2.KBAdvPackage;

public class KBAdvPacketEddyURL extends KBAdvPacketBase{

    private Integer refTxPower;

    private String url;

    private  final static int EDDYSTONE_URL_ENCODING_MAX = 14;

    // # of URL Scheme Prefix types
    private  static final int EDDYSTONE_URL_PREFIX_MAX = 4;

    // Array of URL Scheme Prefices
    private static String []eddystoneURLPrefix = {"http://www.",  "https://www.",
            "http://",  "https://"};

    // Array of URLs to be encoded
    private static String []eddystoneURLEncoding = {
            ".com/",
            ".org/",
            ".edu/",
            ".net/",
            ".info/",
            ".biz/",
            ".gov/",
            ".com/",
            ".org/",
            ".edu/",
            ".net/",
            ".info/",
            ".biz/",
            ".gov/"
    };


    public Integer getRefTxPower()
    {
        return refTxPower;
    }

    public String getUrl()
    {
        return url;
    }

    public int getAdvType()
    {
        return KBAdvType.EddyURL;
    }


    public static int decodeURL(char[] urlOrg, int nSrcLength, char[] urlDec)
    {
        int i, j, k;
        int decIndex = 0;

        //first
        if (urlOrg[0] > EDDYSTONE_URL_PREFIX_MAX){
            return 0;
        }

        //add url head
        char[] urlPrefex = eddystoneURLPrefix[urlOrg[0]].toCharArray();
        for (i = 0; i < urlPrefex.length; i++){
            urlDec[decIndex++] = urlPrefex[i];
        }

        //add middle web
        for (j = 1; j < nSrcLength; j++){
            if (urlOrg[j] <= EDDYSTONE_URL_ENCODING_MAX) {
                char[] urlSuffix = eddystoneURLEncoding[urlOrg[j]].toCharArray();
                for (k = 0; k < urlSuffix.length; k++) {
                    urlDec[decIndex++] = urlSuffix[k];
                }
            }
            else
            {
                urlDec[decIndex++] = urlOrg[j];
            }
        }

        return decIndex;
    }

    public boolean parseAdvPacket(byte[] beaconData)
    {
        super.parseAdvPacket(beaconData);
        int nSrvIndex = 1;  //skip adv type

        refTxPower = (int)beaconData[nSrvIndex++];

        char []urlCharEnc = new char[18];
        int j = 0, k = 0;
        for (j = nSrvIndex, k = 0; j < beaconData.length; j++) {
            urlCharEnc[k++] += (char) beaconData[j];
        }
        char []urlCharDec = new char[40];
        int nDecLen = decodeURL(urlCharEnc, k, urlCharDec);
        if (nDecLen == 0){
            url = "N/A";
        }else{
            url = "";
            for (int i = 0; i < nDecLen; i++){
                url += urlCharDec[i];
            }
        }

        return true;
    }
}
