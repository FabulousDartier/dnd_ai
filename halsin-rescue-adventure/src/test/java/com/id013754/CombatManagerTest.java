package com.id013754;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class CombatManagerTest {

    @Mock
    private Combatant mockAttacker;
    @Mock
    private Combatant mockTarget;
    @Mock
    private Combatant mockHealer;

    // --- performAttackRoll Tests ---

    @Test
    void performAttackRoll_nullParameters_shouldReturnFalseAndPrintError() {
        assertFalse(CombatManager.performAttackRoll(null, mockTarget, "greatsword"));
        assertFalse(CombatManager.performAttackRoll(mockAttacker, null, "greatsword"));
        assertFalse(CombatManager.performAttackRoll(mockAttacker, mockTarget, null));
    }

    @Test
    void performAttackRoll_hitScenario_greatsword() {
        when(mockAttacker.getAbilityModifier("str")).thenReturn(3);
        when(mockAttacker.getProficiencyBonus()).thenReturn(2);
        when(mockTarget.getAC()).thenReturn(15);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD20).thenReturn(12);
            boolean result = CombatManager.performAttackRoll(mockAttacker, mockTarget, "greatsword");
            assertTrue(result, "Attack should hit.");
        }
    }

    @Test
    void performAttackRoll_missScenario_longbow() {
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(2);
        when(mockAttacker.getProficiencyBonus()).thenReturn(2);
        when(mockTarget.getAC()).thenReturn(18);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD20).thenReturn(10);
            boolean result = CombatManager.performAttackRoll(mockAttacker, mockTarget, "longbow");
            assertFalse(result, "Attack should miss.");
        }
    }

    @Test
    void performAttackRoll_variousAttackTypes_shouldUseCorrectAbilityModifier() {
        when(mockAttacker.getProficiencyBonus()).thenReturn(2);
        when(mockTarget.getAC()).thenReturn(10);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD20).thenReturn(10);

            // STR attacks
            when(mockAttacker.getAbilityModifier("str")).thenReturn(1);
            assertTrue(CombatManager.performAttackRoll(mockAttacker, mockTarget, "mace"));
            assertTrue(CombatManager.performAttackRoll(mockAttacker, mockTarget, "staff"));
            // "scimitar_goblin" and "melee_generic" always use STR for attack roll; covered
            // by specific tests.
            clearInvocations(mockAttacker); // Reset for next set of stubs if needed

            // DEX attacks
            when(mockAttacker.getAbilityModifier("dex")).thenReturn(1);
            assertTrue(CombatManager.performAttackRoll(mockAttacker, mockTarget, "rapier"));
            assertTrue(CombatManager.performAttackRoll(mockAttacker, mockTarget, "crossbow"));
            clearInvocations(mockAttacker);

            // INT attacks
            when(mockAttacker.getAbilityModifier("intl")).thenReturn(1);
            assertTrue(CombatManager.performAttackRoll(mockAttacker, mockTarget, "firebolt"));
        }
    }

    @Test
    void performAttackRoll_unknownAttackType_defaultsToStrengthAndHits() {
        when(mockAttacker.getAbilityModifier("str")).thenReturn(1);
        when(mockAttacker.getProficiencyBonus()).thenReturn(2);
        when(mockTarget.getAC()).thenReturn(10);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD20).thenReturn(8);
            assertTrue(CombatManager.performAttackRoll(mockAttacker, mockTarget, "unknown_weapon"));
        }
    }

    @Test
    void performAttackRoll_scimitarGoblin_alwaysUsesStrForAttackRoll() {
        // This test confirms that "scimitar_goblin" uses STR for the attack roll,
        // regardless of DEX value, as per CombatManager.performAttackRoll logic.
        when(mockAttacker.getProficiencyBonus()).thenReturn(2);
        when(mockTarget.getAC()).thenReturn(10);
        when(mockAttacker.getAbilityModifier("str")).thenReturn(3); // STR is used
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(5); // DEX is higher, but should NOT be used for attack
                                                                    // roll

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD20).thenReturn(5); // 5 (roll) + 3 (STR) + 2 (prof) = 10. Should hit
                                                                      // AC 10.
            boolean result = CombatManager.performAttackRoll(mockAttacker, mockTarget, "scimitar_goblin");
            assertTrue(result, "Scimitar attack roll using STR should hit.");
            verify(mockAttacker, times(1)).getAbilityModifier("str");
            verify(mockAttacker, never()).getAbilityModifier("dex"); // DEX should not be consulted for attack roll
        }
    }

    @Test
    void performAttackRoll_meleeGeneric_alwaysUsesStrForAttackRoll() {
        when(mockAttacker.getProficiencyBonus()).thenReturn(2);
        when(mockTarget.getAC()).thenReturn(10);
        when(mockAttacker.getAbilityModifier("str")).thenReturn(3); // STR is used
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(5); // DEX is higher, but should NOT be used for attack
                                                                    // roll

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD20).thenReturn(5); // 5 (roll) + 3 (STR) + 2 (prof) = 10. Should hit
                                                                      // AC 10.
            boolean result = CombatManager.performAttackRoll(mockAttacker, mockTarget, "melee_generic");
            assertTrue(result, "Melee generic attack roll using STR should hit.");
            verify(mockAttacker, times(1)).getAbilityModifier("str");
            verify(mockAttacker, never()).getAbilityModifier("dex");
        }
    }

    // --- calculateDamageRoll Tests ---
    @Test
    void calculateDamageRoll_nullParameters_shouldReturnZeroAndPrintError() {
        assertEquals(0, CombatManager.calculateDamageRoll(null, mockTarget, "greatsword"));
        assertEquals(0, CombatManager.calculateDamageRoll(mockAttacker, null, "greatsword"));
        assertEquals(0, CombatManager.calculateDamageRoll(mockAttacker, mockTarget, null));
    }

    @Test
    void calculateDamageRoll_greatsword() {
        when(mockAttacker.getAbilityModifier("str")).thenReturn(2);
        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD6).thenReturn(3).thenReturn(4);
            int damage = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "greatsword");
            assertEquals(7 + 2, damage);
        }
    }

    @Test
    void calculateDamageRoll_rapier() {
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(1);
        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD8).thenReturn(5);
            int damage = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "rapier");
            assertEquals(5 + 1, damage);
        }
    }

    @Test
    void calculateDamageRoll_firebolt() {
        when(mockAttacker.getAbilityModifier("intl")).thenReturn(3);
        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD10).thenReturn(6);
            int damage = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "firebolt");
            assertEquals(6 + 3, damage);
        }
    }

    @Test
    void calculateDamageRoll_unknownAttackType_defaultsToD4WithNoModifier() {
        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD4).thenReturn(2);
            int damage = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "unknown_weapon");
            assertEquals(2, damage);
            verify(mockAttacker, never()).getAbilityModifier(anyString());
        }
    }

    @Test
    void calculateDamageRoll_scimitarGoblin_usesDexWhenHigher() {
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(3); // DEX is higher
        when(mockAttacker.getAbilityModifier("str")).thenReturn(1);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD6).thenReturn(4);
            int damageDex = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "scimitar_goblin");
            assertEquals(4 + 3, damageDex);

            verify(mockAttacker, times(2)).getAbilityModifier("dex"); // Called in 'if' AND for damage
            verify(mockAttacker, times(1)).getAbilityModifier("str"); // Called in 'if'
        }
    }

    @Test
    void calculateDamageRoll_scimitarGoblin_usesStrWhenHigherOrEqual() {
        when(mockAttacker.getAbilityModifier("str")).thenReturn(3); // STR is higher or equal
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(1);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD6).thenReturn(4);
            int damageStr = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "scimitar_goblin");
            assertEquals(4 + 3, damageStr);

            verify(mockAttacker, times(2)).getAbilityModifier("str"); // Called in 'if' AND for damage
            verify(mockAttacker, times(1)).getAbilityModifier("dex"); // Called in 'if'
        }
    }

    @Test
    void calculateDamageRoll_meleeGeneric_usesDexWhenHigher() {
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(4); // DEX is higher
        when(mockAttacker.getAbilityModifier("str")).thenReturn(2);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD6).thenReturn(3);
            int damageDex = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "melee_generic");
            assertEquals(3 + 4, damageDex);

            verify(mockAttacker, times(2)).getAbilityModifier("dex");
            verify(mockAttacker, times(1)).getAbilityModifier("str");
        }
    }

    @Test
    void calculateDamageRoll_meleeGeneric_usesStrWhenHigherOrEqual() {
        when(mockAttacker.getAbilityModifier("str")).thenReturn(4); // STR is higher or equal
        when(mockAttacker.getAbilityModifier("dex")).thenReturn(2);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD6).thenReturn(3);
            int damageStr = CombatManager.calculateDamageRoll(mockAttacker, mockTarget, "melee_generic");
            assertEquals(3 + 4, damageStr);

            verify(mockAttacker, times(2)).getAbilityModifier("str");
            verify(mockAttacker, times(1)).getAbilityModifier("dex");
        }
    }

    // --- performHealing Tests ---
    @Test
    void performHealing_nullParameters_shouldNotProceedAndPrintError() {
        CombatManager.performHealing(null, mockTarget);
        verify(mockTarget, never()).receiveHealing(anyInt());

        CombatManager.performHealing(mockHealer, null);
    }

    @Test
    void performHealing_onPlayerTarget() {
        Player mockPlayerTarget = mock(Player.class);
        when(mockHealer.getAbilityModifier("wis")).thenReturn(3);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD8).thenReturn(5);
            CombatManager.performHealing(mockHealer, mockPlayerTarget);
            verify(mockPlayerTarget, times(1)).receiveHealing(8);
        }
    }

    @Test
    void performHealing_onNpcTarget() {
        NPC mockNpcTarget = mock(NPC.class);
        when(mockHealer.getAbilityModifier("wis")).thenReturn(2);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD8).thenReturn(4);
            CombatManager.performHealing(mockHealer, mockNpcTarget);
            verify(mockNpcTarget, times(1)).receiveHealing(6);
        }
    }

    @Test
    void performHealing_onGenericCombatantTarget_thatIsNotPlayerOrNpc() {
        Combatant otherCombatant = mock(Combatant.class);
        when(mockHealer.getAbilityModifier("wis")).thenReturn(1);

        try (MockedStatic<DiceRoller> mockedDiceRoller = mockStatic(DiceRoller.class)) {
            mockedDiceRoller.when(DiceRoller::rollD8).thenReturn(3);
            CombatManager.performHealing(mockHealer, otherCombatant);
            verify(otherCombatant, never()).receiveHealing(anyInt());
        }
    }
}
