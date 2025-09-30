import java.io.*;
import java.net.*;
public class Server {
    private int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    public void start(ServerGUI gui) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                gui.log("Sunucu başlatıldı, port: " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    gui.log("Bağlantı kabul edildi: " + socket.getRemoteSocketAddress());
                    new Thread(() -> handleClient(socket, gui)).start();
                }
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                gui.log("Sunucu hatası: " + e.getMessage());
                gui.log(sw.toString());
            }
        }).start();
    }

    private void handleClient(Socket socket, ServerGUI gui) {
        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            String command = dis.readUTF();

            
            if ("UPLOAD".equals(command)) {
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();

                try (FileOutputStream fos = new FileOutputStream("received_" + fileName)) {
                    byte[] buffer = new byte[4096];
                    long remaining = fileSize;
                    int read;
                    while (remaining > 0 && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, read);
                        remaining -= read;
                    }
                    fos.flush();
                }

                
                dos.writeUTF("UPLOAD_OK");
                dos.flush();
                gui.log("Dosya alındı: received_" + fileName + " (" + fileSize + " bytes)");
            }

            
            else if (command.startsWith("DOWNLOAD:")) {
                String fileName = command.substring(9);
                File file = new File(fileName);
                if (!file.exists()) {
                    dos.writeLong(-1); 
                    dos.flush();
                    gui.log("İstenen dosya bulunamadı: " + fileName);
                } else {
                    dos.writeLong(file.length());
                    dos.flush();
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, read);
                        }
                        dos.flush();
                    }
                    gui.log("Dosya gönderildi: " + fileName + " (" + file.length() + " bytes)");
                }
            } else {
                gui.log("Bilinmeyen komut: " + command);
            }

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            gui.log("İstemci bağlantı hatası: " + e.getMessage());
            gui.log(sw.toString());
        } finally {
            try {
                socket.close();
            } catch (IOException ignore) {}
        }
    }
}
