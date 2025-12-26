package src.gui;

import src.engine.DecryptionEngine;
import src.network.CryptoServer;
import src.engine.FileEncryptionHandler;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ServerGUI extends JFrame {

    private static ServerGUI instance;
    private JTextArea logArea;
    private JComboBox<String> methodCombo;
    private JTextField keyField;
    private JTextArea encryptedTextArea;
    private JButton decryptButton;
    private JButton saveFileButton;
    private JButton openFileButton;
    private CryptoServer server;
    private DecryptionEngine decryptionEngine;
    private FileEncryptionHandler fileHandler;
    
    
    private String receivedFileName;
    private String receivedFileContent;
    private boolean isFileMode = false;
    private File lastDecryptedFile = null;

    public ServerGUI() {
        instance = this;
        decryptionEngine = new DecryptionEngine();
        fileHandler = new FileEncryptionHandler();
        initComponents();
        server = new CryptoServer(this);
        server.startServer();
    }

    private void initComponents() {
        setTitle("Sunucu - Mesaj ve Dosya Deşifreleme");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientTab = new JButton("İstemci (Şifreleme)");
        JButton serverTab = new JButton("Sunucu (Deşifreleme)");

        clientTab.setBackground(Color.WHITE);
        serverTab.setBackground(new Color(76, 175, 80));
        serverTab.setForeground(Color.WHITE);

        clientTab.addActionListener(e -> {
            if (ClientGUI.getInstance() != null && ClientGUI.getInstance().isVisible()) {
                ClientGUI.getInstance().toFront();
                ClientGUI.getInstance().requestFocus();
            } else {
                SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
            }
        });

        topPanel.add(clientTab);
        topPanel.add(serverTab);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Sunucu - Mesaj ve Dosya Deşifreleme");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(76, 175, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel methodLabel = new JLabel("Deşifreleme Yöntemi");
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
        mainPanel.add(methodCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel keyLabel = new JLabel("Anahtar");
        keyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(keyLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        keyField = new JTextField();
        keyField.setFont(new Font("Arial", Font.PLAIN, 14));
        keyField.setMaximumSize(new Dimension(800, 40));
        keyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(keyField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel encLabel = new JLabel("Şifreli Mesaj / Dosya");
        encLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        encLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(encLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        encryptedTextArea = new JTextArea(5, 40);
        encryptedTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        encryptedTextArea.setLineWrap(true);
        encryptedTextArea.setWrapStyleWord(true);
        JScrollPane encScrollPane = new JScrollPane(encryptedTextArea);
        encScrollPane.setMaximumSize(new Dimension(800, 120));
        encScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(encScrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        decryptButton = new JButton("Mesajı Deşifrele");
        decryptButton.setBackground(new Color(76, 175, 80));
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setFont(new Font("Arial", Font.BOLD, 16));
        decryptButton.setPreferredSize(new Dimension(200, 50));
        decryptButton.addActionListener(e -> performDecryption());
        
        saveFileButton = new JButton("Dosyayı Kaydet");
        saveFileButton.setBackground(new Color(255, 152, 0));
        saveFileButton.setForeground(Color.WHITE);
        saveFileButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveFileButton.setPreferredSize(new Dimension(200, 50));
        saveFileButton.setEnabled(false);
        saveFileButton.addActionListener(e -> saveDecryptedFile());
        
        openFileButton = new JButton("Dosyayı Aç");
        openFileButton.setBackground(new Color(33, 150, 243));
        openFileButton.setForeground(Color.WHITE);
        openFileButton.setFont(new Font("Arial", Font.BOLD, 16));
        openFileButton.setPreferredSize(new Dimension(200, 50));
        openFileButton.setEnabled(false);
        openFileButton.addActionListener(e -> openDecryptedFile());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(800, 60));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(decryptButton);
        buttonPanel.add(saveFileButton);
        buttonPanel.add(openFileButton);
        
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel logLabel = new JLabel("Sunucu Log:");
        logLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(logLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setMaximumSize(new Dimension(800, 200));
        logScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(logScroll);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    public void setReceivedData(String method, String key, String encryptedMessage) {
        isFileMode = false;
        saveFileButton.setEnabled(false);
        openFileButton.setEnabled(false);
        methodCombo.setSelectedItem(method);
        keyField.setText(key);
        encryptedTextArea.setText(encryptedMessage);
    }
    
    public void setReceivedFileData(String method, String key, String fileName, String encryptedContent) {
        isFileMode = true;
        receivedFileName = fileName;
        receivedFileContent = encryptedContent;
        saveFileButton.setEnabled(true);
        
        methodCombo.setSelectedItem(method);
        keyField.setText(key);
        encryptedTextArea.setText("[DOSYA ALINDI] " + fileName + "\n\n" + 
            "İçerik Boyutu: " + encryptedContent.length() + " bytes\n\n" +
            "Önizleme (ilk 200 karakter):\n" +
            encryptedContent.substring(0, Math.min(200, encryptedContent.length())) + "...");
    }

    private void performDecryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();
        String encryptedText = encryptedTextArea.getText().trim();

        if (encryptedText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen şifreli mesaj girin!");
            return;
        }
        
        if (isFileMode) {
            JOptionPane.showMessageDialog(this, 
                "Bu bir dosya! Deşifrelemek için 'Dosyayı Kaydet' butonunu kullanın.",
                "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!method.contains("DES") && !method.contains("AES")) {
            if (!method.startsWith("Polybius") && !method.startsWith("Pigpen") && key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Anahtar boş bırakılamaz!");
                return;
            }
        }

        try {
            String decrypted = decryptionEngine.decrypt(method, key, encryptedText);

            log("=== DEŞİFRE EDİLDİ ===\nYöntem: " + method +
                    "\nSonuç: " + decrypted + "\n");

            JOptionPane.showMessageDialog(this,
                    "Çözülen Mesaj:\n" + decrypted,
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Deşifreleme hatası: " + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveDecryptedFile() {
        if (!isFileMode || receivedFileContent == null) {
            JOptionPane.showMessageDialog(this, "Kaydedilecek dosya yok!");
            return;
        }
        
        try {
            File tempEncrypted = File.createTempFile("encrypted_", ".tmp");
            java.nio.file.Files.write(tempEncrypted.toPath(), receivedFileContent.getBytes("UTF-8"));
            
            JFileChooser saveChooser = new JFileChooser();
            String suggestedName = receivedFileName.replace(".encrypted", "");
            saveChooser.setSelectedFile(new File("decrypted_" + suggestedName));
            
            int result = saveChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File outputFile = saveChooser.getSelectedFile();
                
                FileEncryptionHandler.DecryptionInfo info = 
                    fileHandler.decryptFile(tempEncrypted, outputFile);
                
                log("=== DOSYA DEŞİFRELENDİ ===");
                log("Dosya: " + outputFile.getName());
                log("Yöntem: " + info.method);
                log("Boyut: " + info.fileSize + " bytes");
                
                lastDecryptedFile = outputFile;
                openFileButton.setEnabled(true);
                
                int choice = JOptionPane.showConfirmDialog(this,
                    "Dosya başarıyla deşifrelendi!\n\n" + 
                    "Dosya: " + outputFile.getName() + "\n" +
                    "Konum: " + outputFile.getAbsolutePath() + "\n\n" +
                    "Dosyayı şimdi açmak ister misiniz?",
                    "Başarılı", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    openFile(outputFile);
                }
                
                tempEncrypted.delete();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Dosya kaydetme hatası: " + ex.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void openDecryptedFile() {
        if (lastDecryptedFile == null || !lastDecryptedFile.exists()) {
            JOptionPane.showMessageDialog(this, 
                "Açılacak dosya bulunamadı!",
                "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openFile(lastDecryptedFile);
    }
    
    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(file);
                    log("Dosya açıldı: " + file.getName());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Dosya konumu:\n" + file.getAbsolutePath(),
                "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static ServerGUI getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
    }
}