package src.engine;

import src.algorithms.symmetric.*;
import src.algorithms.asymmetric.RSAKeyGenerator;
import src.algorithms.asymmetric.ECCKeyGenerator;  
import java.io.*;
import java.nio.file.*;
import java.util.Base64;


public class FileEncryptionHandler {
    
    private DESAlgorithm des = new DESAlgorithm();
    private AESAlgorithm aes = new AESAlgorithm();
    private DESLibrary desLib = new DESLibrary();
    private AESLibrary aesLib = new AESLibrary();
    private RSAKeyGenerator rsaKeyGen = new RSAKeyGenerator();
    private ECCKeyGenerator eccKeyGen;  // ← YENİ
    
    public FileEncryptionHandler() {
        try {
            eccKeyGen = new ECCKeyGenerator();
        } catch (Exception e) {
            System.err.println("ECC Key Generator initialization failed: " + e.getMessage());
        }
    }
    
    
    public EncryptionInfo encryptFile(File inputFile, File outputFile, String method, String keyParams) throws Exception {
        System.out.println("\n=== FILE ENCRYPTION START ===");
        System.out.println("Input: " + inputFile.getName());
        System.out.println("Method: " + method);
        System.out.println("Key Params: " + keyParams);
        
        
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
        String asymmetricKeys;
        String keyGenMethod; 
        
        

        if (method.contains("ECC ile Anahtar")) {
            System.out.println(">>> Using ECC for key generation");
            keyGenMethod = "ECC";
    
            if (method.contains("DES")) {
                
                symmetricKey = eccKeyGen.generateSymmetricKey(8, keyParams.isEmpty() ? "auto" : keyParams);
                System.out.println("Generated DES key with ECC: " + symmetricKey);
        
                if (symmetricKey.length() != 8) {
                    throw new IllegalArgumentException("DES anahtarı tam 8 karakter olmalı!");
                }

                if (method.contains("Manuel")) {
                    encryptedContent = des.encrypt(fileContent, symmetricKey);
                } else {
                    encryptedContent = desLib.encrypt(fileContent, symmetricKey);
                }

            } else if (method.contains("AES")) {
                
                symmetricKey = eccKeyGen.generateSymmetricKey(16, keyParams.isEmpty() ? "auto" : keyParams);
                System.out.println("Generated AES key with ECC: " + symmetricKey);
        
                if (symmetricKey.length() != 16) {
                    throw new IllegalArgumentException("AES anahtarı tam 16 karakter olmalı!");
                }
        
                if (method.contains("Manuel")) {
            encryptedContent = aes.encrypt(fileContent, symmetricKey);
                } else {
                    encryptedContent = aesLib.encrypt(fileContent, symmetricKey);
                }
            } else {
                throw new IllegalArgumentException("ECC ile sadece DES/AES destekleniyor!");
            }
    
            
            asymmetricKeys = "ECC_GENERATED";  
        }

        
        
        else if (method.contains("RSA ile Anahtar")) {
            System.out.println(">>> Using RSA for key generation");
            keyGenMethod = "RSA";
            
            if (method.contains("DES")) {
                symmetricKey = rsaKeyGen.generateSymmetricKey(8, keyParams.isEmpty() ? "auto" : keyParams);
                
                if (symmetricKey.length() != 8) {
                    throw new IllegalArgumentException("DES anahtarı tam 8 karakter olmalı!");
                }
                
                if (method.contains("Manuel")) {
                    encryptedContent = des.encrypt(fileContent, symmetricKey);
                } else {
                    encryptedContent = desLib.encrypt(fileContent, symmetricKey);
                }
                
            } else if (method.contains("AES")) {
                symmetricKey = rsaKeyGen.generateSymmetricKey(16, keyParams.isEmpty() ? "auto" : keyParams);
                
                if (symmetricKey.length() != 16) {
                    throw new IllegalArgumentException("AES anahtarı tam 16 karakter olmalı!");
                }
                
                if (method.contains("Manuel")) {
                    encryptedContent = aes.encrypt(fileContent, symmetricKey);
                } else {
                    encryptedContent = aesLib.encrypt(fileContent, symmetricKey);
                }
            } else {
                throw new IllegalArgumentException("RSA ile sadece DES/AES destekleniyor!");
            }
            
            asymmetricKeys = rsaKeyGen.getKeyPairAsString();
        }
        
        
        
        else if (method.contains("Manuel - Direkt Anahtar")) {
            System.out.println(">>> Using manual direct key");
            keyGenMethod = "MANUAL";
            symmetricKey = keyParams;
            asymmetricKeys = "NONE";
            
            if (method.contains("DES")) {
                if (symmetricKey.length() != 8) {
                    throw new IllegalArgumentException("DES anahtarı tam 8 karakter olmalı!");
                }
                encryptedContent = des.encrypt(fileContent, symmetricKey);
                
            } else if (method.contains("AES")) {
                if (symmetricKey.length() != 16) {
                    throw new IllegalArgumentException("AES anahtarı tam 16 karakter olmalı!");
                }
                encryptedContent = aes.encrypt(fileContent, symmetricKey);
            } else {
                throw new IllegalArgumentException("Geçersiz manuel mod!");
            }
        }
        
        else {
            throw new IllegalArgumentException("Desteklenmeyen şifreleme yöntemi!");
        }
        
        System.out.println("Encrypted content length: " + encryptedContent.length());
        
        
        StringBuilder output = new StringBuilder();
        output.append("ENCRYPTED_FILE_V2").append("|");  
        output.append(method).append("|");
        output.append(keyGenMethod).append("|");
        output.append(asymmetricKeys).append("|");
        output.append(symmetricKey).append("|");
        output.append(fileExtension).append("|");
        output.append(encryptedContent);
        
        Files.write(outputFile.toPath(), output.toString().getBytes("UTF-8"));
        
        System.out.println("File encrypted successfully!");
        System.out.println("Key Generation Method: " + keyGenMethod);
        System.out.println("Output: " + outputFile.getName() + " (" + outputFile.length() + " bytes)");
        System.out.println("=== FILE ENCRYPTION END ===\n");
        
        return new EncryptionInfo(method, asymmetricKeys, symmetricKey, fileExtension, inputFile.getName(), keyGenMethod);
    }
    
    
    public DecryptionInfo decryptFile(File inputFile, File outputFile) throws Exception {
        System.out.println("\n=== FILE DECRYPTION START ===");
        System.out.println("Input: " + inputFile.getName());
        
        
        String encryptedData = new String(Files.readAllBytes(inputFile.toPath()), "UTF-8");
        System.out.println("Encrypted file size: " + encryptedData.length() + " characters");
        
        
        boolean isV2 = encryptedData.startsWith("ENCRYPTED_FILE_V2|");
        boolean isV1 = encryptedData.startsWith("ENCRYPTED_FILE_V1|");
        
        if (!isV2 && !isV1) {
            throw new IllegalArgumentException("Bu şifreli bir dosya değil!");
        }
        
        String[] parts;
        String header, method, keyGenMethod, asymmetricKeys, symmetricKey, fileExtension, encryptedContent;
        
        if (isV2) {
            
            parts = encryptedData.split("\\|", 7);
            if (parts.length < 7) {
                throw new IllegalArgumentException("Geçersiz V2 dosya formatı!");
            }
            
            header = parts[0];
            method = parts[1];
            keyGenMethod = parts[2];
            asymmetricKeys = parts[3];
            symmetricKey = parts[4];
            fileExtension = parts[5];
            encryptedContent = parts[6];
            
            System.out.println("Format: V2 (ECC/RSA destekli)");
            System.out.println("Key Generation: " + keyGenMethod);
            
        } else {
            
            parts = encryptedData.split("\\|", 6);
            if (parts.length < 6) {
                throw new IllegalArgumentException("Geçersiz V1 dosya formatı!");
            }
            
            header = parts[0];
            method = parts[1];
            keyGenMethod = "RSA"; 
            asymmetricKeys = parts[2];
            symmetricKey = parts[3];
            fileExtension = parts[4];
            encryptedContent = parts[5];
            
            System.out.println("Format: V1 (Legacy RSA)");
        }
        
        System.out.println("Method: " + method);
        System.out.println("Extension: " + fileExtension);
        System.out.println("Symmetric key: " + symmetricKey + " (length: " + symmetricKey.length() + ")");
        System.out.println("Encrypted content length: " + encryptedContent.length());
        
        String decryptedContent;
        
        
        if (method.contains("DES")) {
            if (method.contains("Manuel")) {
                decryptedContent = des.decrypt(encryptedContent, symmetricKey);
            } else {
                decryptedContent = desLib.decrypt(encryptedContent, symmetricKey);
            }
            
        } else if (method.contains("AES")) {
            if (method.contains("Manuel")) {
                decryptedContent = aes.decrypt(encryptedContent, symmetricKey);
            } else {
                decryptedContent = aesLib.decrypt(encryptedContent, symmetricKey);
            }
        } else {
            throw new IllegalArgumentException("Desteklenmeyen deşifreleme yöntemi: " + method);
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
        
        return new DecryptionInfo(method, symmetricKey, fileExtension, outputFile.getName(), fileBytes.length, keyGenMethod);
    }
    
    
    
    public static class EncryptionInfo {
        public String method;
        public String asymmetricKeys;
        public String symmetricKey;
        public String fileExtension;
        public String originalFileName;
        public String keyGenMethod; 
        
        public EncryptionInfo(String method, String asymmetricKeys, String symmetricKey, 
                            String fileExtension, String originalFileName, String keyGenMethod) {
            this.method = method;
            this.asymmetricKeys = asymmetricKeys;
            this.symmetricKey = symmetricKey;
            this.fileExtension = fileExtension;
            this.originalFileName = originalFileName;
            this.keyGenMethod = keyGenMethod;
        }
    }
    
    public static class DecryptionInfo {
        public String method;
        public String symmetricKey;
        public String fileExtension;
        public String decryptedFileName;
        public long fileSize;
        public String keyGenMethod;
        
        public DecryptionInfo(String method, String symmetricKey, String fileExtension, 
                            String decryptedFileName, long fileSize, String keyGenMethod) {
            this.method = method;
            this.symmetricKey = symmetricKey;
            this.fileExtension = fileExtension;
            this.decryptedFileName = decryptedFileName;
            this.fileSize = fileSize;
            this.keyGenMethod = keyGenMethod;
        }
    }
}