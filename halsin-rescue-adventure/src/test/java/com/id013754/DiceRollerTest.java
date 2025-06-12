package com.id013754;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class DiceRollerTest {

    private static final int REPETITIONS = 1000; // Number of times to roll to check range and distribution

    @RepeatedTest(REPETITIONS)
    void rollD20_shouldReturnValueBetween1And20() {
        int roll = DiceRoller.rollD20();
        assertTrue(roll >= 1 && roll <= 20, "D20 roll should be between 1 and 20. Got: " + roll);
    }

    @RepeatedTest(REPETITIONS)
    void rollD10_shouldReturnValueBetween1And10() {
        int roll = DiceRoller.rollD10();
        assertTrue(roll >= 1 && roll <= 10, "D10 roll should be between 1 and 10. Got: " + roll);
    }

    @RepeatedTest(REPETITIONS)
    void rollD8_shouldReturnValueBetween1And8() {
        int roll = DiceRoller.rollD8();
        assertTrue(roll >= 1 && roll <= 8, "D8 roll should be between 1 and 8. Got: " + roll);
    }

    @RepeatedTest(REPETITIONS)
    void rollD6_shouldReturnValueBetween1And6() {
        int roll = DiceRoller.rollD6();
        assertTrue(roll >= 1 && roll <= 6, "D6 roll should be between 1 and 6. Got: " + roll);
    }

    @RepeatedTest(REPETITIONS)
    void rollD4_shouldReturnValueBetween1And4() {
        int roll = DiceRoller.rollD4();
        assertTrue(roll >= 1 && roll <= 4, "D4 roll should be between 1 and 4. Got: " + roll);
    }

    @Test
    void rollDice_withValidSides_shouldReturnValueInCorrectRange() {
        int sides = 12;
        for (int i = 0; i < REPETITIONS; i++) {
            int roll = DiceRoller.rollDice(sides);
            assertTrue(roll >= 1 && roll <= sides,
                    "Custom dice roll (" + sides + " sides) should be between 1 and " + sides + ". Got: " + roll);
        }
    }

    @Test
    void rollDice_with1Side_shouldAlwaysReturn1() {
        for (int i = 0; i < REPETITIONS; i++) {
            assertEquals(1, DiceRoller.rollDice(1), "Dice roll with 1 side should always be 1.");
        }
    }

    @Test
    void rollDice_with0Sides_shouldReturn0AndPrintError() {
        assertEquals(0, DiceRoller.rollDice(0), "Dice roll with 0 sides should return 0.");
    }

    @Test
    void rollDice_withNegativeSides_shouldReturn0AndPrintError() {
        assertEquals(0, DiceRoller.rollDice(-5), "Dice roll with negative sides should return 0.");
    }

    @Test
    void rollD6_overManyRolls_shouldProduceAllValuesFrom1To6() {
        Set<Integer> distinctRolls = new HashSet<>();
        for (int i = 0; i < REPETITIONS * 10; i++) { // More repetitions for distribution
            distinctRolls.add(DiceRoller.rollD6());
        }
        assertEquals(6, distinctRolls.size(), "Over many D6 rolls, all values from 1 to 6 should appear.");
        for (int i = 1; i <= 6; i++) {
            assertTrue(distinctRolls.contains(i), "Value " + i + " should have appeared in D6 rolls.");
        }
    }
}
