import java.util.*;

public class EncryptionEngine {
    
    private DESAlgorithm des = new DESAlgorithm();
    private AESAlgorithm aes = new AESAlgorithm();
    
    public String encrypt(String method, String key, String message) throws Exception {
        if (method.startsWith("Caesar")) {
            return caesarEncrypt(message, Integer.parseInt(key));
        } else if (method.startsWith("Vigenere")) {
            return vigenereEncrypt(message, key);
        } else if (method.startsWith("Substitution")) {
            return substitutionEncrypt(message, key);
        } else if (method.startsWith("Affine")) {
            String[] parts = key.split(",");
            return affineEncrypt(message, Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } else if (method.startsWith("Rail Fence")) {
            return railFenceEncrypt(message, Integer.parseInt(key));
        } else if (method.startsWith("Route")) {
            String[] parts = key.split(",");
            int cols = Integer.parseInt(parts[0].trim());
            String direction = parts.length > 1 ? parts[1].trim() : "clockwise";
            return routeCipherEncrypt(message, cols, direction);
        } else if (method.startsWith("Columnar")) {
            return columnarTranspositionEncrypt(message, key);
        } else if (method.startsWith("Polybius")) {
            return polybiusEncrypt(message, key);
        } else if (method.startsWith("Pigpen")) {
            return pigpenEncrypt(message);
        } else if (method.startsWith("Hill")) {
            String[] parts = key.split(",");
            int[][] keyMatrix = {{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())},
                                 {Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim())}};
            return hillEncrypt(message, keyMatrix);
        } else if (method.startsWith("DES")) {
            if (key.length() != 8) {
                throw new IllegalArgumentException("DES anahtarı tam 8 karakter olmalı!");
            }
            return des.encrypt(message, key);
        }
        else if (method.startsWith("AES")) {
            if (key.length() != 16) {
                throw new IllegalArgumentException("AES anahtarı tam 16 karakter olmalı!");
            }
            return aes.encrypt(message, key);
        }
        throw new IllegalArgumentException("Geçersiz şifreleme yöntemi!");
    }
    
    
    private String caesarEncrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
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
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append(key.charAt(alphabet.indexOf(c)));
            } else if (Character.isLowerCase(c)) {
                result.append(Character.toLowerCase(key.charAt(alphabet.indexOf(Character.toUpperCase(c)))));
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
                result.append((char) ((a * x + b) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                int x = c - 'a';
                result.append((char) ((a * x + b) % 26 + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    
    private String railFenceEncrypt(String text, int rails) {
        if (rails == 1) return text;
        
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) fence[i] = new StringBuilder();
        
        int rail = 0, direction = 1;
        
        for (char c : text.toCharArray()) {
            fence[rail].append(c);
            if (rail == 0) direction = 1;
            else if (rail == rails - 1) direction = -1;
            rail += direction;
        }
        
        StringBuilder result = new StringBuilder();
        for (StringBuilder sb : fence) result.append(sb);
        return result.toString();
    }
    
    
    private String routeCipherEncrypt(String text, int cols, String direction) {
        
        text = text.replaceAll("\\s+", "").toUpperCase();
        
        
        int rows = (int) Math.ceil((double) text.length() / cols);
        char[][] grid = new char[rows][cols];
        
        
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < text.length()) {
                    grid[i][j] = text.charAt(index++);
                } else {
                    grid[i][j] = '*'; 
                }
            }
        }
        
        
        StringBuilder result = new StringBuilder();
        
        if (direction.equalsIgnoreCase("clockwise") || direction.equalsIgnoreCase("saatYonu")) {
            
            result = readSpiralClockwise(grid, rows, cols);
        } else {
            
            result = readSpiralCounterClockwise(grid, rows, cols);
        }
        
        return result.toString();
    }
    
    
    private StringBuilder readSpiralClockwise(char[][] grid, int rows, int cols) {
        StringBuilder result = new StringBuilder();
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        
        
        int startCol = right;
        int startRow = top;
        
        
        result.append(grid[startRow][startCol]);
        
        while (top <= bottom && left <= right) {
            
            for (int i = top + 1; i <= bottom && right >= left; i++) {
                result.append(grid[i][right]);
            }
            right--;
            
            
            for (int i = right; i >= left && top <= bottom; i--) {
                result.append(grid[bottom][i]);
            }
            bottom--;
            
            
            for (int i = bottom; i >= top && left <= right; i--) {
                result.append(grid[i][left]);
            }
            left++;
            
            
            for (int i = left; i <= right && top <= bottom; i++) {
                result.append(grid[top][i]);
            }
            top++;
        }
        
        return result;
    }
    
    
    private StringBuilder readSpiralCounterClockwise(char[][] grid, int rows, int cols) {
        StringBuilder result = new StringBuilder();
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        
        
        result.append(grid[top][right]);
        
        while (top <= bottom && left <= right) {
            
            for (int i = right - 1; i >= left && top <= bottom; i--) {
                result.append(grid[top][i]);
            }
            top++;
            
            
            for (int i = top; i <= bottom && left <= right; i++) {
                result.append(grid[i][left]);
            }
            left++;
            
            
            for (int i = left; i <= right && top <= bottom; i++) {
                result.append(grid[bottom][i]);
            }
            bottom--;
            
            
            for (int i = bottom; i >= top && left <= right; i--) {
                result.append(grid[i][right]);
            }
            right--;
        }
        
        return result;
    }
    
    
    private String columnarTranspositionEncrypt(String text, String key) {
        text = text.replaceAll("\\s+", "");
        key = key.toUpperCase();
        int[] order = getKeyOrder(key);
        int cols = key.length();
        int rows = (int) Math.ceil((double) text.length() / cols);
        
        char[][] grid = new char[rows][cols];
        int index = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = (index < text.length()) ? text.charAt(index++) : 'X';
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (int col : order) {
            for (int i = 0; i < rows; i++) {
                result.append(grid[i][col]);
            }
        }
        
        return text.length() + ":" + result.toString();
    }
    
    
    private String polybiusEncrypt(String text, String key) {
        char[][] square = createPolybiusSquare(key);
        StringBuilder result = new StringBuilder();
        
        
        printPolybiusSquare(square);
        
        for (char c : text.toUpperCase().toCharArray()) {
            if (c == ' ') {
                result.append(" ");
                continue;
            }
            
            
            if (c == 'İ' || c == 'I') c = 'I';
            if (c == 'J') c = 'I';
            
            
            c = normalizeTurkishChar(c);
            
            if (!Character.isLetter(c)) {
                result.append(c);
                continue;
            }
            
            
            boolean found = false;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (square[i][j] == c) {
                        
                        int row = i + 1;
                        int col = j + 1;
                        System.out.println(c + " -> Satır: " + row + ", Sütun: " + col + " = " + row + col);
                        result.append(row).append(col);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            
            if (!found) {
                result.append("00"); 
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
    
    
    private String pigpenEncrypt(String text) {
        Map<Character, String> pigpenMap = createPigpenMap();
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toUpperCase().toCharArray()) {
            if (pigpenMap.containsKey(c)) {
                result.append(pigpenMap.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    
    private String hillEncrypt(String text, int[][] keyMatrix) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (text.length() % 2 != 0) text += "X";
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < text.length(); i += 2) {
            int[] block = {text.charAt(i) - 'A', text.charAt(i + 1) - 'A'};
            
            int c1 = (keyMatrix[0][0] * block[0] + keyMatrix[0][1] * block[1]) % 26;
            int c2 = (keyMatrix[1][0] * block[0] + keyMatrix[1][1] * block[1]) % 26;
            
            result.append((char) (c1 + 'A'));
            result.append((char) (c2 + 'A'));
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
                    // I ve J için sadece I kullan
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
    
    
    private void printPolybiusSquare(char[][] square) {
        System.out.println("\nPolybius Square:");
        System.out.println("  1 2 3 4 5");
        for (int i = 0; i < 5; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 5; j++) {
                System.out.print(square[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    private Map<Character, String> createPigpenMap() {
        Map<Character, String> map = new HashMap<>();
        
        String[] symbols = {"[1]", "[2]", "[3]", "[4]", "[5]", "[6]", "[7]", "[8]", "[9]",
                           "<1>", "<2>", "<3>", "<4>", "<5>", "<6>", "<7>", "<8>", "<9>",
                           "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}"};
        
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < letters.length; i++) {
            map.put(letters[i], symbols[i]);
        }
        map.put(' ', " ");
        return map;
    }
}