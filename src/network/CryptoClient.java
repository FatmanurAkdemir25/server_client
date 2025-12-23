package src.network;
import java.io.*;
import java.net.*;

public class CryptoClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;
    
    public void sendToServer(String method, String key, String encryptedMessage) throws IOException {
        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println(method);
        out.println(key);
        out.println(encryptedMessage);
        
        socket.close();
    }
}