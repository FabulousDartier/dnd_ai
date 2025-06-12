package com.id013754;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.id013754.factories.NPCFactory;

@ExtendWith(MockitoExtension.class) // Initialize mocks
class NPCFactoryTest {

    private NPCFactory npcFactory;

    @Mock
    private Room mockStartingRoom; // Mocked Room dependency

    @BeforeEach
    void setUp() {
        npcFactory = new NPCFactory();
    }

    @Test
    void createAstarion_shouldReturnCorrectCompanionNPC() {
        CompanionNPC astarion = npcFactory.createAstarion(mockStartingRoom);
        assertNotNull(astarion, "Astarion should not be null.");
        assertEquals("Astarion", astarion.getName());
        assertEquals(
                "A pale elf with sharp features, silver hair, and an air of aristocratic disdain. He moves with a predatory grace.",
                astarion.getDescription());
        assertEquals(mockStartingRoom, astarion.getCurrentRoom());
        assertEquals("High Elf", astarion.getRace());
        assertEquals("Rogue", astarion.getCharacterClass());
        assertEquals(4, astarion.getLevel());
        assertEquals(15, astarion.getAC());
        assertEquals(27, astarion.getMaxHP());
        assertEquals(10, astarion.getSTR());
        assertEquals(17, astarion.getDEX());
        assertEquals(12, astarion.getCON());
        assertEquals(13, astarion.getINT());
        assertEquals(13, astarion.getWIS());
        assertEquals(14, astarion.getCHA());
        assertTrue(astarion.isPlayerControlled(), "Astarion should be player controlled.");
        verify(mockStartingRoom).addNPC(astarion); // Verify interaction with mock room
    }

    @Test
    void createShadowheart_shouldReturnCorrectCompanionNPC() {
        CompanionNPC shadowheart = npcFactory.createShadowheart(mockStartingRoom);
        assertNotNull(shadowheart, "Shadowheart should not be null.");
        assertEquals("Shadowheart", shadowheart.getName());
        assertEquals(
                "A half-elf cleric with dark, braided hair and a guarded expression. She clutches a mysterious artifact",
                shadowheart.getDescription());
        assertEquals(mockStartingRoom, shadowheart.getCurrentRoom());
        assertEquals("High Half-Elf", shadowheart.getRace());
        assertEquals("Cleric (Trickery)", shadowheart.getCharacterClass());
        assertEquals(4, shadowheart.getLevel());
        assertEquals(16, shadowheart.getAC());
        assertEquals(27, shadowheart.getMaxHP());
        assertTrue(shadowheart.isPlayerControlled(), "Shadowheart should be player controlled.");
        verify(mockStartingRoom).addNPC(shadowheart);
    }

    @Test
    void createGale_shouldReturnCorrectCompanionNPC() {
        CompanionNPC gale = npcFactory.createGale(mockStartingRoom);
        assertNotNull(gale, "Gale should not be null.");
        assertEquals("Gale", gale.getName());
        assertEquals(
                "A human wizard with a friendly demeanor, a pendant for arcane knowledge, and a somewhat troubling secret concerning a Netherese Orb in his chest.",
                gale.getDescription());
        assertEquals(mockStartingRoom, gale.getCurrentRoom());
        assertEquals("Human", gale.getRace());
        assertEquals("Wizard", gale.getCharacterClass());
        assertEquals(4, gale.getLevel());
        assertEquals(12, gale.getAC());
        assertEquals(22, gale.getMaxHP());
        assertTrue(gale.isPlayerControlled(), "Gale should be player controlled.");
        verify(mockStartingRoom).addNPC(gale);
    }

    @Test
    void createLaezel_shouldReturnCorrectCompanionNPC() {
        CompanionNPC laezel = npcFactory.createLaezel(mockStartingRoom);
        assertNotNull(laezel, "Lae'zel should not be null.");
        assertEquals("Lae'zel", laezel.getName());
        assertEquals(
                "A stern and disciplined Githyanki warrior, her yellow eyes sharp and her demeanor uncompromising. She is dressed in typical Githyanki armor.",
                laezel.getDescription());
        assertEquals(mockStartingRoom, laezel.getCurrentRoom());
        assertEquals("Githyanki", laezel.getRace());
        assertEquals("Fighter", laezel.getCharacterClass());
        assertEquals(4, laezel.getLevel());
        assertEquals(17, laezel.getAC());
        assertEquals(36, laezel.getMaxHP());
        assertTrue(laezel.isPlayerControlled(), "Lae'zel should be player controlled.");
        verify(mockStartingRoom).addNPC(laezel);
    }

    @Test
    void createGoblinWarrior_shouldReturnCorrectNPC() {
        String warriorName = "Gobbo the Smasher";
        NPC goblinWarrior = npcFactory.createGoblinWarrior(mockStartingRoom, warriorName);
        assertNotNull(goblinWarrior, "Goblin Warrior should not be null.");
        assertEquals(warriorName, goblinWarrior.getName());
        assertEquals("A snarling goblin warrior wielding a crude scimitar and shield.", goblinWarrior.getDescription());
        assertEquals(mockStartingRoom, goblinWarrior.getCurrentRoom());
        assertEquals("Goblin", goblinWarrior.getRace());
        assertEquals("Warrior", goblinWarrior.getCharacterClass());
        assertEquals(3, goblinWarrior.getLevel());
        assertEquals(15, goblinWarrior.getAC());
        assertEquals(7, goblinWarrior.getMaxHP());
        assertFalse(goblinWarrior.isPlayerControlled(), "Goblin Warrior should not be player controlled.");
        verify(mockStartingRoom).addNPC(goblinWarrior);
    }

    @Test
    void createGoblinArcher_shouldReturnCorrectNPC() {
        String archerName = "Snipa";
        NPC goblinArcher = npcFactory.createGoblinArcher(mockStartingRoom, archerName);
        assertNotNull(goblinArcher, "Goblin Archer should not be null.");
        assertEquals(archerName, goblinArcher.getName());
        assertEquals("A wiry goblin archer with a nasty grin, nocking an arrow.", goblinArcher.getDescription());
        assertEquals("Goblin", goblinArcher.getRace());
        assertEquals("Archer", goblinArcher.getCharacterClass());
        assertFalse(goblinArcher.isPlayerControlled());
        verify(mockStartingRoom).addNPC(goblinArcher);
    }

    @Test
    void createTormentingGoblin_shouldReturnCorrectNPC() {
        String tormentorName = "Pokey";
        NPC tormentingGoblin = npcFactory.createTormentingGoblin(mockStartingRoom, tormentorName);
        assertNotNull(tormentingGoblin, "Tormenting Goblin should not be null.");
        assertEquals(tormentorName, tormentingGoblin.getName());
        assertEquals("A young, jeering goblin poking at the caged bear with a stick.",
                tormentingGoblin.getDescription());
        assertEquals("Goblin", tormentingGoblin.getRace());
        assertEquals("Tormentor", tormentingGoblin.getCharacterClass());
        assertEquals(5, tormentingGoblin.getMaxHP());
        assertFalse(tormentingGoblin.isPlayerControlled());
        verify(mockStartingRoom).addNPC(tormentingGoblin);
    }

    @Test
    void createBeastmasterGoblin_shouldReturnCorrectNPC() {
        String beastmasterName = "Whipsnarl";
        NPC beastmasterGoblin = npcFactory.createBeastmasterGoblin(mockStartingRoom, beastmasterName);
        assertNotNull(beastmasterGoblin, "Beastmaster Goblin should not be null.");
        assertEquals(beastmasterName, beastmasterGoblin.getName());
        assertEquals("A goblin with a crude whip, seemingly in charge of the Worgs and the torment.",
                beastmasterGoblin.getDescription());
        assertEquals("Goblin", beastmasterGoblin.getRace());
        assertEquals("Beastmaster", beastmasterGoblin.getCharacterClass());
        assertEquals(11, beastmasterGoblin.getMaxHP());
        assertFalse(beastmasterGoblin.isPlayerControlled());
        verify(mockStartingRoom).addNPC(beastmasterGoblin);
    }

    @Test
    void createHalsin_shouldReturnCorrectCompanionNPC() {
        String halsinName = "Halsin";
        CompanionNPC halsin = npcFactory.createHalsin(mockStartingRoom, halsinName);
        assertNotNull(halsin, "Halsin should not be null.");
        assertEquals(halsinName, halsin.getName());
        assertEquals(
                "A large, powerfully built wood elf with a calm but weary demeanor. He looks relieved to see someone other than goblins. There are some kid goblins throwing rocks at him. This must be druid Halsin, the person who you have to rescue from this camp.",
                halsin.getDescription());
        assertEquals("Wood Elf", halsin.getRace());
        assertEquals("Druid", halsin.getCharacterClass());
        assertEquals(31, halsin.getMaxHP());
        assertTrue(halsin.isPlayerControlled(), "Halsin (as CompanionNPC) should be player controlled.");
        verify(mockStartingRoom).addNPC(halsin);
    }

    @Test
    void createPriestessGut_shouldReturnCorrectNPC() {
        NPC priestessGut = npcFactory.createPriestessGut(mockStartingRoom);
        assertNotNull(priestessGut, "Priestess Gut should not be null.");
        assertEquals("Priestess Gut", priestessGut.getName());
        assertEquals(
                "A bloated goblin female adorned with crude holy symbols of the Absolute. She has a disturbingly serene smile and eyes that gleam with fanaticism.",
                priestessGut.getDescription());
        assertEquals("Goblin", priestessGut.getRace());
        assertEquals("Priestess (War Domain)", priestessGut.getCharacterClass());
        assertEquals(5, priestessGut.getLevel());
        assertEquals(16, priestessGut.getAC());
        assertEquals(32, priestessGut.getMaxHP());
        assertFalse(priestessGut.isPlayerControlled());
        verify(mockStartingRoom).addNPC(priestessGut);
    }

    @Test
    void createDrorRagzlin_shouldReturnCorrectNPC() {
        NPC drorRagzlin = npcFactory.createDrorRagzlin(mockStartingRoom);
        assertNotNull(drorRagzlin, "Dror Ragzlin should not be null.");
        assertEquals("Dror Ragzlin", drorRagzlin.getName());
        assertEquals(
                "A large, imposing hobgoblin with a guttural voice and a crown of jagged metal. He brandishes a massive warhammer and surveys his throne room with cruel authority.",
                drorRagzlin.getDescription());
        assertEquals("Goblin", drorRagzlin.getRace()); 
        assertEquals("Warlord", drorRagzlin.getCharacterClass());
        assertEquals(42, drorRagzlin.getMaxHP());
        assertFalse(drorRagzlin.isPlayerControlled());
        verify(mockStartingRoom).addNPC(drorRagzlin);
    }

    @Test
    void createMinthara_shouldReturnCorrectNPC() {
        NPC minthara = npcFactory.createMinthara(mockStartingRoom);
        assertNotNull(minthara, "Minthara should not be null.");
        assertEquals("Minthara", minthara.getName());
        assertEquals(
                "A drow commander with striking white hair and an aura of cold, ruthless efficiency. She wears dark, well-maintained armor and carries a wicked-looking mace.",
                minthara.getDescription());
        assertEquals("Goblin", minthara.getRace()); 
        assertEquals("Commander (Paladin/Cleric)", minthara.getCharacterClass());
        assertEquals(27, minthara.getMaxHP());
        assertFalse(minthara.isPlayerControlled());
        verify(mockStartingRoom).addNPC(minthara);
    }
}
