package com.id013754;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    private Player testPlayer;
    private Room startingRoom;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        startingRoom = new Room("Test Room", "A room for testing.");
        testPlayer = new Player("TestPlayer", startingRoom, 16, 25, 2, 10, 17, 12, 13, 13, 14);
        testItem1 = new Item("Key", "A small brass key.");
        testItem2 = new Item("Potion", "A bubbling green potion.");
    }

    @Test
    void testPlayerCreation() {
        assertNotNull(testPlayer, "Player should be created successfully");
        assertEquals("TestPlayer", testPlayer.getName(), "Player name should match");
        assertNotNull(testPlayer.getInventory(), "Player's inventory should be initialized");
        assertTrue(testPlayer.getInventory().isEmpty(), "Inventory should be empty");
        assertEquals(startingRoom, testPlayer.getCurrentRoom(), "Room should match");
    }

    @Test
    void testAddItem() {
        assertTrue(testPlayer.addItem(testItem1), "addItem() should return true for a valid Item");
        assertEquals(1, testPlayer.getInventory().size(), "Inventory size should be 1");
        assertTrue(testPlayer.getInventory().contains(testItem1), "Inventory should contain the 'testItem1'");
    }

    @Test
    void testAddNullItem() {
        assertFalse(testPlayer.addItem(null), "addItem should return False");
        assertTrue(testPlayer.getInventory().isEmpty(), "Inventory should remain empty when adding invalid item");
    }

    @Test
    void testRemoveItem() {
        testPlayer.addItem(testItem1);
        testPlayer.addItem(testItem2);
        assertEquals(2, testPlayer.getInventory().size(), "Inventory should return 2");

        assertTrue(testPlayer.removeItem(testItem1), "removeItem() should return true when removing testItem1");
        assertEquals(1, testPlayer.getInventory().size(), "Inventory should be 1 after removing testItem2");
        assertTrue(testPlayer.getInventory().contains(testItem2), "Inventory should contain testItem2.");
        assertFalse(testPlayer.getInventory().contains(testItem1), "Inventory should not contain testItem1");
    }

    @Test
    void testRemoveNonExistentItem() {
        testPlayer.addItem(testItem1);
        assertFalse(testPlayer.removeItem(testItem2),
                "removeItem() should return false when removing non-existent item");
        assertEquals(1, testPlayer.getInventory().size(), "Inventory size should be 1");
    }

    @Test
    void testGetItemByName() {
        testPlayer.addItem(testItem1);
        testPlayer.addItem(testItem2);

        assertEquals(testItem1, testPlayer.getItemByName("Key"), "Should find the item by exact name");
        assertEquals(testItem1, testPlayer.getItemByName("key"), "Should find the item by lowercase name");
        assertEquals(testItem1, testPlayer.getItemByName("KEY"), "Should find the item by UPPERCASE name");
        assertEquals(testItem2, testPlayer.getItemByName("potion"), "Should find the item2 by its name");
        assertNull(testPlayer.getItemByName("sword"), "Should return null when searching for sword");
        assertNull(testPlayer.getItemByName(""), "Should return null when the string is empty");
        assertNull(testPlayer.getItemByName(null), "Should return null when the item parameter is null");
    }

    @Test
    void testGetInventoryDescription() {
        assertEquals("Your inventory is empty.", testPlayer.getInventoryDescription(),
                "Desc should match when the inventory is emtpy");

        testPlayer.addItem(testItem1);
        assertEquals("You are carrying: Key.", testPlayer.getInventoryDescription(),
                "Desc should match when the inventory has only Key");

        testPlayer.addItem(testItem2);
        String descTwoItems = testPlayer.getInventoryDescription();
        assertTrue(descTwoItems.startsWith("You are carrying: "), "Desc should start with correct phrase");
        assertTrue(descTwoItems.contains("Key"), "Desc should contain Key");
        assertTrue(descTwoItems.contains("Potion"), "Desc should contain Potion");
        assertTrue(descTwoItems.contains(", "), "Desc should contain comma");
    }
}
