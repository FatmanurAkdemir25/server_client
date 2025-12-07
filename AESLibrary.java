import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;

public class AESLibrary {
    
    
    public String encrypt(String plaintext, String key) throws Exception {
        
        byte[] keyBytes = prepareKey(key);
        
        
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
        
        
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    
    public String decrypt(String ciphertext, String key) throws Exception {
        
        byte[] keyBytes = prepareKey(key);
        
        
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, "UTF-8");
    }
    
    
    private byte[] prepareKey(String key) {
        byte[] keyBytes = new byte[16];
        byte[] temp = key.getBytes();
        
        
        System.arraycopy(temp, 0, keyBytes, 0, Math.min(temp.length, 16));
        
        return keyBytes;
    }
}