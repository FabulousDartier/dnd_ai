package com.id013754;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.id013754.factories.ItemFactory;
import com.id013754.factories.NPCFactory;
import com.id013754.factories.RoomFactory;

/* Implement Singleton to enture only one instance of Game exits
 * Uses factories to create game objects (items, rooms, player)
 */

public class Game {
    // --- THIS IS THE MAIN ENTRY POINT TO START GAME ---
    public static void main(String[] args) {
        Game gameInstance = Game.getInstance();
        gameInstance.startGame();
    }

    // ANSI Color Codes
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\u001B[1m";

    // Private static final instance of the Game class itself
    private static final Game instance = new Game();

    // --- Game State Fields ---
    private final Map<String, Room> rooms;
    private final Map<String, Player> players;
    private List<CompanionNPC> companions;
    private boolean gameOver;
    private final Scanner scanner;
    private RoomFactory roomFactory;
    private ItemFactory itemFactory;
    private NPCFactory npcFactory;
    private AI_DM_Client ai_DM_Client;

    // --- Combat State ---
    private boolean isInCombat;
    private List<Combatant> turnOrder;
    private List<NPC> currentEnemiesInCombat;
    private int currentTurnIndex;

    // --- Quest Flags ---
    private boolean isHalsinFreed; // After clear all goblins and 3 bosses
    private boolean isGutDefeated;
    private boolean isRagzlinDefeated;
    private boolean isMintharaDefeated;
    private boolean isGoblinAggroGlobally;
    private boolean isGutsQuartersDoorUnlocked;

    private Game() {
        this.rooms = new HashMap<>();
        this.players = new HashMap<>(); // Human players
        this.companions = new ArrayList<>();
        this.gameOver = false;
        this.scanner = new Scanner(System.in);
        this.roomFactory = new RoomFactory();
        this.itemFactory = new ItemFactory();
        this.npcFactory = new NPCFactory();
        this.isGutsQuartersDoorUnlocked = false;
        this.isHalsinFreed = false;
        this.isGutDefeated = false;
        this.isRagzlinDefeated = false;
        this.isMintharaDefeated = false;
        this.isGoblinAggroGlobally = false;
        this.isInCombat = false;
        this.turnOrder = new ArrayList<>();
        this.currentEnemiesInCombat = new ArrayList<>();
        this.currentTurnIndex = 0;

        // Instantiate AI Client
        try {
            this.ai_DM_Client = new AI_DM_Client();
        } catch (RuntimeException e) {
            System.err.println("FATAL: Failed to initizlize AI Client.");
            this.ai_DM_Client = null;
        }

        initializeGameWorld();

    }

    // Public static method to provide access to single instance Game
    public static Game getInstance() {
        return instance;
    }

    /*
     * Initializies the game world by creating room using RoomFactory,
     * creating items using ItemFactory, NPCs, setting up exits
     */
    @SuppressWarnings("unused")
    private void initializeGameWorld() {
        System.out.println(ANSI_CYAN + "Initializing game world..." + ANSI_RESET);

        // --- Room Creation ---
        Room entrance = (Room) roomFactory.createGoblinCampEntrance();
        Room courtyard = roomFactory.createGoblinCampCourtyard();
        Room shatteredSanctum = roomFactory.createShatterSanctumMainHall();
        Room gutAltarRoom = roomFactory.createBloodiedShrine();
        Room gutPrivateRoom = roomFactory.createGutsQuarter();
        Room ragzlinThrone = roomFactory.createRagzlinsThroneRoom();
        Room mintharaPost = roomFactory.createMintharasCommandPost();
        Room voloCageArea = roomFactory.createMakeshiftPrison();
        Room worgPens = roomFactory.createWorgPens();

        // --- Add Room to map ---
        rooms.put(entrance.getName().toLowerCase(), entrance);
        rooms.put(courtyard.getName().toLowerCase(), courtyard);
        rooms.put(shatteredSanctum.getName().toLowerCase(), shatteredSanctum);
        rooms.put(gutAltarRoom.getName().toLowerCase(), gutAltarRoom);
        rooms.put(gutPrivateRoom.getName().toLowerCase(), gutPrivateRoom);
        rooms.put(ragzlinThrone.getName().toLowerCase(), ragzlinThrone);
        rooms.put(mintharaPost.getName().toLowerCase(), mintharaPost);
        rooms.put(voloCageArea.getName().toLowerCase(), voloCageArea);
        rooms.put(worgPens.getName().toLowerCase(), worgPens);
        System.out.println(ANSI_GREEN + "Rooms created: " + ANSI_RESET + rooms.size());

        // --- Item Creation and Placement ---
        Item healthPotion = itemFactory.createHealthPotion();
        Item goblinScimitar = itemFactory.createGoblinScimitar();
        Item rock = itemFactory.createRock();
        Item gutsKey = itemFactory.createGutsSanctumKey();
        Item thievesTools = itemFactory.createThievesTools();
        Item smokyPowder = itemFactory.createSmokyPowderSatchel();
        Item goblinScribblings = itemFactory.createGoblinScribblings();
        Item worgPenKey = itemFactory.createWorgPenKey();

        courtyard.addItem(rock);
        courtyard.addItem(goblinScimitar);
        gutPrivateRoom.addItem(healthPotion);
        gutPrivateRoom.addItem(worgPenKey);
        courtyard.addItem(healthPotion);
        mintharaPost.addItem(healthPotion);
        gutPrivateRoom.addItem(healthPotion);
        voloCageArea.addItem(healthPotion);
        worgPens.addItem(healthPotion);
        entrance.addItem(healthPotion);

        mintharaPost.addItem(gutsKey); // Key is in Minthara's Post for now
        shatteredSanctum.addItem(thievesTools);
        courtyard.addItem(smokyPowder);
        ragzlinThrone.addItem(goblinScribblings);
        System.out.println(ANSI_GREEN + "Items created and placed." + ANSI_RESET);

        // --- Exit Setup ---
        // Entrance <-> Courtyard
        entrance.addExit("enter", courtyard);
        courtyard.addExit("leave", entrance);
        // Courtyard <-> Shattered Sanctum
        courtyard.addExit("enter sanctum", shatteredSanctum);
        shatteredSanctum.addExit("leave sanctum", courtyard);
        // Shattered Sanctum connections
        shatteredSanctum.addExit("go west", gutAltarRoom);
        shatteredSanctum.addExit("go north", ragzlinThrone);
        shatteredSanctum.addExit("go east", mintharaPost);
        shatteredSanctum.addExit("approach cages", voloCageArea);
        shatteredSanctum.addExit("open ornate door", worgPens); // To Word Pens (Eastern side)
        // Connections back from side areas to Shattered Sanctum
        gutAltarRoom.addExit("go east", shatteredSanctum);
        ragzlinThrone.addExit("go south", shatteredSanctum);
        mintharaPost.addExit("go west", shatteredSanctum);
        voloCageArea.addExit("leave cages", shatteredSanctum);
        worgPens.addExit("leave pens", shatteredSanctum);
        // Connection within Gut's area
        gutAltarRoom.addExit("enter quarter", gutPrivateRoom);
        gutPrivateRoom.addExit("leave quarter", gutAltarRoom);
        System.out.println(ANSI_GREEN + "Exits configured." + ANSI_RESET);

        // --- Player setup ---
        Player startingPlayer = new Player("Astarion", courtyard, 16, 25, 2, 10, 17, 12, 13, 13, 14);
        players.put(startingPlayer.getName().toLowerCase(), startingPlayer);
        startingPlayer.addItem(healthPotion);

        // --- Companion NPC Setup ---
        CompanionNPC shadowheart = npcFactory.createShadowheart(courtyard);
        CompanionNPC gale = npcFactory.createGale(courtyard);
        CompanionNPC laezel = npcFactory.createLaezel(courtyard);
        shadowheart.addItemToEquipment(itemFactory.createHealthPotion());
        shadowheart.addItemToEquipment(itemFactory.createHealthPotion());
        gale.addItemToEquipment(itemFactory.createHealthPotion());
        gale.addItemToEquipment(itemFactory.createHealthPotion());
        laezel.addItemToEquipment(itemFactory.createHealthPotion());
        laezel.addItemToEquipment(itemFactory.createHealthPotion());

        companions.add(shadowheart);
        companions.add(gale);
        companions.add(laezel);

        System.out.println(
                ANSI_GREEN + "Companions created: " + ANSI_RESET + companions.size() + " (Shadowheart, Gale, Lae'zel)");

        // --- Set up Halsin cage ---
        NPC tormentorGoblin1 = npcFactory.createTormentingGoblin(worgPens, "Goblin Runt One");
        NPC tormentorGoblin2 = npcFactory.createTormentingGoblin(worgPens, "Goblin Runt Two");
        NPC beastmasterGoblin = npcFactory.createBeastmasterGoblin(worgPens, "Beastmaster Zog");
        CompanionNPC halsin = npcFactory.createHalsin(worgPens, "Halsin");

        // --- Place Goblin Leaders ---
        NPC priestessGut = npcFactory.createPriestessGut(gutAltarRoom);
        NPC goblinGut1 = npcFactory.createGoblinArcher(gutAltarRoom, "Tracker Klek");
        NPC goblinGut2 = npcFactory.createGoblinWarrior(gutAltarRoom, "Warrior Hozra");
        NPC goblinGut3 = npcFactory.createBeastmasterGoblin(gutAltarRoom, "Beastmaster Mezzka");
        NPC goblinGut4 = npcFactory.createGoblinArcher(gutAltarRoom, "Tracker Klek");

        NPC drorRagzlin = npcFactory.createDrorRagzlin(ragzlinThrone);
        NPC goblinDror1 = npcFactory.createGoblinWarrior(ragzlinThrone, "Warrior Bal");
        NPC goblinDror2 = npcFactory.createGoblinWarrior(ragzlinThrone, "Warrior Bil");
        NPC goblinDror4 = npcFactory.createGoblinWarrior(ragzlinThrone, "Warrior Bol");
        NPC goblinDror3 = npcFactory.createBeastmasterGoblin(gutAltarRoom, "Beastmaster Rancer");

        NPC minthara = npcFactory.createMinthara(mintharaPost);
        NPC goblinMinthara1 = npcFactory.createGoblinArcher(mintharaPost, "Tracker Nik-Nuk");
        NPC goblinMinthara2 = npcFactory.createGoblinArcher(mintharaPost, "Sharp-eye Aggy");
        NPC goblinMinthara3 = npcFactory.createBeastmasterGoblin(mintharaPost, "Beastmaster Roo");
        NPC goblinMinthara4 = npcFactory.createGoblinWarrior(mintharaPost, "Warrior Sluck");

        // Shattered Sanctum NPC
        NPC goblinSS_01 = npcFactory.createGoblinWarrior(shatteredSanctum, "Warrior Booyag");
        NPC goblinSS_02 = npcFactory.createGoblinWarrior(shatteredSanctum, "Brawler Gurd");
        NPC goblinSS_03 = npcFactory.createGoblinWarrior(shatteredSanctum, "Warrior Puce");
        NPC goblinSS_04 = npcFactory.createGoblinWarrior(shatteredSanctum, "Guard Gurgon");
        NPC goblinSS_05 = npcFactory.createGoblinWarrior(shatteredSanctum, "Brawler Olak");

        // Courtyard NPC
        NPC goblinCourtyard_01 = npcFactory.createBeastmasterGoblin(courtyard, "Beastmaster Crak");
        NPC goblinCourtyard_02 = npcFactory.createGoblinArcher(courtyard, "Tracker Grikka");
        NPC goblinCourtyard_03 = npcFactory.createGoblinWarrior(courtyard, "Warrior Zok");
        NPC goblinCourtyard_04 = npcFactory.createGoblinArcher(courtyard, "Tracker Krozz");
        NPC goblinCourtyard_05 = npcFactory.createGoblinWarrior(courtyard, "Warrior Grikka");

        NPC gribbo = npcFactory.createTormentingGoblin(voloCageArea, "Gribbo");

        System.out.println(ANSI_GREEN + "NPCs placed in the world." + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Game world initialization completed." + ANSI_RESET);
        System.out.println("You are playing as " + ANSI_BLUE + startingPlayer.getName() + ANSI_RESET);
        System.out.println("Current location: " + ANSI_YELLOW + startingPlayer.getCurrentRoom().getName() + ANSI_RESET);
    }

    public void startGame() {
        delay(1000);
        System.out.println(
                ANSI_BOLD + ANSI_PURPLE + "\nWELCOME TO THE BALDUR'S GATE 3 TEXT-BASED ADVENTURE!" + ANSI_RESET);

        Player currentPlayer = players.get("astarion");
        if (currentPlayer == null) {
            System.err.println("Error: Starting player 'Astarion' not found! Check initilization again");
            return;
        }

        // Check if AI Client failed to initilize
        if (this.ai_DM_Client == null) {
            System.err.println("Warning: AI Dungeon Master is not available (failed to load API key?).");
        }

        checkForCombatInitiation(currentPlayer, currentPlayer.getCurrentRoom());
        if (!isInCombat) {
            delay(1000);
            System.out.println("\n" + currentPlayer.getCurrentRoom().getSurroundingDetail(currentPlayer));
        }

        // Main game loop
        while (!this.gameOver) {
            if (this.isInCombat) {

            } else {
                System.out.println("\nType 'h' for more commands.");
                System.out.print("> ");
                String input = this.scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("quit")) {
                    this.gameOver = true;
                    System.out.println("Quitting game");
                    break;
                }

                try {
                    this.processExplorationCommand(currentPlayer, input);
                } catch (Exception e) {
                    System.err
                            .println("[ERROR] An expected error occured during processing command: " + e.getMessage());
                }

            }
            if (this.gameOver) {
                break;
            }
            if (!isInCombat && !this.gameOver) {
                checkWinCondition(currentPlayer);
            }
        }
        this.scanner.close();
        System.out.println("Game has ended.");
    }

    private void checkForCombatInitiation(Player player, Room room) {
        if (isInCombat)
            return;

        List<NPC> hostilesInRoom = new ArrayList<>();
        if (isGoblinAggroGlobally) {
            hostilesInRoom = room.getNPCs().stream().filter(
                    npc -> npc.getRace().equals("Goblin") && !(npc instanceof CompanionNPC) && !npc.isDefeated())
                    .collect(Collectors.toList());
            if (!hostilesInRoom.isEmpty()) {
                System.out.println("The goblins in " + room.getName() + "spot you and attack. You prepare to fight!");
            }
        }

        if (!hostilesInRoom.isEmpty()) {
            startCombat(player, hostilesInRoom);
        }
    }

    private void processExplorationCommand(Player player, String command) {
        // Split the command into two parts: command and arguement. Example: "go north"
        // -> ["go", "north"]
        String lowerCaseCommand = command.toLowerCase();
        String[] parts = lowerCaseCommand.split("\\s", 2);
        String commandWord = parts[0];
        String arguments = (parts.length > 1) ? parts[1] : "";

        Room currentRoom = player.getCurrentRoom();
        Room potentialNextRoom = currentRoom.getExit(lowerCaseCommand);
        if (potentialNextRoom != null) {
            boolean tryToMove = true;
            if (currentRoom.getName().equalsIgnoreCase("Bloodied Shrine") &&
                    lowerCaseCommand.equals("enter quarter") && // Assuming "enter quarters" is the key
                    potentialNextRoom.getName().equalsIgnoreCase("Gut's Quarters") &&
                    !isGutsQuartersDoorUnlocked) {
                System.out.println("The door to Gut's Quarters is firmly locked.");
                tryToMove = false;
            }
            if (tryToMove) {
                movePlayer(player, lowerCaseCommand);
            }
            return;
        }

        switch (commandWord) {
            case "s":
                System.out.println("--- Game Summary ---");
                System.out.print(
                        "You are Astarion, a vampire spawn rogue, recently escaped from a mind flayer nautiloid that crashed. "
                                + "Like your companions - Shadowheart, Gale, and Lae'zel - you have a parasitic mind flayer tadpole in your head, a ticking clock threatening to transform you into a mind flayer."
                                + "Your urgent goal is to find a cure. Rumours and desperate hope have led your party to learn of a powerful druid named Halsin, who is said to possess knowledge that could help remove these parasites."
                                + "Your search has brought you to the outskirts of a dangerous Goblin Camp, where you've discovered that Halsin himself has been captured by the goblins and is being held prisoner."
                                + "Rescuing him is now your immediate priority, as he may be your best chance at survival.\n");
                break;
            case "h":
                System.out.println("Available commands: ");
                System.out.println("s: To get game summary and objective.");
                System.out.println("look or l: Ask AI Dungeon Master to decribe your current location.");
                System.out
                        .println("list: Shows a detailed list of room contents and exits (standard description).");
                System.out.println("<exit_phrase> (e.g., enter sanctum, go west, leave)");
                System.out.println("inventory or i: Shows items you are carrying.");
                System.out.println("take <item_name>: Picks up an item from the room.");
                System.out.println("drop <item_name>: Drops an item from your inventory into the room.");
                System.out.println(
                        "talk to <npc_name> or ask <npc_name> or talk <npc_name>: Starts a conversation with an NPC.");
                System.out.println(
                        "describe <available_item_name>: show description of the item (AI Offline) or use AI Dungeon Master to describe it (AI Online).");
                System.out.println("attack: Initiates combat with hostile NPCs.");
                System.out
                        .println("help halsin: Specific action to rescue Halsin in a specific room.");
                System.out.println("use <item> [on <target>] (e.g. use Gut's Sanctum Key)");
                System.out.println("steal <item_name>: perform sleight of hands check (1D20 + DEX bonus).");
                System.out.println("quit: Exits the game.");
                break;
            case "list":
                System.out.println(currentRoom.getSurroundingDetail(player));
                break;
            case "look":
            case "l":
                // Give the player the detailed description including items, other players, and
                // exits;
                String description;
                if (ai_DM_Client != null) {
                    // List of exits
                    List<String> exitsList = new ArrayList<>();
                    for (Map.Entry<String, Room> entry : currentRoom.getExitsList().entrySet()) {
                        String direction = entry.getKey();
                        Room room = entry.getValue();
                        String outputString = direction + " leading to the " + room.getName();
                        exitsList.add(outputString);
                    }
                    String allExits = String.join(", ", exitsList);

                    // List of items
                    // @formatter:off
                        List<String> itemNames = currentRoom.getItems().stream().map(Item::getName).collect(Collectors.toList());
                        
                        String itemsString = itemNames.isEmpty() ? "nothing notable" : String.join(", ", itemNames);
                        // List of Real Player and NPCs
                        List<String> characterLists = new ArrayList<>();
                        currentRoom.getPlayers().stream().filter(p -> !p.equals(player)).map(Player::getName).forEach(characterLists::add);
                        currentRoom.getNPCs().stream().map(NPC::getName).forEach(characterLists::add);
                        String charactersString = characterLists.isEmpty() ? "no one here" : String.join(", ", characterLists);
                        // @formatter:on

                    String prompt = "You are the Dungeon Master describing a room for a player in a text-based adventure game. The player "
                            + player.getName() + " is in the "
                            + currentRoom.getName() + ". " +
                            " Provide a brief, atmospheric description (2-3 sentences) of the current scene. " +
                            " Then, clearly list the items you see. " +
                            " Reference info: Room base description is " + currentRoom.getDescription() + ". " +
                            " Items to list: [" + itemsString + "] " +
                            " Others present: [" + charactersString + "]. " +
                            " Available exits: [" + allExits + "]";
                    // DEBUG: System.out.println(prompt);
                    description = ai_DM_Client.generateContent(prompt);
                    if (description == null) {
                        System.out.println(
                                "The AI Dungeon Master seems lost in thought (offline) ... proceed using default description.");
                        description = "\n" + currentRoom.getSurroundingDetail(player);
                    } else {
                        description = description.trim();
                    }
                } else {
                    description = "\n" + currentRoom.getSurroundingDetail(player);
                }
                System.out.println("\"" + description + "\"");
                break;
            case "inventory":
            case "i":
                System.out.println(player.getInventoryDescription());
                break;
            case "take":
                if (!arguments.isEmpty()) {
                    if (arguments.equalsIgnoreCase("Gut's Sanctum Key")) {
                        System.out.println(
                                "Goblins are not happy to see you taking Gut's Sanctum Key. Maybe you have to perform your sleight of hands instead (use command 'steal' <item_name).");
                    } else {
                        takeItem(player, arguments);
                    }
                } else {
                    System.out.println("Take what?");
                }
                break;
            case "drop":
                if (!arguments.isEmpty()) {
                    dropItem(player, arguments);
                } else {
                    System.out.println("Drop what?");
                }
                break;
            case "talk":
            case "ask":
                if (!arguments.isEmpty()) {
                    String targetNPCName = arguments;
                    if (arguments.toLowerCase().startsWith("to ")) {
                        if (arguments.length() > 3) {
                            targetNPCName = arguments.substring(3).trim();
                        } else {
                            System.out.println("Talk to whom?");
                            break;
                        }
                    }
                    if (targetNPCName.isEmpty()) {
                        System.out.println("Talk to whom?");
                        break;
                    } else {
                        talkToNPC(player, targetNPCName);
                    }
                } else {
                    System.out.println("Talk to whom?");
                }
                break;
            case "describe":
                if (!arguments.isEmpty()) {
                    describeItem(player, arguments);
                } else {
                    System.out.println("Describe what item?");
                }
                break;
            case "attack":
                if (isInCombat) {
                    System.out.println("You are already in combat! Use combat commands.");
                    break;
                }
                List<NPC> hostilesToAttack;
                hostilesToAttack = currentRoom.getNPCs().stream()
                        .filter(npc -> !npc.isDefeated() && !(npc instanceof CompanionNPC))
                        .filter(npc -> (npc.getRace().equalsIgnoreCase("Goblin")))
                        .collect(Collectors.toList());
                if (hostilesToAttack.isEmpty()) {
                    System.out.println(
                            "There's no one immediately hostile to attack here with a general command.");
                    break;
                }
                System.out.println("You prepare to attack the hostile threats!");

                if (!hostilesToAttack.isEmpty()) {
                    startCombat(player, hostilesToAttack);
                }
                break;
            case "help":
                if (arguments.equalsIgnoreCase("halsin")) {
                    delay(1000);
                    Item worgPenKey = player.getItemByName("Worg Pen Key");
                    if (currentRoom.getName().equalsIgnoreCase("Worg Pens")) {
                        if (worgPenKey != null && worgPenKey.getName().equalsIgnoreCase("Worg Pen Key")) {
                            System.out.println(
                                    "You retrieved the Worg Pen Key. You heard a click as you turned the key. You can see the relief from his eye.");
                            if (isHalsinFreed) {
                                System.out.println(
                                        "Halsin is already free and waiting for you to deal with the goblin leaders.");
                                break;
                            }
                            List<NPC> hostilesInRoom = currentRoom.getNPCs().stream()
                                    .filter(npc -> npc.getRace().equalsIgnoreCase("goblin") && !npc.isDefeated())
                                    .collect(Collectors.toList());
                            if (!hostilesInRoom.isEmpty()) {
                                System.out.println("You move to help the caged druid! The goblins turn on you");
                                startCombat(player, hostilesInRoom);
                            } else {
                                System.out.println(
                                        "The surrounding goblins are dealt with. Halsin looks at you expectantly from his cage.");
                            }

                        } else {
                            System.out.println("Halsin whisperd: \""
                                    + "get the key from Gut's Quarter Room without getting caught then come back here to help me out of cage."
                                    + "\"");
                        }
                    } else {
                        System.out.println("There is no one named Halsin in this room to help");
                    }
                } else {
                    System.out.println("You don't see Halsin here to help.");
                }
                break;
            case "use":
                if (!arguments.isEmpty()) {
                    String[] useArgs = arguments.split("\\s+on\\s+", 2);
                    String itemName = useArgs[0].trim();
                    String targetName = (useArgs.length > 1) ? useArgs[1].trim() : null;
                    handleUseItem(player, itemName, targetName, null);
                } else {
                    System.out.println("Use what? (e.g., use health potion, use Gut's Sanctum Key)");
                }
                break;
            case "steal":
                if (!arguments.isEmpty()) {
                    stealItem(player, arguments);
                } else {
                    System.out.println("Steal what? ");
                }
                break;
            default:
                System.out.println("I don't understand your '" + command + "'. Try 'help' for commands");
                break;
        }

    }

    public void describeItem(Player player, String itemName) {
        if (player == null || itemName == null || itemName.trim().isEmpty()) {
            System.out.println("Warning: Invalid player or item name.");
            return;
        }

        Room currentRoom = player.getCurrentRoom();
        if (currentRoom == null) {
            System.out.println(player.getName() + " is in invalid room.");
            return;
        }

        Item itemToDescribe = currentRoom.getItemByName(itemName);
        if (itemToDescribe != null) {
            String itemDescription;
            if (ai_DM_Client != null) {
                String prompt = String.format(
                        "You are an expert Dungeon Master describing an item for a player in a text-based adventure game. "
                                +
                                "The player '%s' is in Goblin Camp, specifically in the room '%s'. " +
                                "They are looking closely at an item named '%s'." +
                                "Its basic description is: '%s'." +
                                "Provide a more vivid, atmospheric, and detailed description of this item (2-3 sentences). "
                                +
                                "Focus on sensory details, its apparent condition, or any interesting features not immediately obvious from the basic description."
                                +
                                "Do not repeat the item's name or basic description in your response, but expand on it.",
                        player.getName(),
                        player.getCurrentRoom(),
                        itemToDescribe,
                        itemToDescribe.getDescription());

                String aiDescription = ai_DM_Client.generateContent(prompt);
                if (aiDescription != null && !aiDescription.trim().isEmpty()) {
                    itemDescription = aiDescription.trim();
                } else {
                    // Fallback if AI is offline or return empty
                    System.out.println(
                            "The AI Dungeon Master ponders, but offers no further insight on the " + itemToDescribe);
                    itemDescription = itemToDescribe.getName() + ": " + itemToDescribe.getDescription();
                }
            } else {
                // if AI is offline then default desc
                itemDescription = itemToDescribe.getName() + ": " + itemToDescribe.getDescription();
            }
            System.out.println("\"" + itemDescription + "\"");
        } else {
            System.out.println("There is no '" + itemName + "' here to describe.");
        }
    }

    public void stealItem(Player mainPlayer, String itemName) {
        delay(1000);
        System.out.println("You are attempting to steal the " + itemName + "...");
        delay(1000);
        System.out.println("Difficult Class: 15");
        System.out.print("Press Enter to roll: \n");

        scanner.nextLine();

        int diceRoll = DiceRoller.rollD20(); // This will generate a number from 1 to 20
        int bonus = mainPlayer.getAbilityModifier("dex"); // Get the DEX bonus

        int totalRoll = diceRoll + bonus;

        delay(500);
        System.out.println("You roll: ");
        delay(1000);
        System.out.print("(1D20): " + diceRoll);
        delay(1000);
        System.out.print(" + (DEX bonus): " + bonus);
        delay(1000);
        System.out.print(" = " + totalRoll + "\n");

        if (totalRoll >= 15) {
            takeItem(mainPlayer, itemName);
        } else {
            System.out.println("The goblin guards caught you stealing. It's time to return the item.\n");
        }
    }

    public void startCombat(Player mainPlayer, List<NPC> enemiesInRoom) {
        if (isInCombat) {
            System.out.println("Already in combat!");
            return;
        }

        delay(2000);
        System.out.println("\n--- COMBAT STARTS! ---");
        delay(1000);
        this.isInCombat = true;
        this.currentEnemiesInCombat.clear();
        this.turnOrder.clear();
        this.turnOrder.add(mainPlayer);

        for (CompanionNPC companion : this.companions) {
            if (companion.getCurrentRoom().equals(mainPlayer.getCurrentRoom()) && !companion.isDefeated()) {
                this.turnOrder.add(companion);
            }
        }

        for (NPC enemy : enemiesInRoom) {
            if (!enemy.isDefeated()) {
                this.turnOrder.add(enemy);
                this.currentEnemiesInCombat.add(enemy);
            }
        }

        if (this.currentEnemiesInCombat.isEmpty()) {
            System.out.println("No valid enemies to fight.");
            endCombat(mainPlayer, true);
            return;
        }

        Map<Combatant, Integer> initiativeRolls = new HashMap<>();
        for (Combatant c : this.turnOrder) {
            int roll = DiceRoller.rollD4() + c.getInitiativeBonus();
            initiativeRolls.put(c, roll);
        }

        this.turnOrder.sort(Comparator.comparingInt(initiativeRolls::get).reversed()); // Sort descending order of the
                                                                                       // initiative rolls

        System.out.println("\nTurn Order:");
        for (int i = 0; i < this.turnOrder.size(); i++) {
            Combatant c = this.turnOrder.get(i);
            System.out.println((i + 1) + ". " + c.getName() + " (Initiative: " + initiativeRolls.get(c) + ")");

        }
        this.currentTurnIndex = 0;
        processNextCombatTurn();
    }

    private void processNextCombatTurn() {
        if (!isInCombat)
            return;

        boolean allEnemiesDefeated = currentEnemiesInCombat.stream().allMatch(Combatant::isDefeated);
        boolean allPlayerSideDefeated = turnOrder.stream().filter(Combatant::isPlayerControlled)
                .allMatch(Combatant::isDefeated);
        if (allEnemiesDefeated) {
            endCombat(getPlayer("astarion"), true);
            return;
        }
        if (allPlayerSideDefeated) {
            endCombat(getPlayer("astarion"), false);
            return;
        }

        Combatant currentCombatant = turnOrder.get(currentTurnIndex);
        if (currentCombatant.isDefeated()) {
            advanceTurn();
            return;
        }

        if (currentCombatant.isPlayerControlled()) {
            // Player or player-controlled companion's turn
            delay(2000);
            System.out.println("\nAvailable Targets:");
            List<NPC> attackableEnemies = new ArrayList<>();
            int targetNum = 1;
            for (NPC enemy : currentEnemiesInCombat) {
                if (!enemy.isDefeated()) {
                    System.out.println(targetNum + ". " + enemy.getName() + " (HP: " + enemy.getCurrentHP() + "/"
                            + enemy.getMaxHP() + ")");
                    attackableEnemies.add(enemy);
                    targetNum++;
                }
            }

            List<Combatant> possibleAlliesList = turnOrder.stream()
                    .filter(c -> (c instanceof Player || c instanceof CompanionNPC) && !c.isDefeated())
                    .collect(Collectors.toList());

            delay(1000);
            System.out.println("Available allies for healing: ");
            int allyNum = 1;
            for (Combatant ally : possibleAlliesList) {
                System.out.println(allyNum + ". " + ally.getName() + " (HP: " + ally.getCurrentHP() + "/"
                        + ally.getMaxHP() + ")");
                allyNum++;
            }

            if (attackableEnemies.isEmpty() && currentEnemiesInCombat.stream().anyMatch(e -> !e.isDefeated())) {
                System.out.println("No targets left!");
            } else if (attackableEnemies.isEmpty() && currentEnemiesInCombat.stream().allMatch(Combatant::isDefeated)) {
                endCombat(getPlayer("astarion"), true);
                return;
            }

            System.out.print(currentCombatant.getName() + "'s Actions" + "(" + currentCombatant.getCurrentHP() + "/"
                    + currentCombatant.getMaxHP() + ")" + ": ");
            String charName = currentCombatant.getName().toLowerCase();
            switch (charName) {
                case "astarion":
                    System.out.print(
                            "shoot(Longbow) <num>, cast firebolt <num>, attack(Rapier) <num>, use health potion on <ally_num>, ");
                    break;
                case "gale":
                    System.out.print(
                            "cast firebolt <num>, shoot (Crossbow) <num> , attack(Staff) <num>, use health potion on <ally_num>, ");
                    break;
                case "lae'zel":
                case "laezel":
                    System.out.print(
                            "attack(Greatsword) <num>, shoot(Crossbow) <num>, use health potion on <ally_num>, ");
                    break;
                case "shadowheart":
                    System.out.print(
                            "cast heal <ally_num>, attack(Mace) <num>, shoot(Shortbow) <num>, use health potion on <ally_num>, ");
                    break;
                default:
                    System.out.print("attack <num>, use health potion on <ally_num>, ");
                    break;
            }
            System.out.print("pass\n");

            System.out.print("[COMBAT COMMAND]: ");
            String input = scanner.nextLine().trim();

            boolean turnCompleted = processCombatCommand(input, currentCombatant,
                    attackableEnemies, possibleAlliesList);

            if (turnCompleted) {
                allEnemiesDefeated = currentEnemiesInCombat.stream().allMatch(Combatant::isDefeated);
                if (allEnemiesDefeated) {
                    endCombat(getPlayer("Astarion"), true);
                } else {
                    allPlayerSideDefeated = turnOrder.stream().filter(Combatant::isPlayerControlled)
                            .allMatch(Combatant::isDefeated);
                    if (allPlayerSideDefeated) {
                        endCombat(getPlayer("Astarion"), false);
                    } else if (isInCombat) {
                        advanceTurn();
                    }
                }
                advanceTurn();
            } else {
                if (isInCombat) {
                    processNextCombatTurn();
                }
            }
        } else if (currentCombatant instanceof NPC) {
            executeEnemyTurn((NPC) currentCombatant);
            if (isInCombat) {
                advanceTurn();

            }
        }
    }

    private boolean processCombatCommand(String command, Combatant currentActor,
            List<NPC> availableEnemyTargets, List<Combatant> availableAllyTargetsList) {
        if (!isInCombat || currentActor.isDefeated())
            return false;

        delay(2000);
        String lowerCaseCommand = command.toLowerCase();
        String[] parts = lowerCaseCommand.split("\\s+");
        String actionWord = parts[0];
        boolean turnTaken = false;
        String attackType = "melee_generic"; // Default

        switch (actionWord) {
            case "attack":
                if (parts.length < 2) {
                    System.out.println("Attack whom? Specify target number.");
                    return false;
                }
                if (currentActor.getName().equalsIgnoreCase("Astarion"))
                    attackType = "rapier";
                else if (currentActor.getName().equalsIgnoreCase("Lae'zel"))
                    attackType = "greatsword";
                else if (currentActor.getName().equalsIgnoreCase("Shadowheart"))
                    attackType = "mace";
                else if (currentActor.getName().equalsIgnoreCase("Gale"))
                    attackType = "staff";
                try {
                    int targetNumber = Integer.parseInt(parts[1]);
                    if (targetNumber > 0 && targetNumber <= availableEnemyTargets.size()) {
                        NPC target = availableEnemyTargets.get(targetNumber - 1);
                        if (!target.isDefeated()) {
                            performPlayerAttack(currentActor, target, attackType);
                            turnTaken = true;
                        } else
                            System.out.println(target.getName() + " is already defeated.");
                    } else
                        System.out.println("Invalid target number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid target number. Use a number from the list.");
                }
                break;
            case "shoot":
                if (parts.length < 2) {
                    System.out.println("Shoot whom? Specify target number.");
                    return false;
                }
                if (currentActor.getName().equalsIgnoreCase("Astarion"))
                    attackType = "longbow";
                else if (currentActor.getName().equalsIgnoreCase("Lae'zel"))
                    attackType = "crossbow";
                else if (currentActor.getName().equalsIgnoreCase("Gale"))
                    attackType = "crossbow";
                else if (currentActor.getName().equals("Shadowheart")) {
                    attackType = "shortbow";
                } else {
                    System.out.println(currentActor.getName() + " cannot 'shoot' with their current setup.");
                    return false;
                }
                try {
                    int targetNumber = Integer.parseInt(parts[1]);
                    if (targetNumber > 0 && targetNumber <= availableEnemyTargets.size()) {
                        NPC target = availableEnemyTargets.get(targetNumber - 1);
                        if (!target.isDefeated()) {
                            performPlayerAttack(currentActor, target, attackType);
                            turnTaken = true;
                        } else
                            System.out.println(target.getName() + " is already defeated.");
                    } else
                        System.out.println("Invalid target number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid target number. Use a number from the list.");
                }
                break;
            case "cast":
                if (parts.length < 2) {
                    System.out.println("Cast what? And on whom? (ex: 'cast firebolt 1', 'cast heal 2')");
                    return false;
                }
                String spellName = parts[1].toLowerCase();
                int targetNumberSpell = -1;

                if (spellName.equals("firebolt")) {
                    if (parts.length < 3) {
                        System.out.println("Cast firebolt on whom? Specify target number.");
                        return false;
                    }
                    try {
                        targetNumberSpell = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid target number for firebolt.");
                        return false;
                    }

                    if (!currentActor.getName().equalsIgnoreCase("Gale")
                            && !currentActor.getName().equalsIgnoreCase("Astarion")) {
                        System.out.println(currentActor.getName() + " doesn't know Firebolt!");
                        return false;
                    }
                    attackType = "firebolt";
                    if (targetNumberSpell > 0 && targetNumberSpell <= availableEnemyTargets.size()) {
                        NPC target = availableEnemyTargets.get(targetNumberSpell - 1);
                        if (!target.isDefeated()) {
                            performPlayerAttack(currentActor, target, attackType);
                            turnTaken = true;
                        } else
                            System.out.println(target.getName() + " is already defeated.");
                    } else
                        System.out.println("Invalid target number for spell.");

                } else if (spellName.equals("heal")) {
                    if (parts.length < 3) {
                        System.out.println("Cast heal on whom? Specify target number from turn order (or 'self').");
                        return false;
                    }
                    if (!currentActor.getName().equalsIgnoreCase("Shadowheart")) {
                        System.out.println(currentActor.getName() + " cannot cast Heal!");
                        return false;
                    }

                    try {
                        targetNumberSpell = Integer.parseInt(parts[2]);
                        if (!currentActor.getName().equalsIgnoreCase("shadowheart")) {
                            System.out.println(currentActor.getName() + "cannot cast healing");
                            return false;
                        }

                        List<Combatant> possibleAlliesList = turnOrder.stream()
                                .filter(c -> c.isPlayerControlled() && !c.isDefeated())
                                .collect(Collectors.toList());

                        if (targetNumberSpell > 0 && targetNumberSpell <= possibleAlliesList.size()) {
                            Combatant healTarget = possibleAlliesList.get(targetNumberSpell - 1);
                            if (currentActor instanceof Player || currentActor instanceof CompanionNPC) {
                                CombatManager.performHealing((Combatant) currentActor, (Combatant) healTarget);
                                turnTaken = true;
                            } else {
                                System.out.println("Cannot heal for target number " + (targetNumberSpell - 1));
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid target number for heal. Use a number from turn order or 'self'.");
                    }
                } else {
                    System.out.println("Unknown spell: " + spellName);
                    return false;
                }
                break;
            case "pass":
                System.out.println(currentActor.getName() + " passes their turn.");
                turnTaken = true;
                break;
            case "use":
                if (parts.length < 2) {
                    System.out.println("Use what item?");
                    return false;
                }
                String itemName = "";
                String targetIdentifier = null; // Can be an ally number or "self"

                if (lowerCaseCommand.startsWith("use health potion")) {
                    itemName = "health potion";
                    // Check for "on <target>"
                    String tempArgs = lowerCaseCommand.substring("use health potion".length()).trim();
                    if (tempArgs.startsWith("on ")) {
                        targetIdentifier = tempArgs.substring(3).trim();
                    } else if (!tempArgs.isEmpty()) { // e.g. "use health potion self"
                        targetIdentifier = tempArgs;
                    }
                } else {
                    System.out.println("You can only 'use health potion' in combat right now.");
                    return false;
                }
                return handleUseItem(currentActor, itemName, targetIdentifier, availableAllyTargetsList);

            default:
                System.out.println(
                        "Unknown combat command. Available: attack <num>, shoot <num>, cast <spell> <num>, pass");
                turnTaken = false; // Explicitly false for unknown command
                break;
        }
        return turnTaken; // Return whether a valid turn-ending action was taken
    }

    private void performPlayerAttack(Combatant attacker, NPC target, String attackType) {
        switch (attackType) {
            case "crossbow":
            case "longbow":
            case "shortbow":
                System.out.println(attacker.getName() + " shoots at " + target.getName());
                break;
            case "greatsword":
            case "mace":
                System.out.println(attacker.getName() + " attacks at " + target.getName());
                break;
            case "firebolt":
                System.out.println(attacker.getName() + " cast Firebolt at " + target.getName());
                break;
        }

        boolean hit = CombatManager.performAttackRoll(attacker, target, attackType);
        delay(2000);
        if (hit) {
            System.out.println(">>> Hit!");
            int damageRoll = CombatManager.calculateDamageRoll(attacker, target, attackType);
            target.takeDamage(damageRoll);
            System.out
                    .println("[COMBAT LOGS]: " + attacker.getName() + " deals " + damageRoll + " on " + target.getName()
                            + "(" + target.getCurrentHP() + "/" + target.getMaxHP() + ")");
        } else {
            System.out.println(">>> Miss!");
        }
    }

    private void executeEnemyTurn(NPC enemy) {
        delay(2000);
        if (enemy.isDefeated())
            return;

        List<Combatant> possibleTargets = turnOrder.stream().filter(c -> c.isPlayerControlled() && !c.isDefeated())
                .collect(Collectors.toList());
        if (!possibleTargets.isEmpty()) {
            Combatant target = possibleTargets.get(DiceRoller.rollDice(possibleTargets.size()) - 1);
            String enemyAttackType = "melee_generic";
            if (enemy.getCharacterClass().equalsIgnoreCase("archer")) {
                enemyAttackType = "shortbow";
            } else if (enemy.getCharacterClass().equalsIgnoreCase("warrior")) {
                enemyAttackType = "scimitar_goblin";
            }
            System.out.println(enemy.getName() + " attacks " + target.getName() + "!");
            boolean hit = CombatManager.performAttackRoll(enemy, target, enemyAttackType);
            if (hit) {
                System.out.println(">>> Hit!");
                int damage = CombatManager.calculateDamageRoll(enemy, target, enemyAttackType);
                target.takeDamage(damage);
            } else {
                System.out.println(">>> Miss!");
            }
        }
    }

    private void advanceTurn() {
        if (!isInCombat) {
            return;
        }
        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
        processNextCombatTurn();
    }

    private void endCombat(Player playerContext, boolean playerVictory) {
        System.out.println("\n--- COMBAT ENDS! ---");
        if (playerVictory) {
            Room currentRoom = playerContext.getCurrentRoom();
            System.out.println("Victory! All enemies have been defeated in " + currentRoom + ".");
            List<NPC> toRemove = new ArrayList<>();
            for (NPC enemy : currentEnemiesInCombat) {
                if (enemy.isDefeated()) {
                    toRemove.add(enemy);
                    if (enemy.getName().equalsIgnoreCase("Priestess Gut")) {
                        System.out.println("Priestess Gut has been defeated!.");
                        isGutDefeated = true;
                    } else if (enemy.getName().equalsIgnoreCase("Dror Ragzlin")) {
                        System.out.println("Dror Ragzlin has been defeated!.");
                        isRagzlinDefeated = true;
                    } else if (enemy.getName().equals("Minthara")) {
                        System.out.println("Minthara has been defeated!.");
                        isMintharaDefeated = true;
                    }
                }
            }
            for (NPC defeatedEnemy : toRemove) {
                currentRoom.removeNPC(defeatedEnemy);
            }

            if (currentRoom.getName().equalsIgnoreCase("Worg Pens") && !isHalsinFreed) {
                List<NPC> hostilesInRoom = currentRoom.getNPCs().stream().filter(
                        npc -> npc.getRace().equals("Goblin") && !(npc instanceof CompanionNPC) && !npc.isDefeated())
                        .collect(Collectors.toList());
                if (hostilesInRoom.isEmpty()) {
                    NPC halsinNPC = currentRoom.getNPCByName("Halsin");
                    if (halsinNPC != null) {
                        System.out
                                .println("\nWith the goblins defeated in this room, Halsin looks weary but grateful.");
                        isHalsinFreed = true;
                        this.isGoblinAggroGlobally = true;
                        System.out.println(
                                "\n[ALERT] You hear alarm horns and furious shout from the direction of the main camp... The entire goblin camp is now on high alert and hostile!");

                        if (ai_DM_Client != null) {
                            String prompt = "You are the Dungeon Master in the game Baldur's Gate 3. The player "
                                    + playerContext.getName()
                                    + " and all their companions have just defeated the goblins tormenting Halsin in the Worg Pens. "
                                    + "Halsin, the Archdruid is now free from immediate threat. "
                                    + "Generate Halsin's dialogue where he thanks the party and explains that to find a cure for their tadpoles, "
                                    + "the three goblin leaders (Priestess Gut, Dror Ragzlin, and Minthara) must be defeated. "
                                    + "Keep it concise and in character for Halsin.";
                            String halsinDialogue = ai_DM_Client.generateContent(prompt);
                            if (halsinDialogue != null) {
                                System.out.println("Halsin says: \"" + halsinDialogue.trim() + "\"");
                            } else {
                                System.out.println(
                                        "Halsin nod thankfully. 'The leaders...Gut, Ragzlin, Minthara... they must be dealt with before they plan an attack on my village'. (AI Dungeon Master lost in thought...)");
                            }
                        } else {
                            System.out.println(
                                    "Halsin nods thankfully. 'The leaders...Gut, Ragzlin, Minthara... they must be dealt with before they plan an attack on my village'. ");
                        }
                        System.out.println(
                                "(OBJECTIVE: Defeat the three Goblin leaders: Priestess Gut, Dror Ragzlin, Minthara), clear all goblins in this camp.");

                    }
                }
            }

        } else {
            System.out.println("Defeat! Your party has fallen.");
            this.gameOver = true;
        }

        this.isInCombat = false;
        this.turnOrder.clear();
        this.currentEnemiesInCombat.clear();
        this.currentTurnIndex = 0;
        if (!this.gameOver) {
            checkForCombatInitiation(playerContext, playerContext.getCurrentRoom());
            if (!isInCombat)
                System.out.println("\n" + playerContext.getCurrentRoom().getSurroundingDetail(playerContext));
        }
    }

    private boolean handleUseItem(Combatant itemUser, String itemName, String targetIdentifier,
            List<Combatant> availableAllyTargetsList) {
        Room currentRoom = itemUser.getCurrentRoom();

        if (itemName.equalsIgnoreCase("Health Potion")) {
            Item potion = null;
            if (itemUser instanceof Player) {
                potion = ((Player) itemUser).getItemByName("Health Potion");
            } else if (itemUser instanceof CompanionNPC) {
                List<Item> companionItems = ((CompanionNPC) itemUser).getEquipment();
                for (Item i : companionItems) {
                    if (i.getName().equalsIgnoreCase("Health Potion")) {
                        potion = i;
                        break;
                    }
                }
            }

            if (potion == null) {
                System.out.println(itemUser.getName() + " doesn't have a Health Potion.");
                return false; // Turn not taken if no potion
            }

            Combatant target = itemUser; // Default to self
            if (targetIdentifier != null && !targetIdentifier.isEmpty()) {
                if (targetIdentifier.equalsIgnoreCase("self")) {
                    target = itemUser;
                } else if (isInCombat && availableAllyTargetsList != null && !availableAllyTargetsList.isEmpty()) {
                    try {
                        int targetNum = Integer.parseInt(targetIdentifier);
                        if (targetNum > 0 && targetNum <= availableAllyTargetsList.size()) {
                            target = availableAllyTargetsList.get(targetNum - 1);
                        } else {
                            System.out.println("Invalid ally target number for potion.");
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid target for potion. Use 'self' or a number from the ally list.");
                        return false;
                    }
                } else if (!isInCombat) { // Exploration mode targeting by name
                    Combatant foundTarget = null;
                    if (itemUser.getName().equalsIgnoreCase(targetIdentifier)) { // Targeting self by name
                        foundTarget = itemUser;
                    } else { // Targeting a companion by name
                        for (CompanionNPC comp : companions) {
                            if (comp.getName().equalsIgnoreCase(targetIdentifier)
                                    && comp.getCurrentRoom().equals(currentRoom)) {
                                foundTarget = comp;
                                break;
                            }
                        }
                    }
                    if (foundTarget != null) {
                        target = foundTarget;
                    } else {
                        System.out.println("Cannot find '" + targetIdentifier + "' nearby to use the potion on.");
                        return false;
                    }
                } else {
                    System.out
                            .println("Invalid target for potion in combat. Use 'self' or a number from the ally list.");
                    return false;
                }
            }

            if (target.isDefeated()) {
                System.out.println(target.getName() + " is defeated and cannot be healed by a potion.");
                return false; // Potion not used, turn not taken
            }
            if (target.getCurrentHP() == target.getMaxHP()) {
                System.out.println(target.getName() + " is already at full health.");
                return false; // Potion not used, turn not taken
            }

            boolean potionRemoved = false;
            if (itemUser instanceof Player) {
                potionRemoved = ((Player) itemUser).removeItem(potion);
            } else if (itemUser instanceof CompanionNPC) {
                potionRemoved = ((CompanionNPC) itemUser).removeItemFromEquipment(potion);
            }

            if (potionRemoved) {
                int healing = DiceRoller.rollD10();
                System.out.println(itemUser.getName() + " uses a Health Potion on "
                        + (target.equals(itemUser) ? "themselves" : target.getName()) + ".");
                target.receiveHealing(healing);
                if (itemUser instanceof Player) {
                    currentRoom.notifyObserver(itemUser.getName() + " used a Health Potion on "
                            + availableAllyTargetsList.get(Integer.parseInt(targetIdentifier) - 1).getName()
                            + " for " + healing + "HP.", (Player) itemUser);
                } else {
                    currentRoom.notifyObserver(itemUser.getName() + " used a Health Potion on "
                            + availableAllyTargetsList.get(Integer.parseInt(targetIdentifier) - 1).getName()
                            + " for " + healing + "HP.", null);
                }
                return true; // Potion used, turn taken
            } else {
                System.out.println(itemUser.getName()
                        + " tried to use a Health Potion, but something went wrong (potion not removed).");
                return false;
            }
        } else if (itemName.equalsIgnoreCase("Gut's Sanctum Key")) {
            if (!currentRoom.getName().equalsIgnoreCase("Bloodied Shrine")) {
                System.out.println("You can't use the " + itemName + " here.");
                return false;
            }
            Item key = null;
            if (itemUser instanceof Player)
                key = ((Player) itemUser).getItemByName(itemName);
            else if (itemUser instanceof CompanionNPC)
                key = ((CompanionNPC) itemUser).getEquipment().stream()
                        .filter(i -> i.getName().equalsIgnoreCase(itemName)).findFirst().orElse(null);

            if (key != null) {
                if (isGutsQuartersDoorUnlocked) {
                    System.out.println("The door to Gut's quarters is already unlocked.");
                    return false; // Not a turn-ending action
                }
                isGutsQuartersDoorUnlocked = true;

                // Consume the key
                if (itemUser instanceof Player)
                    ((Player) itemUser).removeItem(key);

                System.out.println("You use the " + itemName
                        + ". You hear a click from the door leading to Gut's private quarters. It seems to be unlocked now.");
                currentRoom.notifyObserver(itemUser.getName() + " used " + itemName + " and unlocked a door.",
                        (itemUser instanceof Player ? (Player) itemUser : null));
                return true;
            } else {
                System.out.println("You don't have a '" + itemName + "'.");
                return false;
            }
        } else {
            System.out.println("You can't use the '" + itemName + "' in this way, or it's not a usable item.");
            return false;
        }
    }

    private void talkToNPC(Player player, String npcName) {
        Room currentRoom = player.getCurrentRoom();
        NPC targetNPC = currentRoom.getNPCByName(npcName);

        if (targetNPC != null) {
            System.out.println("You approach " + targetNPC.getName() + "...");

            if (targetNPC.getName().equalsIgnoreCase("Halsin") && isHalsinFreed
                    && !(isGutDefeated && isRagzlinDefeated && isMintharaDefeated)) {
                if (ai_DM_Client != null) {
                    String prompt = "You are an expert Dungeon Master in the video game Baldur's Gate 3. Player "
                            + player.getName() +
                            " is talking to " + targetNPC.getName() + " in the room " + player.getCurrentRoom()
                            + " in Goblin Camp." +
                            ". Halsin has been freed but the three goblin leaders are not yet defeated. " +
                            "Generate a brief, in-character response from Halsin, likely urging the player to deal with the leaders.";
                    String dialogue = ai_DM_Client.generateContent(prompt);
                    if (dialogue != null) {
                        System.out.println("Halsin says: \"" + dialogue.trim() + "\"");
                    } else {
                        System.out.println(
                                "Halsin looks at you intensly. 'The leader must be stopped. There is no other way.' (AI Dungeon Master is offline...)");
                    }
                } else {
                    System.out.println(
                            "Halsin looks at you intensly. 'The leader must be stopped. There is no other way.'");

                }
                return;
            }

            if (ai_DM_Client != null) {
                String initialPrompt = "You are the Dungeon Master in the video game Baldur's Gate 3. Player"
                        + player.getName() +
                        "initiates conversation with " + targetNPC.getName() + "in the room " + player.getCurrentRoom()
                        + " in Goblin Camp. " +
                        "Generates a brief, in-character opening line or greeting from " + targetNPC.getName();
                String initialDialogue = ai_DM_Client.generateContent(initialPrompt);
                if (initialDialogue != null && !initialDialogue.trim().isEmpty()) {
                    System.out.println(targetNPC.getName() + " says: \"" + initialDialogue.trim() + "\"");
                } else {
                    System.out.println(targetNPC.getName() + " looks at you expentantly");
                }

                // Dialogue loop
                String playerDialogueInput;
                while (true) {
                    delay(1000);
                    System.out.println("Enter your dialogue or type 'end talk' to stop: ");
                    playerDialogueInput = this.scanner.nextLine().trim();

                    if (playerDialogueInput.equalsIgnoreCase("end talk")) {
                        System.out.println("You end the conversation with " + targetNPC.getName() + ".");
                        break;
                    }

                    String conversationPrompt = "You are " + targetNPC.getName() + " in the video game Baldur's Gate 3."
                            +
                            "You are in Goblin Camp, in the room " + targetNPC.getCurrentRoom() + ". " +
                            player.getName() + " just said to you: \"" + playerDialogueInput + "\"." +
                            "Respond naturally and in-character. Keep it brief (1-2 sentences)." +
                            "Context: "
                            + (isHalsinFreed ? "Halsin has been freed" : "Halsin is still missing or captive") +
                            ". The main player objective is to rescue Halsin and deal with the goblin leaders.";
                    String npcResponse = ai_DM_Client.generateContent(conversationPrompt);
                    if (npcResponse != null) {
                        System.out.println(targetNPC.getName() + " says: \"" + npcResponse.trim() + "\"");

                    } else {
                        System.out.println(targetNPC.getName()
                                + " considers your words but doesn't reply immediately. (AI error or no response)");
                    }
                }
            } else {
                // Fallback if AI is not available.
                System.out.println(targetNPC.getName()
                        + " looks at you expetantly but says nothing. (AI DM is offline, please check API)");
            }
        } else {
            System.out.println("There is no one here by the name of '" + npcName + "' to talk to");
        }
    }

    private void movePlayer(Player player, String direction) {
        Room currentRoom = player.getCurrentRoom();
        Room newRoom = currentRoom.getExit(direction.toLowerCase());
        delay(1000);

        if (newRoom != null) {
            // Implement Observer later (leave current room message)
            // print "You leave the room"
            currentRoom.notifyObserver(player.getName() + " " + direction, null);
            player.setCurrentRoom(newRoom);

            List<CompanionNPC> companionsToFollow = new ArrayList<>(this.companions);
            for (CompanionNPC companion : companionsToFollow) {
                if (companion.getCurrentRoom().equals(currentRoom)) {
                    currentRoom.notifyObserver(companion.getName() + " follows " + player.getName() + ".", null);
                    companion.setCurrentRoom(newRoom);
                    newRoom.notifyObserver(companion.getName() + " arrives with " + player.getName() + ".", null);
                }
            }

            newRoom.notifyObserver(player.getName() + " has arrived.", player);
            System.out.println("\nYou have arrived in " + newRoom.getName() + ". \n");
            delay(500);

            String description;
            if (ai_DM_Client != null) {
                // Ask for a brief initial impression
                String prompt = "You are the Dungeon Master of the game Baldur's Gate 3. " +
                        "The players have just arrive in the " + newRoom.getName() +
                        " of the Goblin Camp in the game Baldur's Gate 3. Give a brief (1-2 sentence) initial impression of the scene. "
                        +
                        "Static description: " + newRoom.getDescription();
                description = ai_DM_Client.generateContent(prompt);
                if (description == null) {
                    System.out.println(
                            "The AI Dungeon Master seems lost in thought(offline)... proceedin using default description");
                    description = "\n" + player.getCurrentRoom().getDescription();
                } else {
                    description = "\"" + description.trim() + "\"" + "\n";
                }
            } else {
                description = "AI Dungeon Master is currently unavailable to describe the scene.\n";
            }
            System.out.println(description);
            delay(1000);
            System.out.println(newRoom.getSurroundingDetail(player));

            checkForCombatInitiation(player, newRoom);
        } else {
            System.out.println("You can't go that way");
        }
    }

    private void takeItem(Player player, String itemName) {
        Room currentRoom = player.getCurrentRoom();
        Item itemToTake = currentRoom.getItemByName(itemName);

        if (itemToTake != null) {
            if (currentRoom.removeItem(itemToTake)) {
                if (player.addItem(itemToTake)) {
                    System.out.println("You take the " + itemToTake.getName() + ".");
                    currentRoom.notifyObserver(player.getName() + " took the " + itemName, null);
                } else {
                    System.out.println("You couldn't pick up the " + itemToTake.getName() + " for some reason.");
                    currentRoom.addItem(itemToTake);
                }
            } else {
                System.out.println("The " + itemName + " seems to have disappeared!");
            }
        } else {
            System.out.println("There is no '" + itemName + "' here to take.");
        }
    }

    private void dropItem(Player player, String itemName) {
        Item itemToDrop = player.getItemByName(itemName);
        Room currentRoom = player.getCurrentRoom();

        if (itemToDrop != null) {
            if (player.removeItem(itemToDrop)) {
                currentRoom.addItem(itemToDrop);
                System.out.println("You drop the '" + itemToDrop + "'.");
                currentRoom.notifyObserver(player.getName() + " dropped the " + itemName, null);
            } else {
                System.out.println("You don't seem to have the '" + itemName + "' any more.");
            }
        } else {
            System.out.println("You don't have a '" + itemName + "' in your inventory.");
        }
    }

    public Room getRoom(String name) {
        return rooms.get(name.toLowerCase());
    }

    public Player getPlayer(String name) {
        return players.get(name.toLowerCase());
    }

    private void checkWinCondition(Player player) {
        if (isHalsinFreed && isGutDefeated && isRagzlinDefeated && isMintharaDefeated) {
            System.out.println("\n\n********************************************************************");
            System.out.println("CONGRATULATIONS, " + player.getName() + "!");
            System.out.println("You have freed Halsin and defeated the three goblin leaders!");
            System.out.println("The Absolute's immediate threat in this region has been quelled.");
            System.out.println("Halsin will now be able to help you seek a cure for the tadpole.");
            System.out.println("You have won the game!");
            System.out.println("********************************************************************");
            this.gameOver = true;
        }
    }

    private void delay(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
