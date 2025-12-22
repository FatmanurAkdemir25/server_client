import java.util.*;

public class DecryptionEngine {
    
    private DESAlgorithm des = new DESAlgorithm();
    private AESAlgorithm aes = new AESAlgorithm();
    private DESLibrary desLib = new DESLibrary();
    private AESLibrary aesLib = new AESLibrary();
    
    public String decrypt(String method, String key, String encryptedText) throws Exception {
        System.out.println("\n=== HYBRID DECRYPTION DEBUG ===");
        System.out.println("Method: " + method);
        System.out.println("Key: " + key);
        System.out.println("Encrypted Text Length: " + encryptedText.length());

        // Klasik şifreleme metodları
        if (method.startsWith("Caesar")) {
            return caesarDecrypt(encryptedText, Integer.parseInt(key));
        } else if (method.startsWith("Vigenere")) {
            return vigenereDecrypt(encryptedText, key);
        } else if (method.startsWith("Substitution")) {
            return substitutionDecrypt(encryptedText, key);
        } else if (method.startsWith("Affine")) {
            String[] parts = key.split(",");
            return affineDecrypt(encryptedText, Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } else if (method.startsWith("Rail Fence")) {
            return railFenceDecrypt(encryptedText, Integer.parseInt(key));
        } else if (method.startsWith("Route")) {
            String[] parts = key.split(",");
            int cols = Integer.parseInt(parts[0].trim());
            String direction = parts.length > 1 ? parts[1].trim() : "clockwise";
            return routeCipherDecrypt(encryptedText, cols, direction);
        } else if (method.startsWith("Columnar")) {
            return columnarTranspositionDecrypt(encryptedText, key);
        } else if (method.startsWith("Polybius")) {
            return polybiusDecrypt(encryptedText, key);
        } else if (method.startsWith("Pigpen")) {
            return pigpenDecrypt(encryptedText);
        } else if (method.startsWith("Hill")) {
            String[] parts = key.split(",");
            int[][] keyMatrix = {{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())},
                                 {Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim())}};
            return hillDecrypt(encryptedText, keyMatrix);
        } else if (method.startsWith("Playfair")) {
            return playfairDecrypt(encryptedText, key);
        }
        
        // HİBRİT DEŞİFRELEME
        else if (method.contains("DES") && method.contains("Manuel")) {
            return hybridDESManualDecrypt(encryptedText);
        } else if (method.contains("AES") && method.contains("Manuel")) {
            return hybridAESManualDecrypt(encryptedText);
        } else if (method.contains("DES") && method.contains("Java")) {
            return hybridDESLibraryDecrypt(encryptedText);
        } else if (method.contains("AES") && method.contains("Java")) {
            return hybridAESLibraryDecrypt(encryptedText);
        }
        
        throw new IllegalArgumentException("Geçersiz deşifreleme yöntemi!");
    }
    
    // ==========================================
    //  HİBRİT DEŞİFRELEME METODLARı
    // ==========================================
    
    /**
     * Hibrit DES Manuel deşifreleme
     * Format: RSA_KEYS|DES_KEY|ENCRYPTED_MESSAGE
     */
    private String hybridDESManualDecrypt(String encryptedData) throws Exception {
        System.out.println("\n=== HYBRID DES MANUAL DECRYPT ===");
        
        String[] parts = encryptedData.split("\\|");
        if (parts.length < 3) {
            throw new Exception("Geçersiz format! Beklenen: RSA_KEYS|DES_KEY|ENCRYPTED_MESSAGE");
        }
        
        String rsaKeys = parts[0];
        String desKey = parts[1];
        String encrypted = parts[2];
        
        System.out.println("DES Key: " + desKey);
        System.out.println("Encrypted Message: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");
        
        // DES ile deşifrele
        String decrypted = des.decrypt(encrypted, desKey);
        
        System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }
    
    /**
     * Hibrit AES Manuel deşifreleme
     * Format: RSA_KEYS|AES_KEY|ENCRYPTED_MESSAGE
     */
    private String hybridAESManualDecrypt(String encryptedData) throws Exception {
        System.out.println("\n=== HYBRID AES MANUAL DECRYPT ===");
        
        String[] parts = encryptedData.split("\\|");
        if (parts.length < 3) {
            throw new Exception("Geçersiz format! Beklenen: RSA_KEYS|AES_KEY|ENCRYPTED_MESSAGE");
        }
        
        String rsaKeys = parts[0];
        String aesKey = parts[1];
        String encrypted = parts[2];
        
        System.out.println("AES Key: " + aesKey);
        System.out.println("Encrypted Message: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");
        
        // AES ile deşifrele
        String decrypted = aes.decrypt(encrypted, aesKey);
        
        System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }
    
    /**
     * Hibrit DES Library deşifreleme
     */
    private String hybridDESLibraryDecrypt(String encryptedData) throws Exception {
        System.out.println("\n=== HYBRID DES LIBRARY DECRYPT ===");
        
        String[] parts = encryptedData.split("\\|");
        if (parts.length < 3) {
            throw new Exception("Geçersiz format!");
        }
        
        String rsaKeys = parts[0];
        String desKey = parts[1];
        String encrypted = parts[2];
        
        System.out.println("DES Key: " + desKey);
        
        // DES Library ile deşifrele
        String decrypted = desLib.decrypt(encrypted, desKey);
        
        System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }
    
    /**
     * Hibrit AES Library deşifreleme
     */
    private String hybridAESLibraryDecrypt(String encryptedData) throws Exception {
        System.out.println("\n=== HYBRID AES LIBRARY DECRYPT ===");
        
        String[] parts = encryptedData.split("\\|");
        if (parts.length < 3) {
            throw new Exception("Geçersiz format!");
        }
        
        String rsaKeys = parts[0];
        String aesKey = parts[1];
        String encrypted = parts[2];
        
        System.out.println("AES Key: " + aesKey);
        
        // AES Library ile deşifrele
        String decrypted = aesLib.decrypt(encrypted, aesKey);
        
        System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }
    
    // ==========================================
    //  KLASİK DEŞİFRELEME METODLARı (DEĞİŞMEDİ)
    // ==========================================
    
    private String caesarDecrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
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
        int rail = 0, direction = 1;
        
        for (int i = 0; i < len; i++) {
            railLengths[rail]++;
            if (rail == 0) direction = 1;
            else if (rail == rails - 1) direction = -1;
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
            if (rail == 0) direction = 1;
            else if (rail == rails - 1) direction = -1;
            rail += direction;
        }
        return result.toString();
    }
    
    private String routeCipherDecrypt(String text, int cols, String direction) {
        int rows = (int) Math.ceil((double) text.length() / cols);
        char[][] grid = new char[rows][cols];
        
        int index = 0;
        
        if (direction.equalsIgnoreCase("clockwise") || direction.equalsIgnoreCase("saatYonu")) {
            index = writeSpiralClockwise(grid, text, rows, cols);
        } else {
            index = writeSpiralCounterClockwise(grid, text, rows, cols);
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != '*') {
                    result.append(grid[i][j]);
                }
            }
        }
        
        return result.toString();
    }
    
    private int writeSpiralClockwise(char[][] grid, String text, int rows, int cols) {
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        int index = 0;
        
        grid[top][right] = text.charAt(index++);
        
        while (top <= bottom && left <= right && index < text.length()) {
            for (int i = top + 1; i <= bottom && right >= left && index < text.length(); i++) {
                grid[i][right] = text.charAt(index++);
            }
            right--;
            
            for (int i = right; i >= left && top <= bottom && index < text.length(); i--) {
                grid[bottom][i] = text.charAt(index++);
            }
            bottom--;
            
            for (int i = bottom; i >= top && left <= right && index < text.length(); i--) {
                grid[i][left] = text.charAt(index++);
            }
            left++;
            
            for (int i = left; i <= right && top <= bottom && index < text.length(); i++) {
                grid[top][i] = text.charAt(index++);
            }
            top++;
        }
        
        return index;
    }
    
    private int writeSpiralCounterClockwise(char[][] grid, String text, int rows, int cols) {
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        int index = 0;
        
        grid[top][right] = text.charAt(index++);
        
        while (top <= bottom && left <= right && index < text.length()) {
            for (int i = right - 1; i >= left && top <= bottom && index < text.length(); i--) {
                grid[top][i] = text.charAt(index++);
            }
            top++;
            
            for (int i = top; i <= bottom && left <= right && index < text.length(); i++) {
                grid[i][left] = text.charAt(index++);
            }
            left++;
            
            for (int i = left; i <= right && top <= bottom && index < text.length(); i++) {
                grid[bottom][i] = text.charAt(index++);
            }
            bottom--;
            
            for (int i = bottom; i >= top && left <= right && index < text.length(); i--) {
                grid[i][right] = text.charAt(index++);
            }
            right--;
        }
        
        return index;
    }
    
    private String columnarTranspositionDecrypt(String text, String key) {
        int colonIndex = text.indexOf(':');
        int originalLength = Integer.parseInt(text.substring(0, colonIndex));
        text = text.substring(colonIndex + 1);
        
        key = key.toUpperCase();
        int cols = key.length();
        int rows = text.length() / cols;
        int[] order = getKeyOrder(key);
        
        char[][] grid = new char[rows][cols];
        int index = 0;
        
        for (int col : order) {
            for (int i = 0; i < rows; i++) {
                grid[i][col] = text.charAt(index++);
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.append(grid[i][j]);
            }
        }
        return result.substring(0, originalLength);
    }
    
    private String polybiusDecrypt(String text, String key) {
        char[][] square = createPolybiusSquare(key);
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                result.append(' ');
                continue;
            }
            
            if (i + 1 < text.length() && Character.isDigit(text.charAt(i)) && Character.isDigit(text.charAt(i + 1))) {
                int row = Character.getNumericValue(text.charAt(i)) - 1;
                int col = Character.getNumericValue(text.charAt(i + 1)) - 1;
                
                if (row >= 0 && row < 5 && col >= 0 && col < 5) {
                    result.append(square[row][col]);
                }
                i++;
            }
        }
        return result.toString();
    }
    
    private char normalizeTurkishChar(char c) {
        switch(c) {
            case 'Ç': return 'C';
            case 'Ğ': return 'G';
            case 'İ': return 'I';
            case 'Ö': return 'O';
            case 'Ş': return 'S';
            case 'Ü': return 'U';
            default: return c;
        }
    }
    
    private String pigpenDecrypt(String text) {
        Map<String, Character> reversePigpenMap = createReversePigpenMap();
        StringBuilder result = new StringBuilder();
        
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == ' ') {
                result.append(' ');
                i++;
                continue;
            }
            
            if (i + 2 < text.length()) {
                String symbol = text.substring(i, i + 3);
                if (reversePigpenMap.containsKey(symbol)) {
                    result.append(reversePigpenMap.get(symbol));
                    i += 3;
                } else {
                    result.append(text.charAt(i));
                    i++;
                }
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }
        return result.toString();
    }
    
    private String hillDecrypt(String text, int[][] keyMatrix) {
        int[][] invMatrix = invertMatrix(keyMatrix);
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < text.length(); i += 2) {
            int[] block = {text.charAt(i) - 'A', text.charAt(i + 1) - 'A'};
            
            int p1 = (invMatrix[0][0] * block[0] + invMatrix[0][1] * block[1]) % 26;
            int p2 = (invMatrix[1][0] * block[0] + invMatrix[1][1] * block[1]) % 26;
            
            if (p1 < 0) p1 += 26;
            if (p2 < 0) p2 += 26;
            
            result.append((char) (p1 + 'A'));
            result.append((char) (p2 + 'A'));
        }
        return result.toString();
    }
    
    private int[] getKeyOrder(String key) {
        char[] keyChars = key.toCharArray();
        int[] order = new int[key.length()];
        
        Map<Character, List<Integer>> charIndices = new HashMap<>();
        for (int i = 0; i < keyChars.length; i++) {
            charIndices.computeIfAbsent(keyChars[i], k -> new ArrayList<>()).add(i);
        }
        
        char[] sortedKey = key.toCharArray();
        Arrays.sort(sortedKey);
        
        int position = 0;
        for (char c : sortedKey) {
            List<Integer> indices = charIndices.get(c);
            if (!indices.isEmpty()) {
                order[position++] = indices.remove(0);
            }
        }
        return order;
    }
    
    private char[][] createPolybiusSquare(String key) {
        Set<Character> used = new HashSet<>();
        StringBuilder alphabet = new StringBuilder();
        
        if (key != null && !key.trim().isEmpty() && !key.equalsIgnoreCase("default")) {
            for (char c : key.toUpperCase().toCharArray()) {
                c = normalizeTurkishChar(c);
                if (Character.isLetter(c) && !used.contains(c) && c != 'J') {
                    if (c == 'J') c = 'I';
                    if (!used.contains(c)) {
                        alphabet.append(c);
                        used.add(c);
                    }
                }
            }
        }
        
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!used.contains(c) && c != 'J') {
                alphabet.append(c);
                used.add(c);
            }
        }
        
        char[][] square = new char[5][5];
        int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (index < alphabet.length()) {
                    square[i][j] = alphabet.charAt(index++);
                }
            }
        }
        return square;
    }
    
    private Map<String, Character> createReversePigpenMap() {
        Map<String, Character> map = new HashMap<>();
        String[] symbols = {"[1]", "[2]", "[3]", "[4]", "[5]", "[6]", "[7]", "[8]", "[9]",
                           "<1>", "<2>", "<3>", "<4>", "<5>", "<6>", "<7>", "<8>", "<9>",
                           "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}"};
        
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < letters.length; i++) {
            map.put(symbols[i], letters[i]);
        }
        return map;
    }
    
    private int[][] invertMatrix(int[][] matrix) {
        int det = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) % 26;
        if (det < 0) det += 26;
        
        int detInv = modInverse(det, 26);
        
        int[][] inv = new int[2][2];
        inv[0][0] = (matrix[1][1] * detInv) % 26;
        inv[0][1] = (-matrix[0][1] * detInv) % 26;
        inv[1][0] = (-matrix[1][0] * detInv) % 26;
        inv[1][1] = (matrix[0][0] * detInv) % 26;
        
        if (inv[0][1] < 0) inv[0][1] += 26;
        if (inv[1][0] < 0) inv[1][0] += 26;
        
        return inv;
    }
    
    private int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return -1;
    }
    
    private String playfairDecrypt(String text, String key) {
        char[][] matrix = createPlayfairMatrix(key);
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);
            
            int[] posA = findPosition(matrix, a);
            int[] posB = findPosition(matrix, b);
            
            if (posA[0] == posB[0]) {
                result.append(matrix[posA[0]][(posA[1] + 4) % 5]);
                result.append(matrix[posB[0]][(posB[1] + 4) % 5]);
            } else if (posA[1] == posB[1]) {
                result.append(matrix[(posA[0] + 4) % 5][posA[1]]);
                result.append(matrix[(posB[0] + 4) % 5][posB[1]]);
            } else {
                result.append(matrix[posA[0]][posB[1]]);
                result.append(matrix[posB[0]][posA[1]]);
            }
        }
        
        return result.toString();
    }
    
    private char[][] createPlayfairMatrix(String key) {
        char[][] matrix = new char[5][5];
        boolean[] used = new boolean[26];
        used['J' - 'A'] = true;
        
        int row = 0, col = 0;
        
        if (key != null && !key.isEmpty()) {
            for (char c : key.toUpperCase().toCharArray()) {
                if (!Character.isLetter(c)) continue;
                if (c == 'J') c = 'I';
                
                int index = c - 'A';
                if (!used[index]) {
                    matrix[row][col] = c;
                    used[index] = true;
                    col++;
                    if (col == 5) {
                        col = 0;
                        row++;
                    }
                }
            }
        }
        
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c == 'J') continue;
            int index = c - 'A';
            if (!used[index]) {
                matrix[row][col] = c;
                used[index] = true;
                col++;
                if (col == 5) {
                    col = 0;
                    row++;
                }
            }
        }
        
        return matrix;
    }
    
    private int[] findPosition(char[][] matrix, char c) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{0, 0};
    }
}