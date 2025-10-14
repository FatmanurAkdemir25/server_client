import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

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
                
                while (true) {
                    Socket socket = serverSocket.accept();
                    gui.log("İstemci bağlandı: " + socket.getInetAddress());
                    new Thread(() -> handleClient(socket)).start();
                }
            } catch (IOException e) {
                gui.log("Sunucu hatası: " + e.getMessage());
            }
        }).start();
    }
    
    private void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String method = in.readLine();
            String key = in.readLine();
            String encryptedMessage = in.readLine();
            
            gui.log("Alınan veri:");
            gui.log("  Yöntem: " + method);
            gui.log("  Anahtar: " + key);
            gui.log("  Şifreli Mesaj: " + encryptedMessage);
            
            SwingUtilities.invokeLater(() -> {
                gui.setReceivedData(method, key, encryptedMessage);
            });
            
            socket.close();
        } catch (IOException e) {
            gui.log("İstemci işleme hatası: " + e.getMessage());
        }
    }
    
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            gui.log("Sunucu kapatma hatası: " + e.getMessage());
        }
    }
}