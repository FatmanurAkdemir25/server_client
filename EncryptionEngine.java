public class EncryptionEngine {
    
    public String encrypt(String method, String key, String message) throws Exception {
        if (method.startsWith("Caesar")) {
            int shift = Integer.parseInt(key);
            if (shift < 1 || shift > 25) {
                throw new IllegalArgumentException("Kaydırma sayısı 1-25 arası olmalı!");
            }
            return caesarEncrypt(message, shift);
            
        } else if (method.startsWith("Vigenere")) {
            if (!key.matches("[a-zA-Z]+")) {
                throw new IllegalArgumentException("Anahtar sadece harflerden oluşmalı!");
            }
            return vigenereEncrypt(message, key);
            
        } else if (method.startsWith("Substitution")) {
            if (key.length() != 26 || !key.matches("[A-Z]+")) {
                throw new IllegalArgumentException("Anahtar 26 büyük harften oluşmalı!");
            }
            return substitutionEncrypt(message, key);
            
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
            return affineEncrypt(message, a, b);
            
        } else if (method.startsWith("Rail Fence")) {
            int rails = Integer.parseInt(key);
            if (rails < 2) {
                throw new IllegalArgumentException("Ray sayısı 2 veya daha fazla olmalı!");
            }
            return railFenceEncrypt(message, rails);
        }
        
        throw new IllegalArgumentException("Geçersiz şifreleme yöntemi!");
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
    
    private String railFenceEncrypt(String text, int rails) {
        if (rails == 1) return text;
        
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            fence[i] = new StringBuilder();
        }
        
        int rail = 0;
        int direction = 1; // 1: aşağı, -1: yukarı
        
        for (char c : text.toCharArray()) {
            fence[rail].append(c);
            
            if (rail == 0) {
                direction = 1;
            } else if (rail == rails - 1) {
                direction = -1;
            }
            
            rail += direction;
        }
        
        StringBuilder result = new StringBuilder();
        for (StringBuilder sb : fence) {
            result.append(sb);
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
}
