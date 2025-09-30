import javax.swing.*;
import java.awt.*;
import java.io.File;
public class ClientGUI extends JFrame{
    private JTextArea logArea;
    private JTextField hostField, portField, downloadField;

    public ClientGUI() {
        setTitle("İstemci");
        setSize(600, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel top = new JPanel();
        top.add(new JLabel("Sunucu IP:"));
        hostField = new JTextField("127.0.0.1", 12);
        top.add(hostField);
        top.add(new JLabel("Port:"));
        portField = new JTextField("5001", 6);
        top.add(portField);

        add(top, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton uploadBtn = new JButton("Dosya Gönder");
        uploadBtn.addActionListener(e -> {
            String host = hostField.getText().trim();
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçerli bir port girin.");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                Client client = new Client(host, port); 
                client.sendFile(f, this);
            }
        });
        bottom.add(uploadBtn);

        bottom.add(new JLabel("İndirilecek dosya adı (server'da):"));
        downloadField = new JTextField(12);
        bottom.add(downloadField);

        JButton downloadBtn = new JButton("Dosya İndir");
        downloadBtn.addActionListener(e -> {
            String host = hostField.getText().trim();
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçerli bir port girin.");
                return;
            }
            String fileName = downloadField.getText().trim();
            if (fileName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "İndirilecek dosya adını yazın.");
                return;
            }
            Client client = new Client(host, port);
            client.downloadFile(fileName, this);
        });
        bottom.add(downloadBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
    }
}
