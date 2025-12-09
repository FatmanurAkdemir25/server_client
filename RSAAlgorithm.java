import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAAlgorithm {
    
    private BigInteger n, d, e;
    private int bitLength = 512; 
    
 
    public String encrypt(String plaintext, String key) {
        try {
            
            if (key.contains(",")) {
                String[] parts = key.split(",");
                BigInteger p = new BigInteger(parts[0].trim());
                BigInteger q = new BigInteger(parts[1].trim());
                generateKeys(p, q);
            } else {
                
                generateKeys();
            }
            
            System.out.println("\n=== RSA Manuel Encryption ===");
            System.out.println("Public Key (e): " + e);
            System.out.println("Modulus (n): " + n);
            
            StringBuilder result = new StringBuilder();
            
            
            result.append(n.toString()).append(":");
            result.append(e.toString()).append(":");
            
            
            for (char c : plaintext.toCharArray()) {
                BigInteger m = BigInteger.valueOf((int) c);
                BigInteger encrypted = m.modPow(e, n);
                result.append(encrypted.toString()).append(",");
            }
            
            
            if (result.charAt(result.length() - 1) == ',') {
                result.setLength(result.length() - 1);
            }
            
            return result.toString();
            
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }
    
    
    public String decrypt(String ciphertext, String key) {
        try {
            System.out.println("\n=== RSA Manuel Decryption ===");
            
            
            String[] parts = ciphertext.split(":");
            if (parts.length < 3) {
                throw new Exception("Geçersiz şifreli metin formatı!");
            }
            
            BigInteger n = new BigInteger(parts[0]);
            BigInteger e = new BigInteger(parts[1]);
            String[] encryptedChars = parts[2].split(",");
            
            System.out.println("Public Key (e): " + e);
            System.out.println("Modulus (n): " + n);
            
            
            if (key.contains(",")) {
                String[] keyParts = key.split(",");
                BigInteger p = new BigInteger(keyParts[0].trim());
                BigInteger q = new BigInteger(keyParts[1].trim());
                
                
                BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
                
                
                BigInteger d = e.modInverse(phi);
                
                System.out.println("Private Key (d): " + d);
                
                
                StringBuilder result = new StringBuilder();
                for (String encChar : encryptedChars) {
                    BigInteger encrypted = new BigInteger(encChar);
                    BigInteger decrypted = encrypted.modPow(d, n);
                    result.append((char) decrypted.intValue());
                }
                
                return result.toString();
            } else {
                throw new Exception("Deşifreleme için p,q değerleri gerekli!");
            }
            
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }
    
    
    private void generateKeys(BigInteger p, BigInteger q) {
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        
        e = BigInteger.valueOf(65537);
        
        
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(BigInteger.TWO);
        }
        
        
        d = e.modInverse(phi);
    }
    
    
    private void generateKeys() {
        SecureRandom random = new SecureRandom();
        
        
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        
        generateKeys(p, q);
    }
}
