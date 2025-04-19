package com.nexuscale.nexusscalemanage.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Timestamp;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Date;

public class EncryptionDecryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "UvfwhE2yGaWbb+92WbXrAQ==";

    // 生成 AES 密钥
    private static String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(128, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // params expirationTime 单位毫秒
    // 加密方法
    public static String encrypt(String phoneNumber, long expirationTime) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            String dataToEncrypt = phoneNumber + "|" + expirationTime;
            byte[] encryptedBytes = cipher.doFinal(dataToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 解密方法
    public static String[] decrypt(String encryptedData) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);
            return decryptedData.split("\\|");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        System.out.println(SECRET_KEY);
        String phoneNumber = "1234567890";
        long expirationTime = new Date().getTime() + 3600000; // 过期时间为当前时间加 1 小时  60 60

        String encrypted = encrypt(phoneNumber, expirationTime);
        System.out.println("加密后的数据: " + encrypted);

        String[] decrypted = decrypt(encrypted);
        System.out.println("解密后的电话号码: " + decrypted[0]);
        System.out.println("解密后的过期时间: " + decrypted[1]);
        System.out.println(new Date().getTime());
    }
}