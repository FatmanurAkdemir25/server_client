package src.algorithms.asymmetric;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * RSA Anahtar Üreteci
 * Bu sınıf RSA ile simetrik şifreleme anahtarları (DES/AES) üretir
 */
public class RSAKeyGenerator {
    
    private BigInteger n, d, e;
    private BigInteger p, q;
    private int bitLength = 512;
    
    /**
     * Belirli uzunlukta rastgele simetrik anahtar üretir
     * @param keyLength Anahtar uzunluğu (8 byte DES için, 16 byte AES için)
     * @param rsaParams RSA parametreleri (p,q veya "auto")
     * @return Üretilen simetrik anahtar
     */
    public String generateSymmetricKey(int keyLength, String rsaParams) throws Exception {
        // RSA key pair üret
        if (rsaParams != null && rsaParams.contains(",")) {
            String[] parts = rsaParams.split(",");
            p = new BigInteger(parts[0].trim());
            q = new BigInteger(parts[1].trim());
            generateKeys(p, q);
        } else {
            generateKeys();
        }
        
        System.out.println("\n=== RSA Key Generation ===");
        System.out.println("p: " + p);
        System.out.println("q: " + q);
        System.out.println("Public Key (e): " + e);
        System.out.println("Private Key (d): " + d);
        System.out.println("Modulus (n): " + n);
        
        // Rastgele simetrik anahtar üret
        SecureRandom random = new SecureRandom();
        StringBuilder symmetricKey = new StringBuilder();
        
        // ASCII yazdırılabilir karakterler kullan (33-126 arası)
        for (int i = 0; i < keyLength; i++) {
            // Güvenli karakterler: A-Z, a-z, 0-9
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            int index = random.nextInt(chars.length());
            symmetricKey.append(chars.charAt(index));
        }
        
        String key = symmetricKey.toString();
        System.out.println("Generated Symmetric Key: " + key);
        System.out.println("Key Length: " + key.length() + " bytes");
        
        return key;
    }
    
    /**
     * RSA public ve private key'leri string formatında döndürür
     * Format: "p:q:e:d:n"
     */
    public String getKeyPairAsString() {
        return p.toString() + ":" + q.toString() + ":" + 
               e.toString() + ":" + d.toString() + ":" + n.toString();
    }
    
    /**
     * Verilen p ve q değerleri ile RSA anahtarları üret
     */
    private void generateKeys(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        // Public exponent (genelde 65537 kullanılır)
        e = BigInteger.valueOf(65537);
        
        // e ve phi aralarında asal olmalı
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(BigInteger.TWO);
        }
        
        // Private exponent
        d = e.modInverse(phi);
    }
    
    /**
     * Rastgele p ve q değerleri ile RSA anahtarları üret
     */
    private void generateKeys() {
        SecureRandom random = new SecureRandom();
        
        // Rastgele asal sayılar üret
        p = BigInteger.probablePrime(bitLength / 2, random);
        q = BigInteger.probablePrime(bitLength / 2, random);
        
        generateKeys(p, q);
    }
    
    /**
     * P ve Q değerlerini döndür
     */
    public BigInteger getP() {
        return p;
    }
    
    public BigInteger getQ() {
        return q;
    }
    
    public BigInteger getE() {
        return e;
    }
    
    public BigInteger getD() {
        return d;
    }
    
    public BigInteger getN() {
        return n;
    }
}