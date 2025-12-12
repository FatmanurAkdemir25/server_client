import javax.swing.*;
import java.awt.*;
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

    public ClientGUI() {
        instance = this;
        client = new CryptoClient();
        encryptionEngine = new EncryptionEngine();
        initComponents();
    }

    private void initComponents() {
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
            if (ServerGUI.getInstance() != null && ServerGUI.getInstance().isVisible()) {
                ServerGUI.getInstance().toFront();
                ServerGUI.getInstance().requestFocus();
            } else {
                SwingUtilities.invokeLater(() -> {
                    ServerGUI server = new ServerGUI();
                    server.setVisible(true);
                });
            }
        });

        topPanel.add(clientTab);
        topPanel.add(serverTab);
        add(topPanel, BorderLayout.NORTH);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ----------- HİZALAMA KODU -----------
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("İstemci - Mesaj Şifreleme");
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
                "DES (Manuel Implementasyon)",
                "AES (Manuel Implementasyon)",
                "DES (Java Kütüphanesi)",
                "AES (Java Kütüphanesi)",
                "RSA (Manuel Implementasyon)",
                "RSA (Java Kütüphanesi)"
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

        keyField = new JTextField();
        keyField.setFont(new Font("Arial", Font.PLAIN, 14));
        keyField.setMaximumSize(new Dimension(800, 40));
        keyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(keyField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel messageLabel = new JLabel("Mesaj");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        messageArea = new JTextArea(5, 40);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane msgScrollPane = new JScrollPane(messageArea);
        msgScrollPane.setMaximumSize(new Dimension(800, 120));
        msgScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(msgScrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Şifrele Butonu (Hizasız Olan Kısmı Düzelttik) ---
        encryptButton = new JButton("Şifrele");
        encryptButton.setBackground(new Color(33, 150, 243));
        encryptButton.setForeground(Color.WHITE);
        encryptButton.setFont(new Font("Arial", Font.BOLD, 16));
        encryptButton.setMaximumSize(new Dimension(800, 50));
        encryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);  // *** KRİTİK DÜZELTME ***
        encryptButton.addActionListener(e -> performEncryption());
        mainPanel.add(encryptButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel resultLabel = new JLabel("Şifrelenmiş Mesaj:");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        resultScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(resultScrollPane);

        add(mainPanel, BorderLayout.CENTER);

        updateKeyFieldHint();
    }

    private void updateKeyFieldHint() {
        String method = (String) methodCombo.getSelectedItem();
        if (method.startsWith("Caesar")) keyField.setToolTipText("Örnek: 3 (1-25)");
        else if (method.startsWith("Vigenere")) keyField.setToolTipText("Örnek: ANAHTAR");
        else if (method.startsWith("Substitution")) keyField.setToolTipText("26 harf dizilimi");
        else if (method.startsWith("Affine")) keyField.setToolTipText("Örnek: 5,8 (a,b)");
        else if (method.startsWith("Rail Fence")) keyField.setToolTipText("Örnek: 3");
        else if (method.startsWith("Route")) keyField.setToolTipText("Örnek: 5,clockwise");
        else if (method.startsWith("Columnar")) keyField.setToolTipText("Örnek: KEY");
        else if (method.startsWith("Polybius")) keyField.setToolTipText("Anahtar opsiyonel");
        else if (method.startsWith("Pigpen")) keyField.setToolTipText("default yazın");
        else if (method.startsWith("Hill")) keyField.setToolTipText("Örnek: 3,3,2,5 (matris)");
        else if (method.startsWith("Playfair")) keyField.setToolTipText("Anahtar kelime");
        else if (method.contains("DES")) keyField.setToolTipText("8 karakter");
        else if (method.contains("AES")) keyField.setToolTipText("16 karakter");
        else if (method.contains("RSA") && method.contains("Manuel"))
            keyField.setToolTipText("Örnek: 61,53 veya auto");
        else if (method.contains("RSA") && method.contains("Java"))
            keyField.setToolTipText("auto");
    }

    private void performEncryption() {
        String method = (String) methodCombo.getSelectedItem();
        String key = keyField.getText().trim();
        String message = messageArea.getText().trim();

        if (method.startsWith("Polybius") && key.isEmpty()) key = "";
        if (method.startsWith("Pigpen") && key.isEmpty()) key = "default";

        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen mesaj girin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!method.startsWith("Polybius") && !method.startsWith("Pigpen") && key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen anahtar girin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String encrypted = encryptionEngine.encrypt(method, key, message);
            resultArea.setText(encrypted);

            client.sendToServer(method, key, encrypted);

            JOptionPane.showMessageDialog(this,
                    "Şifreleme başarılı!\nVeriler sunucuya gönderildi.",
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Sunucuya bağlanılamadı!",
                    "Bağlantı Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage(),
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
