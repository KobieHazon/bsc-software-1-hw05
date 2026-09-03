package il.ac.tau.cs.sw1.ex5;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14000;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	
	String[] mVocabulary;
	int[][] mBigramCounts;
	
	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException{
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);
		
	}
	
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException{ // Q 1
		File fromFile = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile));
		int wordCnt = 0;
		String line;
		String[] tempVocab = new String[MAX_VOCABULARY_SIZE];
		while ((line = bufferedReader.readLine()) != null && wordCnt < MAX_VOCABULARY_SIZE) {
			String[] words = line.split(" ");
			for (int i = 0; i < words.length && wordCnt < MAX_VOCABULARY_SIZE; i++) {
				String word = findLegalForm(words[i]);
				
				if (word != null && !alreadyInVocab(word, tempVocab)) {
			        tempVocab[wordCnt] = word;
			        wordCnt++;
				}
			}
		}
		bufferedReader.close();
		return java.util.Arrays.copyOf(tempVocab, wordCnt);
	}
	
	private static String findLegalForm(String word) {
		boolean isWord = true;
		for (char c : word.toCharArray()) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				return word.toLowerCase();
			}
			else if (!Character.isDigit(c)) {
				isWord = false;
			}
	    }
		if (isWord) {
			return SOME_NUM;
		}
		return null;
	}
	
	private static boolean alreadyInVocab(String word, String[] vocab) {
		for (String check: vocab) {
			if (check == null) {
				return false;
			}
			else if (check.equals(word)) {
				return true;	
			}
		}
		return false;
	}
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException{ // Q - 2
		int[][] countsArray = new int[vocabulary.length][vocabulary.length];
		File fromFile = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile));
		String line;
		String oldWord;
		String currWord;
		while ((line = bufferedReader.readLine()) != null) {
			String[] wordArray = line.split(" ");
			for (int i = 1; i < wordArray.length; i++) {
				oldWord = findLegalForm(wordArray[i-1]);
				currWord = findLegalForm(wordArray[i]);
				if (oldWord != null && currWord != null) {
					addToCount(vocabulary, oldWord, currWord, countsArray);
				}
			}
		}
		bufferedReader.close();
		return countsArray;

	}
	
	private static void addToCount(String[] vocab, String oldWord, String currWord, int[][] count) {
		int x = ELEMENT_NOT_FOUND;
		int y = ELEMENT_NOT_FOUND;
		for (int i = 0; i < vocab.length; i++) {
			if (x != ELEMENT_NOT_FOUND && y != ELEMENT_NOT_FOUND) {
				break;
			}
			if (vocab[i].equals(oldWord)) {
				x = i;
			}
			if (vocab[i].equals(currWord)) {
				y = i;
			}
		}
		if (x != ELEMENT_NOT_FOUND && y != ELEMENT_NOT_FOUND) {
			count[x][y]++;
		}
	}
	
	
	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException{ // Q-3
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName + VOC_FILE_SUFFIX));
		bufferedWriter.write(Integer.toString(this.mVocabulary.length) + " words"); 
		for (int i = 0; i < this.mVocabulary.length; i++) {
			bufferedWriter.write(System.lineSeparator() + Integer.toString(i) + "," + this.mVocabulary[i]);
		}
		bufferedWriter.close();
		
		bufferedWriter = new BufferedWriter(new FileWriter(fileName + COUNTS_FILE_SUFFIX));
		for (int i = 0; i < this.mBigramCounts.length; i++) {
			for (int j = 0; j < this.mBigramCounts.length; j++) {
				if (this.mBigramCounts[i][j] != 0) {
					bufferedWriter.write(Integer.toString(i) + "," + Integer.toString(j) + ":" + this.mBigramCounts[i][j] + System.lineSeparator());
				}
			}
		}
		bufferedWriter.close();
	}
	
	
	
	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException{ // Q - 4
		File fromFile = new File(fileName + VOC_FILE_SUFFIX);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile));
		String line = bufferedReader.readLine();
		String[] splitLine = line.split(" ");
		String[] tempVocab = new String[Integer.valueOf(splitLine[0])];
		while ((line = bufferedReader.readLine()) != null) {
			splitLine = line.split(",");
			tempVocab[Integer.valueOf(splitLine[0])] = splitLine[1];
		}
		bufferedReader.close();
		this.mVocabulary = tempVocab;
		
		fromFile = new File(fileName + COUNTS_FILE_SUFFIX);
		bufferedReader = new BufferedReader(new FileReader(fromFile));
		int[][] tempCounts = new int[this.mVocabulary.length][this.mVocabulary.length];
		while ((line = bufferedReader.readLine()) != null) {
			splitLine = line.split(",|:");
			tempCounts[Integer.valueOf(splitLine[0])][Integer.valueOf(splitLine[1])] = Integer.valueOf(splitLine[2]);
		}
		bufferedReader.close();
	}

	
	
	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: word is in lowercase
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word){  // Q - 5
		for (int i = 0; i < this.mVocabulary.length; i++) {
			if (this.mVocabulary[i].equals(word)) {
				return i;
			}
		}
		return ELEMENT_NOT_FOUND;
	}
	
	
	
	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2){ //  Q - 6
		int firstIndex = this.getWordIndex(word1);
		int secondIndex = this.getWordIndex(word2);
		return (firstIndex == ELEMENT_NOT_FOUND || secondIndex == ELEMENT_NOT_FOUND) ? 0 : this.mBigramCounts[firstIndex][secondIndex];
	}
	
	
	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word){ //  Q - 7
		int maxCount = 0;
		String maxWord = null;
		for (String checkWord: this.mVocabulary) {
			int tempCount = this.getBigramCount(word, checkWord);
			if (tempCount > maxCount) {
				maxCount = tempCount;
				maxWord = checkWord;
			}
		}
		return maxWord;
	}
	
	
	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence){  //  Q - 8
		String[] words = sentence.split(" ");
		if (words.length == 1) {
			return (this.getWordIndex(words[0]) == ELEMENT_NOT_FOUND ? true : false);
		}
		for (int i = 0; i < words.length - 1; i++) {
			if (this.getBigramCount(words[i], words[i + 1]) <= 0) {
				return false;
			}
		}
		return true;
	}
	
	
	
	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = 0, otherwise
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2){ //  Q - 9
		return (double)calcCosineSum(arr1, arr2)/(Math.sqrt(calcCosineSum(arr1, arr1)) * Math.sqrt(calcCosineSum(arr2, arr2)));
	}
	
	
	private static int calcCosineSum(int[] arr1, int[] arr2) {
		int sum = 0;
		for (int i = 0; i < arr1.length; i++) {
			sum += arr1[i] * arr2[i];
		}
		return sum;
	}

	
	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized), 
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word){ //  Q - 10
		int[] compVector = this.createVector(this.getWordIndex(word));
		String similarWord = this.mVocabulary[0];
		double maxSimilar = 0;
		for (int i = 0; i < this.mVocabulary.length; i++) {
			double compCosineSim = calcCosineSim(compVector, this.createVector(i));
			if (!this.mVocabulary[i].equals(word) && compCosineSim > maxSimilar) {
				maxSimilar = compCosineSim;
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
