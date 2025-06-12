# CS1OP - Object-Oriented Programming - Coursework Project
# Project Reflection Report
- Module Code: CS1OP
- Assignment report Title: Project Reflection
- Student Number: 31013754
- Actual hrs spent for the assignment: 150
- Which Artificial Intelligence tools used (if applicable): Google Gemini and Google Gemini API for AI Dungeon Master.

## 1. Introduction 
This report reflects on the development of "Rescue Halsin from the Goblin Camp," a Java-based text adventure game inspired by Baldur's Gate 3. The project involved creating a navigable world, party and combat systems, item, NPC management, and integrating the Google Gemini API as a AI Dungeon Master for dynamic narrative content. It emphasized Object-Oriented Programming and the application of Singleton, Observer, and Factory design patterns. This document analyzes the utility of AI tools and the effectiveness of these design patterns in the project.

## 2. Analysis of AI Support in Software Development 
### How AI Tools Were Used
- **In-Game Content (Google Gemini API):** The gemini-2.0-flash model, via AI_DM_Client.java, dynamically generated room descriptions (look command), initial NPC dialogue (talk to command), and key narrative moments (e.g., Halsin's rescue dialogue), acting as an AI Dungeon Master.
- **Development Assistance (Gemini):**
    - **Unit Test Generation:** Provided foundational structures for unit tests across various classes (e.g., Item, NPC, Game, factories, etc), which were then reviewed and refined by myself.
    - **Debugging:** Assisted in diagnosing and suggesting fixes for errors like NullPointerExceptions, ExceptionInInitializerError, and Maven dependency issues.
    - **Code Refinement:** Offered suggestions for code improvements for better performace and assistance in coding Mermaid diagram.

- **Benefits of Using AI Tools**
    - Enhanced Player Experience: Dynamic AI-generated narrative content made the game more immersive and less predictable.
    - Learning & Exploration: Provide explaination and examples to help me understand software principles (Singleton, Factory and Observer)
    - Improved Code Quality: Direct suggestions led to more correct and robust code implementations.

- **Challenges and Limitations**
    - Prompt Engineering: Requiring careful prompt design that aligns project's structure.
    - Verification Overhead: It is neccessary to thoroughly review and modify to ensure correctness and completeness.
    - Risk of Over-Reliance: Despite of the usefulness of AI in code generation, over-reliance can lead to the lack of code understanding and lose the flow of code connection.
    - Contextual Gaps: AI sometimes lacked full project context, requiring human judgment to adapt suggestions.
    - API Dependency: The AI DM feature's reliability is tied to Gemini API availability as user might not have Gemini API or want to play the game offline.

### Overall Impact on Your Learning and Development
AI tools positively impacted the project by enabling a richer narrative and accelerating development tasks like testing and debugging. This experience highlighted AI's role as a powerful assistant, emphasizing the need for critical evaluation and human oversight. It improved code-debugging skills and demonstrated the practical benefits of AI in modern software development.

## 3. Analysis of Software Patterns in the Project 
### How the Patterns Were Used
- **Singleton (Game.java):** The Game class, managing overall game state and logic, was implemented as a Singleton with a private constructor and a static getInstance() method, ensuring a single, global point of access.
- **Observer (Room.java as Subject, Player.java as Observer):** Room objects (Subjects) notify registered Player Observers about in-game events (e.g., movement, item interactions) via an update() method, facilitating dynamic feedback.
- **Factory (ItemFactory.java, NPCFactory.java, RoomFactory.java):** These classes encapsulated the creation logic for Item, NPC/CompanionNPC, and Room objects, abstracting instantiation details from the Game class, particularly during world initialization.

### Benefits of Using Software Patterns
- **Singleton:** Provided centralized control and easy global access to game state, ensuring consistency. 
- **Observer:** Decoupled event sources (Room) from event consumers (Player), allowing for flexible and dynamic notifications without tight coupling. Enhanced extensibility for future observer types.
- **Factory:** Encapsulated complex object creation logic, improving code organization, maintainability (changes localized to factories), and readability of the Game class. Decoupled Game from concrete product instantiation.

### Challenges and Limitations
- **Singleton**: Introduced testability challenges due to global state; mitigated with reflection in tests, but highlighted a common trade-off of the pattern. I personally found hard time to design the test.

- **Observer:** In highly complex systems (not an issue here), managing numerous notifications or avoiding update cascades could be challenging.

- **Factory:** Added a few extra classes; potentially overkill for extremely simple object creation, but beneficial for this project's variety of predefined entities.

### Overall Impact on Your Project
Design patterns were crucial for a well-structured, maintainable, and flexible game. Singleton offered clear state management, Observer enabled clean event communication, and Factories provided entity creation. While minor challenges like Singleton testability existed, the architectural benefits significantly outweighed them, providing a robust foundation.

## 4. Ethical and Legal Considerations
- **4.1 Reflect on any ethical concerns related to my use of AI tools, such as:**
    - Over-reliance: Mitigated by critically reviewing, understanding, and often modifying AI-generated code (especially unit tests), using AI as a starting point rather than a final solution.

    - Academic integrity: AI was used as a productivity and learning tool. Core design and pattern implementations were original. AI-assisted code (e.g., unit test templates) was edited and validated by myself. 

- **4.2. Data Handling and Privacy**
    - The project primarily handles the Google Gemini API key. This is managed by requiring users to input their own key into a config.properties file, which is explicitly recommended for exclusion via .gitignore to prevent accidental exposure. No other personal user data is collected or stored by the game.

- **4.3. Broader Ethical and Legal Implications**
    - Potential misuse: The software's nature (game text adventure) presents a very low risk of misuse.
    - Accessibility and inclusivity: Text-based design is inherently accessible to screen readers. AI-generated narrative content from Gemini could potentially carry model biases, a general concern with generative AI.
    - Intellectual Property: Game elements are "inspired by" Baldur's Gate 3, acceptable for educational fair use. Commercial use would require licensing. Use of Google Gemini API is subject to its terms of service.

## 5. Conclusion 
The development of the "Rescue Halsin" text adventure served as a practical exercise in applying Object-Oriented Programming principles and selected software design patterns. The integration of AI tools, specifically the Gemini API for dynamic in-game narrative and a conversational AI for development support (such as unit test generation and debugging), proved beneficial in enhancing the project's scope and accelerating certain development tasks.

A key finding from this work relates to the utility of AI as a development aid; it can significantly improve productivity and offer novel solutions, particularly in content generation. However, this must be balanced with rigorous human oversight to ensure the accuracy, relevance, and originality of the output. The chosen design patterns—Singleton, Observer, and Factory—were instrumental in establishing an organized and maintainable codebase, each contributing to modularity and clear separation of concerns, though considerations like the testability of Singletons were noted. The project also reinforced the importance of ethical practices, such as transparent AI usage and secure handling of credentials like API keys.

Future software development projects will benefit from the lessons learned here, particularly regarding the judicious use of AI tools as a supportive element, always complemented by critical developer evaluation. The experience also solidified the value of selecting appropriate design patterns to manage complexity. In essence, this project provided a valuable opportunity to consolidate understanding of software construction, the practical application of design patterns, and the evolving role of AI in the development lifecycle, emphasizing the continued need for transparency and responsible use of such technologies.

## 6. References

Bloch, J. (2018). Effective Java (3rd ed.). Addison-Wesley Professional.

Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley Professional.

Google. (2025). Gemini API Documentation. Retrieved from [https://ai.google.dev/gemini-api/docs]

Mockito. Mockito Javadoc. Retrieved from [https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/module-summary.html]

JUnit 5. (2024). JUnit 5 User Guide. Retrieved from [https://junit.org/junit5/docs/current/user-guide/]
