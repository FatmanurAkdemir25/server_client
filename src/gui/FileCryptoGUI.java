package src.gui;



import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.engine.FileEncryptionHandler;
import src.engine.FileEncryptionHandler.*;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

public class FileCryptoGUI extends JFrame {
    private static FileCryptoGUI instance;
    
    private JComboBox<String> methodCombo;
    private JTextField rsaParamsField;
    private JTextField selectedFileField;
    private JButton selectFileButton;
    private JButton encryptFileButton;
    private JButton decryptFileButton;
    private JButton viewFileButton;
    private JTextArea infoArea;
    private JTextArea contentArea;
    
    private File selectedFile;
    private FileEncryptionHandler fileHandler;
    
    public FileCryptoGUI() {
        instance = this;
        fileHandler = new FileEncryptionHandler();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Dosya Şifreleme");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // ÜST TAB BUTTONLARI
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientTab = new JButton("Mesaj Şifreleme");
        JButton serverTab = new JButton("Mesaj Deşifreleme");
        JButton fileTab = new JButton("Dosya İşlemleri");
        
        clientTab.setBackground(Color.WHITE);
        serverTab.setBackground(Color.WHITE);
        fileTab.setBackground(new Color(255, 152, 0));
        fileTab.setForeground(Color.WHITE);
        
        clientTab.addActionListener(e -> {
            if (ClientGUI.getInstance() != null) {
                ClientGUI.getInstance().setVisible(true);
                ClientGUI.getInstance().toFront();
            } else {
                new ClientGUI().setVisible(true);
            }
        });
        
        serverTab.addActionListener(e -> {
            if (ServerGUI.getInstance() != null) {
                ServerGUI.getInstance().setVisible(true);
                ServerGUI.getInstance().toFront();
            } else {
                new ServerGUI().setVisible(true);
            }
        });
        
        topPanel.add(clientTab);
        topPanel.add(serverTab);
        topPanel.add(fileTab);
        add(topPanel, BorderLayout.NORTH);
        
        // ANA PANEL
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // BAŞLIK
        JLabel titleLabel = new JLabel("Dosya Şifreleme ve Deşifreleme");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 152, 0));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // ŞİFRELEME YÖNTEMİ
        JLabel methodLabel = new JLabel("Şifreleme Yöntemi");
        methodLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(methodLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        methodCombo = new JComboBox<>(new String[]{
                "DES (Manuel Implementasyon)",
                "AES (Manuel Implementasyon)",
                "DES (Java Kütüphanesi)",
                "AES (Java Kütüphanesi)"
        });
        methodCombo.setMaximumSize(new Dimension(850, 40));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(methodCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // RSA PARAMETRELERİ
        JLabel rsaLabel = new JLabel("RSA Parametreleri (p,q veya 'auto')");
        rsaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rsaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(rsaLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        rsaParamsField = new JTextField("auto");
        rsaParamsField.setFont(new Font("Arial", Font.PLAIN, 14));
        rsaParamsField.setMaximumSize(new Dimension(850, 40));
        rsaParamsField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(rsaParamsField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // DOSYA SEÇİMİ
        JLabel fileLabel = new JLabel("Dosya Seçimi");
        fileLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(fileLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel filePanel = new JPanel(new BorderLayout(10, 0));
        filePanel.setMaximumSize(new Dimension(850, 40));
        filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        selectedFileField = new JTextField();
        selectedFileField.setEditable(false);
        selectedFileField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        selectFileButton = new JButton("Dosya Seç");
        selectFileButton.addActionListener(e -> selectFile());
        
        filePanel.add(selectedFileField, BorderLayout.CENTER);
        filePanel.add(selectFileButton, BorderLayout.EAST);
        mainPanel.add(filePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // BUTONLAR
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(850, 50));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        encryptFileButton = new JButton("Dosyayı Şifrele");
        encryptFileButton.setBackground(new Color(76, 175, 80));
        encryptFileButton.setForeground(Color.WHITE);
        encryptFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        encryptFileButton.addActionListener(e -> encryptFile());
        
        decryptFileButton = new JButton("Dosyayı Deşifrele");
        decryptFileButton.setBackground(new Color(33, 150, 243));
        decryptFileButton.setForeground(Color.WHITE);
        decryptFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        decryptFileButton.addActionListener(e -> decryptFile());
        
        viewFileButton = new JButton("Dosya Bilgilerini Göster");
        viewFileButton.setBackground(new Color(255, 152, 0));
        viewFileButton.setForeground(Color.WHITE);
        viewFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewFileButton.addActionListener(e -> viewFileInfo());
        
        buttonPanel.add(encryptFileButton);
        buttonPanel.add(decryptFileButton);
        buttonPanel.add(viewFileButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // BİLGİ ALANI
        JLabel infoLabel = new JLabel("İşlem Bilgileri:");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        infoArea = new JTextArea(6, 40);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        infoArea.setBackground(new Color(240, 240, 240));
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setMaximumSize(new Dimension(850, 150));
        infoScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(infoScroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // İÇERİK ÖNIZLEME
        JLabel contentLabel = new JLabel("Dosya İçeriği Önizleme:");
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(contentLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        contentArea = new JTextArea(10, 40);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setMaximumSize(new Dimension(850, 250));
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(contentScroll);
        
        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileField.setText(selectedFile.getAbsolutePath());
            
            // Dosya içeriğini göster
            try {
                previewFile(selectedFile);
            } catch (Exception e) {
                contentArea.setText("Dosya önizlemesi yapılamadı: " + e.getMessage());
            }
        }
    }
    
    private void previewFile(File file) throws Exception {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        
        // Dosya tipine göre önizleme
        String fileName = file.getName().toLowerCase();
        
        if (fileName.endsWith(".txt") || fileName.endsWith(".java") || 
            fileName.endsWith(".xml") || fileName.endsWith(".json") ||
            fileName.endsWith(".html") || fileName.endsWith(".css")) {
            // Metin dosyası - doğrudan göster
            String content = new String(fileBytes);
            if (content.length() > 2000) {
                content = content.substring(0, 2000) + "\n\n... (içerik kısaltıldı)";
            }
            contentArea.setText(content);
        } else if (fileName.endsWith(".encrypted")) {
            // Şifreli dosya
            contentArea.setText("Şifreli dosya - 'Dosya Bilgilerini Göster' butonuna tıklayın");
        } else {
            // Binary dosya - hex göster
            StringBuilder hex = new StringBuilder();
            int limit = Math.min(fileBytes.length, 500);
            for (int i = 0; i < limit; i++) {
                hex.append(String.format("%02X ", fileBytes[i]));
                if ((i + 1) % 16 == 0) hex.append("\n");
            }
            if (fileBytes.length > 500) {
                hex.append("\n... (içerik kısaltıldı)");
            }
            contentArea.setText("Binary Dosya (Hex):\n" + hex.toString());
        }
        
        infoArea.setText("Dosya: " + file.getName() + "\n" +
                        "Boyut: " + fileBytes.length + " bytes\n" +
                        "Yol: " + file.getAbsolutePath());
    }
    
    private void encryptFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir dosya seçin!");
            return;
        }
        
        String method = (String) methodCombo.getSelectedItem();
        String rsaParams = rsaParamsField.getText().trim();
        
        if (rsaParams.isEmpty()) {
            rsaParams = "auto";
        }
        
        // Çıktı dosyası seç
        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setSelectedFile(new File(selectedFile.getName() + ".encrypted"));
        
        int result = saveChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = saveChooser.getSelectedFile();
            
            try {
                EncryptionInfo info = fileHandler.encryptFile(selectedFile, outputFile, method, rsaParams);
                
                infoArea.setText("=== ŞİFRELEME BAŞARILI ===\n" +
                               "Orijinal Dosya: " + info.originalFileName + "\n" +
                               "Şifreli Dosya: " + outputFile.getName() + "\n" +
                               "Yöntem: " + info.method + "\n" +
                               "Simetrik Anahtar: " + info.symmetricKey + "\n" +
                               "Dosya Tipi: " + info.fileExtension);
                
                contentArea.setText("Dosya başarıyla şifrelendi!\n\n" +
                                   "Şifreli dosya yolu:\n" + outputFile.getAbsolutePath() + "\n\n" +
                                   "Not: Bu dosyayı deşifrelemek için 'Dosyayı Deşifrele' butonunu kullanın.");
                
                JOptionPane.showMessageDialog(this, 
                    "Dosya başarıyla şifrelendi!\n" + outputFile.getName(),
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Şifreleme hatası: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void decryptFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir şifreli dosya seçin!");
            return;
        }
        
        // Çıktı dosyası seç
        JFileChooser saveChooser = new JFileChooser();
        String originalName = selectedFile.getName();
        if (originalName.endsWith(".encrypted")) {
            originalName = originalName.substring(0, originalName.length() - 10);
        } else {
            originalName = "decrypted_" + originalName;
        }
        saveChooser.setSelectedFile(new File(originalName));
        
        int result = saveChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = saveChooser.getSelectedFile();
            
            try {
                DecryptionInfo info = fileHandler.decryptFile(selectedFile, outputFile);
                
                infoArea.setText("=== DEŞİFRELEME BAŞARILI ===\n" +
                               "Deşifreli Dosya: " + info.decryptedFileName + "\n" +
                               "Yöntem: " + info.method + "\n" +
                               "Simetrik Anahtar: " + info.symmetricKey + "\n" +
                               "Dosya Tipi: " + info.fileExtension + "\n" +
                               "Boyut: " + info.fileSize + " bytes");
                
                // Deşifreli dosyayı önizle
                previewFile(outputFile);
                
                JOptionPane.showMessageDialog(this, 
                    "Dosya başarıyla deşifrelendi!\n" + outputFile.getName(),
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Deşifreleme hatası: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void viewFileInfo() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir dosya seçin!");
            return;
        }
        
        try {
            FileInfo info = fileHandler.viewEncryptedFileInfo(selectedFile);
            
            infoArea.setText("=== ŞİFRELİ DOSYA BİLGİLERİ ===\n" +
                           "Dosya Adı: " + info.fileName + "\n" +
                           "Şifreleme Yöntemi: " + info.method + "\n" +
                           "Simetrik Anahtar: " + info.symmetricKey + "\n" +
                           "Orijinal Dosya Tipi: " + info.fileExtension + "\n" +
                           "Şifreli İçerik Boyutu: " + info.encryptedSize + " karakter");
            
            contentArea.setText("Bu bir şifreli dosyadır.\n\n" +
                               "Dosyayı görmek için 'Dosyayı Deşifrele' butonunu kullanın.\n\n" +
                               "Deşifreleme için gereken bilgiler dosya içinde saklanmıştır.");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Bu bir şifreli dosya değil veya okunamıyor!",
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static FileCryptoGUI getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileCryptoGUI gui = new FileCryptoGUI();
            gui.setVisible(true);
        });
    }
}