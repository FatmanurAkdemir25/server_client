import java.io.*;
import java.net.*;
public class Client {
    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendFile(File file, ClientGUI gui) {
        new Thread(() -> {
            try (Socket socket = new Socket(host, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream());
                 FileInputStream fis = new FileInputStream(file)) {

                dos.writeUTF("UPLOAD");
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                dos.flush();

                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, read);
                }
                dos.flush();

                
                String resp = dis.readUTF();
                gui.log("Sunucudan cevap: " + resp);
                gui.log("Dosya gönderildi: " + file.getName() + " (" + file.length() + " bytes)");
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                gui.log("Gönderim hatası: " + e.getMessage());
                gui.log(sw.toString());
            }
        }).start();
    }

    
    public void downloadFile(String fileName, ClientGUI gui) {
        new Thread(() -> {
            try (Socket socket = new Socket(host, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                dos.writeUTF("DOWNLOAD:" + fileName);
                dos.flush();

                long fileSize = dis.readLong();
                if (fileSize == -1) {
                    gui.log("Sunucuda dosya bulunamadı: " + fileName);
                    return;
                }

                try (FileOutputStream fos = new FileOutputStream("downloaded_" + fileName)) {
                    byte[] buffer = new byte[4096];
                    long remaining = fileSize;
                    int read;
                    while (remaining > 0 && (read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, read);
                        remaining -= read;
                    }
                    fos.flush();
                }

                gui.log("Dosya indirildi: downloaded_" + fileName + " (" + fileSize + " bytes)");
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                gui.log("İndirme hatası: " + e.getMessage());
                gui.log(sw.toString());
            }
        }).start();
    }
}
