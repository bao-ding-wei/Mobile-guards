package com.boyzhang.projectmobilesafe.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密编码工具
 *
 * @author HaiFeng
 */
public class EncodingUtils {

    //-------------------------------------------------------------------------------------------------

    /**
     * MD5加密
     *
     * @param noPassString 要加密的字符串
     * @return
     */
    public static String md5(String noPassString) {
        StringBuffer buffer = null;
        try {
            // 获取MD5算法对象
            MessageDigest instance = MessageDigest.getInstance("MD5");
            // 对字符串加密,返回字符数组
            byte[] digest = instance.digest(noPassString.getBytes());
            buffer = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;// 通过与算法获取字节的低八位
                String hexString = Integer.toHexString(i);// 将整数转为16进制
                // 如果返回的16进制数的长度小于两位数,就在其前面补0
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                buffer.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    //-------------------------------------------------------------------------------------------------

    /**
     * Base64加密--192和256位可能不可用
     *
     * @param seed  加密种子
     * @param plain 要加密的字串
     * @return
     * @throws Exception
     */
    public static String enBase64(String seed, String plain) {
        byte[] rawKey;
        byte[] encrypted = new byte[0];
        try {
            rawKey = getRawKey(seed.getBytes());
            encrypted = encrypt(rawKey, plain.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /**
     * Base64解密
     *
     * @param seed      解密种子
     * @param encrypted 要解密的字串
     * @return
     * @throws Exception
     */
    public static String deBase64(String seed, String encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = Base64.decode(encrypted.getBytes(), Base64.DEFAULT);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(seed);
        keygen.init(128, random); // 192和256位可能不可用
        SecretKey key = keygen.generateKey();
        byte[] raw = key.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] plain) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plain);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    //-------------------------------------------------------------------------------------------------

    /**
     * 文件特征码
     *
     * @param path 文件的路径
     * @return 返回Md5特征码
     */
    public static String getFileMd5(String path) {

        File file = new File(path);
        try {
            FileInputStream in = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len;

            //获取到数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");

            while ((len = in.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);//更新数字摘要
            }

            byte[] digestResult = messageDigest.digest();

            StringBuffer sb = new StringBuffer();

            for (byte b : digestResult) {
                int number = b & 0xff; // 加盐 +1 // 通过与算法获取字节的低八位;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();//返回

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
