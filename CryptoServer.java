import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class CryptoServer extends JFrame {
    private static CryptoServer instance;
    private JTextArea logArea;
    private ServerSocket serverSocket;
    private JComboBox<String> methodCombo;
    private JTextField keyField;
    private JTextArea encryptedTextArea;
    private JButton decryptButton;
    
    public CryptoServer() {
        instance = this;
        setTitle("Sunucu - Mesaj Deşifreleme");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientTab = new JButton("İstemci (Şifreleme)");
        JButton serverTab = new JButton("Sunucu (Deşifreleme)");
        clientTab.setBackground(Color.WHITE);
        serverTab.setBackground(new Color(76, 175, 80));
        serverTab.setForeground(Color.WHITE);
        
        clientTab.addActionListener(e -> {
            if (CryptoClient.getInstance() != null && CryptoClient.getInstance().isVisible()) {
                CryptoClient.getInstance().toFront();
                CryptoClient.getInstance().requestFocus();
            } else {
                SwingUtilities.invokeLater(() -> {
                    CryptoClient client = new CryptoClient();
                    client.setVisible(true);
                });
            }
        });
        
        topPanel.add(clientTab);
        topPanel.add(serverTab);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Sunucu - Mesaj Deşifreleme");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(76, 175, 80));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel methodLabel = new JLabel("Deşifreleme Yöntemi");
        methodLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(methodLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        methodCombo = new JComboBox<>(new String[]{
            "Caesar Cipher (Kaydırma)",
            "Vigenere Cipher",
            "Substitution Cipher",
            "Affine Cipher"
        });
        methodCombo.setMaximumSize(new Dimension(800, 40));
        mainPanel.add(methodCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel keyLabel = new JLabel("Anahtar (Kaydırma Sayısı)");
        keyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(keyLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        keyField = new JTextField();
        keyField.setMaximumSize(new Dimension(800, 40));
        keyField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(keyField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel encryptedLabel = new JLabel("Şifreli Mesaj");
        encryptedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(encryptedLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        encryptedTextArea = new JTextArea(5, 40);
        encryptedTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        encryptedTextArea.setLineWrap(true);
        encryptedTextArea.setWrapStyleWord(true);
        JScrollPane encScrollPane = new JScrollPane(encryptedTextArea);
        encScrollPane.setMaximumSize(new Dimension(800, 120));
        mainPanel.add(encScrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        decryptButton = new JButton("Deşifrele");
        decryptButton.setBackground(new Color(76, 175, 80));
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setFont(new Font("Arial", Font.BOLD, 16));
        decryptButton.setMaximumSize(new Dimension(800, 50));
        decryptButton.addActionListener(e -> performDecryption());
        mainPanel.add(decryptButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel logLabel = new JLabel("Sunucu Log:");
        logLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(logLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setMaximumSize(new Dimension(800, 150));
        mainPanel.add(logScrollPane);
        
        add(mainPanel, BorderLayout.CENTER);
        
        startServer();
    }
    
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(9999);
                log("Sunucu başlatıldı. Port: 9999");
                
                while (true) {
                    Socket socket = serverSocket.accept();
                    log("İstemci bağlandı: " + socket.getInetAddress());
                    
                    new Thread(() -> handleClient(socket)).start();
                }
            } catch (IOException e) {
                log("Sunucu hatası: " + e.getMessage());
            }
        }).start();
    }
    
    private void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            String method = in.readLine();
            String key = in.readLine();
            String encryptedMessage = in.readLine();
            
            log("Alınan veri:");
            log("  Yöntem: " + method);
            log("  Anahtar: " + key);
            log("  Şifreli Mesaj: " + encryptedMessage);
            
            SwingUtilities.invokeLater(() -> {
                methodCombo.setSelectedItem(method);
                keyField.setText(key);
                encryptedTextArea.setText(encryptedMessage);
            });
            
            socket.close();
        } catch (IOException e) {
            log("İstemci işleme hatası: " + e.getMessage());
        }
    }
    
    private void performDecryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();
        String encrypted = encryptedTextArea.getText().trim();
        
        if (key.isEmpty() || encrypted.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String decrypted = "";
            
            if (method.startsWith("Caesar")) {
                int shift = Integer.parseInt(key);
                decrypted = caesarDecrypt(encrypted, shift);
            } else if (method.startsWith("Vigenere")) {
                decrypted = vigenereDecrypt(encrypted, key);
            } else if (method.startsWith("Substitution")) {
                decrypted = substitutionDecrypt(encrypted, key);
            } else if (method.startsWith("Affine")) {
                String[] parts = key.split(",");
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                decrypted = affineDecrypt(encrypted, a, b);
            }
            
            log("\n=== DEŞİFRELEME SONUCU ===");
            log("Yöntem: " + method);
            log("Anahtar: " + key);
            log("Şifreli Mesaj: " + encrypted);
            log("Çözülmüş Mesaj: " + decrypted);
            log("========================\n");
            
            JOptionPane.showMessageDialog(this, 
                "Deşifreleme Başarılı!\n\nÇözülmüş Mesaj:\n" + decrypted,
                "Sonuç", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Deşifreleme hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String caesarDecrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = shift % 26;
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append((char) ((c - 'A' - shift + 26) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                result.append((char) ((c - 'a' - shift + 26) % 26 + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String vigenereDecrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = key.toUpperCase();
        int keyIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                if (Character.isUpperCase(c)) {
                    result.append((char) ((c - 'A' - shift + 26) % 26 + 'A'));
                } else {
                    result.append((char) ((c - 'a' - shift + 26) % 26 + 'a'));
                }
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String substitutionDecrypt(String text, String key) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        key = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int index = key.indexOf(c);
                result.append(index >= 0 ? alphabet.charAt(index) : c);
            } else if (Character.isLowerCase(c)) {
                int index = key.indexOf(Character.toUpperCase(c));
                result.append(index >= 0 ? Character.toLowerCase(alphabet.charAt(index)) : c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return -1;
    }
    
    private String affineDecrypt(String text, int a, int b) {
        StringBuilder result = new StringBuilder();
        int aInv = modInverse(a, 26);
        
        if (aInv == -1) {
            throw new IllegalArgumentException("'a' değeri için ters mod bulunamadı!");
        }
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int y = c - 'A';
                int x = (aInv * (y - b + 26)) % 26;
                result.append((char) (x + 'A'));
            } else if (Character.isLowerCase(c)) {
                int y = c - 'a';
                int x = (aInv * (y - b + 26)) % 26;
                result.append((char) (x + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static CryptoServer getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CryptoServer server = new CryptoServer();
            server.setVisible(true);
        });
    }
}