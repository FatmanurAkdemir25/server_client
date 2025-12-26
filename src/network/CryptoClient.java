package src.network;
import java.io.*;
import java.net.*;


public class CryptoClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;
    private static final int SOCKET_TIMEOUT = 10000; // 10 seconds
    
    
    public void sendToServer(String method, String key, String encryptedMessage) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream out = new DataOutputStream(
                 new BufferedOutputStream(socket.getOutputStream()))) {
    
            out.writeUTF("MESSAGE");
            out.writeUTF(method);
            out.writeUTF(key);
            out.writeUTF(encryptedMessage);
            out.flush();
            
            System.out.println("Message sent to server");
        }
    }
    
    
    public void sendFileToServer(String method, String key, String fileName, String encryptedContent) throws IOException {
        Socket socket = null;
        DataOutputStream out = null;
        
        try {
            System.out.println("\n=== SENDING FILE TO SERVER ===");
            System.out.println("File: " + fileName);
            System.out.println("Size: " + encryptedContent.length() + " bytes");
            System.out.println("Method: " + method);
            
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
            
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            out.writeUTF("FILE");
            out.writeUTF(method);
            out.writeUTF(key);
            out.writeUTF(fileName);
            out.writeUTF(encryptedContent);
            out.flush();
            
            System.out.println("File sent successfully: " + fileName);
            Thread.sleep(200);
            
        } catch (SocketTimeoutException e) {
            System.err.println("Server connection timed out");
            throw new IOException("Sunucu bağlantısı zaman aşımına uğradı", e);
        } catch (ConnectException e) {
            System.err.println("Cannot connect to server");
            throw new IOException("Sunucuya bağlanılamıyor. Sunucu çalışıyor mu?", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Dosya gönderme işlemi kesildi", e);
        } catch (Exception e) {
            System.err.println("File send error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Dosya gönderilemedi: " + e.getMessage(), e);
        } finally {
            try {
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}