import javax.swing.*;
import java.awt.*;
public class ServerGUI extends JFrame{
    private JTextArea logArea;
    private JTextField portField;
    private Server server;

    public ServerGUI() {
        setTitle("Sunucu");
        setSize(500, 360);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel top = new JPanel();
        top.add(new JLabel("Port:"));
        portField = new JTextField("5001", 8); 
        top.add(portField);
        JButton startBtn = new JButton("Başlat");
        top.add(startBtn);

        add(top, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startBtn.addActionListener(e -> {
            String ptxt = portField.getText().trim();
            try {
                int port = Integer.parseInt(ptxt);
                server = new Server(port);
                server.start(this);
                log("Sunucu dinlemeye başladı, port: " + port);
                portField.setEnabled(false);
                startBtn.setEnabled(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçerli bir port numarası girin (ör. 5001).");
            }
        });
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
    }
}
