package com.id013754;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.id013754.factories.RoomFactory;

class RoomFactoryTest {

    private RoomFactory roomFactory;

    @BeforeEach
    void setUp() {
        roomFactory = new RoomFactory();
    }

    @Test
    void createGoblinCampEntrance_shouldReturnCorrectRoom() {
        Room room = roomFactory.createGoblinCampEntrance();
        assertNotNull(room, "Goblin Camp Entrance room should not be null.");
        assertEquals("Goblin Camp Entrance", room.getName(), "Room name mismatch.");
        assertEquals(
                "You are standing at battered entrance to a goblin camp. Crude watchtowers flank the path, guarded by bored-looking gobnlins.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createGoblinCampCourtyard_shouldReturnCorrectRoom() {
        Room room = roomFactory.createGoblinCampCourtyard();
        assertNotNull(room, "Goblin Camp Courtyard room should not be null.");
        assertEquals("Goblin Camp Courtyard", room.getName(), "Room name mismatch.");
        assertEquals(
                "The courtyard buzzes with unpleasant activity. Goblins mill around, some tending a spit roast, others lounging or arguing. A large, imposing door leading into the main building.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createShatterSanctumMainHall_shouldReturnCorrectRoom() {
        Room room = roomFactory.createShatterSanctumMainHall();
        assertNotNull(room, "Shattered Sanctum (Main Hall) room should not be null.");
        assertEquals("Shattered Sanctum (Main Hall)", room.getName(), "Room name mismatch.");
        assertEquals(
                "You've entered the main hall of an old Selunite temple, now defiled by the goblins. Statues are broken, and crude banners hand on the walls. Passages lead West (towards an altar), North (towards a throne), and east (towards a command post). Stairs lead down near the back.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createBloodiedShrine_shouldReturnCorrectRoom() {
        Room room = roomFactory.createBloodiedShrine();
        assertNotNull(room, "Bloodied Shrine room should not be null.");
        assertEquals("Bloodied Shrine", room.getName(), "Room name mismatch.");
        assertEquals(
                "This side chamber is dominated by a blood-stained altar dedicated to the Absolute. Tools of dubious purpose lie nearby. A sturdy door leads further west.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createGutsQuarter_shouldReturnCorrectRoom() {
        Room room = roomFactory.createGutsQuarter();
        assertNotNull(room, "Gut's Quarters room should not be null.");
        assertEquals("Gut's Quarters", room.getName(), "Room name mismatch.");
        assertEquals(
                "This appears to be Priestess Gut's private room, containing a cot and some personal effects. There might be a cell nearby.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createRagzlinsThroneRoom_shouldReturnCorrectRoom() {
        Room room = roomFactory.createRagzlinsThroneRoom();
        assertNotNull(room, "Ragzlin's Throne Room room should not be null.");
        assertEquals("Ragzlin's Throne Room", room.getName(), "Room name mismatch.");
        assertEquals(
                "A crude throne sits atop a platform at the far end of this large chamber where Dror Ragzlin holds court. To the side lies the unsettling corpse of a Mind Flayer, apparently dissected.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createMintharasCommandPost_shouldReturnCorrectRoom() {
        Room room = roomFactory.createMintharasCommandPost();
        assertNotNull(room, "Minthara's Command Post room should not be null.");
        assertEquals("Minthara's Command Post", room.getName(), "Room name mismatch.");
        assertEquals(
                "This area seems more organized, likely used by the drow commander Minthara. Maps and plans are scattered accross a table. A rickery bridge crosses a chasm to the east leading to her post.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createMakeshiftPrison_shouldReturnCorrectRoom() {
        Room room = roomFactory.createMakeshiftPrison();
        assertNotNull(room, "Makeshift Prison room should not be null.");
        assertEquals("Makeshift Prison", room.getName(), "Room name mismatch.");
        assertEquals(
                "Crudge cages line this section fo the Sanctum. You see a widely travellerd wizard Volo trapped in a cage, guarded by a goblin names Gribbo.",
                room.getDescription(), "Room description mismatch.");
    }

    @Test
    void createWorgPens_shouldReturnCorrectRoom() {
        Room room = roomFactory.createWorgPens();
        assertNotNull(room, "Worg Pens room should not be null.");
        assertEquals("Worg Pens", room.getName(), "Room name mismatch.");
        assertEquals(
                "Filthy cages line the walls of this dark, foul-smell cave. Growls echo around you. In one of the larger cages, you spot the shape of a large, weary-looking wood elf man. There are some tormentor goblins around him - this must be the Halsin, the person you have to rescue him from this prison...",
                room.getDescription(), "Room description mismatch.");
    }
}
