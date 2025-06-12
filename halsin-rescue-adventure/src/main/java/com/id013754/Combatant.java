package com.id013754;

public interface Combatant {
    String getName();

    int getInitiativeBonus();

    int getCurrentHP();

    int getMaxHP();

    int getAC();

    void takeDamage(int amount);

    boolean isDefeated();

    int getProficiencyBonus();

    int getAbilityModifier(String abilityName);

    boolean isPlayerControlled();

    void receiveHealing(int amount);

    Room getCurrentRoom();
}
