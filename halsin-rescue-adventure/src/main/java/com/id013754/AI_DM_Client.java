package com.id013754; // Make sure this matches your package name!

// Imports...
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Client class responsible for communicating with the Google Gemini API
 * to generate narrative content (descriptions, dialogue, etc.).
 */
public class AI_DM_Client {

    // config.properties should now be placed in src/main/resources
    private static final String CONFIG_FILE = "config.properties";
    private static final String API_KEY_PROPERTY = "GEMINI_API_KEY"; // Correct property name

    // Use gemini-1.5-flash-latest for faster, cheaper model
    private static final String API_MODEL = "gemini-2.0-flash";
    private static final String API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final String apiKey;
    private HttpClient httpClient;
    private Gson gson;

    /**
     * Constructs the AIDMClient.
     * Loads the API key and initializes the HTTP client and Gson parser.
     */
    public AI_DM_Client() {
        this.apiKey = loadApiKey();
        if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
            System.err.println("*********************************************************");
            // Updated error message to reflect new location
            System.err.println("ERROR: Gemini API Key not found or empty.");
            System.err.println("Please ensure '" + CONFIG_FILE + "' exists in 'src/main/resources'");
            System.err.println("and contains the line: " + API_KEY_PROPERTY + "=YOUR_API_KEY");
            System.err.println("*********************************************************");
            throw new RuntimeException(
                    "Failed to initialize AI Client: Gemini API Key is missing or invalid. Check src/main/resources/"
                            + CONFIG_FILE);
        }
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.gson = new Gson();
        System.out.println("AIDMClient initialized. API Key loaded.");
    }

    /**
     * Loads the Gemini API key from the configuration file located in the classpath
     * (expects config.properties in src/main/resources).
     *
     * @return The API key string, or null if not found or an error occurs.
     */
    private String loadApiKey() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Error: Cannot find configuration file '" + CONFIG_FILE
                        + "' in classpath (src/main/resources).");
                return null;
            }
            prop.load(input);
            String key = prop.getProperty(API_KEY_PROPERTY);
            if (key == null || key.trim().isEmpty()) {
                System.err.println(
                        "Error: Property '" + API_KEY_PROPERTY + "' not found or empty in '" + CONFIG_FILE + "'.");
                return null;
            }
            return key;
        } catch (IOException ex) {
            System.err.println("Error loading API key from " + CONFIG_FILE + ": " + ex.getMessage());
            return null;
        }
    }

    /**
     * Sends a prompt to the Gemini API and returns the generated text response.
     *
     * @param prompt The text prompt to send to the AI.
     * @return The AI-generated text response, or null in case of error.
     */
    @SuppressWarnings("UseSpecificCatch")
    public String generateContent(String prompt) {
        // ... (generateContent method remains the same as the previous version) ...
        if (apiKey == null) {
            System.err.println("AI Client Error: API Key is not available.");
            return null;
        }

        String apiUrlWithKey = API_BASE_URL + API_MODEL + ":generateContent?key=" + apiKey;

        try {
            JsonObject requestBodyJson = new JsonObject();
            JsonObject content = new JsonObject();
            JsonObject part = new JsonObject();
            part.addProperty("text", prompt);
            content.add("parts", gson.toJsonTree(new JsonObject[] { part }));
            requestBodyJson.add("contents", gson.toJsonTree(new JsonObject[] { content }));

            String requestBody = gson.toJson(requestBodyJson);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrlWithKey))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            System.out.println("[AI DM] DM is articulating his thought...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return parseResponse(response.body());
            } else {
                System.err.println("AI API Error: Received status code " + statusCode);
                System.err.println("Response Body: " + response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error communicating with AI API: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing AI API JSON response: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error during AI API call: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses the JSON response string from the Gemini API to extract the generated
     * text.
     */
    private String parseResponse(String responseBody) {
        // ... (parseResponse method remains the same as the previous version) ...
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            if (jsonResponse.has("candidates") && jsonResponse.get("candidates").isJsonArray()) {
                JsonObject candidate = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject();
                if (candidate.has("content") && candidate.get("content").isJsonObject()) {
                    JsonObject content = candidate.getAsJsonObject("content");
                    if (content.has("parts") && content.get("parts").isJsonArray()) {
                        JsonObject part = content.getAsJsonArray("parts").get(0).getAsJsonObject();
                        if (part.has("text") && part.get("text").isJsonPrimitive()) {
                            return part.get("text").getAsString();
                        }
                    }
                }
            }
            if (jsonResponse.has("promptFeedback")
                    && jsonResponse.getAsJsonObject("promptFeedback").has("blockReason")) {
                System.err.println("AI Warning: Prompt blocked. Reason: "
                        + jsonResponse.getAsJsonObject("promptFeedback").get("blockReason").getAsString());
                return "[AI blocked this response due to safety settings]";
            }

            System.err.println("AI Parsing Error: Could not find expected text in response structure.");
            System.err.println("Response Body: " + responseBody);
            return null;
        } catch (JsonSyntaxException | IllegalStateException | IndexOutOfBoundsException e) {
            System.err.println("Error parsing AI API JSON response structure: " + e.getMessage());
            System.err.println("Response Body: " + responseBody);
            return null;
        }
    }

} // End of AIDMClient class
