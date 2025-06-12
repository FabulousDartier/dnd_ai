package com.id013754;

//Companion NPC inherits NPC class attribute
public class CompanionNPC extends NPC {
    public CompanionNPC(String name, String description, Room startingRoom,
            String race, String characterClass, int level,
            int AC, int maxHP, int str, int dex, int con, int intl, int wis, int cha) {

        super(name, description, startingRoom, race, characterClass,
                level, AC, maxHP, str, dex, con, intl, wis, cha);
    }

    @Override
    public boolean isPlayerControlled() {
        return true;
    }
}
