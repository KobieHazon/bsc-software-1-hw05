import il.ac.tau.cs.sw1.ex5.BigramModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class RunHw5Checks {
    public static void main(String[] args) throws IOException {
        testVocabularyAndCounts();
        testSaveAndLoad();
        testSentenceLegality();
        testSimilarity();
        System.out.println("18 Java checks passed");
    }

    private static void testVocabularyAndCounts() throws IOException {
        Path fixture = writeFixture("Alpha beta 123 123 beta bad! a1 Alpha");
        BigramModel model = new BigramModel();
        String[] vocabulary = model.buildVocabularyIndex(fixture.toString());
        assertArrayEquals(new String[] {"alpha", "beta", BigramModel.SOME_NUM}, vocabulary, "vocabulary");

        int[][] counts = model.buildCountsArray(fixture.toString(), vocabulary);
        assertEquals(1, counts[0][1], "alpha beta count");
        assertEquals(1, counts[1][2], "beta numeric count");
        assertEquals(1, counts[2][2], "numeric numeric count");
        assertEquals(1, counts[2][1], "numeric beta count");
        assertEquals(0, counts[1][0], "invalid token breaks beta alpha bigram");
    }

    private static void testSaveAndLoad() throws IOException {
        Path fixture = writeFixture("red blue red green red blue");
        Path modelFile = Files.createTempFile("bigram-model", "");
        BigramModel model = new BigramModel();
        model.initModel(fixture.toString());
        model.saveModel(modelFile.toString());

        BigramModel loaded = new BigramModel();
        loaded.loadModel(modelFile.toString());
        assertEquals(0, loaded.getWordIndex("red"), "loaded red index");
        assertEquals(2, loaded.getBigramCount("red", "blue"), "loaded red blue count");
        assertEquals(1, loaded.getBigramCount("blue", "red"), "loaded blue red count");
        assertEquals(0, loaded.getBigramCount("missing", "red"), "missing bigram count");
    }

    private static void testSentenceLegality() throws IOException {
        Path fixture = writeFixture("love is all you need love is real");
        BigramModel model = new BigramModel();
        model.initModel(fixture.toString());
        assertEquals(true, model.isLegalSentence("love is all"), "legal sentence");
        assertEquals(false, model.isLegalSentence("love all"), "missing bigram");
        assertEquals(true, model.isLegalSentence("love"), "known one-word sentence");
        assertEquals(false, model.isLegalSentence("unknown"), "unknown one-word sentence");
        assertEquals("is", model.getMostFrequentProceeding("love"), "most frequent proceeding");
    }

    private static void testSimilarity() throws IOException {
        assertEquals(11.0 / 30, BigramModel.calcCosineSim(new int[] {1, 2, 0, 4, 2}, new int[] {5, 0, 3, 1, 1}), "cosine");
        assertEquals(0.0, BigramModel.calcCosineSim(new int[] {0, 0}, new int[] {1, 2}), "zero vector cosine");

        Path fixture = writeFixture("good alpha beta good alpha beta great alpha beta emma thinks deeply she thinks deeply");
        BigramModel model = new BigramModel();
        model.initModel(fixture.toString());
        assertEquals("great", model.getClosestWord("good"), "closest to good");
        assertEquals(null, model.getClosestWord("unknown"), "unknown closest word");
    }

    private static Path writeFixture(String content) throws IOException {
        Path fixture = Files.createTempFile("bigram-fixture", ".txt");
        Files.writeString(fixture, content + System.lineSeparator());
        return fixture;
    }

    private static void assertArrayEquals(String[] expected, String[] actual, String label) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(label + " expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual));
        }
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Math.abs(expected - actual) > 1e-12) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
