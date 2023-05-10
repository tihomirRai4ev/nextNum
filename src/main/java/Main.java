public class Main {
    public static void main(String[] args) {
        int[] randomNums = {-1, 0, 1, 2, 3, 4};
        float[] probabilities = {0.01f, 0.3f, 0.0f, 0.58f, 0.1f, 0.01f};
        RandomGen randomGen = new RandomGen(randomNums, probabilities);
        int[] counts = new int[randomNums.length];
        int numIterations = 100;
        for (int i = 0; i < numIterations; i++) {
            int num = randomGen.nextNum();
            for (int j = 0; j < randomNums.length; j++) {
                if (num == randomNums[j]) {
                    counts[j]++;
                    break;
                }
            }
        }


        for (int i = 0; i < randomNums.length; i++) {
            System.out.println(randomNums[i] + ": " +counts[i] + " times");
        }
    }
}