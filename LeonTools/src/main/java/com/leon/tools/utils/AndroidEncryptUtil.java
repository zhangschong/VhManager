package com.leon.tools.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 用于系统中的各种加解密
 * <p/>
 * Created by lijing on 2014/9/24.
 */
public class AndroidEncryptUtil {

    public static String encryptDESCBC(String encryptData, String key, String iv)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skey, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptData.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    public static String encryptDESCBC(String encryptData, String key, String iv, int StrFlags)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skey, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptData.getBytes());
        return Base64.encodeToString(encryptedData, StrFlags);
    }

//    public static byte[] encryptDESCBCToBytes(String encryptData, String key, String iv)
//            throws Exception {
//        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
//        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, skey, zeroIv);
//        byte[] encryptedData = cipher.doFinal(encryptData.getBytes());
//        return Base64.encode(encryptedData, Base64.DEFAULT);
//    }

    public static String decryptDESCBC(String decryptData, String key, String iv)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skey, zeroIv);
        byte[] byteMi = Base64.decode(decryptData, Base64.DEFAULT);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }

    public static String encryptDESCBC7(String encryptData, String key, String iv)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skey, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptData.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    public static String decryptDESCBC7(String decryptData, String key, String iv)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, skey, zeroIv);
        byte[] byteMi = Base64.decode(decryptData, Base64.DEFAULT);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }

    /**
     * AES 数据加密解密方法 加密算法密匙长度为256 现今最高的
     *
     * @author Administrator
     */
    public static final String AES = "AES";
    public static final String SHA1 = "SHA1PRNG";
    public static final String CRYPTO = "Crypto";
    public static final String CIPHER = "AES/ECB/ZeroBytePadding";

    /**
     * 加密
     *
     * @param content  ：文本
     * @param password ：密码
     * @return
     */
    public static String encryptAES(String content, String password) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);

            SecureRandom sr = SecureRandom.getInstance(SHA1, CRYPTO);//
            // android加密设置
            sr.setSeed(password.getBytes());
            kgen.init(256, sr); // 192 and 256 bits may not be available
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);

            Cipher cipher = Cipher.getInstance(CIPHER);// Android加密设置
            byte[] byteContent = content.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return parseByte2HexStr(cipher.doFinal(byteContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decryptAES(String content, String password) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);

            SecureRandom sr = SecureRandom.getInstance(SHA1, CRYPTO);//
            // Android加密设置
            sr.setSeed(password.getBytes());
            kgen.init(256, sr);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);

            Cipher cipher = Cipher.getInstance(CIPHER);// 加密设置
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return new String(cipher.doFinal(parseHexStr2Byte(content)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
