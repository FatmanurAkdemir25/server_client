package src.gui;

import javax.swing.*;
import java.awt.*;

public class KeyHelperDialog extends JDialog {
    private JTextField targetField;
    
    public KeyHelperDialog(JFrame parent, String method, JTextField targetField) {
        super(parent, "Anahtar Yardımcısı", true);
        this.targetField = targetField;
        setSize(500, 450);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Anahtar Örnekleri - " + method);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        
        if (method.contains("Manuel") && method.contains("DES")) {
            addInfo(panel, "DES anahtarı TAM 8 karakter olmalı!");
            addExample(panel, "secret12", "8 karakter - basit");
            addExample(panel, "PASSWORD", "8 karakter - büyük harf");
            addExample(panel, "12345678", "8 karakter - sayısal");
            addExample(panel, "P@ssw0rd", "8 karakter - özel karakter");
            
        } else if (method.contains("Manuel") && method.contains("AES")) {
            addInfo(panel, "AES anahtarı TAM 16 karakter olmalı!");
            addExample(panel, "mysecretkey12345", "16 karakter - standart");
            addExample(panel, "ABCDEFGH12345678", "16 karakter - karışık");
            addExample(panel, "encryption_key16", "16 karakter - alt tire");
            addExample(panel, "Str0ng!Key#12345", "16 karakter - güçlü");
            
        } else if (method.contains("Kütüphane")) {
            addInfo(panel, "ℹ️ 'auto' önerilir - otomatik güvenli anahtar");
            addExample(panel, "auto", "Otomatik RSA parametreleri");
            addExample(panel, "61,53", "Manuel RSA: p=61, q=53");
            addExample(panel, "97,89", "Manuel RSA: p=97, q=89");
            addExample(panel, "127,131", "Manuel RSA: p=127, q=131");
            
        } else if (method.startsWith("Caesar")) {
            addExample(panel, "3", "Klasik kaydırma (shift 3)");
            addExample(panel, "13", "ROT13 (yarı alfabe)");
            addExample(panel, "7", "Orta güvenlik");
            addExample(panel, "5", "Hafif kaydırma");
            
        } else if (method.startsWith("Vigenere")) {
            addExample(panel, "KEY", "Basit anahtar kelime");
            addExample(panel, "SECRET", "Orta güvenlik");
            addExample(panel, "CRYPTOGRAPHY", "Uzun anahtar - güvenli");
            addExample(panel, "SIFRE", "Türkçe anahtar");
            
        } else if (method.startsWith("Substitution")) {
            addExample(panel, "QWERTYUIOPASDFGHJKLZXCVBNM", "QWERTY sırası");
            addExample(panel, "ZYXWVUTSRQPONMLKJIHGFEDCBA", "Ters alfabe");
            addExample(panel, "XZNLWEBGJHQYUCVPARDSIFKOTM", "Karışık alfabe");
            
        } else if (method.startsWith("Affine")) {
            addInfo(panel, "'a' değeri 26 ile aralarında asal olmalı!");
            addExample(panel, "5,8", "a=5, b=8 (klasik)");
            addExample(panel, "7,3", "a=7, b=3 (güçlü)");
            addExample(panel, "11,15", "a=11, b=15 (alternatif)");
            addExample(panel, "17,20", "a=17, b=20 (kompleks)");
            
        } else if (method.startsWith("Rail Fence")) {
            addExample(panel, "2", "2 ray - minimum");
            addExample(panel, "3", "3 ray - klasik");
            addExample(panel, "4", "4 ray - daha güvenli");
            addExample(panel, "5", "5 ray - yüksek güvenlik");
            
        } else if (method.startsWith("Route")) {
            addExample(panel, "3,clockwise", "3 sütun, saat yönü");
            addExample(panel, "4,clockwise", "4 sütun, saat yönü");
            addExample(panel, "3,counterclockwise", "3 sütun, ters yön");
            addExample(panel, "5,saatYonu", "5 sütun (Türkçe)");
            
        } else if (method.startsWith("Columnar")) {
            addExample(panel, "KEY", "3 harfli basit");
            addExample(panel, "CIPHER", "6 harfli orta");
            addExample(panel, "CRYPTOGRAPHY", "12 harfli uzun");
            addExample(panel, "ZEBRA", "Alfabetik sıralama");
            
        } else if (method.startsWith("Polybius")) {
            addInfo(panel, "ℹ️ Boş bırakılabilir - varsayılan grid kullanılır");
            addExample(panel, "", "Varsayılan 5x5 grid");
            addExample(panel, "SECRET", "Özel anahtar ile grid");
            addExample(panel, "KEYWORD", "Karmaşık anahtar");
            
        } else if (method.startsWith("Pigpen")) {
            addInfo(panel, "ℹ️ Pigpen sabit şema kullanır");
            addExample(panel, "default", "Varsayılan şema");
            addExample(panel, "", "Boş da bırakılabilir");
            
        } else if (method.startsWith("Hill")) {
            addInfo(panel, "2x2 matris: a,b,c,d formatında");
            addExample(panel, "1,2,3,5", "[[1,2], [3,5]]");
            addExample(panel, "3,3,2,5", "[[3,3], [2,5]]");
            addExample(panel, "4,7,1,3", "[[4,7], [1,3]]");
            
        } else if (method.startsWith("Playfair")) {
            addExample(panel, "KEY", "3 harfli basit");
            addExample(panel, "SECRET", "6 harfli orta");
            addExample(panel, "PLAYFAIR", "8 harfli güçlü");
            addExample(panel, "CRYPTOGRAPHY", "12 harfli çok güçlü");
        }
        
        panel.add(Box.createVerticalGlue());
        
        add(new JScrollPane(panel));
    }
    
    private void addExample(JPanel panel, String key, String description) {
        JPanel examplePanel = new JPanel(new BorderLayout(10, 5));
        examplePanel.setMaximumSize(new Dimension(450, 45));
        examplePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(3, 0, 3, 0),
            BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
        examplePanel.setBackground(Color.WHITE);
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        JLabel keyLabel = new JLabel(key.isEmpty() ? "(boş)" : key);
        keyLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        keyLabel.setForeground(new Color(0, 100, 0));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(Color.DARK_GRAY);
        
        labelPanel.add(keyLabel);
        labelPanel.add(descLabel);
        
        JButton useButton = new JButton("Kullan");
        useButton.setFont(new Font("Arial", Font.PLAIN, 11));
        useButton.setBackground(new Color(33, 150, 243));
        useButton.setForeground(Color.WHITE);
        useButton.setFocusPainted(false);
        useButton.setPreferredSize(new Dimension(70, 35));
        useButton.addActionListener(e -> {
            targetField.setText(key);
            dispose();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(useButton);
        
        examplePanel.add(labelPanel, BorderLayout.CENTER);
        examplePanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(examplePanel);
    }
    
    private void addInfo(JPanel panel, String info) {
        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(255, 140, 0));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 5));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);
    }
}