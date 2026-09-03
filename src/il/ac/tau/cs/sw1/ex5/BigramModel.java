package il.ac.tau.cs.sw1.ex5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class BigramModel {
    public static final int MAX_VOCABULARY_SIZE = 14000;
    public static final String VOC_FILE_SUFFIX = ".voc";
    public static final String COUNTS_FILE_SUFFIX = ".counts";
    public static final String SOME_NUM = "some_num";
    public static final int ELEMENT_NOT_FOUND = -1;

    String[] mVocabulary;
    int[][] mBigramCounts;

    // DO NOT CHANGE THIS !!!
    public void initModel(String fileName) throws IOException {
        mVocabulary = buildVocabularyIndex(fileName);
        mBigramCounts = buildCountsArray(fileName, mVocabulary);
    }

    public String[] buildVocabularyIndex(String fileName) throws IOException {
        String[] vocabulary = new String[MAX_VOCABULARY_SIZE];
        int wordCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null && wordCount < MAX_VOCABULARY_SIZE) {
                String[] words = line.split(" ");
                for (String rawWord : words) {
                    if (wordCount >= MAX_VOCABULARY_SIZE) {
                        break;
                    }
                    String word = findLegalForm(rawWord);
                    if (word != null && !alreadyInVocab(word, vocabulary, wordCount)) {
                        vocabulary[wordCount] = word;
                        wordCount++;
                    }
                }
            }
        }
        return Arrays.copyOf(vocabulary, wordCount);
    }

    private static String findLegalForm(String word) {
        if (word.isEmpty()) {
            return null;
        }

        boolean sawDigit = false;
        boolean sawLetter = false;
        for (char value : word.toCharArray()) {
            if ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z')) {
                sawLetter = true;
            } else if (Character.isDigit(value)) {
                sawDigit = true;
            } else {
                return null;
            }
        }

        if (sawLetter && !sawDigit) {
            return word.toLowerCase();
        }
        if (sawDigit && !sawLetter) {
            return SOME_NUM;
        }
        return null;
    }

    private static boolean alreadyInVocab(String word, String[] vocabulary, int size) {
        for (int i = 0; i < size; i++) {
            if (vocabulary[i].equals(word)) {
                return true;
            }
        }
        return false;
    }

    public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException {
        int[][] countsArray = new int[vocabulary.length][vocabulary.length];

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] wordArray = line.split(" ");
                for (int i = 1; i < wordArray.length; i++) {
                    String oldWord = findLegalForm(wordArray[i - 1]);
                    String currentWord = findLegalForm(wordArray[i]);
                    if (oldWord != null && currentWord != null) {
                        addToCount(vocabulary, oldWord, currentWord, countsArray);
                    }
                }
            }
        }
        return countsArray;
    }

    private static void addToCount(String[] vocabulary, String oldWord, String currentWord, int[][] counts) {
        int x = ELEMENT_NOT_FOUND;
        int y = ELEMENT_NOT_FOUND;
        for (int i = 0; i < vocabulary.length; i++) {
            if (x != ELEMENT_NOT_FOUND && y != ELEMENT_NOT_FOUND) {
                break;
            }
            if (vocabulary[i].equals(oldWord)) {
                x = i;
            }
            if (vocabulary[i].equals(currentWord)) {
                y = i;
            }
        }

        if (x != ELEMENT_NOT_FOUND && y != ELEMENT_NOT_FOUND) {
            counts[x][y]++;
        }
    }

    public void saveModel(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + VOC_FILE_SUFFIX))) {
            writer.write(Integer.toString(this.mVocabulary.length) + " words");
            for (int i = 0; i < this.mVocabulary.length; i++) {
                writer.write(System.lineSeparator() + i + "," + this.mVocabulary[i]);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + COUNTS_FILE_SUFFIX))) {
            for (int i = 0; i < this.mBigramCounts.length; i++) {
                for (int j = 0; j < this.mBigramCounts.length; j++) {
                    if (this.mBigramCounts[i][j] != 0) {
                        writer.write(i + "," + j + ":" + this.mBigramCounts[i][j] + System.lineSeparator());
                    }
                }
            }
        }
    }

    public void loadModel(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName + VOC_FILE_SUFFIX))) {
            String line = reader.readLine();
            String[] splitLine = line.split(" ");
            String[] vocabulary = new String[Integer.valueOf(splitLine[0])];
            while ((line = reader.readLine()) != null) {
                splitLine = line.split(",", 2);
                vocabulary[Integer.valueOf(splitLine[0])] = splitLine[1];
            }
            this.mVocabulary = vocabulary;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName + COUNTS_FILE_SUFFIX))) {
            int[][] counts = new int[this.mVocabulary.length][this.mVocabulary.length];
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",|:");
                counts[Integer.valueOf(splitLine[0])][Integer.valueOf(splitLine[1])] = Integer.valueOf(splitLine[2]);
            }
            this.mBigramCounts = counts;
        }
    }

    public int getWordIndex(String word) {
        for (int i = 0; i < this.mVocabulary.length; i++) {
            if (this.mVocabulary[i].equals(word)) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

    public int getBigramCount(String word1, String word2) {
        int firstIndex = this.getWordIndex(word1);
        int secondIndex = this.getWordIndex(word2);
        if (firstIndex == ELEMENT_NOT_FOUND || secondIndex == ELEMENT_NOT_FOUND) {
            return 0;
        }
        return this.mBigramCounts[firstIndex][secondIndex];
    }

    public String getMostFrequentProceeding(String word) {
        int maxCount = 0;
        String maxWord = null;
        for (String checkWord : this.mVocabulary) {
            int tempCount = this.getBigramCount(word, checkWord);
            if (tempCount > maxCount) {
                maxCount = tempCount;
                maxWord = checkWord;
            }
        }
        return maxWord;
    }

    public boolean isLegalSentence(String sentence) {
        String[] words = sentence.split(" ");
        if (words.length == 1) {
            return this.getWordIndex(words[0]) != ELEMENT_NOT_FOUND;
        }
        for (int i = 0; i < words.length - 1; i++) {
            if (this.getBigramCount(words[i], words[i + 1]) <= 0) {
                return false;
            }
        }
        return true;
    }

    public static double calcCosineSim(int[] arr1, int[] arr2) {
        int numerator = calcCosineSum(arr1, arr2);
        int firstNorm = calcCosineSum(arr1, arr1);
        int secondNorm = calcCosineSum(arr2, arr2);
        if (firstNorm == 0 || secondNorm == 0) {
            return 0.0;
        }
        return numerator / (Math.sqrt(firstNorm) * Math.sqrt(secondNorm));
    }

    private static int calcCosineSum(int[] arr1, int[] arr2) {
        int sum = 0;
        for (int i = 0; i < arr1.length; i++) {
            sum += arr1[i] * arr2[i];
        }
        return sum;
    }

    public String getClosestWord(String word) {
        int wordIndex = this.getWordIndex(word);
        if (wordIndex == ELEMENT_NOT_FOUND) {
            return null;
        }

        int[] comparisonVector = this.createVector(wordIndex);
        String similarWord = null;
        double maxSimilarity = -1.0;
        for (int i = 0; i < this.mVocabulary.length; i++) {
            if (this.mVocabulary[i].equals(word)) {
                continue;
            }
            double comparisonSimilarity = calcCosineSim(comparisonVector, this.createVector(i));
            if (comparisonSimilarity > maxSimilarity) {
                maxSimilarity = comparisonSimilarity;
                similarWord = this.mVocabulary[i];
            }
        }
        return similarWord;
    }

    private int[] createVector(int index) {
        int[] vector = new int[this.mVocabulary.length];
        for (int i = 0; i < this.mVocabulary.length; i++) {
            vector[i] = this.mBigramCounts[index][i];
        }
        return vector;
    }
}
