package il.ac.tau.cs.sw1.ex5;

import java.io.IOException;
import java.util.Arrays;

public class BigramModelTester {
    public static final String ALL_YOU_NEED_FILENAME = "resources/hw5/all_you_need.txt";
    public static final String EMMA_FILENAME = "resources/hw5/emma.txt";
    public static final String ALL_YOU_NEED_MODEL_DIR = "resources/hw5/all_you_need_model";

    public static void main(String[] args) throws IOException {
        BigramModel model = new BigramModel();
        String[] vocabulary = model.buildVocabularyIndex(ALL_YOU_NEED_FILENAME);
        if (vocabulary.length != 5) {
            System.out.println("Error 1.1");
        }
        if (!Arrays.equals(vocabulary, new String[] {"love", "all", "you", "need", "is"})) {
            System.out.println("Error 1.2");
        }

        int[][] counts = model.buildCountsArray(ALL_YOU_NEED_FILENAME, vocabulary);
        if (counts[0][0] != 3) {
            System.out.println("Error 2.1");
        }

        if (counts[1][2] != 3) {
            System.out.println("Error 2.2");
        }

        model.initModel(ALL_YOU_NEED_FILENAME);
        model.saveModel(ALL_YOU_NEED_MODEL_DIR);
        model.loadModel(ALL_YOU_NEED_MODEL_DIR);

        if (!Arrays.equals(model.mVocabulary, new String[] {"love", "all", "you", "need", "is"})) {
            System.out.println("Error 4.1");
        }

        if (model.getWordIndex("love") != 0) {
            System.out.println("Error 5.1");
        }

        if (model.mBigramCounts[0][0] != 3) {
            System.out.println("Error 6.1");
        }

        if (model.mBigramCounts[1][2] != 3) {
            System.out.println("Error 6.2");
        }

        if (model.getBigramCount("is", "love") != 2) {
            System.out.println("Error 6.3");
        }

        if (model.getBigramCount("penny", "lane") != 0) {
            System.out.println("Error 6.4");
        }
        if (!model.getMostFrequentProceeding("is").equals("love")) {
            System.out.println("Error 7.1");
        }

        if (!model.isLegalSentence("love is all")) {
            System.out.println("Error 8.1");
        }

        if (model.isLegalSentence("love is is")) {
            System.out.println("Error 8.2");
        }

        if (model.isLegalSentence("love the beatles")) {
            System.out.println("Error 8.3");
        }

        if (BigramModel.calcCosineSim(new int[] {1, 2, 0, 4, 2}, new int[] {5, 0, 3, 1, 1}) != 11.0 / 30) {
            System.out.println("Error 9.1");
        }

        model.initModel(EMMA_FILENAME);

        if (!model.getClosestWord("good").equals("great")) {
            System.out.println("Error 10.1");
        }

        if (!model.getClosestWord("emma").equals("she")) {
            System.out.println("Error 10.2");
        }

        System.out.println("done!");
    }
}
