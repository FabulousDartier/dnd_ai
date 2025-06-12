package com.id013754;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player implements Observer, Combatant {
    private String name;
    private List<Item> inventory;
    private Room currentRoom;

    // --- DnD Attributes ---
    private int STR; // Strength
    private int DEX; // Dexterity
    private int CON; // Constitution
    private int INTL; // Intelligence
    private int WIS; // Wisdom
    private int CHA; // Charisma

    // --- Combat Attributes ---
    private int maxHP;
    private int currentHP;
    private int AC; // Armour Class
    private int proficiencyBonus;

    public Player(String name, Room startingRoom, int AC, int maxHP, int proficiencyBonus,
            int str, int dex, int con, int intl, int wis, int cha) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty.");
        }
        if (startingRoom == null) {
            throw new IllegalArgumentException("Starting room cannot be null");
        }
        if (maxHP < 1) {
            throw new IllegalArgumentException("Player maxHP cannot be less than 1.");
        }
        this.name = name;
        this.inventory = new ArrayList<>();
        this.currentRoom = startingRoom;

        this.STR = str;
        this.DEX = dex;
        this.CON = con;
        this.INTL = intl;
        this.WIS = wis;
        this.CHA = cha;

        this.AC = AC;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.proficiencyBonus = proficiencyBonus;

        if (this.currentRoom != null) {
            this.currentRoom.attach(this);
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void update(String message) {
        System.out.println("[Notification]: " + message);
    }

    // --- Combatant Implementation ---
    @Override
    public int getInitiativeBonus() {
        return NPC.calculateAbilityModifier(this.DEX);
    }

    @Override
    public int getCurrentHP() {
        return currentHP;
    }

    @Override
    public int getMaxHP() {
        return maxHP;
    }

    @Override
    public int getAC() {
        return AC;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount < 0)
            return;
        this.currentHP -= amount;
        if (this.currentHP < 0) {
            this.currentHP = 0;
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
    public int getProficiencyBonus() {
        return proficiencyBonus;
    }

    @Override
    public int getAbilityModifier(String abilityName) {
        switch (abilityName.toLowerCase()) {
            case "str" -> {
                return NPC.calculateAbilityModifier(this.STR);
            }
            case "dex" -> {
                return NPC.calculateAbilityModifier(this.DEX);
            }
            case "con" -> {
                return NPC.calculateAbilityModifier(this.CON);
            }
            case "intl" -> {
                return NPC.calculateAbilityModifier(this.INTL);
            }
            case "wis" -> {
                return NPC.calculateAbilityModifier(this.WIS);
            }
            case "cha" -> {
                return NPC.calculateAbilityModifier(this.CHA);
            }
            default -> throw new IllegalArgumentException("Invalid ability name.");
        }
    }

    @Override
    public boolean isPlayerControlled() {
        return true;
    }

    // Getters for Ability Scores
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
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room newRoom) {
        if (newRoom == null) {
            throw new IllegalArgumentException("New room cannot be null");
        }

        if (this.currentRoom != null) {
            this.currentRoom.detach(this);
        }

        this.currentRoom = newRoom;
        this.currentRoom.attach(this);
    }

    public boolean addItem(Item item) {
        if (item != null) {
            return inventory.add(item);
        }
        return false;
    }

    public boolean removeItem(Item item) {
        return inventory.remove(item);
    }

    public Item getItemByName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }

        String lowerCaseItemName = itemName.toLowerCase();
        for (Item item : inventory) {
            if (item.getName().toLowerCase().equals(lowerCaseItemName)) {
                return item;
            }
        }

        return null;
    }

    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    public String getInventoryDescription() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        } else {
            String items = inventory.stream()
                    .map(Item::getName) // Assumes Item::toString returns the name
                    .collect(Collectors.joining(", "));
            return "You are carrying: " + items + ".";
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", currentRoom=" + (currentRoom != null ? currentRoom.getName() : "null") +
                ", inventory= " + inventory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
