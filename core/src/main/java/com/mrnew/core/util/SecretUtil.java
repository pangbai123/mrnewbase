package com.mrnew.core.util;


import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 加密相关工具类
 */
public class SecretUtil {
    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String HMAC_MD5 = "HmacMD5";


    private static final String DES = "DES";
    private static final String MODE = "DES/ECB/PKCS5Padding";

    /**
     * 获取md5值
     *
     * @param input
     * @return
     */
    public static String getMD5Digest(String input) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        if (input == null) return null;
        byte[] buffer = input.getBytes();
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据加密类型获取摘要
     *
     * @param input      待加密的数据
     * @param encryptKey 加密使用的key
     * @param HMAC_NAME  加密类型
     */
    public static String getHashDigest(String input, String encryptKey, String HMAC_NAME) throws Exception {
        if (input == null || encryptKey == null) return null;
        byte[] keyBytes = encryptKey.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_NAME);

        Mac mac = Mac.getInstance(HMAC_NAME);
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(input.getBytes());

        return byte2HexString(rawHmac);
    }

    /**
     * DES加密
     *
     * @param encryptKey 密钥
     * @param input      加密前的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encodeDes(String encryptKey, String input) throws Exception {
        if (input == null || encryptKey == null) return null;
        byte[] data = encrypt(encryptKey, input);
        return byte2HexString(data);
    }

    /**
     * DES加密
     *
     * @param encryptKey 密钥
     * @param input      加密前的字符串
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String encryptKey, String input) throws Exception {
        if (input == null || encryptKey == null) return null;
        return doFinal(encryptKey, Cipher.ENCRYPT_MODE, input.getBytes());
    }

    /**
     * DES解密
     *
     * @param encryptKey 密钥
     * @param input      解密前的字符串
     * @return encode方法返回的字符串
     * @throws Exception
     */
    public static String decodeDes(String encryptKey, String input) throws Exception {
        if (input == null || encryptKey == null) return null;
        byte[] data = String2Byte(input); // Hex.decodeHex(input.toCharArray());
        return new String(decrypt(encryptKey, data));
    }

    /**
     * DES解密
     *
     * @param encryptKey 密钥
     * @param input      encrypt方法返回的字节数组
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(String encryptKey, byte[] input) throws Exception {
        if (input == null || encryptKey == null) return null;
        return doFinal(encryptKey, Cipher.DECRYPT_MODE, input);
    }

    /**
     * 执行加密解密操作
     *
     * @param key    密钥
     * @param opmode 操作类型：Cipher.ENCRYPT_MODE-加密，Cipher.DECRYPT_MODE-解密
     * @param input  加密解密前的字节数组
     * @return
     * @throws Exception
     */
    private static byte[] doFinal(String key, int opmode, byte[] input)
            throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(MODE);
        // 用密匙初始化Cipher对象
        // IvParameterSpec param = new IvParameterSpec(IV);
        // cipher.init(Cipher.DECRYPT_MODE, securekey, param, sr);
        cipher.init(opmode, securekey, sr);
        // 执行加密解密操作
        return cipher.doFinal(input);
    }

    /**
     * byte[]转换成字符串
     *
     * @param b
     * @return
     */
    public static String byte2HexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            String stmp = Integer.toHexString(aB & 0xff);
            if (stmp.length() == 1)
                sb.append("0").append(stmp);
            else
                sb.append(stmp);
        }
        return sb.toString();
    }

    /**
     * 16进制转换成byte[]
     *
     * @param hexString
     * @return
     */
    public static byte[] String2Byte(String hexString) {
        if (hexString.length() % 2 == 1)
            return null;
        byte[] ret = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            ret[i / 2] = Integer.decode("0x" + hexString.substring(i, i + 2))
                    .byteValue();
        }
        return ret;
    }

    /**
     * 对String排序
     *
     * @param encryptTextList
     * @return
     */
    private static String sortAndToString(ArrayList<String> encryptTextList) {
        String[] strings = new String[encryptTextList.size()];
        for (int i = 0; i < encryptTextList.size(); i++) {
            strings[i] = encryptTextList.get(i);
        }

        Arrays.sort(strings);
        StringBuilder content = new StringBuilder();
        for (String string : strings) {
            content.append(string);
        }
        return content.toString();
    }
}
