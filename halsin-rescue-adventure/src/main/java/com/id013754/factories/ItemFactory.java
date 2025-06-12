package com.id013754.factories;

import com.id013754.Item;

/* Factory class is used to create Item objects for the game
 */
public class ItemFactory {
    public Item createRock() {
        return new Item("Rock", "A fist-sized, ordinary rock.");
    }

    public Item createGoblinScimitar() {
        return new Item("Goblin Scimitar", "A crude, notched scimtar, commonly used by goblins.");
    }

    public Item createHealthPotion() {
        return new Item("Health Potion",
                "A small vial containing a swirling red liquid. Smells faintly sweet. (can heal 1D10)");
    }

    public Item createPriestessKey() {
        return new Item("Ornate Key", "A heavy iron key, intricately carved with strange symbols. It feels important.");
    }

    public Item createWorgPenKey() {
        return new Item("Worg Pen Key",
                "A rusty iron key, likely for one of the cages in the Worg Pens. This must be the key to help Halsin out of his cage.");
    }

    public Item createThievesTools() {
        return new Item("Thieves' Tools", "A small pouch containing various lockpicks and tools for disarming traps.");
    }

    public Item createSmokyPowderSatchel() {
        return new Item("Smoky Powder Satchel",
                "A small, heavy satchel leaking a fine black powder. It smells volatile.");
    }

    public Item createGoblinScribblings() {
        return new Item("Goblin Scribblings",
                "A piece of soiled parchment with crude drawings and barely legible goblin script. It seems to depict a map or a plan.");
    }

    public Item createGutsSanctumKey() {
        return new Item("Gut's Sanctum Key",
                "A rather gruesome key, fashioned from bone and sinew, taken from Priestess Gut. It likely unlocks her private chambers.");
    }

}
