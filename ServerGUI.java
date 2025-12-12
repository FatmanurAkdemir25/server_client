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
        setSize(850, 700);  // ---- CLIENT İLE AYNI BOYUT ----
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---------------------------------------------------
        // ÜST TAB BUTTONLARI
        // ---------------------------------------------------
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

        // ---------------------------------------------------
        // ANA PANEL (CLIENT İLE AYNI TASARIM)
        // ---------------------------------------------------
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ----- BAŞLIK -----
        JLabel titleLabel = new JLabel("Sunucu - Mesaj Deşifreleme");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(76, 175, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ============================
        //   DEŞİFRELEME YÖNTEMİ
        // ============================
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
                "DES (Manuel Implementasyon)",
                "AES (Manuel Implementasyon)",
                "DES (Java Kütüphanesi)",
                "AES (Java Kütüphanesi)",
                "RSA (Manuel Implementasyon)",
                "RSA (Java Kütüphanesi)"
        });
        methodCombo.setMaximumSize(new Dimension(800, 40));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(methodCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ============================
        //   ANAHTAR
        // ============================
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

        // ============================
        //   ŞİFRELİ MESAJ
        // ============================
        JLabel encLabel = new JLabel("Şifreli Mesaj");
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

        // ============================
        //   DEŞİFRE BUTONU
        // ============================
        decryptButton = new JButton("Deşifrele");
        decryptButton.setBackground(new Color(76, 175, 80));
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setFont(new Font("Arial", Font.BOLD, 16));
        decryptButton.setMaximumSize(new Dimension(800, 50));
        decryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        decryptButton.addActionListener(e -> performDecryption());
        mainPanel.add(decryptButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ============================
        //   LOG BAŞLIK
        // ============================
        JLabel logLabel = new JLabel("Sunucu Log:");
        logLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(logLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // ============================
        //   LOG ALANI
        // ============================
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", 13, 13));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setMaximumSize(new Dimension(800, 200));
        logScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(logScroll);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    public void setReceivedData(String method, String key, String encryptedMessage) {
        methodCombo.setSelectedItem(method);
        keyField.setText(key);
        encryptedTextArea.setText(encryptedMessage);
    }

    private void performDecryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();
        String encryptedText = encryptedTextArea.getText().trim();

        if (encryptedText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen şifreli mesaj girin!");
            return;
        }

        if (!method.startsWith("Polybius") && !method.startsWith("Pigpen") && key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Anahtar boş bırakılamaz!");
            return;
        }

        try {
            String decrypted = decryptionEngine.decrypt(method, key, encryptedText);

            log("=== DEŞİFRE EDİLDİ ===\nYöntem: " + method +
                    "\nAnahtar: " + key +
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
