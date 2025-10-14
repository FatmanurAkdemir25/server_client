public class DecryptionEngine {
    
    public String decrypt(String method, String key, String encryptedText) throws Exception {
        if (method.startsWith("Caesar")) {
            int shift = Integer.parseInt(key);
            return caesarDecrypt(encryptedText, shift);
            
        } else if (method.startsWith("Vigenere")) {
            return vigenereDecrypt(encryptedText, key);
            
        } else if (method.startsWith("Substitution")) {
            return substitutionDecrypt(encryptedText, key);
            
        } else if (method.startsWith("Affine")) {
            String[] parts = key.split(",");
            int a = Integer.parseInt(parts[0].trim());
            int b = Integer.parseInt(parts[1].trim());
            return affineDecrypt(encryptedText, a, b);
            
        } else if (method.startsWith("Rail Fence")) {
            int rails = Integer.parseInt(key);
            return railFenceDecrypt(encryptedText, rails);
        }
        
        throw new IllegalArgumentException("Geçersiz deşifreleme yöntemi!");
    }
    
    private String caesarDecrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = shift % 26;
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append((char) ((c - 'A' - shift + 26) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                result.append((char) ((c - 'a' - shift + 26) % 26 + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String vigenereDecrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = key.toUpperCase();
        int keyIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                if (Character.isUpperCase(c)) {
                    result.append((char) ((c - 'A' - shift + 26) % 26 + 'A'));
                } else {
                    result.append((char) ((c - 'a' - shift + 26) % 26 + 'a'));
                }
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String substitutionDecrypt(String text, String key) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        key = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int index = key.indexOf(c);
                result.append(index >= 0 ? alphabet.charAt(index) : c);
            } else if (Character.isLowerCase(c)) {
                int index = key.indexOf(Character.toUpperCase(c));
                result.append(index >= 0 ? Character.toLowerCase(alphabet.charAt(index)) : c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String affineDecrypt(String text, int a, int b) {
        StringBuilder result = new StringBuilder();
        int aInv = modInverse(a, 26);
        
        if (aInv == -1) {
            throw new IllegalArgumentException("'a' değeri için ters mod bulunamadı!");
        }
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                int y = c - 'A';
                int x = (aInv * (y - b + 26)) % 26;
                result.append((char) (x + 'A'));
            } else if (Character.isLowerCase(c)) {
                int y = c - 'a';
                int x = (aInv * (y - b + 26)) % 26;
                result.append((char) (x + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private String railFenceDecrypt(String text, int rails) {
        if (rails == 1) return text;
        
        int len = text.length();
        
        int[] railLengths = new int[rails];
        int rail = 0;
        int direction = 1;
        
        for (int i = 0; i < len; i++) {
            railLengths[rail]++;
            
            if (rail == 0) {
                direction = 1;
            } else if (rail == rails - 1) {
                direction = -1;
            }
            
            rail += direction;
        }
        
        StringBuilder[] fence = new StringBuilder[rails];
        int index = 0;
        
        for (int i = 0; i < rails; i++) {
            fence[i] = new StringBuilder();
            for (int j = 0; j < railLengths[i]; j++) {
                fence[i].append(text.charAt(index++));
            }
        }
        
        StringBuilder result = new StringBuilder();
        rail = 0;
        direction = 1;
        int[] positions = new int[rails];
        
        for (int i = 0; i < len; i++) {
            result.append(fence[rail].charAt(positions[rail]++));
            
            if (rail == 0) {
                direction = 1;
            } else if (rail == rails - 1) {
                direction = -1;
            }
            
            rail += direction;
        }
        
        return result.toString();
    }
    
    private int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return -1;
    }
}
