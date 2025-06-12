package com.id013754; // Match the package of the class being tested

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Room class.
 */
class RoomTest {

    private Room testRoom;
    private Room northRoom;
    private Item testItem1;
    private NPC testNPC1;
    private Player testPlayerObserver; // A player to act as an observer

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        // Create a fresh room for each test
        testRoom = new Room("Test Chamber", "A plain chamber for testing.");
        northRoom = new Room("North Chamber", "A chamber to the north.");

        // Create a test item
        testItem1 = new Item("Test Key", "A generic key for testing.");

        // Create a test NPC. NPC constructor requires a starting room.
        // NPC(name, desc, room, race, class, lvl, AC, HP, str, dex, con, int, wis, cha)
        testNPC1 = new NPC("Test Goblin", "A generic goblin.", testRoom,
                "Goblin", "Warrior", 1, 13, 7,
                10, 12, 10, 8, 8, 8);

        // Create a test Player to act as an Observer
        // Player(name, room, AC, maxHP, prof, str, dex, con, int, wis, cha)
        testPlayerObserver = new Player("ObserverPlayer", testRoom, 10, 10, 2, 10, 10, 10, 10, 10, 10);
    }

    @Test
    void testRoomCreation() {
        assertEquals("Test Chamber", testRoom.getName(), "Room name should be set correctly.");
        assertEquals("A plain chamber for testing.", testRoom.getDescription(),
                "Room description should be set correctly.");
        assertNotNull(testRoom.getExitDirections(), "Exits map should be initialized."); // Using getExitsList from user
        // Game.java
        assertTrue(testRoom.getExitDirections().isEmpty(), "New room should have no exits initially.");
        assertNotNull(testRoom.getItems(), "Items list should be initialized.");
        assertTrue(testRoom.getItems().isEmpty(), "New room should have no items initially.");
        // NPC list is initialized, but testNPC1 is added via its own constructor.
        // Observer list is initialized, testPlayerObserver added via its constructor.
    }

    @Test
    void testAddAndGetExit() {
        testRoom.addExit("north", northRoom);
        assertEquals(northRoom, testRoom.getExit("north"), "Should return the correct room for 'north' exit.");
        assertEquals(northRoom, testRoom.getExit("NORTH"), "Exit lookup should be case-insensitive.");
        assertNull(testRoom.getExit("south"), "Should return null for a non-existent exit.");
        assertTrue(testRoom.getExitDirections().contains("north"), "getExitDirections should include 'north'.");
    }

    @Test
    void testAddInvalidExit() {
        testRoom.addExit(null, northRoom); // Invalid direction
        testRoom.addExit("east", null); // Invalid neighbor
        testRoom.addExit("", northRoom); // Empty direction
        assertTrue(testRoom.getExitDirections().isEmpty(), "Should not add exits with null or empty components.");
    }

    @Test
    void testAddItemAndGetItems() {
        testRoom.addItem(testItem1);
        assertEquals(1, testRoom.getItems().size(), "Room should have 1 item after adding.");
        assertTrue(testRoom.getItems().contains(testItem1), "Room should contain the added item.");
        assertEquals(testItem1, testRoom.getItemByName("Test Key"), "Should find item by name.");
        assertEquals(testItem1, testRoom.getItemByName("test key"), "getItemByName should be case-insensitive.");
    }

    @Test
    void testAddNullItemToRoom() {
        testRoom.addItem(null);
        assertTrue(testRoom.getItems().isEmpty(), "Adding null item should not change item list.");
    }

    @Test
    void testRemoveItemFromRoom() {
        testRoom.addItem(testItem1);
        assertTrue(testRoom.removeItem(testItem1), "removeItem should return true for existing item.");
        assertFalse(testRoom.getItems().contains(testItem1), "Item should be removed.");
        assertFalse(testRoom.removeItem(testItem1), "removeItem should return false if item not present.");
    }

    @Test
    void testGetItemByNameNotFound() {
        assertNull(testRoom.getItemByName("NonExistent Key"), "Should return null for non-existent item.");
    }

    @Test
    void testAddAndGetNPCs() {
        // testNPC1 was already added to testRoom via its constructor.
        Room anotherRoom = new Room("Another Room", "Another place.");
        NPC npc2 = new NPC("Guard", "A stern guard.", anotherRoom, "Human", "Guard", 2, 16, 15, 14, 12, 12, 11, 11, 10);

        // Check initial placement
        assertTrue(testRoom.getNPCs().contains(testNPC1), "Test Chamber should contain testNPC1.");
        assertEquals(1, testRoom.getNPCs().size(), "Test Chamber should have 1 NPC initially from setup.");
        assertTrue(anotherRoom.getNPCs().contains(npc2), "Another Room should contain npc2.");

        // Test moving npc2 to testRoom
        npc2.setCurrentRoom(testRoom); // This calls removeNPC on anotherRoom and addNPC on testRoom
        assertTrue(testRoom.getNPCs().contains(npc2), "Test Chamber should now contain npc2.");
        assertFalse(anotherRoom.getNPCs().contains(npc2), "Another Room should no longer contain npc2.");
        assertEquals(2, testRoom.getNPCs().size(), "Test Chamber should have 2 NPCs after move.");

        assertEquals(testNPC1, testRoom.getNPCByName("Test Goblin"), "Should find NPC by name.");
        assertEquals(npc2, testRoom.getNPCByName("Guard"), "Should find second NPC by name.");
    }

    @Test
    void testRemoveNPCFromRoom() {
        // testNPC1 is added during setUp by its constructor
        assertTrue(testRoom.getNPCs().contains(testNPC1), "NPC should be in room initially.");
        assertTrue(testRoom.removeNPC(testNPC1), "removeNPC should return true for existing NPC.");
        assertFalse(testRoom.getNPCs().contains(testNPC1), "NPC should be removed.");
        assertFalse(testRoom.removeNPC(testNPC1), "removeNPC should return false if NPC not present.");
    }

    @Test
    void testGetNPCByNameNotFound() {
        assertNull(testRoom.getNPCByName("NonExistent Goblin"), "Should return null for non-existent NPC.");
    }

    // Basic Observer Pattern Tests for Room
    private static class MockObserver implements Observer {
        public String lastMessage = null;
        public String name = "MockObserver";

        @Override
        public void update(String message) {
            this.lastMessage = message;
        }

        @Override
        public String getName() {
            return name;
        }

        // Basic equals for testing removal
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            MockObserver other = (MockObserver) obj;
            return name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    @Test
    void testAttachObserver() {
        MockObserver mockObs = new MockObserver();
        testRoom.attach(mockObs);
        testRoom.detach(mockObs);
    }

    @Test
    void testDetachObserver() {
        MockObserver mockObs = new MockObserver();
        testRoom.attach(mockObs);
        testRoom.detach(mockObs);
        testRoom.notifyObserver("Test Message After Detach", null);
        assertNull(mockObs.lastMessage, "Detached observer should not receive notifications.");
    }

    @Test
    void testNotifyObservers() {
        MockObserver obs1 = new MockObserver();
        obs1.name = "Obs1";
        MockObserver obs2 = new MockObserver();
        obs2.name = "Obs2";

        testRoom.attach(obs1);
        testRoom.attach(obs2);

        testRoom.notifyObserver("Hello Observers!", null); // Originator is null, all should get it

        assertEquals("Hello Observers!", obs1.lastMessage, "Observer 1 should receive the message.");
        assertEquals("Hello Observers!", obs2.lastMessage, "Observer 2 should receive the message.");
    }

    @Test
    void testNotifyObserversWithOriginator() {
        // testPlayerObserver was attached in setUp
        MockObserver obs2 = new MockObserver();
        obs2.name = "Obs2";
        testRoom.attach(obs2); // testPlayerObserver is already attached

        testRoom.notifyObserver("Event by Player", testPlayerObserver);

        assertEquals("Event by Player", obs2.lastMessage,
                "Observer 2 should receive message when originator is different.");
    }

    @Test
    void testGetPlayers() {
        // testPlayerObserver is added in setUp
        List<Player> playersInRoom = testRoom.getPlayers();
        assertEquals(1, playersInRoom.size(), "Should be one player (observer) in the room.");
        assertTrue(playersInRoom.contains(testPlayerObserver),
                "The list of players should contain testPlayerObserver.");
    }

    @Test
    void testDetailedDescriptionShowsNPCsAndPlayers() {
        // testNPC1 and testPlayerObserver are in testRoom from setUp
        String description = testRoom.getSurroundingDetail(testPlayerObserver); // Player looking is testPlayerObserver

        assertTrue(description.contains(testNPC1.getName()), "Detailed description should list NPCs.");
        assertFalse(description.contains("Also here (players): " + testPlayerObserver.getName()),
                "Player looking should not be listed as 'also here'.");

        // Add another player/observer
        Player anotherPlayer = new Player("AnotherPlayer", testRoom, 10, 10, 2, 10, 10, 10, 10, 10, 10);
        // testRoom.attach(anotherPlayer); // Player constructor attaches

        description = testRoom.getSurroundingDetail(testPlayerObserver);
        assertTrue(description.contains("Also here (players): " + anotherPlayer.getName()),
                "Detailed description should list other players.");
    }
}
