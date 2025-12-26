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


public class FileEncryptionHandler {
    
    private DESAlgorithm des = new DESAlgorithm();
    private AESAlgorithm aes = new AESAlgorithm();
    private DESLibrary desLib = new DESLibrary();
    private AESLibrary aesLib = new AESLibrary();
    private RSAKeyGenerator rsaKeyGen = new RSAKeyGenerator();
    
    
    public EncryptionInfo encryptFile(File inputFile, File outputFile, String method, String rsaParams) throws Exception {
        System.out.println("\n=== FILE ENCRYPTION START ===");
        System.out.println("Input: " + inputFile.getName());
        System.out.println("Method: " + method);
        System.out.println("RSA Params: " + rsaParams);
        
        
        byte[] fileBytes = Files.readAllBytes(inputFile.toPath());
        System.out.println("File size: " + fileBytes.length + " bytes");
        
        
        String fileName = inputFile.getName();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
        }
        
        
        String fileContent = Base64.getEncoder().encodeToString(fileBytes);
        System.out.println("Base64 content length: " + fileContent.length());
        
        String encryptedContent;
        String symmetricKey;
        String rsaKeys;
        
        
        if (method.contains("DES") && method.contains("Manuel")) {
            System.out.println(">>> Using MANUAL DES");
            symmetricKey = rsaKeyGen.generateSymmetricKey(8, rsaParams);
            System.out.println("Generated key: " + symmetricKey + " (length: " + symmetricKey.length() + ")");
            
            encryptedContent = des.encrypt(fileContent, symmetricKey);
            rsaKeys = rsaKeyGen.getKeyPairAsString();
            
        } else if (method.contains("AES") && method.contains("Manuel")) {
            System.out.println(">>> Using MANUAL AES");
            symmetricKey = rsaKeyGen.generateSymmetricKey(16, rsaParams);
            System.out.println("Generated key: " + symmetricKey + " (length: " + symmetricKey.length() + ")");
            
            encryptedContent = aes.encrypt(fileContent, symmetricKey);
            rsaKeys = rsaKeyGen.getKeyPairAsString();
            
        } else if (method.contains("DES") && (method.contains("Kütüphane") || method.contains("Java"))) {
            System.out.println(">>> Using LIBRARY DES");
            symmetricKey = rsaKeyGen.generateSymmetricKey(8, rsaParams);
            System.out.println("Generated key: " + symmetricKey + " (length: " + symmetricKey.length() + ")");
            
            
            if (symmetricKey.length() != 8) {
                System.err.println("ERROR: DES key must be exactly 8 characters!");
                System.err.println("Current key: '" + symmetricKey + "' (length: " + symmetricKey.length() + ")");
                throw new IllegalArgumentException("DES anahtarı tam olarak 8 karakter olmalı! Şu an: " + symmetricKey.length());
            }
            
            try {
                encryptedContent = desLib.encrypt(fileContent, symmetricKey);
                System.out.println("DES encryption successful");
            } catch (Exception e) {
                System.err.println("DES encryption failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            rsaKeys = rsaKeyGen.getKeyPairAsString();
            
        } else if (method.contains("AES") && (method.contains("Kütüphane") || method.contains("Java"))) {
            System.out.println(">>> Using LIBRARY AES");
            symmetricKey = rsaKeyGen.generateSymmetricKey(16, rsaParams);
            System.out.println("Generated key: " + symmetricKey + " (length: " + symmetricKey.length() + ")");
            
            
            if (symmetricKey.length() != 16) {
                System.err.println("ERROR: AES key must be exactly 16 characters!");
                System.err.println("Current key: '" + symmetricKey + "' (length: " + symmetricKey.length() + ")");
                throw new IllegalArgumentException("AES anahtarı tam olarak 16 karakter olmalı! Şu an: " + symmetricKey.length());
            }
            
            try {
                encryptedContent = aesLib.encrypt(fileContent, symmetricKey);
                System.out.println("AES encryption successful");
            } catch (Exception e) {
                System.err.println("AES encryption failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            rsaKeys = rsaKeyGen.getKeyPairAsString();
            
        } else {
            throw new IllegalArgumentException("Dosya şifreleme sadece DES/AES ile yapılabilir!");
        }
        
        System.out.println("Encrypted content length: " + encryptedContent.length());
        
        
        StringBuilder output = new StringBuilder();
        output.append("ENCRYPTED_FILE_V1").append("|");
        output.append(method).append("|");
        output.append(rsaKeys).append("|");
        output.append(symmetricKey).append("|");
        output.append(fileExtension).append("|");
        output.append(encryptedContent);
        
        Files.write(outputFile.toPath(), output.toString().getBytes("UTF-8"));
        
        System.out.println("File encrypted successfully!");
        System.out.println("Output: " + outputFile.getName() + " (" + outputFile.length() + " bytes)");
        System.out.println("=== FILE ENCRYPTION END ===\n");
        
        return new EncryptionInfo(method, rsaKeys, symmetricKey, fileExtension, inputFile.getName());
    }
    
    
    public DecryptionInfo decryptFile(File inputFile, File outputFile) throws Exception {
        System.out.println("\n=== FILE DECRYPTION START ===");
        System.out.println("Input: " + inputFile.getName());
        
        
        String encryptedData = new String(Files.readAllBytes(inputFile.toPath()), "UTF-8");
        System.out.println("Encrypted file size: " + encryptedData.length() + " characters");
        
        if (!encryptedData.startsWith("ENCRYPTED_FILE_V1|")) {
            throw new IllegalArgumentException("Bu şifreli bir dosya değil!");
        }
        
       
        String[] parts = encryptedData.split("\\|", 6);
        if (parts.length < 6) {
            System.err.println("ERROR: Invalid encrypted file format!");
            System.err.println("Parts found: " + parts.length);
            for (int i = 0; i < parts.length; i++) {
                System.err.println("Part " + i + ": " + parts[i].substring(0, Math.min(50, parts[i].length())));
            }
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
        System.out.println("Symmetric key: " + symmetricKey + " (length: " + symmetricKey.length() + ")");
        System.out.println("Encrypted content length: " + encryptedContent.length());
        
        String decryptedContent;
        
       
        if (method.contains("DES") && method.contains("Manuel")) {
            System.out.println(">>> Using MANUAL DES decryption");
            try {
                decryptedContent = des.decrypt(encryptedContent, symmetricKey);
                System.out.println("DES decryption successful");
            } catch (Exception e) {
                System.err.println("DES decryption failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            
        } else if (method.contains("AES") && method.contains("Manuel")) {
            System.out.println(">>> Using MANUAL AES decryption");
            try {
                decryptedContent = aes.decrypt(encryptedContent, symmetricKey);
                System.out.println("AES decryption successful");
            } catch (Exception e) {
                System.err.println("AES decryption failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            
        } else if (method.contains("DES") && (method.contains("Kütüphane") || method.contains("Java"))) {
            System.out.println(">>> Using LIBRARY DES decryption");
            
            
            if (symmetricKey.length() != 8) {
                System.err.println("ERROR: DES key must be 8 characters!");
                throw new IllegalArgumentException("DES anahtarı 8 karakter olmalı!");
            }
            
            try {
                decryptedContent = desLib.decrypt(encryptedContent, symmetricKey);
                System.out.println("DES decryption successful");
            } catch (Exception e) {
                System.err.println("DES decryption failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            
        } else if (method.contains("AES") && (method.contains("Kütüphane") || method.contains("Java"))) {
            System.out.println(">>> Using LIBRARY AES decryption");
            
            
            if (symmetricKey.length() != 16) {
                System.err.println("ERROR: AES key must be 16 characters!");
                throw new IllegalArgumentException("AES anahtarı 16 karakter olmalı!");
            }
            
            try {
                decryptedContent = aesLib.decrypt(encryptedContent, symmetricKey);
                System.out.println("AES decryption successful");
            } catch (Exception e) {
                System.err.println("AES decryption failed: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            
        } else {
            throw new IllegalArgumentException("Desteklenmeyen şifreleme yöntemi: " + method);
        }
        
        System.out.println("Decrypted content length: " + decryptedContent.length());
        
        
        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(decryptedContent);
            System.out.println("Base64 decoded successfully: " + fileBytes.length + " bytes");
        } catch (Exception e) {
            System.err.println("Base64 decode failed: " + e.getMessage());
            throw new IllegalArgumentException("Dosya içeriği Base64 formatında değil!");
        }
        
        
        Files.write(outputFile.toPath(), fileBytes);
        
        System.out.println("File decrypted successfully!");
        System.out.println("Output: " + outputFile.getName() + " (" + fileBytes.length + " bytes)");
        System.out.println("=== FILE DECRYPTION END ===\n");
        
        return new DecryptionInfo(method, symmetricKey, fileExtension, outputFile.getName(), fileBytes.length);
    }
    
    
    public FileInfo viewEncryptedFileInfo(File inputFile) throws Exception {
        String encryptedData = new String(Files.readAllBytes(inputFile.toPath()), "UTF-8");
        
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