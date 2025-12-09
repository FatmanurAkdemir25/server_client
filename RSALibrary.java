import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RSALibrary {
    
    private KeyPair keyPair;
    private static final int KEY_SIZE = 1024; // 1024-bit RSA
    
    // RSA Şifreleme (Kütüphane ile)
    public String encrypt(String plaintext, String key) throws Exception {
        try {
            System.out.println("\n=== RSA Library Encryption ===");
            
            // Anahtar çifti üret veya kullan
            if (key.equals("auto") || key.isEmpty()) {
                generateKeyPair();
            } else {
                // Özel format: kullanıcı kendi anahtarını sağlayamaz, otomatik üret
                generateKeyPair();
            }
            
            // Public key ile şifrele
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
            
            // Public key'i Base64 olarak ekle (deşifreleme için private key gerekecek)
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            
            PrivateKey privateKey = keyPair.getPrivate();
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            
            System.out.println("Public Key (Base64): " + publicKeyStr.substring(0, 50) + "...");
            System.out.println("Private Key (Base64): " + privateKeyStr.substring(0, 50) + "...");
            System.out.println("Encrypted Text: " + encryptedText);
            
            // Format: PRIVATE_KEY:ENCRYPTED_TEXT
            return privateKeyStr + ":" + encryptedText;
            
        } catch (Exception e) {
            throw new Exception("RSA şifreleme hatası: " + e.getMessage());
        }
    }
    
    // RSA Deşifreleme (Kütüphane ile)
    public String decrypt(String ciphertext, String key) throws Exception {
        try {
            System.out.println("\n=== RSA Library Decryption ===");
            
            // Format: PRIVATE_KEY:ENCRYPTED_TEXT
            String[] parts = ciphertext.split(":");
            if (parts.length != 2) {
                throw new Exception("Geçersiz şifreli metin formatı! Format: PRIVATE_KEY:ENCRYPTED_TEXT");
            }
            
            String privateKeyStr = parts[0];
            String encryptedText = parts[1];
            
            System.out.println("Private Key Length: " + privateKeyStr.length());
            System.out.println("Encrypted Text: " + encryptedText);
            
            // Private key'i decode et
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            
            // Şifreli metni decode et
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            
            // Deşifrele
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            String result = new String(decryptedBytes, "UTF-8");
            System.out.println("Decrypted: " + result);
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("RSA deşifreleme hatası: " + e.getMessage());
        }
    }
    
    // Anahtar çifti üretimi
    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE);
        keyPair = keyGen.generateKeyPair();
        
        System.out.println("RSA Key Pair Generated (Size: " + KEY_SIZE + " bits)");
    }
}