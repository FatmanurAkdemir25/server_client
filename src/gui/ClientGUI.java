package src.gui;

import src.engine.EncryptionEngine;
import src.engine.FileEncryptionHandler;
import src.network.CryptoClient;
import src.utils.CryptoMetrics;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ClientGUI extends JFrame {
    private static ClientGUI instance;
    private JComboBox<String> methodCombo;
    private JTextField keyField;
    private JTextArea messageArea;
    private JButton encryptButton;
    private JTextArea resultArea;
    private CryptoClient client;
    private EncryptionEngine encryptionEngine;
    private FileEncryptionHandler fileHandler;
    
    
    private boolean isFileMode = false;
    private File selectedFile = null;
    private JPanel filePanel;
    private JTextField filePathField;

    public ClientGUI() {
        instance = this;
        client = new CryptoClient();
        encryptionEngine = new EncryptionEngine();
        fileHandler = new FileEncryptionHandler();
        initComponents();
    }

    private void initComponents() {
        setTitle("İstemci - Mesaj ve Dosya Şifreleme");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientTab = new JButton("İstemci (Şifreleme)");
        JButton serverTab = new JButton("Sunucu (Deşifreleme)");
        
        clientTab.setBackground(new Color(33, 150, 243));
        clientTab.setForeground(Color.WHITE);
        serverTab.setBackground(Color.WHITE);

        serverTab.addActionListener(e -> {
            if (ServerGUI.getInstance() != null && ServerGUI.getInstance().isVisible()) {
                ServerGUI.getInstance().toFront();
                ServerGUI.getInstance().requestFocus();
            } else {
                SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
            }
        });

        topPanel.add(clientTab);
        topPanel.add(serverTab);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("İstemci - Mesaj ve Dosya Şifreleme");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel methodLabel = new JLabel("Şifreleme Yöntemi");
        methodLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(methodLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        methodCombo = new JComboBox<>(new String[]{
                "Caesar Cipher (Kaydırma)",
                "Vigenere Cipher",
                "Substitution Cipher",
                "Affine Cipher",
                "Rail Fence Cipher (Zigzag)",
                "Route Cipher (Yönlü Şifre)",
                "Columnar Transposition (Sütunlu)",
                "Polybius Square Cipher",
                "Pigpen Cipher (Domuz Ağılı)",
                "Hill Cipher (Matris Şifreleme)",
                "Playfair Cipher",
                "DES (Manuel - Direkt Anahtar)",
                "AES (Manuel - Direkt Anahtar)",
                "DES (Kütüphane - RSA ile Anahtar)",
                "AES (Kütüphane - RSA ile Anahtar)"
        });
        methodCombo.setMaximumSize(new Dimension(800, 40));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodCombo.addActionListener(e -> updateKeyFieldHint());
        mainPanel.add(methodCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel keyLabel = new JLabel("Anahtar");
        keyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(keyLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));
        keyPanel.setMaximumSize(new Dimension(800, 40));
        keyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        keyField = new JTextField();
        keyField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton keyHelperButton = new JButton("💡");
        keyHelperButton.setToolTipText("Anahtar örnekleri");
        keyHelperButton.setPreferredSize(new Dimension(50, 40));
        keyHelperButton.addActionListener(e -> {
            String method = (String) methodCombo.getSelectedItem();
            KeyHelperDialog helper = new KeyHelperDialog(this, method, keyField);
            helper.setVisible(true);
        });
        
        keyPanel.add(keyField);
        keyPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        keyPanel.add(keyHelperButton);
        
        mainPanel.add(keyPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel messageLabel = new JLabel("Mesaj veya Dosya");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setMaximumSize(new Dimension(800, 180));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton messageTabBtn = new JButton("Metin Mesaj");
        JButton fileTabBtn = new JButton("Dosya Seç");
        
        messageTabBtn.setBackground(new Color(33, 150, 243));
        messageTabBtn.setForeground(Color.WHITE);
        fileTabBtn.setBackground(Color.LIGHT_GRAY);
        
        
        messageArea = new JTextArea(6, 40);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane msgScrollPane = new JScrollPane(messageArea);
        msgScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        
        filePanel = new JPanel(new BorderLayout(10, 10));
        filePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        filePanel.setVisible(false);
        
        filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton selectFileBtn = new JButton("Dosya Seç");
        
        selectFileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                isFileMode = true;
            }
        });
        
        filePanel.add(new JLabel("Seçili Dosya:"), BorderLayout.NORTH);
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(selectFileBtn, BorderLayout.EAST);
        
        
        messageTabBtn.addActionListener(e -> {
            messageTabBtn.setBackground(new Color(33, 150, 243));
            messageTabBtn.setForeground(Color.WHITE);
            fileTabBtn.setBackground(Color.LIGHT_GRAY);
            fileTabBtn.setForeground(Color.BLACK);
            msgScrollPane.setVisible(true);
            filePanel.setVisible(false);
            isFileMode = false;
            selectedFile = null;
        });
        
        fileTabBtn.addActionListener(e -> {
            messageTabBtn.setBackground(Color.LIGHT_GRAY);
            messageTabBtn.setForeground(Color.BLACK);
            fileTabBtn.setBackground(new Color(33, 150, 243));
            fileTabBtn.setForeground(Color.WHITE);
            msgScrollPane.setVisible(false);
            filePanel.setVisible(true);
            isFileMode = true;
        });
        
        tabPanel.add(messageTabBtn);
        tabPanel.add(fileTabBtn);
        
        inputPanel.add(tabPanel);
        inputPanel.add(msgScrollPane);
        inputPanel.add(filePanel);
        
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        encryptButton = new JButton("Şifrele ve Sunucuya Gönder");
        encryptButton.setBackground(new Color(33, 150, 243));
        encryptButton.setForeground(Color.WHITE);
        encryptButton.setFont(new Font("Arial", Font.BOLD, 16));
        encryptButton.setMaximumSize(new Dimension(800, 50));
        encryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        encryptButton.addActionListener(e -> performEncryption());
        mainPanel.add(encryptButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel resultLabel = new JLabel("Sonuç:");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(resultLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        resultArea.setBackground(new Color(240, 240, 240));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setMaximumSize(new Dimension(800, 130));
        resultScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(resultScrollPane);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);

        updateKeyFieldHint();
    }

    private void updateKeyFieldHint() {
        String method = (String) methodCombo.getSelectedItem();
        
        if (method.contains("Manuel")) {
            if (method.contains("DES")) {
                keyField.setToolTipText("8 karakter DES anahtarı (örn: secret12)");
            } else if (method.contains("AES")) {
                keyField.setToolTipText("16 karakter AES anahtarı (örn: mysecretkey12345)");
            }
        } else if (method.contains("Kütüphane")) {
            keyField.setToolTipText("RSA parametreleri: 'auto' veya 'p,q' (örn: 61,53)");
        } else if (method.startsWith("Caesar")) {
            keyField.setToolTipText("Kaydırma sayısı (örn: 3)");
        } else if (method.startsWith("Vigenere")) {
            keyField.setToolTipText("Anahtar kelime (örn: SECRET)");
        }
    }

    private void performEncryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();

        
        if (method.startsWith("Polybius") && key.isEmpty()) key = "";
        if (method.startsWith("Pigpen") && key.isEmpty()) key = "default";
        if (method.contains("Kütüphane") && key.isEmpty()) key = "auto";
        
        
        if (isFileMode && method.contains("Manuel")) {
            if (method.contains("DES") && key.length() != 8) {
                JOptionPane.showMessageDialog(this, 
                    "Manuel DES için tam 8 karakterlik anahtar gerekli!\nÖrnek: secret12", 
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (method.contains("AES") && key.length() != 16) {
                JOptionPane.showMessageDialog(this, 
                    "Manuel AES için tam 16 karakterlik anahtar gerekli!\nÖrnek: mysecretkey12345", 
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        if (isFileMode) {
            performFileEncryption(method, key);
        } else {
            performMessageEncryption(method, key);
        }
    }
    
    private void performMessageEncryption(String method, String key) {
        String message = messageArea.getText().trim();
        
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Lütfen mesaj girin!", 
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            long startTime = System.nanoTime();
            String encrypted = encryptionEngine.encrypt(method, key, message);
            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1_000_000.0;
            
            resultArea.setText("=== MESAJ ŞİFRELENDİ ===\n\n" +
                "Şifreli: " + encrypted.substring(0, Math.min(100, encrypted.length())) + "...\n\n" +
                "Orijinal: " + message.length() + " bytes\n" +
                "Şifreli: " + encrypted.length() + " bytes\n" +
                "Süre: " + String.format("%.3f", durationMs) + " ms");

            
            CryptoMetrics.logEncryption(method, message, encrypted, durationMs);
            
            
            client.sendToServer(method, key, encrypted);

            JOptionPane.showMessageDialog(this, 
                "Mesaj şifrelendi ve sunucuya gönderildi!\n\n" +
                "Sunucu ekranında deşifre edebilirsiniz.",
                "Başarılı", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Sunucuya bağlanılamadı!\n" + e.getMessage(), 
                "Bağlantı Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Şifreleme hatası: " + e.getMessage(), 
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performFileEncryption(String method, String key) {
        if (selectedFile == null || !selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, 
                "Lütfen geçerli bir dosya seçin!", 
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            resultArea.setText("Dosya şifreleniyor...\n" + selectedFile.getName());
            
            
            File tempInput = File.createTempFile("input_", ".tmp");
            File tempOutput = File.createTempFile("encrypted_", ".tmp");
            
            
            java.nio.file.Files.copy(selectedFile.toPath(), tempInput.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            
            String rsaParams = method.contains("Manuel") ? "auto" : (key.isEmpty() ? "auto" : key);
            
        
            FileEncryptionHandler.EncryptionInfo info = 
                fileHandler.encryptFile(tempInput, tempOutput, method, rsaParams);
            
            
            byte[] encryptedBytes = java.nio.file.Files.readAllBytes(tempOutput.toPath());
            String encryptedContent = new String(encryptedBytes, "UTF-8");
            
            resultArea.setText("Sunucuya gönderiliyor...");
            
            
            String keyToSend = method.contains("Manuel") ? key : info.symmetricKey;
            
            client.sendFileToServer(method, keyToSend, 
                selectedFile.getName() + ".encrypted", encryptedContent);
            
            resultArea.setText(
                "=== DOSYA ŞİFRELENDİ VE GÖNDERİLDİ ===\n\n" +
                "Dosya: " + selectedFile.getName() + "\n" +
                "Yöntem: " + info.method + "\n" +
                "Anahtar: " + keyToSend + "\n" +
                "Orijinal: " + selectedFile.length() + " bytes\n" +
                "Şifreli: " + encryptedContent.length() + " bytes\n\n" +
                "Sunucuya gönderildi!");
            
            JOptionPane.showMessageDialog(this,
                "Dosya başarıyla şifrelendi ve sunucuya gönderildi!\n\n" +
                "Dosya: " + selectedFile.getName() + "\n" +
                "Yöntem: " + info.method + "\n" +
                "Anahtar: " + keyToSend + "\n\n" +
                "Sunucu ekranında:\n" +
                "1. 'Dosyayı Kaydet' butonuna tıklayın\n" +
                "2. Dosyayı kaydedin ve deşifreleyin\n" +
                "3. 'Dosyayı Aç' ile görüntüleyin",
                "Başarılı", 
                JOptionPane.INFORMATION_MESSAGE);
            
            
            tempInput.delete();
            tempOutput.delete();
            
        } catch (Exception e) {
            resultArea.setText("❌ HATA: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this, 
                "Dosya şifreleme/gönderme hatası:\n\n" + e.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static ClientGUI getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI client = new ClientGUI();
            client.setVisible(true);
        });
    }
}