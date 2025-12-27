package src.algorithms.asymmetric;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;


public class ECCAlgorithm {
    
    private KeyPair keyPair;
    private static final String CURVE_NAME = "secp256r1"; 
    
    public ECCAlgorithm() throws Exception {
        generateKeyPair();
    }
    
    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(CURVE_NAME);
        keyGen.initialize(ecSpec, new SecureRandom());
        
        this.keyPair = keyGen.generateKeyPair();
        
        System.out.println("ECC Algorithm initialized (Curve: " + CURVE_NAME + ")");
    }
    
    
    public String encrypt(String plaintext, String recipientPublicKeyStr) throws Exception {
        System.out.println("\n[ECCAlgorithm] ECIES Encryption");
        System.out.println("Plaintext length: " + plaintext.length());
        
        
        PublicKey recipientPublicKey;
        
        if (recipientPublicKeyStr.equals(getPublicKeyAsString())) {
            System.out.println("Using self-encryption (same key pair)");
            recipientPublicKey = keyPair.getPublic();
        } else {
            recipientPublicKey = parsePublicKey(recipientPublicKeyStr);
        }
        
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(recipientPublicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        
        System.out.println("Shared secret generated: " + sharedSecret.length + " bytes");
        
        
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] aesKey = sha256.digest(sharedSecret);
        
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(aesKey, 0, 16, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
        
        
        String publicKeyStr = Base64.getEncoder().encodeToString(
            keyPair.getPublic().getEncoded()
        );
        String encryptedStr = Base64.getEncoder().encodeToString(encryptedBytes);
        
        System.out.println("Encryption successful");
        System.out.println("Encrypted length: " + encryptedStr.length());
        
        
        return publicKeyStr + ":" + encryptedStr;
    }
    
    
    public String decrypt(String encryptedPackage) throws Exception {
        System.out.println("\n[ECCAlgorithm] ECIES Decryption");
        System.out.println("Package length: " + encryptedPackage.length());
        
        
        String[] parts = encryptedPackage.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted package format! Expected 'PublicKey:EncryptedData'");
        }
        
        byte[] senderPublicKeyBytes = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);
        
        System.out.println("Sender public key: " + senderPublicKeyBytes.length + " bytes");
        System.out.println("Encrypted data: " + encryptedBytes.length + " bytes");
        
        
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(senderPublicKeyBytes);
        PublicKey senderPublicKey = keyFactory.generatePublic(keySpec);
        
        
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(senderPublicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        
        System.out.println("Shared secret reconstructed: " + sharedSecret.length + " bytes");
        
        
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] aesKey = sha256.digest(sharedSecret);
        
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(aesKey, 0, 16, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        String result = new String(decryptedBytes, "UTF-8");
        
        System.out.println("Decryption successful");
        System.out.println("Decrypted length: " + result.length());
        
        return result;
    }
    
    
    public String encryptAESKey(String aesKey, String recipientPublicKeyStr) throws Exception {
        System.out.println("\n[ECCAlgorithm] Encrypting AES key with ECC");
        return encrypt(aesKey, recipientPublicKeyStr);
    }
    
    
    public String decryptAESKey(String encryptedAESKey) throws Exception {
        System.out.println("\n[ECCAlgorithm] Decrypting AES key with ECC");
        return decrypt(encryptedAESKey);
    }
    
    
    public String sign(String message) throws Exception {
        System.out.println("\n[ECCAlgorithm] Signing message with ECDSA");
        
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(message.getBytes("UTF-8"));
        
        byte[] signatureBytes = signature.sign();
        String result = Base64.getEncoder().encodeToString(signatureBytes);
        
        System.out.println("Signature created: " + result.substring(0, Math.min(40, result.length())) + "...");
        
        return result;
    }
    
    
    public boolean verify(String message, String signatureStr, String publicKeyStr) throws Exception {
        System.out.println("\n[ECCAlgorithm] Verifying signature with ECDSA");
        
        
        PublicKey publicKey = parsePublicKey(publicKeyStr);
        
        
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        
        
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes("UTF-8"));
        
        boolean isValid = signature.verify(signatureBytes);
        
        System.out.println("Signature verification: " + (isValid ? "VALID" : "INVALID"));
        
        return isValid;
    }
    
    public String getPublicKeyAsString() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
    
    public String getPrivateKeyAsString() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }
    
    private PublicKey parsePublicKey(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }
    
    
    public static void main(String[] args) {
        try {
            System.out.println("\n========== ECC ALGORITHM COMPREHENSIVE TEST ==========\n");
            
            
            ECCAlgorithm ecc = new ECCAlgorithm();
            
            System.out.println("Public Key: " + ecc.getPublicKeyAsString().substring(0, 40) + "...\n");
            
            
            System.out.println("========== TEST 1: ECIES ==========");
            String message1 = "Hello! This is a secret message with ECIES.";
            System.out.println("Original: " + message1);
            
            String encrypted1 = ecc.encrypt(message1, ecc.getPublicKeyAsString());
            System.out.println("\nEncrypted package preview: " + encrypted1.substring(0, Math.min(60, encrypted1.length())) + "...");
            
            String decrypted1 = ecc.decrypt(encrypted1);
            System.out.println("\nDecrypted: " + decrypted1);
            
            boolean test1Pass = message1.equals(decrypted1);
            System.out.println("\n" + (test1Pass ? "TEST 1 PASSED" : "TEST 1 FAILED"));
            
            
            System.out.println("\n\n========== TEST 2: ECC + AES HYBRID ==========");
            String message2 = "This is a long message that will be encrypted with hybrid mode for better performance!";
            System.out.println("Original: " + message2);
            
            
            String aesKey = "MySecretAESKey16"; // 16 karakter
            System.out.println("AES Key: " + aesKey);
            
    
            String encryptedAESKey = ecc.encryptAESKey(aesKey, ecc.getPublicKeyAsString());
            System.out.println("Encrypted AES Key: " + encryptedAESKey.substring(0, Math.min(60, encryptedAESKey.length())) + "...");
            
            
            String decryptedAESKey = ecc.decryptAESKey(encryptedAESKey);
            System.out.println("Decrypted AES Key: " + decryptedAESKey);
            
            boolean test2Pass = aesKey.equals(decryptedAESKey);
            System.out.println("\n" + (test2Pass ? "TEST 2 PASSED" : "TEST 2 FAILED"));
            
            
            System.out.println("\n\n========== TEST 3: ECDSA SIGNATURE ==========");
            String document = "I agree to the terms and conditions.";
            System.out.println("Document: " + document);
            
            String signature = ecc.sign(document);
            System.out.println("\nSignature: " + signature.substring(0, Math.min(40, signature.length())) + "...");
            
            boolean isValid = ecc.verify(document, signature, ecc.getPublicKeyAsString());
            System.out.println("\nSignature Valid: " + (isValid ? "YES" : "NO"));
            
            
            String tamperedDoc = document + " NOT!";
            boolean isInvalid = ecc.verify(tamperedDoc, signature, ecc.getPublicKeyAsString());
            System.out.println("Tampered Document Valid: " + (isInvalid ? "YES (BAD!)" : "NO (GOOD!)"));
            
            boolean test3Pass = isValid && !isInvalid;
            System.out.println("\n" + (test3Pass ? "TEST 3 PASSED" : "TEST 3 FAILED"));
            
            
            System.out.println("\n\n========== TEST SUMMARY ==========");
            System.out.println("ECIES:          " + (test1Pass ? "PASSED" : "FAILED"));
            System.out.println("Hybrid Mode:    " + (test2Pass ? "PASSED" : "FAILED"));
            System.out.println("ECDSA:          " + (test3Pass ? "PASSED" : "FAILED"));
            System.out.println("\nAll tests: " + (test1Pass && test2Pass && test3Pass ? "PASSED" : "SOME FAILED"));
            System.out.println("\n========== TEST COMPLETED ==========\n");
            
        } catch (Exception e) {
            System.err.println("\nTest failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}