package com.id013754.factories;

import com.id013754.Room;

public class RoomFactory {
    public Room createGoblinCampEntrance() {
        return new Room("Goblin Camp Entrance",
                "You are standing at battered entrance to a goblin camp. Crude watchtowers flank the path, guarded by bored-looking gobnlins.");
    }

    public Room createGoblinCampCourtyard() {
        return new Room("Goblin Camp Courtyard",
                "The courtyard buzzes with unpleasant activity. Goblins mill around, some tending a spit roast, others lounging or arguing. A large, imposing door leading into the main building.");
    }

    public Room createShatterSanctumMainHall() {
        return new Room("Shattered Sanctum (Main Hall)",
                "You've entered the main hall of an old Selunite temple, now defiled by the goblins. Statues are broken, and crude banners hand on the walls. Passages lead West (towards an altar), North (towards a throne), and east (towards a command post). Stairs lead down near the back.");

    }

    public Room createBloodiedShrine() {
        return new Room("Bloodied Shrine",
                "This side chamber is dominated by a blood-stained altar dedicated to the Absolute. Tools of dubious purpose lie nearby. A sturdy door leads further west.");

    }

    public Room createGutsQuarter() {
        return new Room("Gut's Quarters",
                "This appears to be Priestess Gut's private room, containing a cot and some personal effects. There might be a cell nearby.");

    }

    public Room createRagzlinsThroneRoom() {
        return new Room("Ragzlin's Throne Room",
                "A crude throne sits atop a platform at the far end of this large chamber where Dror Ragzlin holds court. To the side lies the unsettling corpse of a Mind Flayer, apparently dissected.");

    }

    public Room createMintharasCommandPost() {
        return new Room("Minthara's Command Post",
                "This area seems more organized, likely used by the drow commander Minthara. Maps and plans are scattered accross a table. A rickery bridge crosses a chasm to the east leading to her post.");

    }

    public Room createMakeshiftPrison() {
        return new Room("Makeshift Prison",
                "Crudge cages line this section fo the Sanctum. You see a widely travellerd wizard Volo trapped in a cage, guarded by a goblin names Gribbo.");

    }

    public Room createWorgPens() {
        return new Room("Worg Pens",
                "Filthy cages line the walls of this dark, foul-smell cave. Growls echo around you. In one of the larger cages, you spot the shape of a large, weary-looking wood elf man. There are some tormentor goblins around him - this must be the Halsin, the person you have to rescue him from this prison...");
    }
    // TODO: add create room method for other rooms (Spider's Pit, Roah's storage)

}
