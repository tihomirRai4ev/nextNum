import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;


public class RandomGenTest {

    @Test
    public void whenArrays_hasNullOrEmptyValues_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(null, null));

        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{0}, new float[]{0}));
    }

    @Test
    public void whenParameters_hasDifferentLengths_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{0}, new float[]{0.1f, 0.2f}));
    }

    @Test
    public void whenProbabilities_containsNegative_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{1, 2}, new float[]{0, 1f, -0, 3f}));
    }

    @Test
    public void whenProbabilities_containsAboveOne_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{1, 2}, new float[]{1, 3f, 0, 0f}));
    }

    @Test
    public void whenCumulativeProbabilities_sumsToZero_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{1, 2}, new float[]{0.0f, 0.0f}));
    }

    @Test
    public void whenProbabilities_containsNan_orNotSumsToOne_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{1, 2}, new float[]{Float.NaN, 0.4f}));

        assertThrows(IllegalArgumentException.class,
                () -> new RandomGen(new int[]{1, 2}, new float[]{0.1f, 0.2f}));
    }

    @Test
    public void whenProbabilities_hasCorrectValues_succeeds() {
        int[] randomNums = {-1, -4, 2, 1, 3, 4, 5};
        float[] probabilities = {0.0f, 0.0f, 0.3f, 0.5f, 0.0f, 0.2f, 0.0f};
        RandomMock random = new RandomMock();
        RandomGen randomGen = new RandomGen(randomNums, probabilities, random);
        random.set(0.2f);
        assertTrue(randomGen.nextNum() == 2);
        random.set(0.3f);
        assertTrue(randomGen.nextNum() == 2);
        random.set(0.35f);
        assertTrue(randomGen.nextNum() == 1);
        random.set(0.85f);
        assertTrue(randomGen.nextNum() == 4);
        random.set(0.98f);
        assertTrue(randomGen.nextNum() == 4);
    }

    @Test
    public void whenProbabilities_containsZero_numbWithProbZero_neverReturns() {
        RandomGen randomGen = new RandomGen(new int[]{1, 2, 3, 4}, new float[]{0.1f, 0.0f, 0.6f, 0.3f});
        IntStream.range(0, 1000)
                .forEach(i -> assertNotEquals(randomGen.nextNum(), 2));
    }

    @Test
    public void whenProbabilities_containsOne_numbWithProbOne_alwaysReturns() {
        RandomGen randomGen = new RandomGen(new int[]{1, 2, 3, 4}, new float[]{0.0f, 1.0f, 0.0f, 0.0f});
        IntStream.range(0, 1000)
                .forEach(i -> assertEquals(randomGen.nextNum(), 2));
    }

    // TODO this might fail in very rare cases, adding re-try mechanism will stabalize the test
    @Test
    public void whenProbabilities_verifyNumbersProvided_areExpected() {
        float THRESHOLD = 0.01f; // Precision which we consider good enough when measuring the probability
        int[] randomNums = {0, 1, 2, 3, 4};
        float[] probabilities = {0.01f, 0.09f, 0.4f, 0.49f, 0.01f};
        RandomGen randomGen = new RandomGen(randomNums, probabilities);
        Map<Integer, Integer> map = new HashMap<>();
        int totalNumberOfGeneratedNums = 100000; // the higher nuber the better estimation
        IntStream.range(0, totalNumberOfGeneratedNums)
                .forEach(i -> {
                    int nextNum = randomGen.nextNum();
                    int count = map.getOrDefault(nextNum, 0) + 1;
                    map.put(nextNum, count);
                });

        map.entrySet().stream().forEach(entry -> {
            int numberOfoccurances = entry.getValue();
            int genNum = entry.getKey();
            float percentageOfOccurrancesAgainstTotal = (float) numberOfoccurances / (float) totalNumberOfGeneratedNums;
            assertTrue(Math.abs(percentageOfOccurrancesAgainstTotal - probabilities[genNum]) < THRESHOLD);
        });
    }

    // Ugly workaround of Mockito couldn't open Annotations issue, likely mismatch between java and mockito version
     static class RandomMock extends Random {

        private static float mock;
        public void set(float f) {
            mock = f;
        }
        @Override
        public float nextFloat() {
            return this.mock;
        }
    }
}
