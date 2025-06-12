package com.id013754;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.id013754.factories.ItemFactory;

class ItemFactoryTest {

    private ItemFactory itemFactory;

    @BeforeEach
    void setUp() {
        itemFactory = new ItemFactory();
    }

    @Test
    void createRock_shouldReturnCorrectRockItem() {
        Item rock = itemFactory.createRock();
        assertNotNull(rock, "Rock item should not be null.");
        assertEquals("Rock", rock.getName(), "Rock name mismatch.");
        assertEquals("A fist-sized, ordinary rock.", rock.getDescription(), "Rock description mismatch.");
    }

    @Test
    void createGoblinScimitar_shouldReturnCorrectScimitarItem() {
        Item scimitar = itemFactory.createGoblinScimitar();
        assertNotNull(scimitar, "Goblin Scimitar item should not be null.");
        assertEquals("Goblin Scimitar", scimitar.getName(), "Goblin Scimitar name mismatch.");
        assertEquals("A crude, notched scimtar, commonly used by goblins.", scimitar.getDescription(),
                "Goblin Scimitar description mismatch.");
    }

    @Test
    void createHealthPotion_shouldReturnCorrectPotionItem() {
        Item potion = itemFactory.createHealthPotion();
        assertNotNull(potion, "Health Potion item should not be null.");
        assertEquals("Health Potion", potion.getName(), "Health Potion name mismatch.");
        assertEquals("A small vial containing a swirling red liquid. Smells faintly sweet. (can heal 1D10)",
                potion.getDescription(),
                "Health Potion description mismatch.");
    }

    @Test
    void createPriestessKey_shouldReturnCorrectKeyItem() {
        Item key = itemFactory.createPriestessKey();
        assertNotNull(key, "Ornate Key item should not be null.");
        assertEquals("Ornate Key", key.getName(), "Ornate Key name mismatch.");
        assertEquals("A heavy iron key, intricately carved with strange symbols. It feels important.",
                key.getDescription(), "Ornate Key description mismatch.");
    }

    @Test
    void createWorgPenKey_shouldReturnCorrectKeyItem() {
        Item key = itemFactory.createWorgPenKey();
        assertNotNull(key, "Worg Pen Key item should not be null.");
        assertEquals("Worg Pen Key", key.getName(), "Worg Pen Key name mismatch.");
        assertEquals(
                "A rusty iron key, likely for one of the cages in the Worg Pens. This must be the key to help Halsin out of his cage.",
                key.getDescription(),
                "Worg Pen Key description mismatch.");
    }

    @Test
    void createThievesTools_shouldReturnCorrectToolsItem() {
        Item tools = itemFactory.createThievesTools();
        assertNotNull(tools, "Thieves' Tools item should not be null.");
        assertEquals("Thieves' Tools", tools.getName(), "Thieves' Tools name mismatch.");
        assertEquals("A small pouch containing various lockpicks and tools for disarming traps.",
                tools.getDescription(), "Thieves' Tools description mismatch.");
    }

    @Test
    void createSmokyPowderSatchel_shouldReturnCorrectSatchelItem() {
        Item satchel = itemFactory.createSmokyPowderSatchel();
        assertNotNull(satchel, "Smoky Powder Satchel item should not be null.");
        assertEquals("Smoky Powder Satchel", satchel.getName(), "Smoky Powder Satchel name mismatch.");
        assertEquals("A small, heavy satchel leaking a fine black powder. It smells volatile.",
                satchel.getDescription(), "Smoky Powder Satchel description mismatch.");
    }

    @Test
    void createGoblinScribblings_shouldReturnCorrectScribblingsItem() {
        Item scribblings = itemFactory.createGoblinScribblings();
        assertNotNull(scribblings, "Goblin Scribblings item should not be null.");
        assertEquals("Goblin Scribblings", scribblings.getName(), "Goblin Scribblings name mismatch.");
        assertEquals(
                "A piece of soiled parchment with crude drawings and barely legible goblin script. It seems to depict a map or a plan.",
                scribblings.getDescription(), "Goblin Scribblings description mismatch.");
    }

    @Test
    void createGutsSanctumKey_shouldReturnCorrectKeyItem() {
        Item key = itemFactory.createGutsSanctumKey();
        assertNotNull(key, "Gut's Sanctum Key item should not be null.");
        assertEquals("Gut's Sanctum Key", key.getName(), "Gut's Sanctum Key name mismatch.");
        assertEquals(
                "A rather gruesome key, fashioned from bone and sinew, taken from Priestess Gut. It likely unlocks her private chambers.",
                key.getDescription(), "Gut's Sanctum Key description mismatch.");
    }
}
