package com.id013754.lan_coop_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.id013754.CompanionNPC;
import com.id013754.Game;
import com.id013754.Room;
import com.id013754.factories.NPCFactory;

public class GameServer {

    // The single instance of the Game.java logic. AKA DM master
    private static Game game;
    private static NPCFactory npcFactory;

    // Thread-safe list to store handlers for each connected player
    private static final List<PlayerHandler> connectedPlayers = new CopyOnWriteArrayList<>();

    // Thread-safe map to track which characters are taken by players as their
    // PRIMARY
    // Key: character name (lowercase), true or false (availibility)
    private static final Map<String, Boolean> availablePrimaryCharacters = new ConcurrentHashMap<>();

    // Thread-safe map to track who controls which character in the game.
    // This will include both primary and additionally claimed characters.
    private static final Map<String, PlayerHandler> characterControllerMap = new ConcurrentHashMap<>();

    // Server state to manage game phases
    private enum GameState {
        WAITING_FOR_PLAYERS, ASSIGNING_REMAINING, GAME_IN_PROGRES
    }

    private static volatile GameState currentGameState = GameState.WAITING_FOR_PLAYERS;

    public static void main(String[] args) {
        int portNumber = 12345;
        System.out.println("GameServer: Starting server on port: " + portNumber);

        game = Game.getInstance();
        npcFactory = new NPCFactory();

        availablePrimaryCharacters.put("astarion", true);
        availablePrimaryCharacters.put("shadowheart", true);
        availablePrimaryCharacters.put("gale", true);
        availablePrimaryCharacters.put("lae'zel", true);

        System.out.println("GameServer: Game world initilized. Waiting for players...");

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("GameServer: New player connecting from " + clientSocket.getRemoteSocketAddress());

                PlayerHandler playerHandler = new PlayerHandler(clientSocket);
                new Thread(playerHandler).start();
            }
        } catch (IOException e) {
            System.err.println("GameServer error: " + e.getMessage());
        }
    }

    private static void broadcastMessage(String messsage, PlayerHandler excludePlayer) {
        for (PlayerHandler handler : connectedPlayers) {
            if (handler != excludePlayer) {
                handler.sendMessage(messsage);
            }
        }
    }

    private static void broadcastToAll(String message) {
        for (PlayerHandler handler : connectedPlayers) {
            handler.sendMessage(message);
        }
    }

    private static synchronized void attemptToStartGame(PlayerHandler starter) {
        if (currentGameState != GameState.WAITING_FOR_PLAYERS) {
            starter.sendMessage("The game is already strating or in progress");
            return;
        }

        broadcastToAll(starter.primaryCharacter.getName() + " has initiated the game start!");
        currentGameState = GameState.ASSIGNING_REMAINING;

        // Start a new thread to manage the assignment process so the server isn't
        // blocked
        new Thread(() -> assignRemainingCharacters()).start();
    }

    private static void assignRemainingCharacters() {
        // Find characters that were never picked as primary
        List<String> unassigned = availablePrimaryCharacters.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (unassigned.isEmpty()) {
            startGameForAll();
            return;
        }

        broadcastToAll("The following characters need a controller: " + String.join(", ", unassigned));

        for (String charNameToAssign : unassigned) {
            // Check if another thread assigned it in the meantime
            if (characterControllerMap.containsKey(charNameToAssign)) {
                continue;
            }

            broadcastToAll("Who will control " + charNameToAssign + "? Type 'claim " + charNameToAssign
                    + "' to take control.");

            // Wait until this character is claimed
            while (!characterControllerMap.containsKey(charNameToAssign)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // All characters are now assigned
        startGameForAll();
    }

    private static void startGameForAll() {
        currentGameState = GameState.GAME_IN_PROGRES;
        broadcastToAll("\nAll characters are ready! The adventure begins...");
        Room startingRoom = game.getRoom("Goblin Camp Courtyard");
        for (PlayerHandler handler : connectedPlayers) {
            handler.sendMessage("\n--- You have arrive ---");
            handler.sendMessage(startingRoom.getSurroundingDetail(null));
        }
    }

    private static class PlayerHandler implements Runnable {
        private final Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private CompanionNPC primaryCharacter;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            writer.println(message);
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // --- Character Selection Loop for Primary Character ---
                while (this.primaryCharacter == null) {
                    if (currentGameState != GameState.WAITING_FOR_PLAYERS) {
                        writer.println("Sorry, the game is already starting. Please try again later.");
                        socket.close();
                        return;
                    }
                    List<String> choices = availablePrimaryCharacters.entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    if (choices.isEmpty()) {
                        writer.println("Sorry, all character are currently taken.");
                        socket.close();
                        return;
                    }

                    writer.println("Welcome! Please choose your primary character: ");
                    writer.println("Available: " + String.join(", ", choices));
                    writer.print("Your choice: ");
                    writer.flush();

                    String choice = reader.readLine();
                    if (choice == null) {
                        return;
                    }

                    String chosenCharName = choice.trim().toLowerCase();

                    synchronized (availablePrimaryCharacters) {
                        if (availablePrimaryCharacters.getOrDefault(chosenCharName, false)) {
                            availablePrimaryCharacters.put(chosenCharName, false);
                            Room startingRoom = game.getRoom("Goblin Camp Courtyard");
                            switch (chosenCharName) {
                                case "astarion":
                                    this.primaryCharacter = npcFactory.createAstarion(startingRoom);
                                    break;
                                case "shadowheart":
                                    this.primaryCharacter = npcFactory.createShadowheart(startingRoom);
                                    break;
                                case "gale":
                                    this.primaryCharacter = npcFactory.createGale(startingRoom);
                                    break;
                                case "lae'zel":
                                    this.primaryCharacter = npcFactory.createLaezel(startingRoom);
                                    break;
                            }
                            characterControllerMap.put(choice, this);
                            writer.println("You have chosen to play as " + this.primaryCharacter.getName() + ".");
                        } else {
                            writer.print(
                                    "Invalid choice or character was just taken. Please choose from the available list.");
                        }
                    }
                }

                // --- Player has joined ---
                connectedPlayers.add(this);
                broadcastMessage(this.primaryCharacter.getName() + " has joined the lobby!", this);
                writer.println("You are in the lobby. Type '/start' when everyone is ready");

                // --- Main Command Loop ---
                String clientCommand;
                while ((clientCommand = reader.readLine()) != null) {
                    if ("/quit".equalsIgnoreCase(clientCommand)) {
                        break;

                    }
                    if (currentGameState == GameState.WAITING_FOR_PLAYERS) {
                        if ("/start".equalsIgnoreCase(clientCommand)) {
                            attemptToStartGame(this);
                        } else {
                            // Lobby chat
                            broadcastMessage(this.primaryCharacter.getName() + ": " + clientCommand, this);
                        }
                    } else if (currentGameState == GameState.ASSIGNING_REMAINING) {
                        if (clientCommand.toLowerCase().startsWith("claim ")) {
                            String charToClaim = clientCommand.substring(6).trim().toLowerCase();
                            synchronized (characterControllerMap) {
                                if (availablePrimaryCharacters.getOrDefault(charToClaim, true)
                                        && !characterControllerMap.containsKey(charToClaim)) {
                                    characterControllerMap.put(charToClaim, this);
                                    broadcastToAll(this.primaryCharacter.getName() + " has taken control of "
                                            + charToClaim + "!");
                                } else {
                                    writer.println(charToClaim + " is not available to be claimed.");
                                }
                            }
                        } else {
                            writer.println("The game is starting. Please wait for assignment prompt or type '/quit'.");
                        }
                    } else { // GAME_IN_PROCESS
                        String broadcast = this.primaryCharacter.getName() + " used command: " + clientCommand;
                        System.out.println("GameServer: " + broadcast);
                        broadcastMessage(broadcast, this);
                    }
                }
            } catch (IOException e) {
                System.out.println(
                        "GameServer: Player " + (primaryCharacter != null ? primaryCharacter.getName() : "UNKNOWN")
                                + " may have disconnected: " + e.getMessage());

            } finally {
                // Clean up
                if (primaryCharacter != null) {
                    System.out.println("GameServer: " + primaryCharacter.getName() + " has disconnected.");
                    // Make the character available
                    availablePrimaryCharacters.put(primaryCharacter.getName().toLowerCase(), true);
                    characterControllerMap.remove(primaryCharacter.getName().toLowerCase());
                    broadcastMessage(primaryCharacter.getName() + " has left the game.", this);
                }

                // Remove any claimed characters
                characterControllerMap.entrySet().removeIf(entry -> entry.getValue() == this);
                connectedPlayers.remove(this);
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
