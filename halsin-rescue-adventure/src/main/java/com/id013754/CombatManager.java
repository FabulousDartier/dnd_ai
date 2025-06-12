package com.id013754;

public class CombatManager {
    public static boolean performAttackRoll(Combatant attacker, Combatant target, String attackType) {
        if (attacker == null || target == null || attackType == null) {
            System.err.println("Error in performAttackRoll: attacker, enemy, or attackType cannot be null");
            return false;
        }

        int roll = DiceRoller.rollD20();
        int attackRollBonus = 0;
        String abilityForAttack = "str";

        switch (attackType.toLowerCase()) {
            case "greatsword":
            case "mace":
            case "scimitar_goblin":
            case "melee_generic":
            case "staff":
                abilityForAttack = "str";
                break;
            case "longbow":
            case "crossbow":
            case "shortbow":
            case "rapier":
                abilityForAttack = "dex";
                break;
            case "firebolt":
                abilityForAttack = "intl";
                break;
            default:
                System.err.println(
                        "Warning: Unknown attackType '" + attackType + "' in performAttackRoll. Defaulting to str.");
                abilityForAttack = "str";
                break;
        }
        attackRollBonus = attacker.getAbilityModifier(abilityForAttack) + attacker.getProficiencyBonus();
        int totalAttackRoll = roll + attackRollBonus;

        System.out.println("[COMBAT LOGS]: " + attacker.getName() + " rolls " + roll + "(D20) + " + attackRollBonus
                + "(bonus) = " + totalAttackRoll + " to hit AC of " + target.getAC());
        return totalAttackRoll >= target.getAC();
    }

    public static int calculateDamageRoll(Combatant attacker, Combatant target, String attackType) {
        if (attacker == null || target == null || attackType == null) {
            System.err.println("Error in calculateDamageRoll: attacker, target, or attackType cannot be null.");
            return 0;
        }

        int damageRoll = 0;
        int abilityModifierValue = 0;
        String abilityModifierType = "";

        switch (attackType.toLowerCase()) {
            case "greatsword":
                damageRoll = DiceRoller.rollD6() + DiceRoller.rollD6();
                abilityModifierType = "str";
                break;
            case "rapier":
            case "longbow":
                damageRoll = DiceRoller.rollD8();
                abilityModifierType = "dex";
                break;
            case "mace":
            case "staff":
                damageRoll = DiceRoller.rollD6();
                abilityModifierType = "str";
                break;
            case "crossbow":
            case "shortbow":
                damageRoll = DiceRoller.rollD6();
                abilityModifierType = "dex";
                break;
            case "firebolt":
                damageRoll = DiceRoller.rollD10();
                abilityModifierType = "intl";
                break;
            case "melee_generic":
            case "scimitar_goblin":
                damageRoll = DiceRoller.rollD6();
                if (attacker.getAbilityModifier("dex") > attacker.getAbilityModifier("str")) {
                    abilityModifierType = "dex";

                } else {
                    abilityModifierType = "str";
                }
                break;
            default:
                System.err.println("Warning: Unknown attackType '" + attackType + "' in calculateAttackRoll.");
                damageRoll = DiceRoller.rollD4();
                break;
        }

        // Add ability modifier to damage if applicable
        if (!abilityModifierType.isEmpty()) {
            abilityModifierValue = attacker.getAbilityModifier(abilityModifierType);
            damageRoll += abilityModifierValue;
        }

        return damageRoll;
    }

    public static void performHealing(Combatant healer, Combatant target) {
        if (healer == null || target == null) {
            System.err.println("Error in performHealing: healer or target cannot be null.");
            return;
        }

        int healingAmount = DiceRoller.rollD8() + healer.getAbilityModifier("wis");
        if (target instanceof Player) {
            ((Player) target).receiveHealing(healingAmount);
        } else if (target instanceof NPC) {
            ((NPC) target).receiveHealing(healingAmount);
        }

        System.out.println(
                healer.getName() + " casts heal " + healingAmount + "HP "
                        + "on " + target.getName() + "(" + target.getCurrentHP()
                        + "/" + target.getMaxHP() + ")");
    }
}
