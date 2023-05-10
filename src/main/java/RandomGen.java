import java.util.Objects;
import java.util.*;

public class RandomGen {

    private int[] randomNums;
    private float[] cumProbabilities;
    private int totalNonZeroProbElement = 0;
    private Random random;
    private static final float PRECISION = 0.0001f;

    public RandomGen(int[] randomNums, float[] probabilities) {
        this(randomNums, probabilities, new Random());
    }

    public RandomGen(int[] randomNums, float[] probabilities, Random random) {
        if (randomNums == null
                || probabilities == null
                || randomNums.length != probabilities.length
                || probabilities.length == 0) {
            throw new IllegalArgumentException("RandomNums/Probabilities must have the same size or not be empty");
        }

        List<Integer> validNums = new ArrayList<>(); // Will contain only valid nums here
        cumProbabilities = new float[probabilities.length];

        for (int i = 0; i < probabilities.length; i++) {
            float prob = probabilities[i];
            if (isLessThan(prob, 0.0f)
                    || isGreaterThan(prob, 1.0f)
                    || isNanOrInfinite(prob)) {
                throw new IllegalArgumentException("Probability provided has invalid value: " + prob);
            }

            if (equalsWithPrecision(prob, 0.0f)) {
                continue;
            }

            cumProbabilities[totalNonZeroProbElement] = totalNonZeroProbElement == 0 ? prob
                    : prob + cumProbabilities[totalNonZeroProbElement - 1];

            if (isGreaterThan(cumProbabilities[totalNonZeroProbElement], 1.0f)) {
                throw new IllegalArgumentException("Cumulative probability exceeds 1");
            }

            totalNonZeroProbElement++;
            validNums.add(randomNums[i]);
        }

        if (totalNonZeroProbElement == 0) {
            throw new IllegalArgumentException("Only zero probabilities provided");
        }

        if (!Objects.equals(cumProbabilities[totalNonZeroProbElement - 1], 1.0f)) {
            throw new IllegalArgumentException("Total probability should be 1 with precision of: " + PRECISION);
        }

        // This wouldn't be needed, but because it is asked to be int[]
        this.randomNums = validNums.stream().mapToInt(Integer::intValue).toArray();
        this.random = random;
    }

    public int nextNum() {
        float rand = random.nextFloat();
        for (int i = 0; i < cumProbabilities.length; i++) {
            if (rand <= cumProbabilities[i]) {
                return randomNums[i];
            }
        }

        return randomNums[randomNums.length - 1];
    }

    private static boolean equalsWithPrecision(float a, float b) {
        return Math.abs(a - b) < PRECISION;
    }

    private static boolean isNanOrInfinite(float a) {
        return Float.isNaN(a) || Float.isInfinite(a);
    }

    private static boolean isGreaterThan(float a, float b) {
        if (equalsWithPrecision(a, b)) {
            return false;
        }
        return a > b;
    }

    private static boolean isLessThan(float a, float b) {
        if (equalsWithPrecision(a, b)) {
            return false;
        }
        return a < b;
    }
}
