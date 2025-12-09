import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RSALibrary {
    
    private KeyPair keyPair;
    private static final int KEY_SIZE = 1024; 
    
    
    public String encrypt(String plaintext, String key) throws Exception {
        try {
            System.out.println("\n=== RSA Library Encryption ===");
            
            
            if (key.equals("auto") || key.isEmpty()) {
                generateKeyPair();
            } else {
                
                generateKeyPair();
            }
            
            
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
            
            
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            
            PrivateKey privateKey = keyPair.getPrivate();
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            
            System.out.println("Public Key (Base64): " + publicKeyStr.substring(0, 50) + "...");
            System.out.println("Private Key (Base64): " + privateKeyStr.substring(0, 50) + "...");
            System.out.println("Encrypted Text: " + encryptedText);
            
            
            return privateKeyStr + ":" + encryptedText;
            
        } catch (Exception e) {
            throw new Exception("RSA şifreleme hatası: " + e.getMessage());
        }
    }
    
    
    public String decrypt(String ciphertext, String key) throws Exception {
        try {
            System.out.println("\n=== RSA Library Decryption ===");
            
            
            String[] parts = ciphertext.split(":");
            if (parts.length != 2) {
                throw new Exception("Geçersiz şifreli metin formatı! Format: PRIVATE_KEY:ENCRYPTED_TEXT");
            }
            
            String privateKeyStr = parts[0];
            String encryptedText = parts[1];
            
            System.out.println("Private Key Length: " + privateKeyStr.length());
            System.out.println("Encrypted Text: " + encryptedText);
            
            
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            
            
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            
            
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
    
    
    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE);
        keyPair = keyGen.generateKeyPair();
        
        System.out.println("RSA Key Pair Generated (Size: " + KEY_SIZE + " bits)");
    }
}