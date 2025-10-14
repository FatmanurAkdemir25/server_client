import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private static ServerGUI instance;
    private JTextArea logArea;
    private JComboBox<String> methodCombo;
    private JTextField keyField;
    private JTextArea encryptedTextArea;
    private JButton decryptButton;
    private CryptoServer server;
    private DecryptionEngine decryptionEngine;
    
    public ServerGUI() {
        instance = this;
        decryptionEngine = new DecryptionEngine();
        initComponents();
        server = new CryptoServer(this);
        server.startServer();
    }
    
    private void initComponents() {
        setTitle("Sunucu - Mesaj Deşifreleme");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clientTab = new JButton("🔒 İstemci (Şifreleme)");
        JButton serverTab = new JButton("🔓 Sunucu (Deşifreleme)");
        clientTab.setBackground(Color.WHITE);
        serverTab.setBackground(new Color(76, 175, 80));
        serverTab.setForeground(Color.WHITE);
        
        clientTab.addActionListener(e -> {
            if (ClientGUI.getInstance() != null && ClientGUI.getInstance().isVisible()) {
                ClientGUI.getInstance().toFront();
                ClientGUI.getInstance().requestFocus();
            } else {
                SwingUtilities.invokeLater(() -> {
                    ClientGUI client = new ClientGUI();
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
            "Caesar Cipher",
            "Vigenere Cipher",
            "Substitution Cipher",
            "Affine Cipher",
            "Rail Fence Cipher"
        });
        methodCombo.setMaximumSize(new Dimension(800, 40));
        mainPanel.add(methodCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel keyLabel = new JLabel("🔑 Anahtar (Kaydırma Sayısı)");
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
    }
    
    public void setReceivedData(String method, String key, String encryptedMessage) {
        methodCombo.setSelectedItem(method);
        keyField.setText(key);
        encryptedTextArea.setText(encryptedMessage);
    }
    
    private void performDecryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();
        String encrypted = encryptedTextArea.getText().trim();
        
        if (key.isEmpty() || encrypted.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", 
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String decrypted = decryptionEngine.decrypt(method, key, encrypted);
            
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
            JOptionPane.showMessageDialog(this, 
                "Deşifreleme hatası: " + e.getMessage(), 
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static ServerGUI getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI serverGUI = new ServerGUI();
            serverGUI.setVisible(true);
        });
    }
}
