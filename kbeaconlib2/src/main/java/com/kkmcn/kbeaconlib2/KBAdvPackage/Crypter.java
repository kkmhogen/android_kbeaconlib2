/*
 * This file is part of SimpleTextCrypt.
 * Copyright (c) 2015-2020, Aidin Gharibnavaz <aidin@aidinhut.com>
 *
 * SimpleTextCrypt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleTextCrypt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleTextCrypt.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kkmcn.kbeaconlib2.KBAdvPackage;

import android.annotation.SuppressLint;

import java.security.GeneralSecurityException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * Provides methods for encrypting and decrypting data.
 */
@SuppressLint("GetInstance")
public class Crypter {

    // ECB encrypt
    public static byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {


        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");


        SecretKey KeySpec = new SecretKeySpec(key, "AES/ECB/NoPadding");


        cipher.init(Cipher.ENCRYPT_MODE, KeySpec);


        return cipher.doFinal(input);
    }


    public static byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {

         Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");


        SecretKey KeySpec = new SecretKeySpec(key, "AES/ECB/NoPadding");


        cipher.init(Cipher.DECRYPT_MODE, KeySpec);


        return cipher.doFinal(input);
    }

    // AES CBC 
    public static byte[] encryptCBC(byte[] key,byte[] iv, byte[] input) throws GeneralSecurityException {


        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");


        SecretKey KeySpec = new SecretKeySpec(key, "AES/CBC/NoPadding");


        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, KeySpec,ivParameterSpec);


        return cipher.doFinal(input);
    }

	//AES CBC
    public static byte[] decryptCBC(byte[] key, byte[] iv,byte[] input) throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");


        SecretKey KeySpec = new SecretKeySpec(key, "AES/CBC/NoPadding");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);


        cipher.init(Cipher.DECRYPT_MODE, KeySpec,ivParameterSpec);


        return cipher.doFinal(input);
    }

    public static byte[] generatorIvBytes() {
        Random random = new Random();
        byte[] ivParam = new byte[16];
        for (int index = 0; index < 16; index++) {
            ivParam[index] = (byte) random.nextInt(256);
        }
        return ivParam;
    }

}
