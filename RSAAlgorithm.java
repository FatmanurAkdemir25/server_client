import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAAlgorithm {
    
    private BigInteger n, d, e;
    private int bitLength = 512; // Küçük anahtar boyutu (hızlı test için)
    
    // RSA Şifreleme
    public String encrypt(String plaintext, String key) {
        try {
            // Anahtar formatı: "p,q" veya otomatik anahtar üretimi
            if (key.contains(",")) {
                String[] parts = key.split(",");
                BigInteger p = new BigInteger(parts[0].trim());
                BigInteger q = new BigInteger(parts[1].trim());
                generateKeys(p, q);
            } else {
                // Otomatik anahtar üret
                generateKeys();
            }
            
            System.out.println("\n=== RSA Manuel Encryption ===");
            System.out.println("Public Key (e): " + e);
            System.out.println("Modulus (n): " + n);
            
            StringBuilder result = new StringBuilder();
            
            // n, e ve şifreli mesajı kaydet
            result.append(n.toString()).append(":");
            result.append(e.toString()).append(":");
            
            // Her karakteri ayrı ayrı şifrele
            for (char c : plaintext.toCharArray()) {
                BigInteger m = BigInteger.valueOf((int) c);
                BigInteger encrypted = m.modPow(e, n);
                result.append(encrypted.toString()).append(",");
            }
            
            // Son virgülü kaldır
            if (result.charAt(result.length() - 1) == ',') {
                result.setLength(result.length() - 1);
            }
            
            return result.toString();
            
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }
    
    // RSA Deşifreleme
    public String decrypt(String ciphertext, String key) {
        try {
            System.out.println("\n=== RSA Manuel Decryption ===");
            
            // Format: n:e:encrypted1,encrypted2,encrypted3...
            String[] parts = ciphertext.split(":");
            if (parts.length < 3) {
                throw new Exception("Geçersiz şifreli metin formatı!");
            }
            
            BigInteger n = new BigInteger(parts[0]);
            BigInteger e = new BigInteger(parts[1]);
            String[] encryptedChars = parts[2].split(",");
            
            System.out.println("Public Key (e): " + e);
            System.out.println("Modulus (n): " + n);
            
            // Private key hesapla
            if (key.contains(",")) {
                String[] keyParts = key.split(",");
                BigInteger p = new BigInteger(keyParts[0].trim());
                BigInteger q = new BigInteger(keyParts[1].trim());
                
                // φ(n) = (p-1)(q-1)
                BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
                
                // d = e^-1 mod φ(n)
                BigInteger d = e.modInverse(phi);
                
                System.out.println("Private Key (d): " + d);
                
                // Her karakteri deşifrele
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
    
    // Verilen p ve q ile anahtar üretimi
    private void generateKeys(BigInteger p, BigInteger q) {
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        // e seç (genelde 65537)
        e = BigInteger.valueOf(65537);
        
        // e ve phi aralarında asal değilse, e'yi değiştir
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(BigInteger.TWO);
        }
        
        // d = e^-1 mod phi
        d = e.modInverse(phi);
    }
    
    // Otomatik anahtar üretimi
    private void generateKeys() {
        SecureRandom random = new SecureRandom();
        
        // İki asal sayı üret
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        
        generateKeys(p, q);
    }
}
