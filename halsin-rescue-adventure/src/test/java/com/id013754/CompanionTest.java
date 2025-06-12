package com.id013754;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanionNPCTest {

    @Mock
    private Room mockStartingRoom; // Mocked Room dependency

    private CompanionNPC companionNPC;
    private final String defaultName = "Shadowheart";
    private final String defaultDescription = "A loyal cleric.";
    private final String defaultRace = "Half-Elf";
    private final String defaultClass = "Cleric";
    private final int defaultLevel = 2;
    private final int defaultAC = 16;
    private final int defaultMaxHP = 20;
    private final int defaultStr = 12;
    private final int defaultDex = 14;
    private final int defaultCon = 13;
    private final int defaultIntl = 10;
    private final int defaultWis = 15;
    private final int defaultCha = 10;

    @BeforeEach
    void setUp() {
        companionNPC = new CompanionNPC(defaultName, defaultDescription, mockStartingRoom, defaultRace, defaultClass,
                defaultLevel, defaultAC, defaultMaxHP, defaultStr, defaultDex, defaultCon,
                defaultIntl, defaultWis, defaultCha);
    }

    @Test
    void constructor_shouldCallSuperConstructorAndInitialize() {
        // Basic checks to ensure super constructor was likely called
        assertEquals(defaultName, companionNPC.getName());
        assertEquals(defaultDescription, companionNPC.getDescription());
        assertEquals(mockStartingRoom, companionNPC.getCurrentRoom());
        assertEquals(defaultRace, companionNPC.getRace());
        assertEquals(defaultClass, companionNPC.getCharacterClass());
        assertEquals(defaultLevel, companionNPC.getLevel());
        assertEquals(defaultAC, companionNPC.getAC());
        assertEquals(defaultMaxHP, companionNPC.getMaxHP());
        assertEquals(defaultMaxHP, companionNPC.getCurrentHP());
        assertEquals(defaultStr, companionNPC.getSTR());
        assertEquals(defaultDex, companionNPC.getDEX());
        assertEquals(defaultCon, companionNPC.getCON());
        assertEquals(defaultIntl, companionNPC.getINT());
        assertEquals(defaultWis, companionNPC.getWIS());
        assertEquals(defaultCha, companionNPC.getCHA());

        // Verify interaction with mockRoom from super constructor
        verify(mockStartingRoom, times(1)).addNPC(companionNPC);
    }

    @Test
    void isPlayerControlled_shouldReturnTrueForCompanionNPC() {
        assertTrue(companionNPC.isPlayerControlled(), "CompanionNPC should be player controlled.");
    }

    // Other functionalities are inherited from NPC and tested in NPCTest.
    // We can add specific tests here if CompanionNPC overrides more methods
    // or adds unique behavior.

    @Test
    void takeDamage_inheritedFunctionalityCheck() {
        companionNPC.takeDamage(5);
        assertEquals(defaultMaxHP - 5, companionNPC.getCurrentHP());
    }

    @Test
    void receiveHealing_inheritedFunctionalityCheck() {
        companionNPC.takeDamage(10); // HP is now defaultMaxHP - 10
        companionNPC.receiveHealing(5); // HP becomes defaultMaxHP - 5
        assertEquals(defaultMaxHP - 5, companionNPC.getCurrentHP());
    }

    @Test
    void isDefeated_inheritedFunctionalityCheck() {
        assertFalse(companionNPC.isDefeated());
        companionNPC.takeDamage(defaultMaxHP);
        assertTrue(companionNPC.isDefeated());
    }
}
