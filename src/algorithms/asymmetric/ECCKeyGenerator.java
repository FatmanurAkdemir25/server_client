package src.algorithms.asymmetric;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;


public class ECCKeyGenerator {
    
    private KeyPair keyPair;
    private static final String CURVE_NAME = "secp256r1"; // P-256
    private String publicKeyString;
    private String privateKeyString;
    
    
    public ECCKeyGenerator() throws Exception {
        generateKeyPair(CURVE_NAME);
    }
    
    
    public ECCKeyGenerator(String curveName) throws Exception {
        generateKeyPair(curveName);
    }
    
    
    private void generateKeyPair(String curveName) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(curveName);
        keyGen.initialize(ecSpec, new SecureRandom());
        
        this.keyPair = keyGen.generateKeyPair();
        this.publicKeyString = Base64.getEncoder().encodeToString(
            keyPair.getPublic().getEncoded()
        );
        this.privateKeyString = Base64.getEncoder().encodeToString(
            keyPair.getPrivate().getEncoded()
        );
        
        System.out.println("ECC Key Pair Generated");
        System.out.println("   Curve: " + curveName);
        System.out.println("   Public Key Size: " + keyPair.getPublic().getEncoded().length + " bytes");
        System.out.println("   Private Key Size: " + keyPair.getPrivate().getEncoded().length + " bytes");
    }
    
    
    public String generateSymmetricKey(int keyLength, String params) throws Exception {
        
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[keyLength];
        random.nextBytes(keyBytes);
        
        
        String symmetricKey = Base64.getEncoder().encodeToString(keyBytes)
                                .substring(0, keyLength);
        
        
        StringBuilder normalizedKey = new StringBuilder();
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < keyLength; i++) {
            int randomIndex = random.nextInt(allowedChars.length());
            normalizedKey.append(allowedChars.charAt(randomIndex));
        }
        
        String finalKey = normalizedKey.toString();
        
        System.out.println("🔑 Generated symmetric key: " + finalKey + 
                         " (length: " + finalKey.length() + ")");
        
        return finalKey;
    }
    
    
    public String encryptSymmetricKey(String symmetricKey, String recipientPublicKey) throws Exception {
        
        PublicKey publicKey = parsePublicKey(recipientPublicKey);
        
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(CURVE_NAME);
        keyGen.initialize(ecSpec, new SecureRandom());
        KeyPair ephemeralPair = keyGen.generateKeyPair();
        
        
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(ephemeralPair.getPrivate());
        keyAgreement.doPhase(publicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        
        
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] aesKey = sha256.digest(sharedSecret);
        
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(aesKey, 0, 16, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] encrypted = cipher.doFinal(symmetricKey.getBytes("UTF-8"));
        
        
        String ephemeralPublicKeyStr = Base64.getEncoder().encodeToString(
            ephemeralPair.getPublic().getEncoded()
        );
        String encryptedKeyStr = Base64.getEncoder().encodeToString(encrypted);
        
        return ephemeralPublicKeyStr + ":" + encryptedKeyStr;
    }
    
    
    public String decryptSymmetricKey(String encryptedPackage) throws Exception {
        
        String[] parts = encryptedPackage.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted key format");
        }
        
        byte[] ephemeralPublicKeyBytes = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(parts[1]);
        
        
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(ephemeralPublicKeyBytes);
        PublicKey ephemeralPublicKey = keyFactory.generatePublic(keySpec);
        
        
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(ephemeralPublicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        
        
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] aesKey = sha256.digest(sharedSecret);
        
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(aesKey, 0, 16, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decrypted = cipher.doFinal(encryptedKeyBytes);
        
        return new String(decrypted, "UTF-8");
    }
    
    
    public String getPublicKeyAsString() {
        return publicKeyString;
    }
    
    
    public String getPrivateKeyAsString() {
        return privateKeyString;
    }
    
   
    public String getKeyPairAsString() {
        return "ECC_KEYPAIR|" + publicKeyString + "|" + privateKeyString;
    }
    
    
    private PublicKey parsePublicKey(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }
    
    
    public static String[] getAvailableCurves() {
        return new String[]{
            "secp256r1",  
            "secp384r1",  
            "secp521r1",  
            "secp256k1"   
        };
    }
    
    
    public static String getCurveInfo(String curveName) {
        switch (curveName) {
            case "secp256r1":
                return "P-256 (256-bit) - NIST standart, hızlı ve güvenli";
            case "secp384r1":
                return "P-384 (384-bit) - Yüksek güvenlik";
            case "secp521r1":
                return "P-521 (521-bit) - Maksimum güvenlik";
            case "secp256k1":
                return "secp256k1 (256-bit) - Bitcoin/Ethereum";
            default:
                return "Bilinmeyen eğri";
        }
    }
    
    
    public static void testECCKeyGenerator() {
        try {
            System.out.println("\n========== ECC KEY GENERATOR TEST ==========\n");
            
            
            ECCKeyGenerator generator1 = new ECCKeyGenerator();
            ECCKeyGenerator generator2 = new ECCKeyGenerator();
            
            
            String desKey = generator1.generateSymmetricKey(8, "auto");
            System.out.println("DES Key: " + desKey + " (length: " + desKey.length() + ")\n");
            
            
            String aesKey = generator1.generateSymmetricKey(16, "auto");
            System.out.println("AES Key: " + aesKey + " (length: " + aesKey.length() + ")\n");
            
            
            String encryptedDES = generator1.encryptSymmetricKey(
                desKey, 
                generator2.getPublicKeyAsString()
            );
            System.out.println("Encrypted DES Key: " + encryptedDES.substring(0, 50) + "...\n");
            
            
            String decryptedDES = generator2.decryptSymmetricKey(encryptedDES);
            System.out.println("Decrypted DES Key: " + decryptedDES);
            System.out.println("DES Key Match: " + desKey.equals(decryptedDES) + "\n");
            
            
            String encryptedAES = generator1.encryptSymmetricKey(
                aesKey, 
                generator2.getPublicKeyAsString()
            );
            System.out.println("Encrypted AES Key: " + encryptedAES.substring(0, 50) + "...\n");
            
            
            String decryptedAES = generator2.decryptSymmetricKey(encryptedAES);
            System.out.println("Decrypted AES Key: " + decryptedAES);
            System.out.println("AES Key Match: " + aesKey.equals(decryptedAES) + "\n");
            
            
            System.out.println("Available Curves:");
            for (String curve : getAvailableCurves()) {
                System.out.println("  - " + curve + ": " + getCurveInfo(curve));
            }
            
            System.out.println("\n========== TEST COMPLETED ==========\n");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        testECCKeyGenerator();
    }
}