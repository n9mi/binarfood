package com.synergy.binarfood.util.random;

import java.util.Random;

public class Randomizer {
    public static int generate(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    public static int generatePrice(int min, int max) {
        Random random = new Random();
        int thousand = 1000;

        return random.nextInt(max / thousand - (min + 4) / thousand + 1) *
                thousand + (min + 4) / thousand * thousand;
    }
}
