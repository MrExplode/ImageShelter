package me.ahornyai.imageshelter.utils;

import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@UtilityClass
public class AESUtil {

    public SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        return keyGenerator.generateKey();
    }

    public SecretKey getKeyFromString(String str) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(str);

        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String getKeyAsString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public byte[] encrypt(byte[] byteArray, SecretKey key, byte[] IV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(byteArray);
    }

    public byte[] decrypt(byte[] byteArray, SecretKey key, byte[] IV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(byteArray);
    }

}
