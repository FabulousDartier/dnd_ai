package com.id013754.factories;

import com.id013754.CompanionNPC;
import com.id013754.NPC;
import com.id013754.Room;

public class NPCFactory {
    public CompanionNPC createAstarion(Room startingRoom) {
        return new CompanionNPC(
                "Astarion",
                "A pale elf with sharp features, silver hair, and an air of aristocratic disdain. He moves with a predatory grace.",
                startingRoom,
                "High Elf", "Rogue", 4, 15, 27,
                10, 17, 12, 13, 13, 14);
    }

    public CompanionNPC createShadowheart(Room startingRoom) {
        return new CompanionNPC(
                "Shadowheart",
                "A half-elf cleric with dark, braided hair and a guarded expression. She clutches a mysterious artifact",
                startingRoom,
                "High Half-Elf", "Cleric (Trickery)", 4, 16, 27,
                12, 14, 13, 10, 17, 10);

    }

    public CompanionNPC createGale(Room startingRoom) {
        return new CompanionNPC(
                "Gale",
                "A human wizard with a friendly demeanor, a pendant for arcane knowledge, and a somewhat troubling secret concerning a Netherese Orb in his chest.",
                startingRoom,
                "Human", "Wizard", 4, 12, 22,
                9, 14, 13, 17, 10, 12);
    }

    public CompanionNPC createLaezel(Room startingRoom) {
        return new CompanionNPC(
                "Lae'zel",
                "A stern and disciplined Githyanki warrior, her yellow eyes sharp and her demeanor uncompromising. She is dressed in typical Githyanki armor.",
                startingRoom,
                "Githyanki", "Fighter", 4, 17, 36,
                16, 12, 14, 11, 10, 8);
    }

    public NPC createGoblinWarrior(Room startingRoom, String name) {
        return new NPC(
                name, "A snarling goblin warrior wielding a crude scimitar and shield.", startingRoom,
                "Goblin", "Warrior", 3, 15, 7,
                10, 14, 10, 10, 8, 8);
    }

    public NPC createGoblinArcher(Room startingRoom, String name) {
        return new NPC(
                name, "A wiry goblin archer with a nasty grin, nocking an arrow.", startingRoom,
                "Goblin", "Archer", 3, 13, 7,
                8, 14, 10, 10, 10, 8);
    }

    public NPC createTormentingGoblin(Room startingRoom, String name) {
        return new NPC(
                name, "A young, jeering goblin poking at the caged bear with a stick.", startingRoom,
                "Goblin", "Tormentor", 1, 12, 5,
                8, 12, 10, 7, 7, 6);
    }

    public NPC createBeastmasterGoblin(Room startingRoom, String name) {
        return new NPC(
                name, "A goblin with a crude whip, seemingly in charge of the Worgs and the torment.", startingRoom,
                "Goblin", "Beastmaster", 3, 14, 11,
                12, 14, 12, 9, 10, 7);
    }

    public CompanionNPC createHalsin(Room startingRoom, String name) {
        return new CompanionNPC(name,
                "A large, powerfully built wood elf with a calm but weary demeanor. He looks relieved to see someone other than goblins. There are some kid goblins throwing rocks at him. This must be druid Halsin, the person who you have to rescue from this camp.",
                startingRoom,
                "Wood Elf", "Druid",
                4, 14, 31, 10, 14, 14, 12, 17, 10);
    }

    public NPC createPriestessGut(Room startingRoom) {
        return new NPC(
                "Priestess Gut",
                "A bloated goblin female adorned with crude holy symbols of the Absolute. She has a disturbingly serene smile and eyes that gleam with fanaticism.",
                startingRoom,
                "Goblin", "Priestess (War Domain)", 5, 16, 32, // Race, Class, Level, AC, maxHP
                14, 10, 14, 12, 15, 13 // STR, DEX, CON, INT, WIS, CHA
        );
    }

    public NPC createDrorRagzlin(Room startingRoom) {
        return new NPC(
                "Dror Ragzlin",
                "A large, imposing hobgoblin with a guttural voice and a crown of jagged metal. He brandishes a massive warhammer and surveys his throne room with cruel authority.",
                startingRoom,
                "Goblin", "Warlord", 5, 17, 42,
                18, 12, 16, 10, 11, 14);
    }

    public NPC createMinthara(Room startingRoom) {
        return new NPC(
                "Minthara",
                "A drow commander with striking white hair and an aura of cold, ruthless efficiency. She wears dark, well-maintained armor and carries a wicked-looking mace.",
                startingRoom,
                "Goblin", "Commander (Paladin/Cleric)", 5, 17, 27,
                13, 15, 13, 12, 14, 16);
    }
}
