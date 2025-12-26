package src.engine;
import java.util.*;

import src.algorithms.symmetric.AESAlgorithm;
import src.algorithms.symmetric.AESLibrary;
import src.algorithms.symmetric.DESAlgorithm;
import src.algorithms.symmetric.DESLibrary;
import src.algorithms.asymmetric.RSAKeyGenerator;

public class EncryptionEngine {
    
    private DESAlgorithm des = new DESAlgorithm();
    private AESAlgorithm aes = new AESAlgorithm();
    private DESLibrary desLib = new DESLibrary();
    private AESLibrary aesLib = new AESLibrary();
    private RSAKeyGenerator rsaKeyGen = new RSAKeyGenerator();
    
    public String encrypt(String method, String key, String plainText) throws Exception {
        System.out.println("\n=== ENCRYPTION DEBUG ===");
        System.out.println("Method: " + method);
        System.out.println("Key: " + key);
        System.out.println("Plain Text Length: " + plainText.length());
        
        
        if (method.startsWith("Caesar")) {
            return caesarEncrypt(plainText, Integer.parseInt(key));
        } else if (method.startsWith("Vigenere")) {
            return vigenereEncrypt(plainText, key);
        } else if (method.startsWith("Substitution")) {
            return substitutionEncrypt(plainText, key);
        } else if (method.startsWith("Affine")) {
            String[] parts = key.split(",");
            return affineEncrypt(plainText, Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } else if (method.startsWith("Rail Fence")) {
            return railFenceEncrypt(plainText, Integer.parseInt(key));
        } else if (method.startsWith("Route")) {
            String[] parts = key.split(",");
            int cols = Integer.parseInt(parts[0].trim());
            String direction = parts.length > 1 ? parts[1].trim() : "clockwise";
            return routeCipherEncrypt(plainText, cols, direction);
        } else if (method.startsWith("Columnar")) {
            return columnarTranspositionEncrypt(plainText, key);
        } else if (method.startsWith("Polybius")) {
            return polybiusEncrypt(plainText, key);
        } else if (method.startsWith("Pigpen")) {
            return pigpenEncrypt(plainText);
        } else if (method.startsWith("Hill")) {
            String[] parts = key.split(",");
            int[][] keyMatrix = {{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())},
                                 {Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim())}};
            return hillEncrypt(plainText, keyMatrix);
        } else if (method.startsWith("Playfair")) {
            return playfairEncrypt(plainText, key);
        }
        
        
        if (method.equals("DES (Manuel - Direkt Anahtar)")) {
            System.out.println(">>> MANUAL DES ENCRYPTION");
            if (key.length() != 8) {
                throw new IllegalArgumentException("DES anahtarı tam 8 karakter olmalı!");
            }
            String encrypted = des.encrypt(plainText, key);
            return "MANUAL_DES|" + key + "|" + encrypted;
        }
        
        
        else if (method.equals("AES (Manuel - Direkt Anahtar)")) {
            System.out.println(">>> MANUAL AES ENCRYPTION");
            if (key.length() != 16) {
                throw new IllegalArgumentException("AES anahtarı tam 16 karakter olmalı!");
            }
            String encrypted = aes.encrypt(plainText, key);
            return "MANUAL_AES|" + key + "|" + encrypted;
        }
        
        
        else if (method.equals("DES (Kütüphane - RSA ile Anahtar)")) {
            System.out.println(">>> LIBRARY DES ENCRYPTION WITH RSA");
            String desKey = rsaKeyGen.generateSymmetricKey(8, key.isEmpty() ? "auto" : key);
            System.out.println("Generated DES Key: " + desKey + " (length: " + desKey.length() + ")");
            
            if (desKey.length() != 8) {
                throw new IllegalArgumentException("DES anahtarı tam 8 karakter olmalı!");
            }
            
            String encrypted = desLib.encrypt(plainText, desKey);
            String rsaKeys = rsaKeyGen.getKeyPairAsString();
            return rsaKeys + "|" + desKey + "|" + encrypted;
        }
        
        
        else if (method.equals("AES (Kütüphane - RSA ile Anahtar)")) {
            System.out.println(">>> LIBRARY AES ENCRYPTION WITH RSA");
            String aesKey = rsaKeyGen.generateSymmetricKey(16, key.isEmpty() ? "auto" : key);
            System.out.println("Generated AES Key: " + aesKey + " (length: " + aesKey.length() + ")");
            
            if (aesKey.length() != 16) {
                throw new IllegalArgumentException("AES anahtarı tam 16 karakter olmalı!");
            }
            
            String encrypted = aesLib.encrypt(plainText, aesKey);
            String rsaKeys = rsaKeyGen.getKeyPairAsString();
            return rsaKeys + "|" + aesKey + "|" + encrypted;
        }
        
        throw new IllegalArgumentException("Geçersiz şifreleme yöntemi: " + method);
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
        if (rails <= 1) return text;
        
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            fence[i] = new StringBuilder();
        }
        
        int rail = 0;
        int direction = 1;
        
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
    
    private String routeCipherEncrypt(String text, int cols, String direction) {
        int rows = (int) Math.ceil((double) text.length() / cols);
        char[][] grid = new char[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '*';
            }
        }
        
        int index = 0;
        for (int i = 0; i < rows && index < text.length(); i++) {
            for (int j = 0; j < cols && index < text.length(); j++) {
                grid[i][j] = text.charAt(index++);
            }
        }
        
        StringBuilder result = new StringBuilder();
        
        if (direction.equalsIgnoreCase("clockwise") || direction.equalsIgnoreCase("saatYonu")) {
            readSpiralClockwise(grid, result, rows, cols);
        } else {
            readSpiralCounterClockwise(grid, result, rows, cols);
        }
        
        return result.toString();
    }
    
    private void readSpiralClockwise(char[][] grid, StringBuilder result, int rows, int cols) {
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        
        result.append(grid[top][right]);
        
        while (top <= bottom && left <= right) {
            for (int i = top + 1; i <= bottom && right >= left; i++) {
                if (grid[i][right] != '*') result.append(grid[i][right]);
            }
            right--;
            
            for (int i = right; i >= left && top <= bottom; i--) {
                if (grid[bottom][i] != '*') result.append(grid[bottom][i]);
            }
            bottom--;
            
            for (int i = bottom; i >= top && left <= right; i--) {
                if (grid[i][left] != '*') result.append(grid[i][left]);
            }
            left++;
            
            for (int i = left; i <= right && top <= bottom; i++) {
                if (grid[top][i] != '*') result.append(grid[top][i]);
            }
            top++;
        }
    }
    
    private void readSpiralCounterClockwise(char[][] grid, StringBuilder result, int rows, int cols) {
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        
        result.append(grid[top][right]);
        
        while (top <= bottom && left <= right) {
            for (int i = right - 1; i >= left && top <= bottom; i--) {
                if (grid[top][i] != '*') result.append(grid[top][i]);
            }
            top++;
            
            for (int i = top; i <= bottom && left <= right; i++) {
                if (grid[i][left] != '*') result.append(grid[i][left]);
            }
            left++;
            
            for (int i = left; i <= right && top <= bottom; i++) {
                if (grid[bottom][i] != '*') result.append(grid[bottom][i]);
            }
            bottom--;
            
            for (int i = bottom; i >= top && left <= right; i--) {
                if (grid[i][right] != '*') result.append(grid[i][right]);
            }
            right--;
        }
    }
    
    private String columnarTranspositionEncrypt(String text, String key) {
        key = key.toUpperCase();
        int cols = key.length();
        int rows = (int) Math.ceil((double) text.length() / cols);
        
        char[][] grid = new char[rows][cols];
        int index = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < text.length()) {
                    grid[i][j] = text.charAt(index++);
                } else {
                    grid[i][j] = 'X';
                }
            }
        }
        
        int[] order = getKeyOrder(key);
        StringBuilder result = new StringBuilder();
        result.append(text.length()).append(":");
        
        for (int col : order) {
            for (int i = 0; i < rows; i++) {
                result.append(grid[i][col]);
            }
        }
        
        return result.toString();
    }
    
    private String polybiusEncrypt(String text, String key) {
        char[][] square = createPolybiusSquare(key);
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toUpperCase().toCharArray()) {
            if (c == ' ') {
                result.append(' ');
                continue;
            }
            
            if (c == 'J') c = 'I';
            
            c = normalizeTurkishChar(c);
            
            if (Character.isLetter(c)) {
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (square[i][j] == c) {
                            result.append(i + 1).append(j + 1);
                            break;
                        }
                    }
                }
            }
        }
        return result.toString();
    }
    
    private String pigpenEncrypt(String text) {
        Map<Character, String> pigpenMap = createPigpenMap();
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toUpperCase().toCharArray()) {
            if (c == ' ') {
                result.append(' ');
            } else if (pigpenMap.containsKey(c)) {
                result.append(pigpenMap.get(c));
            }
        }
        return result.toString();
    }
    
    private String hillEncrypt(String text, int[][] keyMatrix) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        
        if (text.length() % 2 != 0) {
            text += "X";
        }
        
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
    
    private String playfairEncrypt(String text, String key) {
        char[][] matrix = createPlayfairMatrix(key);
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        
        StringBuilder processed = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == 'J') current = 'I';
            
            if (i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (next == 'J') next = 'I';
                
                if (current == next) {
                    processed.append(current).append('X');
                } else {
                    processed.append(current).append(next);
                    i++;
                }
            } else {
                processed.append(current).append('X');
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < processed.length(); i += 2) {
            char a = processed.charAt(i);
            char b = processed.charAt(i + 1);
            
            int[] posA = findPosition(matrix, a);
            int[] posB = findPosition(matrix, b);
            
            if (posA[0] == posB[0]) {
                result.append(matrix[posA[0]][(posA[1] + 1) % 5]);
                result.append(matrix[posB[0]][(posB[1] + 1) % 5]);
            } else if (posA[1] == posB[1]) {
                result.append(matrix[(posA[0] + 1) % 5][posA[1]]);
                result.append(matrix[(posB[0] + 1) % 5][posB[1]]);
            } else {
                result.append(matrix[posA[0]][posB[1]]);
                result.append(matrix[posB[0]][posA[1]]);
            }
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
                    alphabet.append(c);
                    used.add(c);
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
                square[i][j] = alphabet.charAt(index++);
            }
        }
        return square;
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
    
    private Map<Character, String> createPigpenMap() {
        Map<Character, String> map = new HashMap<>();
        String[] symbols = {"[1]", "[2]", "[3]", "[4]", "[5]", "[6]", "[7]", "[8]", "[9]",
                           "<1>", "<2>", "<3>", "<4>", "<5>", "<6>", "<7>", "<8>", "<9>",
                           "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}"};
        
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < letters.length; i++) {
            map.put(letters[i], symbols[i]);
        }
        return map;
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