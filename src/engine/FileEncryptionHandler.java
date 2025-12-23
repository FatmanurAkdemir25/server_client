package src.engine;

import src.algorithms.symmetric.*;
import src.algorithms.asymmetric.RSAKeyGenerator;
import src.algorithms.symmetric.AESAlgorithm;
import src.algorithms.symmetric.AESLibrary;
import src.algorithms.symmetric.DESAlgorithm;
import src.algorithms.symmetric.DESLibrary;
import src.algorithms.asymmetric.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;

/**
 * Dosya şifreleme ve deşifreleme işlemleri
 */
public class FileEncryptionHandler {
    
    private DESAlgorithm des = new DESAlgorithm();
    private AESAlgorithm aes = new AESAlgorithm();
    private DESLibrary desLib = new DESLibrary();
    private AESLibrary aesLib = new AESLibrary();
    private RSAKeyGenerator rsaKeyGen = new RSAKeyGenerator();
    
    /**
     * Dosyayı şifrele
     * @param inputFile Şifrelenecek dosya
     * @param outputFile Şifreli dosyanın kaydedileceği yer
     * @param method Şifreleme yöntemi
     * @param rsaParams RSA parametreleri
     * @return Şifreleme bilgileri (anahtar, vb.)
     */
    public EncryptionInfo encryptFile(File inputFile, File outputFile, String method, String rsaParams) throws Exception {
        System.out.println("\n=== FILE ENCRYPTION ===");
        System.out.println("Input: " + inputFile.getName());
        System.out.println("Method: " + method);
        
        // Dosyayı oku
        byte[] fileBytes = Files.readAllBytes(inputFile.toPath());
        System.out.println("File size: " + fileBytes.length + " bytes");
        
        // Dosya tipini belirle
        String fileName = inputFile.getName();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
        }
        
        // Dosya içeriğini Base64'e çevir (binary dosyalar için)
        String fileContent = Base64.getEncoder().encodeToString(fileBytes);
        
        String encryptedContent;
        String symmetricKey;
        String rsaKeys;
        
        // Şifreleme yöntemine göre işle
        if (method.contains("DES") && method.contains("Manuel")) {
            symmetricKey = rsaKeyGen.generateSymmetricKey(8, rsaParams);
            encryptedContent = des.encrypt(fileContent, symmetricKey);
            rsaKeys = rsaKeyGen.getKeyPairAsString();
        } else if (method.contains("AES") && method.contains("Manuel")) {
            symmetricKey = rsaKeyGen.generateSymmetricKey(16, rsaParams);
            encryptedContent = aes.encrypt(fileContent, symmetricKey);
            rsaKeys = rsaKeyGen.getKeyPairAsString();
        } else if (method.contains("DES") && method.contains("Java")) {
            symmetricKey = rsaKeyGen.generateSymmetricKey(8, rsaParams);
            encryptedContent = desLib.encrypt(fileContent, symmetricKey);
            rsaKeys = rsaKeyGen.getKeyPairAsString();
        } else if (method.contains("AES") && method.contains("Java")) {
            symmetricKey = rsaKeyGen.generateSymmetricKey(16, rsaParams);
            encryptedContent = aesLib.encrypt(fileContent, symmetricKey);
            rsaKeys = rsaKeyGen.getKeyPairAsString();
        } else {
            throw new IllegalArgumentException("Dosya şifreleme sadece DES/AES ile yapılabilir!");
        }
        
        // Şifreli dosyayı kaydet
        // Format: HEADER|RSA_KEYS|SYMMETRIC_KEY|FILE_EXTENSION|ENCRYPTED_CONTENT
        StringBuilder output = new StringBuilder();
        output.append("ENCRYPTED_FILE_V1").append("|");
        output.append(method).append("|");
        output.append(rsaKeys).append("|");
        output.append(symmetricKey).append("|");
        output.append(fileExtension).append("|");
        output.append(encryptedContent);
        
        Files.write(outputFile.toPath(), output.toString().getBytes());
        
        System.out.println("File encrypted successfully!");
        System.out.println("Output: " + outputFile.getName());
        
        return new EncryptionInfo(method, rsaKeys, symmetricKey, fileExtension, inputFile.getName());
    }
    
    /**
     * Dosyayı deşifrele
     * @param inputFile Şifreli dosya
     * @param outputFile Deşifreli dosyanın kaydedileceği yer
     * @return Deşifreleme bilgileri
     */
    public DecryptionInfo decryptFile(File inputFile, File outputFile) throws Exception {
        System.out.println("\n=== FILE DECRYPTION ===");
        System.out.println("Input: " + inputFile.getName());
        
        // Şifreli dosyayı oku
        String encryptedData = new String(Files.readAllBytes(inputFile.toPath()));
        
        // Header kontrolü
        if (!encryptedData.startsWith("ENCRYPTED_FILE_V1|")) {
            throw new IllegalArgumentException("Bu şifreli bir dosya değil!");
        }
        
        // Verileri ayır
        String[] parts = encryptedData.split("\\|", 6);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Geçersiz şifreli dosya formatı!");
        }
        
        String header = parts[0];
        String method = parts[1];
        String rsaKeys = parts[2];
        String symmetricKey = parts[3];
        String fileExtension = parts[4];
        String encryptedContent = parts[5];
        
        System.out.println("Method: " + method);
        System.out.println("Extension: " + fileExtension);
        System.out.println("Key: " + symmetricKey);
        
        String decryptedContent;
        
        // Deşifreleme yöntemine göre işle
        if (method.contains("DES") && method.contains("Manuel")) {
            decryptedContent = des.decrypt(encryptedContent, symmetricKey);
        } else if (method.contains("AES") && method.contains("Manuel")) {
            decryptedContent = aes.decrypt(encryptedContent, symmetricKey);
        } else if (method.contains("DES") && method.contains("Java")) {
            decryptedContent = desLib.decrypt(encryptedContent, symmetricKey);
        } else if (method.contains("AES") && method.contains("Java")) {
            decryptedContent = aesLib.decrypt(encryptedContent, symmetricKey);
        } else {
            throw new IllegalArgumentException("Desteklenmeyen şifreleme yöntemi!");
        }
        
        // Base64'ten geri çevir
        byte[] fileBytes = Base64.getDecoder().decode(decryptedContent);
        
        // Dosyayı kaydet
        Files.write(outputFile.toPath(), fileBytes);
        
        System.out.println("File decrypted successfully!");
        System.out.println("Output: " + outputFile.getName());
        
        return new DecryptionInfo(method, symmetricKey, fileExtension, outputFile.getName(), fileBytes.length);
    }
    
    /**
     * Şifreli dosyanın içeriğini göster (deşifrelemeden)
     * @param inputFile Şifreli dosya
     * @return Dosya bilgileri
     */
    public FileInfo viewEncryptedFileInfo(File inputFile) throws Exception {
        String encryptedData = new String(Files.readAllBytes(inputFile.toPath()));
        
        if (!encryptedData.startsWith("ENCRYPTED_FILE_V1|")) {
            throw new IllegalArgumentException("Bu şifreli bir dosya değil!");
        }
        
        String[] parts = encryptedData.split("\\|", 6);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Geçersiz şifreli dosya formatı!");
        }
        
        String method = parts[1];
        String rsaKeys = parts[2];
        String symmetricKey = parts[3];
        String fileExtension = parts[4];
        String encryptedContent = parts[5];
        
        return new FileInfo(method, symmetricKey, fileExtension, encryptedContent.length(), inputFile.getName());
    }
    
    // İç sınıflar
    public static class EncryptionInfo {
        public String method;
        public String rsaKeys;
        public String symmetricKey;
        public String fileExtension;
        public String originalFileName;
        
        public EncryptionInfo(String method, String rsaKeys, String symmetricKey, String fileExtension, String originalFileName) {
            this.method = method;
            this.rsaKeys = rsaKeys;
            this.symmetricKey = symmetricKey;
            this.fileExtension = fileExtension;
            this.originalFileName = originalFileName;
        }
    }
    
    public static class DecryptionInfo {
        public String method;
        public String symmetricKey;
        public String fileExtension;
        public String decryptedFileName;
        public long fileSize;
        
        public DecryptionInfo(String method, String symmetricKey, String fileExtension, String decryptedFileName, long fileSize) {
            this.method = method;
            this.symmetricKey = symmetricKey;
            this.fileExtension = fileExtension;
            this.decryptedFileName = decryptedFileName;
            this.fileSize = fileSize;
        }
    }
    
    public static class FileInfo {
        public String method;
        public String symmetricKey;
        public String fileExtension;
        public int encryptedSize;
        public String fileName;
        
        public FileInfo(String method, String symmetricKey, String fileExtension, int encryptedSize, String fileName) {
            this.method = method;
            this.symmetricKey = symmetricKey;
            this.fileExtension = fileExtension;
            this.encryptedSize = encryptedSize;
            this.fileName = fileName;
        }
    }
}