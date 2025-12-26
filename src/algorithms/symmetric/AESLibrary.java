package src.algorithms.symmetric;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;


public class AESLibrary {
    
    
    public String encrypt(String plaintext, String key) throws Exception {
        System.out.println("\n[AESLibrary] Encrypt başladı");
        System.out.println("Plaintext uzunluk: " + plaintext.length());
        System.out.println("Key: '" + key + "'");
        System.out.println("Key uzunluk: " + key.length() + " karakter");
        
        
        if (key == null || key.length() != 16) {
            String error = String.format(
                "AES anahtarı TAM 16 karakter olmalı!\n" +
                "Girilen: '%s' (%d karakter)\n" +
                "Örnek geçerli anahtarlar: 'mysecretkey12345', 'PASSWORDPASSWORD', '1234567890123456'",
                key, key == null ? 0 : key.length()
            );
            System.err.println("❌ " + error);
            throw new IllegalArgumentException(error);
        }
        
        byte[] keyBytes = key.getBytes("UTF-8");
        System.out.println("Key bytes uzunluk: " + keyBytes.length);
        
        
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException(
                "Anahtar UTF-8'de 16 byte değil! " +
                "Türkçe karakter kullanmayın. " +
                "Byte uzunluk: " + keyBytes.length
            );
        }
        
        try {
            
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
            
            
            String result = Base64.getEncoder().encodeToString(encryptedBytes);
            
            System.out.println("AES Encryption başarılı");
            System.out.println("Şifreli uzunluk: " + result.length() + " karakter\n");
            
            return result;
            
        } catch (Exception e) {
            System.err.println("AES Encryption hatası: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    
    public String decrypt(String ciphertext, String key) throws Exception {
        System.out.println("\n[AESLibrary] Decrypt başladı");
        System.out.println("Ciphertext uzunluk: " + ciphertext.length());
        System.out.println("Key: '" + key + "'");
        System.out.println("Key uzunluk: " + key.length() + " karakter");
        
        
        if (key == null || key.length() != 16) {
            String error = String.format(
                "AES anahtarı TAM 16 karakter olmalı!\n" +
                "Girilen: '%s' (%d karakter)",
                key, key == null ? 0 : key.length()
            );
            System.err.println(error);
            throw new IllegalArgumentException(error);
        }
        
        byte[] keyBytes = key.getBytes("UTF-8");
        System.out.println("Key bytes uzunluk: " + keyBytes.length);
        
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException(
                "Anahtar UTF-8'de 16 byte değil! Byte uzunluk: " + keyBytes.length
            );
        }
        
        try {
            
            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
            System.out.println("Base64 decoded: " + encryptedBytes.length + " bytes");
            
           
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String result = new String(decryptedBytes, "UTF-8");
            
            System.out.println("AES Decryption başarılı");
            System.out.println("Deşifreli uzunluk: " + result.length() + " karakter\n");
            
            return result;
            
        } catch (Exception e) {
            System.err.println("AES Decryption hatası: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}