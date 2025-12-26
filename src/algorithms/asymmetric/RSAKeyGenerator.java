package src.algorithms.asymmetric;
import java.math.BigInteger;
import java.security.SecureRandom;


public class RSAKeyGenerator {
    
    private BigInteger n, d, e;
    private BigInteger p, q;
    private int bitLength = 512;
    
    
    public String generateSymmetricKey(int keyLength, String rsaParams) throws Exception {
        
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
        
        
        SecureRandom random = new SecureRandom();
        StringBuilder symmetricKey = new StringBuilder();
        
        
        for (int i = 0; i < keyLength; i++) {
            
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            int index = random.nextInt(chars.length());
            symmetricKey.append(chars.charAt(index));
        }
        
        String key = symmetricKey.toString();
        System.out.println("Generated Symmetric Key: " + key);
        System.out.println("Key Length: " + key.length() + " bytes");
        
        return key;
    }
    
    
    public String getKeyPairAsString() {
        return p.toString() + ":" + q.toString() + ":" + 
               e.toString() + ":" + d.toString() + ":" + n.toString();
    }
    
    
    private void generateKeys(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        
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
        
        
        p = BigInteger.probablePrime(bitLength / 2, random);
        q = BigInteger.probablePrime(bitLength / 2, random);
        
        generateKeys(p, q);
    }
    
    
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