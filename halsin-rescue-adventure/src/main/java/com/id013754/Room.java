package com.id013754;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Room implements Subject {
    private final String name;
    private final String description;
    private final Map<String, Room> exits;
    private final List<Item> items;
    private final List<Observer> observers;
    private final List<NPC> npcs;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    // ---Basic Getters---
    public String getName() {
        return name;

    }

    public String getDescription() {
        return description;
    }

    // Method to provide player surround details (items, room description, players
    // and exits)
    public String getSurroundingDetail(Player playerLooking) {
        StringBuilder detailedDesc = new StringBuilder();

        detailedDesc.append("--- ").append(this.name).append(" ---\n");
        detailedDesc.append("Summary: ");
        detailedDesc.append(this.description).append("\n");

        // List items in the room;
        if (!items.isEmpty()) {
            String itemNames = items.stream().map(Item::getName).collect(Collectors.joining(", "));
            detailedDesc.append("You see here: ").append(itemNames).append(".\n");
        } else {
            detailedDesc.append("There are no items of interest here. \n");
        }

        // List other players in the room
        List<String> otherPlayerNames = new ArrayList<>();
        for (Observer obs : observers) {
            if (!obs.equals(playerLooking)) {
                otherPlayerNames.add(obs.getName());
            }
        }
        if (!otherPlayerNames.isEmpty()) {
            detailedDesc.append("Also here (players): ").append(String.join(", ", otherPlayerNames)).append(".\n");
        }

        if (!otherPlayerNames.isEmpty()) {
            detailedDesc.append("Also here: ").append(String.join(", ", otherPlayerNames)).append(".\n");
        }

        // List NPCs
        if (!npcs.isEmpty()) {
            List<String> npcNames = npcs.stream().map(NPC::getName).collect(Collectors.toList());
            if (!npcNames.isEmpty()) {
                detailedDesc.append("You also see: ").append(String.join(", ", npcNames)).append(".\n");
            }
        }

        // List available exits
        if (!exits.isEmpty()) {
            List<String> exitDescription = new ArrayList<>();
            for (Map.Entry<String, Room> entry : exits.entrySet()) {
                String direction = entry.getKey();
                Room destination = entry.getValue();

                // Add a description like "enter (to Goblin Comp couryeard)"
                exitDescription.add(direction + " (to " + destination.getName() + ")");
            }
            detailedDesc.append("Exit: [").append(String.join(", ", exitDescription)).append("]");
        } else {
            detailedDesc.append("There are no obvious exists. ");
        }

        return detailedDesc.toString();
    }

    // ---Exit Management---;
    public void addExit(String direction, Room neighbor) {
        if (direction == null || direction.trim().isEmpty() || neighbor == null) {
            System.err.println("Warning: Attempted to add invalid exit to room '" + this.name + "'");
            return;
        }
        exits.put(direction.toLowerCase(), neighbor);
    }

    public Set<String> getExitDirections() {
        return Collections.unmodifiableSet(exits.keySet());
    }

    public Room getExit(String direction) {
        if (direction == null)
            return null;
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExitsList() {
        return exits;
    }

    // ---Item Management---
    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    public boolean removeItem(Item item) {
        boolean isRemoved = items.remove(item);
        // notifyObserver() is called when a player take an item
        return isRemoved;
    }

    public Item getItemByName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }

        String lowerCaseItemName = itemName.toLowerCase();
        for (Item item : items) {
            if (item.getName().toLowerCase().equals(lowerCaseItemName)) {
                return item;
            }
        }

        return null;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    // ---NPC Management---
    public void addNPC(NPC npc) {
        if (npc != null && !npcs.contains(npc)) {
            npcs.add(npc);
        }
    }

    public boolean removeNPC(NPC npc) {
        return npcs.remove(npc);
    }

    public List<NPC> getNPCs() {
        return new ArrayList<>(npcs);
    }

    public List<Player> getPlayers() {
        // Since players are observer so we return observer
        List<Player> currentPlayers = new ArrayList<>();
        for (Observer obs : observers) {
            if (obs instanceof Player) {
                currentPlayers.add((Player) obs);
            }
        }
        return currentPlayers;
    }

    public NPC getNPCByName(String npcName) {
        if (npcName == null || npcName.trim().isEmpty()) {
            return null;
        }
        String lowerCaseNPCName = npcName.toLowerCase();
        for (NPC npc : npcs) {
            if (npc.getName().toLowerCase().equals(lowerCaseNPCName)) {
                return npc;
            }
        }
        return null;
    }

    // ---Implement Subject---
    @Override
    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(String message, Observer originator) {
        List<Observer> observersToNotify = new ArrayList<>(observers);

        for (Observer obs : observersToNotify) {
            if (originator == null || !obs.equals(originator)) {
                obs.update(message);
            }
        }
    }

    @Override
    public String toString() {
        List<String> exitInfo = new ArrayList<>();
        for (Map.Entry<String, Room> entry : exits.entrySet()) {
            exitInfo.add(entry.getKey() + "->" + entry.getValue().getName());
        }
        return "Room{" +
                "name=" + name + '\'' +
                ", exits=" + String.join(", ", exitInfo) +
                '}';
    }

}
