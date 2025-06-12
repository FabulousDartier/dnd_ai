package com.id013754;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // For Mockito annotations
class NPCTest {

    @Mock
    private Room mockStartingRoom; // Mocked Room dependency
    @Mock
    private Room mockNewRoom;
    @Mock
    private Item mockItem;

    private NPC npc;
    private final String defaultName = "Goblin";
    private final String defaultDescription = "A generic goblin.";
    private final String defaultRace = "Goblin";
    private final String defaultClass = "Warrior";
    private final int defaultLevel = 1;
    private final int defaultAC = 13;
    private final int defaultMaxHP = 10;
    private final int defaultStr = 10;
    private final int defaultDex = 10;
    private final int defaultCon = 10;
    private final int defaultIntl = 8;
    private final int defaultWis = 8;
    private final int defaultCha = 8;

    @BeforeEach
    void setUp() {
        npc = new NPC(defaultName, defaultDescription, mockStartingRoom, defaultRace, defaultClass,
                defaultLevel, defaultAC, defaultMaxHP, defaultStr, defaultDex, defaultCon,
                defaultIntl, defaultWis, defaultCha);
    }

    @Test
    void constructor_validParameters_shouldInitializeCorrectly() {
        assertEquals(defaultName, npc.getName());
        assertEquals(defaultDescription, npc.getDescription());
        assertEquals(mockStartingRoom, npc.getCurrentRoom());
        assertEquals(defaultRace, npc.getRace());
        assertEquals(defaultClass, npc.getCharacterClass());
        assertEquals(defaultLevel, npc.getLevel());
        assertEquals(defaultAC, npc.getAC());
        assertEquals(defaultMaxHP, npc.getMaxHP());
        assertEquals(defaultMaxHP, npc.getCurrentHP()); // Current HP should be max HP initially
        assertEquals(defaultStr, npc.getSTR());
        assertEquals(defaultDex, npc.getDEX());
        assertEquals(defaultCon, npc.getCON());
        assertEquals(defaultIntl, npc.getINT());
        assertEquals(defaultWis, npc.getWIS());
        assertEquals(defaultCha, npc.getCHA());
        assertEquals(2, npc.getProficiencyBonus()); // For level 1-4

        // Verify that the NPC was added to the starting room
        verify(mockStartingRoom, times(1)).addNPC(npc);
    }

    @Test
    void constructor_invalidParameters_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new NPC(null, "Desc", mockStartingRoom, "Race", "Class", 1, 10, 10, 10, 10, 10, 10, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new NPC("Name", null, mockStartingRoom, "Race", "Class", 1, 10, 10, 10, 10, 10, 10, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new NPC("Name", "Desc", null, "Race", "Class", 1, 10, 10, 10, 10, 10, 10, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new NPC("Name", "Desc", mockStartingRoom, null, "Class", 1, 10, 10, 10, 10, 10, 10, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new NPC("Name", "Desc", mockStartingRoom, "Race", null, 1, 10, 10, 10, 10, 10, 10, 10, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new NPC("Name", "Desc", mockStartingRoom, "Race", "Class", 0, 10, 10, 10, 10, 10, 10, 10, 10)); // Level
                                                                                                                      // <
                                                                                                                      // 1
        assertThrows(IllegalArgumentException.class,
                () -> new NPC("Name", "Desc", mockStartingRoom, "Race", "Class", 1, 10, 0, 10, 10, 10, 10, 10, 10)); // MaxHP
                                                                                                                     // <
                                                                                                                     // 1
    }

    @Test
    void getAbilityModifier_shouldCalculateCorrectly() {
        // STR 10 -> Modifier 0
        assertEquals(0, npc.getAbilityModifier("str"));
        // DEX 12 -> Modifier +1
        NPC npcDex12 = new NPC("DexNPC", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 12, 10, 10, 10, 10);
        assertEquals(1, npcDex12.getAbilityModifier("dex"));
        // CON 8 -> Modifier -1
        NPC npcCon8 = new NPC("ConNPC", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 10, 8, 10, 10, 10);
        assertEquals(-1, npcCon8.getAbilityModifier("con"));
        // INT 15 -> Modifier +2
        NPC npcInt15 = new NPC("IntNPC", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 10, 10, 15, 10, 10);
        assertEquals(2, npcInt15.getAbilityModifier("intl"));
        // WIS 7 -> Modifier -2
        NPC npcWis7 = new NPC("WisNPC", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 10, 10, 10, 7, 10);
        assertEquals(-2, npcWis7.getAbilityModifier("wis"));
        // CHA 18 -> Modifier +4
        NPC npcCha18 = new NPC("ChaNPC", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 10, 10, 10, 10, 18);
        assertEquals(4, npcCha18.getAbilityModifier("cha"));
    }

    @Test
    void getAbilityModifier_invalidAbilityName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> npc.getAbilityModifier("invalid"));
    }

    @Test
    void getInitiativeBonus_shouldBeDexModifier() {
        NPC npcDex14 = new NPC("Dexy", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 14, 10, 10, 10, 10); // DEX 14 -> Mod +2
                                                                                                            
        assertEquals(2, npcDex14.getInitiativeBonus());
        assertEquals(npcDex14.getAbilityModifier("dex"), npcDex14.getInitiativeBonus());
    }

    @Test
    void takeDamage_positiveAmount_shouldReduceCurrentHP() {
        npc.takeDamage(5);
        assertEquals(defaultMaxHP - 5, npc.getCurrentHP());
    }

    @Test
    void takeDamage_amountExceedingCurrentHP_shouldSetCurrentHPToZero() {
        npc.takeDamage(defaultMaxHP + 5);
        assertEquals(0, npc.getCurrentHP());
    }

    @Test
    void takeDamage_negativeAmount_shouldNotChangeCurrentHP() {
        npc.takeDamage(-5);
        assertEquals(defaultMaxHP, npc.getCurrentHP());
    }

    @Test
    void receiveHealing_positiveAmount_shouldIncreaseCurrentHP() {
        npc.takeDamage(7); // HP is now 3
        npc.receiveHealing(4); // HP becomes 7
        assertEquals(7, npc.getCurrentHP());
    }

    @Test
    void receiveHealing_amountExceedingMaxHP_shouldSetCurrentHPToMaxHP() {
        npc.receiveHealing(5);
        assertEquals(defaultMaxHP, npc.getCurrentHP());
    }

    @Test
    void receiveHealing_negativeAmount_shouldNotChangeCurrentHP() {
        npc.takeDamage(3); // HP is now 7
        npc.receiveHealing(-2);
        assertEquals(7, npc.getCurrentHP());
    }

    @Test
    void isDefeated_shouldReturnTrueWhenHPIsZeroOrLess() {
        assertFalse(npc.isDefeated());
        npc.takeDamage(defaultMaxHP);
        assertTrue(npc.isDefeated());
    }

    @Test
    void isPlayerControlled_shouldReturnFalseForBaseNPC() {
        assertFalse(npc.isPlayerControlled());
    }

    @Test
    void addItemToEquipment_shouldAddItem() {
        assertTrue(npc.addItemToEquipment(mockItem));
        assertTrue(npc.getEquipment().contains(mockItem));
    }

    @Test
    void removeItemFromEquipment_shouldRemoveItem() {
        npc.addItemToEquipment(mockItem);
        assertTrue(npc.getEquipment().contains(mockItem));
        assertTrue(npc.removeItemFromEquipment(mockItem));
        assertFalse(npc.getEquipment().contains(mockItem));
    }

    @Test
    void getEquipment_shouldReturnCopyOfEquipmentList() {
        npc.addItemToEquipment(mockItem);
        List<Item> equipment = npc.getEquipment();
        equipment.clear(); // Modify the returned list
        assertTrue(npc.getEquipment().contains(mockItem),
                "Modifying returned list should not affect NPC's actual equipment.");
    }

    @Test
    void getEquipmentDescription_shouldReturnCorrectString() {
        when(mockItem.getName()).thenReturn("Rusty Sword");
        Item mockShield = mock(Item.class);
        when(mockShield.getName()).thenReturn("Wooden Shield");

        assertEquals(defaultName + " is not carrying any notable equipment.", npc.getEquipmentDescription());

        npc.addItemToEquipment(mockItem);
        assertEquals(defaultName + " has Rusty Sword.", npc.getEquipmentDescription());

        npc.addItemToEquipment(mockShield);
        // Order might vary, so check for contains
        String desc = npc.getEquipmentDescription();
        assertTrue(desc.contains(defaultName + " has"));
        assertTrue(desc.contains("Rusty Sword"));
        assertTrue(desc.contains("Wooden Shield"));
    }

    @Test
    void setCurrentRoom_shouldUpdateRoomAndHandleOldAndNewRoomNPCLists() {
        npc.setCurrentRoom(mockNewRoom);

        // Verify NPC removed from old room and added to new room
        verify(mockStartingRoom, times(1)).removeNPC(npc);
        verify(mockNewRoom, times(1)).addNPC(npc);
        assertEquals(mockNewRoom, npc.getCurrentRoom());
    }

    @Test
    void setCurrentRoom_withNullNewRoom_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> npc.setCurrentRoom(null));
    }

    @Test
    void proficiencyBonus_shouldBeCorrectForLevel() {
        NPC level1NPC = new NPC("L1", "D", mockStartingRoom, "R", "C", 1, 10, 10, 10, 10, 10, 10, 10, 10);
        assertEquals(2, level1NPC.getProficiencyBonus());
        NPC level4NPC = new NPC("L4", "D", mockStartingRoom, "R", "C", 4, 10, 10, 10, 10, 10, 10, 10, 10);
        assertEquals(2, level4NPC.getProficiencyBonus());
        NPC level5NPC = new NPC("L5", "D", mockStartingRoom, "R", "C", 5, 10, 10, 10, 10, 10, 10, 10, 10);
        assertEquals(3, level5NPC.getProficiencyBonus());
        NPC level8NPC = new NPC("L8", "D", mockStartingRoom, "R", "C", 8, 10, 10, 10, 10, 10, 10, 10, 10);
        assertEquals(3, level8NPC.getProficiencyBonus());
        NPC level9NPC = new NPC("L9", "D", mockStartingRoom, "R", "C", 9, 10, 10, 10, 10, 10, 10, 10, 10); 
        assertEquals(2, level9NPC.getProficiencyBonus()); 
    }

    @Test
    void toString_shouldReturnFormattedString() {
        String expected = defaultName + " (" + defaultRace + " " + defaultClass + ", Lvl " + defaultLevel + ")";
        assertEquals(expected, npc.toString());
    }

    @Test
    void equals_shouldBeTrueForSameName() {
        NPC sameNpc = new NPC(defaultName, "Different Desc", mockStartingRoom, "Elf", "Mage", 5, 15, 20, 12, 12, 12, 12,
                12, 12);
        assertEquals(npc, sameNpc);
        assertEquals(npc.hashCode(), sameNpc.hashCode());
    }

    @Test
    void equals_shouldBeFalseForDifferentName() {
        NPC differentNpc = new NPC("Different Goblin", defaultDescription, mockStartingRoom, defaultRace, defaultClass,
                defaultLevel, defaultAC, defaultMaxHP, defaultStr, defaultDex, defaultCon, defaultIntl, defaultWis,
                defaultCha);
        assertNotEquals(npc, differentNpc);
    }

    @Test
    void equals_shouldBeFalseForNullOrDifferentClass() {
        assertNotEquals(null, npc);
        assertNotEquals("A string", npc);
    }
}
