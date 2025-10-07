import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class CryptoClient extends JFrame {
    private static CryptoClient instance;
    private JComboBox<String> methodCombo;
    private JTextField keyField;
    private JTextArea messageArea;
    private JButton encryptButton;
    private JTextArea resultArea;
    
    public CryptoClient() {
        instance = this;
        setTitle("İstemci - Mesaj Şifreleme");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientTab = new JButton("İstemci (Şifreleme)");
        JButton serverTab = new JButton("Sunucu (Deşifreleme)");
        clientTab.setBackground(new Color(33, 150, 243));
        clientTab.setForeground(Color.WHITE);
        serverTab.setBackground(Color.WHITE);
        
        serverTab.addActionListener(e -> {
            if (CryptoServer.getInstance() != null && CryptoServer.getInstance().isVisible()) {
                CryptoServer.getInstance().toFront();
                CryptoServer.getInstance().requestFocus();
            } else {
                SwingUtilities.invokeLater(() -> {
                    CryptoServer server = new CryptoServer();
                    server.setVisible(true);
                });
            }
        });
        
        topPanel.add(clientTab);
        topPanel.add(serverTab);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("İstemci - Mesaj Şifreleme");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 150, 243));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel methodLabel = new JLabel("Şifreleme Yöntemi");
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
        methodCombo.addActionListener(e -> updateKeyFieldHint());
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
        
        JLabel messageLabel = new JLabel("Mesaj");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        messageArea = new JTextArea(5, 40);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane msgScrollPane = new JScrollPane(messageArea);
        msgScrollPane.setMaximumSize(new Dimension(800, 120));
        mainPanel.add(msgScrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        encryptButton = new JButton("Şifrele");
        encryptButton.setBackground(new Color(33, 150, 243));
        encryptButton.setForeground(Color.WHITE);
        encryptButton.setFont(new Font("Arial", Font.BOLD, 16));
        encryptButton.setMaximumSize(new Dimension(800, 50));
        encryptButton.addActionListener(e -> performEncryption());
        mainPanel.add(encryptButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel resultLabel = new JLabel("Şifrelenmiş Mesaj:");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(resultLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Courier New", Font.BOLD, 14));
        resultArea.setBackground(new Color(240, 240, 240));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setMaximumSize(new Dimension(800, 130));
        mainPanel.add(resultScrollPane);
        
        add(mainPanel, BorderLayout.CENTER);
        
        updateKeyFieldHint();
    }
    
    private void updateKeyFieldHint() {
        String method = (String) methodCombo.getSelectedItem();
        if (method.startsWith("Caesar")) {
            keyField.setToolTipText("Örnek: 3 (kaydırma sayısı 1-25 arası)");
        } else if (method.startsWith("Vigenere")) {
            keyField.setToolTipText("Örnek: ANAHTAR (herhangi bir kelime)");
        } else if (method.startsWith("Substitution")) {
            keyField.setToolTipText("Örnek: QWERTYUIOPASDFGHJKLZXCVBNM (26 harf)");
        } else if (method.startsWith("Affine")) {
            keyField.setToolTipText("Örnek: 5,8 (a,b formatında - a ile 26 aralarında asal olmalı)");
        }
    }
    
    private void performEncryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (key.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String encrypted = "";
            
            if (method.startsWith("Caesar")) {
                int shift = Integer.parseInt(key);
                if (shift < 1 || shift > 25) {
                    throw new IllegalArgumentException("Kaydırma sayısı 1-25 arası olmalı!");
                }
                encrypted = caesarEncrypt(message, shift);
            } else if (method.startsWith("Vigenere")) {
                if (!key.matches("[a-zA-Z]+")) {
                    throw new IllegalArgumentException("Anahtar sadece harflerden oluşmalı!");
                }
                encrypted = vigenereEncrypt(message, key);
            } else if (method.startsWith("Substitution")) {
                if (key.length() != 26 || !key.matches("[A-Z]+")) {
                    throw new IllegalArgumentException("Anahtar 26 büyük harften oluşmalı!");
                }
                encrypted = substitutionEncrypt(message, key);
            } else if (method.startsWith("Affine")) {
                String[] parts = key.split(",");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Anahtar 'a,b' formatında olmalı!");
                }
                int a = Integer.parseInt(parts[0].trim());
                int b = Integer.parseInt(parts[1].trim());
                if (gcd(a, 26) != 1) {
                    throw new IllegalArgumentException("'a' değeri 26 ile aralarında asal olmalı!");
                }
                encrypted = affineEncrypt(message, a, b);
            }
            
            resultArea.setText(encrypted);
            
            sendToServer(method, key, encrypted);
            
            JOptionPane.showMessageDialog(this, 
                "Şifreleme başarılı!\nVeriler sunucuya gönderildi.",
                "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçersiz anahtar formatı!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String caesarEncrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = shift % 26;
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append((char) ((c - 'A' + shift) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                result.append((char) ((c - 'a' + shift) % 26 + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String vigenereEncrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = key.toUpperCase();
        int keyIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                if (Character.isUpperCase(c)) {
                    result.append((char) ((c - 'A' + shift) % 26 + 'A'));
                } else {
                    result.append((char) ((c - 'a' + shift) % 26 + 'a'));
                }
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String substitutionEncrypt(String text, String key) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        key = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int index = alphabet.indexOf(c);
                result.append(key.charAt(index));
            } else if (Character.isLowerCase(c)) {
                int index = alphabet.indexOf(Character.toUpperCase(c));
                result.append(Character.toLowerCase(key.charAt(index)));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String affineEncrypt(String text, int a, int b) {
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int x = c - 'A';
                int y = (a * x + b) % 26;
                result.append((char) (y + 'A'));
            } else if (Character.isLowerCase(c)) {
                int x = c - 'a';
                int y = (a * x + b) % 26;
                result.append((char) (y + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    
    private void sendToServer(String method, String key, String encrypted) {
        try {
            Socket socket = new Socket("localhost", 9999);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            out.println(method);
            out.println(key);
            out.println(encrypted);
            
            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Sunucuya bağlanılamadı!\nSunucunun çalıştığından emin olun.",
                "Bağlantı Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static CryptoClient getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CryptoClient client = new CryptoClient();
            client.setVisible(true);
        });
    }
}