package src.network;
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;
import src.gui.ServerGUI;

public class CryptoServer {
    private ServerSocket serverSocket;
    private ServerGUI gui;
    private static final int PORT = 9999;
    
    public CryptoServer(ServerGUI gui) {
        this.gui = gui;
    }
    
    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                gui.log("Sunucu başlatıldı. Port: " + PORT);
                gui.log("Mesaj ve Dosya transferi için hazır.\n");
                
                while (true) {
                    Socket socket = serverSocket.accept();
                    gui.log("🔌 İstemci bağlandı: " + socket.getInetAddress());
                    new Thread(() -> handleClient(socket)).start();
                }
            } catch (IOException e) {
                gui.log("Sunucu hatası: " + e.getMessage());
            }
        }).start();
    }
    
    private void handleClient(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream())
            );
    
            String protocol = in.readUTF(); 
    
            if ("MESSAGE".equals(protocol)) {
                gui.log("Mesaj protokolü");
                handleMessageProtocol(in);
            } 
            else if ("FILE".equals(protocol)) {
                gui.log("Dosya protokolü");
                handleFileProtocol(in);
            } 
            else {
                gui.log("Bilinmeyen protokol: " + protocol);
            }
    
            socket.close();
    
        } catch (Exception e) {
            gui.log("Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void handleMessageProtocol(DataInputStream in) {
        try {
            String method = in.readUTF();
            String key = in.readUTF();
            String encryptedMessage = in.readUTF();
    
            gui.log("\n=== MESAJ ALINDI ===");
            gui.log("Yöntem: " + method);
            gui.log("Anahtar: " + key);
    
            SwingUtilities.invokeLater(() ->
                gui.setReceivedData(method, key, encryptedMessage)
            );
    
        } catch (Exception e) {
            gui.log("Mesaj hatası: " + e.getMessage());
        }
    }
    
    
    private void handleFileProtocol(DataInputStream in) {
        try {
            String method = in.readUTF();
            String key = in.readUTF();
            String fileName = in.readUTF();
            String encryptedContent = in.readUTF();
    
            gui.log("\n=== DOSYA ALINDI ===");
            gui.log("Dosya: " + fileName);
            gui.log("Yöntem: " + method);
            gui.log("Anahtar: " + key);
    
            SwingUtilities.invokeLater(() ->
                gui.setReceivedFileData(method, key, fileName, encryptedContent)
            );
    
        } catch (Exception e) {
            gui.log("Dosya hatası: " + e.getMessage());
        }
    }
    
    
    private void handleLegacyProtocol(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
            
            String method = reader.readLine();
            String key = reader.readLine();
            String encryptedMessage = reader.readLine();
            
            gui.log("\n=== MESAJ ALINDI (Legacy) ===");
            gui.log("Yöntem: " + method);
            gui.log("");
            
            SwingUtilities.invokeLater(() -> {
                gui.setReceivedData(method, key != null ? key : "", 
                                   encryptedMessage != null ? encryptedMessage : "");
            });
            
        } catch (Exception e) {
            gui.log("Legacy hatası: " + e.getMessage());
        }
    }
    
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            gui.log("Kapatma hatası: " + e.getMessage());
        }
    }
}