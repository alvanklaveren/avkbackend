package com.alvanklaveren.usecase.comics;

import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.MessageImageRepository;
import com.alvanklaveren.repository.MessageRepository;
import com.alvanklaveren.usecase.forum.ForumMessageUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Blob;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ComicGeneratorService {

    private static final String API_BASE = "https://api.x.ai/v1";
    private static final String MODEL = "grok-imagine-image";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final ForumMessageUseCase forumMessageUseCase;
    private final MessageImageRepository messageImageRepository;
    private final MessageRepository messageRepository;

    public ComicGeneratorService(ForumMessageUseCase forumMessageUseCase,
                                 MessageImageRepository messageImageRepository,
                                 MessageRepository messageRepository) {
        this.forumMessageUseCase = forumMessageUseCase;
        this.messageImageRepository = messageImageRepository;
        this.messageRepository = messageRepository;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetch the most recent news items of NU.nl and generate 2 comics for it
     *
     * @return Map containing news items and the two comics as base64 PNG
     */
    public byte[] generateComicsForLatestNews() {

        return generateThreeComics();
    }

    /**
     * Generates 3 comics for three specific news item
     */
    public byte[] generateThreeComics() {

        return generateWithoutStyle();
    }

    private byte[] generateWithoutStyle() {
        String prompt = buildBasePrompt();

        String jsonBody = """
                {
                  "model": "%s",
                  "prompt": "%s",
                  "n": 2,
                  "aspect_ratio": "2:1",
                  "resolution": "1k",
                  "response_format": "b64_json"
                }
                """.formatted(MODEL, escapeJson(prompt));

        String responseJson = callApi("/images/generations", jsonBody);
        return extractBase64Images(responseJson);
    }

    private String buildBasePrompt() {
        return """
                Zet de volgende zaken op een, en ook niet meer dan een, A4 blad:
                Maak drie comics bestaande uit EXACT drie panelen naast elkaar (links-midden-rechts),
                gescheiden door dikke zwarte lijnen. Zorg dat deze comic een kindvriendelijke cartoon-stijl gebruikt
                voor kinderen zes jaar en ouder: felle kleuren, grote ogen, eenvoudige vormen, vrolijke en positieve
                uitstraling op basis van de drie meest recente artikelen op de rss van nu.nl in de categorieen
                wetenschap, tech en politiek.
                
                Belangrijk:
                - Elke naam van een persoon of plaats die genoemd wordt moet groot en duidelijk in het paneel staan
                (met tekstballon of label).
                - Elke comic bestaande uit drie van deze panelen zetten de panelen naast elkaar van links naar rechts.
                - Gebruik spraakballonnen waar dialoog past.
                - Maak het eenvoudig, educatief en leuk voor kinderen vanaf 6 jaar en ouder.
                - gebruik de nederlandse taal.
                - Zorg er voor dat de content akkoord is bij de moderators.
                - en comprimeer het gegenereerde a4 plaatje tot onder de 50 kilobyte als mogelijk
                - gebruik een minimalistische stijl.
                """;
    }

    // Helper methodes (callApi, extractBase64Images, escapeJson)
    private String callApi(String endpoint, String jsonBody) {
        try {
            // Move key to database constants
            var key = "";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + key)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Grok API error: " + response.statusCode() + " - " + response.body());
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Error calling Grok API", e);
        }
    }

    private byte[] extractBase64Images(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode data = root.path("data");

            byte[] image = new byte[0];
            for (JsonNode item : data) {
                String b64 = item.path("b64_json").asText();
                if (!b64.isEmpty()) {
                    image = Base64.getDecoder().decode(b64);
                }
            }
            return image;
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse the Grok API response", e);
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    public void saveSelectedComic(byte[] imageData) {
        MessageDTO messageDTO = new MessageDTO();

        MessageCategoryDTO messageCategoryDTO = new MessageCategoryDTO();
        messageCategoryDTO.code = -2; // Dailies message group
        messageDTO.messageCategory = messageCategoryDTO;

        ForumUserDTO forumUserDTO = new ForumUserDTO();
        forumUserDTO.code = 1;
        messageDTO.forumUser = forumUserDTO;

        messageDTO.messageDate = Date.from(Instant.now());
        messageDTO.description = "Dailies ";
        messageDTO.messageText = "";

        messageDTO = forumMessageUseCase.save(messageDTO);

        Message message = messageRepository.findByCode(messageDTO.code)
                .orElseThrow(() -> new RuntimeException("Could not save dailies message"));

        MessageImage messageImage = new MessageImage();
        messageImage.setMessage(message);
        messageImage.setSortorder(0);
        try {
            Blob blob = new SerialBlob(imageData);
            messageImage.setImage(blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageImage = messageImageRepository.save(messageImage);
        message.setMessageText("[i:" + messageImage.getCode() + "]");
    }
}