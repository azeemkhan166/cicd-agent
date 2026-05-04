/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vineet
 */
@Component
public class EncryptDecryptUtils {

    static final Logger LOGGER = LoggerFactory.getLogger(EncryptDecryptUtils.class);

    private static String encryptionKey;

    @Value("${encryption_key}")
    public void setEncryptionKey(String key) {
        LOGGER.info("encryption_key " + key);
        encryptionKey = key;
    }

    private static final String CHARACTERENCODING = "UTF-8";
    private static final String CIPHERTRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private static final String AESENCRYPTIONALGORITHEM = "AES";

    /**
     * Method for Encrypt Plain String Data
     *
     * @param plainText
     * @return encryptedText
     */
    public static String encrypt(String plainText) {
        String encryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(CIPHERTRANSFORMATION);
            byte[] key = encryptionKey.getBytes(CHARACTERENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(key, AESENCRYPTIONALGORITHEM);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedText = encoder.encodeToString(cipherText);
        } catch (Exception E) {
            System.err.println("Encrypt Exception : " + E.getMessage());
        }
        return encryptedText;
    }

    /**
     * Method For Get encryptedText and Decrypted provided String
     *
     * @param encryptedText
     * @return decryptedText
     */
    public static String decrypt(String encryptedText) {
        String decryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(CIPHERTRANSFORMATION);
            byte[] key = encryptionKey.getBytes(CHARACTERENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(key, AESENCRYPTIONALGORITHEM);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encryptedText.getBytes("UTF8"));
            decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (Exception E) {
            System.err.println("decrypt Exception : " + E.getMessage());
        }
        return decryptedText;
    }

    public static String encryptData(byte[] bytesToEncrypt) {
        // do your encryption here
        String apiJsonResponse = new String(bytesToEncrypt);

        String encryptedString = EncryptDecryptUtils.encrypt(apiJsonResponse);
        if (encryptedString != null) {
            // sending encoded json response in data object as follows

            // reference response: {"data":"thisisencryptedstringresponse"}
            return encryptedString;
        } else {
            return encryptedString;
        }
    }
}
