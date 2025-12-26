package src.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CryptoMetrics {
    private static final String LOG_FILE = "crypto_metrics.log";
    
    public static void logEncryption(String method, String originalMessage, 
                                     String encryptedMessage, double timeMs) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                              .format(new Date());
            
            pw.println("=".repeat(80));
            pw.println("TIMESTAMP: " + timestamp);
            pw.println("METHOD: " + method);
            pw.println("ORIGINAL SIZE: " + originalMessage.length() + " bytes");
            pw.println("ENCRYPTED SIZE: " + encryptedMessage.length() + " bytes");
            pw.printf("ENCRYPTION TIME: %.3f ms%n", timeMs);
            pw.printf("SIZE INCREASE: %.2f%%%n", 
                      ((encryptedMessage.length() - originalMessage.length()) * 100.0 / 
                       originalMessage.length()));
            pw.println();
            
        } catch (IOException e) {
            System.err.println("Log yazma hatası: " + e.getMessage());
        }
    }
}