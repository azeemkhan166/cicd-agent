package com.realcoderz.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EncryptDecryptUtilsTest {

    // AES-128 requires exactly 16 bytes
    static final String TEST_KEY = "TestKey123456789";

    @BeforeAll
    static void setEncryptionKey() {
        ReflectionTestUtils.setField(EncryptDecryptUtils.class, "encryptionKey", TEST_KEY);
    }

    @Test
    void encrypt_returnsNonEmptyBase64String() {
        String encrypted = EncryptDecryptUtils.encrypt("{\"org_id\":1}");
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertFalse(encrypted.contains(" "));
    }

    @Test
    void encryptDecrypt_roundTripRestoresOriginal() {
        String original = "{\"month\":4,\"year\":2024,\"org_id\":101}";
        String encrypted = EncryptDecryptUtils.encrypt(original);
        String decrypted = EncryptDecryptUtils.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptDecrypt_worksForComplexPayload() {
        String original = "{\"employee_Type\":\"Full time\",\"basic\":50000.0,\"gross\":80000.0}";
        String decrypted = EncryptDecryptUtils.decrypt(EncryptDecryptUtils.encrypt(original));
        assertEquals(original, decrypted);
    }

    @Test
    void decrypt_withInvalidBase64_returnsEmptyString() {
        String result = EncryptDecryptUtils.decrypt("!!!not-valid-base64!!!");
        assertEquals("", result);
    }

    @Test
    void decrypt_withRandomString_returnsEmptyString() {
        String result = EncryptDecryptUtils.decrypt("randomjunk");
        assertEquals("", result);
    }

    @Test
    void encryptData_bytesMatchEncryptOutput() {
        String plainText = "{\"status\":\"ok\"}";
        String expected = EncryptDecryptUtils.encrypt(plainText);
        String actual = EncryptDecryptUtils.encryptData(plainText.getBytes());
        assertEquals(expected, actual);
    }

    @Test
    void encrypt_differentInputProducesDifferentOutput() {
        String enc1 = EncryptDecryptUtils.encrypt("{\"id\":1}");
        String enc2 = EncryptDecryptUtils.encrypt("{\"id\":2}");
        assertNotEquals(enc1, enc2);
    }
}
