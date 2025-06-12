package com.id013754;

// this is the base class that generate NPC types such as companions, enemies, merchants

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NPC implements Combatant {
    private String name;
    private String description;
    private Room currentRoom;

    // DnD Attributes
    private String race;
    private String characterClass;
    private int level;
    private int AC; // Armour Class
    private List<Item> equipment;

    // DnD Ability Scores
    private int STR; // Strength
    private int DEX; // Dexterity
    private int CON; // Constitution
    private int INTL; // Intelligence
    private int WIS; // Wisdom
    private int CHA; // Charisma

    private int maxHP;
    private int currentHP;
    private int proficiencyBonus;

    public NPC(String name, String description, Room startingRoom,
            String race, String characterClass, int level,
            int AC, int maxHP, int str, int dex, int con, int intl, int wis, int cha) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("NPC name cannot be null or empty.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("NPC description cannot be null or empty");
        }
        if (startingRoom == null) {
            throw new IllegalArgumentException("NPC starting room cannot be null.");
        }

        if (race == null || race.trim().isEmpty()) {
            throw new IllegalArgumentException("NPC race cannot be null");
        }

        if (characterClass == null || characterClass.trim().isEmpty()) {
            throw new IllegalArgumentException("NPC class cannot be null or empty");
        }

        if (level < 1) {
            throw new IllegalArgumentException("NPC level cannot less than 1");
        }

        if (maxHP < 1) {
            throw new IllegalArgumentException("NPC maxHP cannot be less than 1.");
        }

        this.name = name;
        this.description = description;
        this.currentRoom = startingRoom;

        this.race = race;
        this.characterClass = characterClass;
        this.level = level;
        this.AC = AC;
        this.equipment = new ArrayList<>();

        this.STR = str;
        this.DEX = dex;
        this.CON = con;
        this.INTL = intl;
        this.WIS = wis;
        this.CHA = cha;

        this.maxHP = maxHP;
        this.currentHP = maxHP;

        if (level >= 1 && level <= 4) {
            this.proficiencyBonus = 2;
        } else if (level >= 5 && level <= 8) {
            this.proficiencyBonus = 3;
        } else {
            this.proficiencyBonus = 2;
        }

        if (this.currentRoom != null) {
            this.currentRoom.addNPC(this);
        }
    }

    // Getters for basic info;
    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    };

    @Override
    public Room getCurrentRoom() {
        return currentRoom;
    }

    // Getters for DnD attributes;
    public String getRace() {
        return race;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int getAC() {
        return AC;
    }

    // Getters for Ability Scores;
    public int getSTR() {
        return STR;
    }

    public int getDEX() {
        return DEX;
    }

    public int getCON() {
        return CON;
    }

    public int getINT() {
        return INTL;
    }

    public int getWIS() {
        return WIS;
    }

    public int getCHA() {
        return CHA;
    }

    @Override
    public int getMaxHP() {
        return maxHP;
    };

    @Override
    public int getCurrentHP() {
        return currentHP;
    };

    @Override
    public int getProficiencyBonus() {
        return proficiencyBonus;
    };

    public static int calculateAbilityModifier(int abilityScore) {
        return (int) Math.floor((abilityScore - 10) / 2.0);
    }

    @Override
    public int getAbilityModifier(String abilityName) {
        switch (abilityName.toLowerCase()) {
            case "str" -> {
                return calculateAbilityModifier(this.STR);
            }
            case "dex" -> {
                return calculateAbilityModifier(this.DEX);
            }
            case "con" -> {
                return calculateAbilityModifier(this.CON);
            }
            case "intl" -> {
                return calculateAbilityModifier(this.INTL);
            }
            case "wis" -> {
                return calculateAbilityModifier(this.WIS);
            }
            case "cha" -> {
                return calculateAbilityModifier(this.CHA);
            }
            default -> throw new IllegalArgumentException("Invalid ability name.");
        }
    }

    public int getDEXModifier() {
        return calculateAbilityModifier(this.DEX);
    }

    @Override
    public int getInitiativeBonus() {
        return getDEXModifier();
    }

    @Override
    public void takeDamage(int amount) {
        if (amount < 0)
            return;
        this.currentHP -= amount;
        if (this.currentHP < 0) {
            this.currentHP = 0;
        }

        System.out
                .println(this.name + " takes " + amount + " damage! Current HP: " + this.currentHP + "/" + this.maxHP);
        if (isDefeated()) {
            System.out.println(this.name + " has been defeated!");
        }
    }

    @Override
    public void receiveHealing(int amount) {
        if (amount < 0)
            return;
        this.currentHP += amount;
        if (this.currentHP > this.maxHP) {
            this.currentHP = maxHP;
        }
    }

    @Override
    public boolean isDefeated() {
        return this.currentHP <= 0;
    }

    @Override
    public boolean isPlayerControlled() {
        return false;
    }

    // Equipment Management;
    public boolean addItemToEquipment(Item item) {
        return this.equipment.add(item);
    };

    public boolean removeItemFromEquipment(Item item) {
        return this.equipment.remove(item);
    };

    public List<Item> getEquipment() {
        return new ArrayList<>(equipment);
    }

    public String getEquipmentDescription() {
        if (equipment.isEmpty()) {
            return this.name + " is not carrying any notable equipment.";
        } else {
            String items = equipment.stream()
                    .map(Item::getName)
                    .collect(Collectors.joining(", "));
            return this.name + " has " + items + ".";
        }
    };

    public void setCurrentRoom(Room newRoom) {
        if (newRoom == null) {
            throw new IllegalArgumentException("New room for NPC cannot be null.");
        }

        if (this.currentRoom != null) {
            this.currentRoom.removeNPC(this);
        }

        this.currentRoom = newRoom;
        this.currentRoom.addNPC(this);
    };

    @Override
    public String toString() {
        return name + " (" + race + " " + characterClass + ", Lvl " + level + ")";
    };

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NPC npc = (NPC) o;
        return name.equals(npc.getName());
    };

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
