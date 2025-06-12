package com.id013754;

import java.util.Random;

public class DiceRoller {
    private static final Random random = new Random();

    public static int rollD20() {
        return random.nextInt(20) + 1;
    }

    public static int rollD10() {
        return random.nextInt(10) + 1;
    }

    public static int rollD8() {
        return random.nextInt(8) + 1;
    }

    public static int rollD6() {
        return random.nextInt(6) + 1;
    }

    public static int rollD4() {
        return random.nextInt(4) + 1;
    }

    public static int rollDice(int sides) {
        if (sides <= 0) {
            System.err.println("Warning: Dices cannot be less than or equal 0.");
            return 0;
        }
        return random.nextInt(sides) + 1;
    }

}
