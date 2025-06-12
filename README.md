# CS1OP - Object-Oriented Programming - Coursework Project
# Project 1: Text Adventure Game - "Rescue Halsin from the Goblin Camp"

- **NOTE:**  if you don't know what to do in the game or get stuck then go to [Game Flow / Walkthrough (Main Quest)](#game-flow--walkthrough-main-quest) for some hints or guidance.
- **NOTE:** in order to run this programme smoothly, you should follow [Setup and How to Run](#setup-and-how-to-run). If you don't have Gemini API, then you can create https://aistudio.google.com/app/apikey for FREE TIER USE. Please contact me (id013754@student.reading.ac.uk) if you have any issue of setting up Gemini API key.

# Table of Content
- [Basic Information](#basic-information)
- [Implementation Highlights](#implementation-highlights)
  - [1. Introduction](#1-introduction)
  - [2. Requirements Fulfillment](#2-requirements-fulfillment)
  - [3. Design](#3-design)
    - [a. Overall System Architecture](#a-overall-system-architecture)
    - [b. Simplified Class Diagram (Mermaid)](#b-simplified-class-diagram-mermaid)
    - [c. Class Structure (Summary)](#c-class-structure-summary)
    - [d. Design Pattern Implementation](#d-design-pattern-implementation)
  - [4. Assumptions](#4-assumptions)
- [Setup and How to Run](#setup-and-how-to-run)
- [Basic Commands](#basic-commands)
- [Game Flow / Walkthrough (Main Quest)](#game-flow--walkthrough-main-quest)
- [Future Work](#future-work)

# Basic Information
- **Module Title:** Object-Oriented Programming
- **Module Code:** CS1OP
- **Lecturer responsible:** Pat Parslow
- **Student Number**: 31013754
- **Estimated Hours Spent**: 120 hours
- **AI Tools Used:**
   - Google Gemini API (model: gemini-2.0-flash): Used for dynamic in-game content (room descriptions, NPC dialogue, narrative moments) to enhance player immersion as an AI Dungeon Master.
- **AI Usage Statement:** 
    - Developing AI_DM_Client.java for Gemini API integration.
    - Generating foundational unit tests (reviewed, refined, and validated by me).
    - Debugging critical errors.
    - Obtaining suggestions for code improvements.

# Implementation Highlights

## 1. Introduction
"Rescue Halsin from the Goblin Camp" is a Java-based text adventure game inspired by Baldur's Gate 3. The player, as Astarion(Rogue), navigates the Goblin Camp with companions: Shadowheart(Cleric), Lae'zel(Fighter), Gale(Wizard) to free Archdruid Halsin and defeat three goblin leaders. The game features a navigable world, a party system, [D&D 5e-style](https://www.dndbeyond.com/sources/dnd/basic-rules-2014?srsltid=AfmBOooGYbVQ6MSkKpsUodZ-uDyQPtoYhlzhA9ppt90D3Ft14tVYn1hr) turn-based combat, item interaction, and AI-driven NPC dialogue via the Google Gemini API. Key OOP principles and the Singleton, Observer, and Factory design patterns are implemented.

## 2. Requirements Fulfillment
The project meets all core requirements:

- **Game World:** Implemented with interconnected Room objects, items, and NPCs.
- **Player System:** Supports a main Player (Astarion) and CompanionNPCs with standard interactions (movement, inventory, dialogue, combat). Observer pattern notifies of events.
- **Game Logic:** Includes interaction, item management, quest progression (free Halsin, defeat leaders), and turn-based combat.
- **Win Condition:** Free Halsin AND defeat all three goblin leaders.
- **AI Tool Usage:** Google Gemini API (gemini-2.0-flash) provides dynamic narrative content.

## 3. Design

### a. Overall System Architecture
The game's architecture is designed around a central Game controller that manages the overall game state and flow. It interacts with several key components: Game Entities (Player, NPCs, Items), the Game World (Rooms), Factories for object creation, specialized Managers for combat and AI interaction, and utility classes.

**System Architecture Diagram (Mermaid):**

```
graph TD
    subgraph UserInput["User Input (Console)"]
        direction LR
        InputScanner["Scanner (in Game)"]
    end

    subgraph CoreGameLogic["Core Game Logic"]
        direction TB
        Game["Game (Singleton Controller)"]
        GameLogic["Game Logic & State (Quest Flags, Turn Order)"]
        CommandParser["Command Parser (in Game)"]
    end

    subgraph GameEntities["Game Entities"]
        direction TB
        Player["Player"]
        NPCs["NPC / CompanionNPC"]
        Items["Item"]
    end

    subgraph WorldRepresentation["World Representation"]
        direction TB
        Rooms["Room"]
    end

    subgraph Factories["Object Creation Factories"]
        direction TB
        RoomFactory["RoomFactory"]
        ItemFactory["ItemFactory"]
        NPCFactory["NPCFactory"]
    end

    subgraph SpecializedManagers["Specialized Managers & Utilities"]
        direction TB
        CombatManager["CombatManager"]
        DiceRoller["DiceRoller"]
        AI_DM_Client["AI_DM_Client (Gemini API)"]
    end

    subgraph Output["Output (Console)"]
        direction LR
        GameOutput["System.out.println (from Game, CombatManager etc.)"]
    end

    UserInput --> CommandParser
    CommandParser --> GameLogic
    Game --> GameLogic
    GameLogic --> GameEntities
    GameLogic --> WorldRepresentation
    GameLogic --> SpecializedManagers
    GameLogic --> Output

    Game --> Factories
    Factories --> GameEntities
    Factories --> WorldRepresentation

    SpecializedManagers --> GameEntities
    SpecializedManagers --> Output
    AI_DM_Client --> ExternalAPI["External Gemini API"]

    Player -- interacts with --> Items
    Player -- interacts with --> NPCs
    Player -- is in --> Rooms
    NPCs -- are in --> Rooms
    Items -- are in --> Rooms
```

### b. Simplified Class Diagram (Mermaid)
This diagram shows the primary classes and their core relationships. For full details, refer to the source code.

```
classDiagram
    class Game {
        +getInstance(): Game$
        +startGame(): void
    }
    Game "1" --o "*" Room : manages
    Game "1" --o "*" Player : manages
    Game "1" --o "*" NPC : manages
    Game ..> CombatManager : uses
    Game ..> factories.RoomFactory : uses
    Game ..> factories.ItemFactory : uses
    Game ..> factories.NPCFactory : uses
    Game ..> AI_DM_Client : uses

    class Player {
        -name: String
        -inventory: List~Item~
        -currentRoom: Room
        +update(String): void
    }
    Player --|> Combatant
    Player --|> Observer

    class NPC {
        -name: String
        -currentRoom: Room
        -equipment: List~Item~
    }
    NPC --|> Combatant

    class CompanionNPC {
        +isPlayerControlled(): boolean
    }
    CompanionNPC --|> NPC

    class Room {
        -name: String
        -exits: Map~String, Room~
        +addItem(Item): void
        +addNPC(NPC): void
        +attach(Observer): void
    }
    Room --|> Subject
    Room "1" --o "*" Item : contains
    Room "1" --o "*" NPC : contains

    class Item {
        -name: String
        -description: String
    }

    class factories.RoomFactory {
        +create*(...): Room
    }
    factories.RoomFactory ..> Room : creates

    class factories.ItemFactory {
        +create*(...): Item
    }
    factories.ItemFactory ..> Item : creates

    class factories.NPCFactory {
        +create*(...): NPC
        +create*(...): CompanionNPC
    }
    factories.NPCFactory ..> NPC : creates
    factories.NPCFactory ..> CompanionNPC : creates

    class AI_DM_Client {
        +generateContent(String): String
    }

    class CombatManager {
        +performAttackRoll(Combatant, Combatant, String): boolean$
        +calculateDamageRoll(Combatant, Combatant, String): int$
    }
    CombatManager ..> Combatant : uses
    CombatManager ..> DiceRoller : uses

    class DiceRoller {
        +rollD20(): int$
    }

    class Combatant {
        <<interface>>
        +getName(): String
        +takeDamage(int): void
        +isDefeated(): boolean
    }

    class Observer {
        <<interface>>
        +update(String): void
    }

    class Subject {
        <<interface>>
        +attach(Observer): void
        +notifyObserver(String, Player): void
    }
```

### c. Class Structure (Summary)
- **Game**: Singleton; main controller, game loop, command processing.
- **Player:** User-controlled character; manages inventory, location, implements Observer, Combatant.
- **NPC:** Base for non-player characters; D&D attributes, implements Combatant.
- **CompanionNPC:** Extends NPC; player's party members.
- **Room:** Game location; holds entities, manages exits, implements Subject.
- **Item:** Collectible objects.
- **factories.*:** Handle object instantiation for Room, Item, NPC.
- **AI_DM_Client:** Manages Gemini API communication.
- **CombatManager:** Static methods for combat logic.
- **DiceRoller:** Static methods for dice rolls.
- **Interfaces:** Combatant, Observer, Subject define interaction contracts.

### d. Design Pattern Implementation
- **Singleton (Game):** Ensures a single, globally accessible Game instance manages game state and logic via a private constructor and static getInstance().
- **Observer (Room as Subject, Player as Observer):** Room objects notify Player observers of in-game events (movement, item changes), allowing dynamic feedback without tight coupling.
- **Factory (RoomFactory, ItemFactory, NPCFactory):** Encapsulate creation logic for their respective objects, decoupling the Game class from concrete instantiation details and improving modularity.

## 4. Assumptions
Single-player experience; player controls Astarion, companions follow.
Simplified D&D 5e combat (initiative, attack/damage rolls, HP).
Google Gemini API is available; fallback to pre-written text if API fails.
Player follows the main quest: free Halsin, defeat leaders.
NPC interaction via talk to command, AI generates responses.
Map scope limited to key quest areas.

# Setup and How to Run
**Prerequisites:**
JDK 11+
Apache Maven
Internet connection
Valid Google Gemini API Key

1. **Clone Repository:**
   - git clone [YOUR_CSGITLAB_REPOSITORY_URL_HERE]
   - cd CS1OP-CW1 # Or your repository folder name
   - cd halsin-rescue-adventure
2. **Set Up API Key:**
   - Create src/main/resources/config.properties.
   - Add: GEMINI_API_KEY=YOUR_ACTUAL_GEMINI_API_KEY
   - Crucial: Add config.properties to .gitignore.
3. **Compile:**
   mvn clean compile
4. **Run:**
   mvn exec:java

# Basic Commands
**Exploration:**
- `s`: To get game summary and objective
- `look` / `l`: Use AI Dungeon Master to produce scene narrative dialogue (AI_DM_Client).
- `list`: Room contents (standard).
- Movement: `<exit_phrase>` / `go <phrase>`. (e.g. enter sanctum)
- `inventory / i`: Player's items.
- `take <item> / drop <item>` (e.g. drop health potion)
- `describe <item>`:  show description of the item(AI offline) or use AI DM to describe it(AI online) (e.g. describe health potion)
- `talk to <npc>` / `ask <npc>`: AI-driven dialogue. (e.g. talk to Shadowheart, talk to)
- `attack [target]`: Initiate combat. (e.g. attack or attack goblin)
- `help halsin`: Specific quest action. (can only be used in specific location)
- `use <item> [on <target>]` (e.g. use Gut's Sanctum Key)
- `steal <item>`: Sleight of hand check. (e.g. steal health potion)
- `quit`: Exit.

**Combat (Player-controlled turn):**
- `attack <num>`: Melee attack. (e.g. attack 1)
- `shoot <num>`: Ranged attack. (e.g. shoot 2)
- `cast firebolt <num>`: cast fire bolt (Astarion/Gale). (e.g. cast firebolt 3)
- `cast heal <ally_num_or_self>`: cast healing spell (Shadowheart). (e.g. cast heal 2)
- `use health potion [on <ally_num_or_self>]` (e.g. use health potion on 1)
- `pass`: pass turn.

# Game Flow / Walkthrough (Main Quest)
This section provides a general guide to completing the main objectives. Remember to use `look` or `list` to examine your surroundings and talk to NPCs for clues!

1. **Locate Halsin:**
Navigate to the Worg Pens. You can reach this area from the Shattered Sanctum (Main Hall) by using the command `open ornate door`.
Once in the Worg Pens, type `help halsin`. Halsin will be in a cage. He will tell you he needs the "Worg Pen Key" which is in Priestess Gut's private quarters.

2. **Obtain Gut's Sanctum Key (to access Gut's Quarters):**
Priestess Gut's private quarters are locked. You first need **"Gut's Sanctum Key"**.
This key is located in Minthara's Command Post. Navigate there from the Shattered Sanctum (Main Hall) by typing `go east`.
In Minthara's Command Post, find and take the key. You might need to be sneaky! Type `steal Gut's Sanctum Key`.

3. **Access Gut's Quarters and Get the Worg Pen Key:**
Return to the Bloodied Shrine (west from Shattered Sanctum Main Hall).
With "Gut's Sanctum Key" in your `inventory`, you can unlock the door to her quarters. Type `use Gut's Sanctum Key` or `use Gut's Sanctum Key` on `enter quarter`.
Once the door is unlocked, type `enter quarter` to go into Gut's Quarters.
Inside Gut's Quarters, look for and `take Worg Pen Key`.

4. **Free Halsin:**
Return to the Worg Pens.
With the "Worg Pen Key" in your inventory, type `help halsin` again. This should free him.
Note: Freeing Halsin will likely make all goblins in the camp hostile if they weren't already!

5. **Defeat the Goblin Leaders:**
After freeing Halsin, he will inform you that to truly secure the area (and win the game), you must defeat the three goblin leaders:
Priestess Gut: Found in the Bloodied Shrine or her Gut's Quarters.
Dror Ragzlin: Found in Ragzlin's Throne Room (north from Shattered Sanctum Main Hall).
Minthara: Found in Minthara's Command Post (east from Shattered Sanctum Main Hall).
Navigate to each leader's location and defeat them in combat. Use the `attack` command to initiate combat if it doesn't start automatically.

6. **Win the Game:**
Once Halsin is freed AND all three goblin leaders (Priestess Gut, Dror Ragzlin, and Minthara) are defeated, you will have won the game! A victory message will be displayed.

# Future Work
- Implement LAN Coop 2-4 players.
- Enhanced Combat (more spells, abilities, enemy AI).
- Deeper NPC Interaction (dialogue trees, choices).
- Puzzles beyond key-locks.
- Character Progression (leveling).
- GUI.
- Save/Load game.
- Expanded World.
