import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;

public class AESLibrary {
    
    // AES Şifreleme (Kütüphane ile)
    public String encrypt(String plaintext, String key) throws Exception {
        // Anahtarı 16 byte'a tamamla veya kes
        byte[] keyBytes = prepareKey(key);
        
        // AES anahtarı oluştur
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        // Cipher instance oluştur (AES-128, ECB mode, PKCS5 padding)
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        // Şifreleme
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
        
        // Base64 encode
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    // AES Deşifreleme (Kütüphane ile)
    public String decrypt(String ciphertext, String key) throws Exception {
        // Anahtarı 16 byte'a tamamla veya kes
        byte[] keyBytes = prepareKey(key);
        
        // AES anahtarı oluştur
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        // Cipher instance oluştur
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        // Base64 decode
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        
        // Deşifreleme
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, "UTF-8");
    }
    
    // Anahtarı 16 byte'a ayarla
    private byte[] prepareKey(String key) {
        byte[] keyBytes = new byte[16];
        byte[] temp = key.getBytes();
        
        // İlk 16 byte'ı al veya eksikse 0 ile doldur
        System.arraycopy(temp, 0, keyBytes, 0, Math.min(temp.length, 16));
        
        return keyBytes;
    }
}