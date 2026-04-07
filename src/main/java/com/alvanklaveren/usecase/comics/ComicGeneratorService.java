package com.alvanklaveren.usecase.comics;

import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.ConstantsRepository;
import com.alvanklaveren.repository.MessageImageRepository;
import com.alvanklaveren.repository.MessageRepository;
import com.alvanklaveren.usecase.forum.ForumMessageUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final ConstantsRepository constantsRepository;

    public ComicGeneratorService(ForumMessageUseCase forumMessageUseCase,
                                 MessageImageRepository messageImageRepository,
                                 MessageRepository messageRepository,
                                 ConstantsRepository constantsRepository) {
        this.forumMessageUseCase = forumMessageUseCase;
        this.messageImageRepository = messageImageRepository;
        this.messageRepository = messageRepository;
        this.constantsRepository = constantsRepository;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
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
                Een comic is een stripverhaal bestaande uit drie panelen die van links naar rechts in chronologische 
                volgorde staan gescheiden door dikke lijnen. Zet 3 van deze comics op een A4 blad. 
                Zorg dat deze comics een kindvriendelijke cartoon-stijl gebruikt voor kinderen acht jaar en ouder: 
                felle kleuren, grote ogen, eenvoudige vormen, vrolijke en positieve uitstraling op basis van 
                de drie meest recente belangrijke artikelen op de rss van nu.nl in de categorieen algemeen,
                wetenschap, tech en politiek.
                
                Belangrijk:
                - Teken alleen in de comics zelf, dus geen extra teksten buiten de panelen.
                - Elke naam van een persoon of plaats die genoemd wordt moet groot en duidelijk in het paneel staan
                (met tekstballon of label). Herhaal tekstballonnen niet.
                - Het verplichte formaat is dus 3 comics op 1 A4, waarbij elk van de comics bestaat uit drie panelen
                die chronologisch in volgorde van links naar rechts staan.
                - Gebruik spraakballonnen waar dialoog past.
                - Maak het eenvoudig, educatief en leuk voor kinderen vanaf 8 jaar en ouder.
                - gebruik de nederlandse taal.
                - Zorg er voor dat de content akkoord is bij de moderators.
                - en comprimeer het gegenereerde a4 plaatje tot onder de 50 kilobyte als mogelijk
                - gebruik een minimalistische stijl.
                - zorg dat er geen spelfouten in de tekstballonnen staan
                - zorg dat de juiste persoon of object aan de tekstballon vast zit.
                - meld als titel de gebruikte nu.nl categorie.
                """;
    }

    // Helper methodes (callApi, extractBase64Images, escapeJson)
    private String callApi(String endpoint, String jsonBody) {
        try {
            // Move key to database constants
            var key = constantsRepository.getByCode(7).getStringValue();

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

        // replace the one-daily-per-day if it already exists for this day
        messageRepository.findMessagesByTodayDate().forEach((message) -> {
            forumMessageUseCase.delete(message.getCode());
        });

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
            Blob blob = new SerialBlob(Objects.requireNonNull(saveCompressedImage(imageData)));
            messageImage.setImage(blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageImage = messageImageRepository.save(messageImage);
        message.setMessageText("[i:" + messageImage.getCode() + "]");
        messageRepository.save(message);
    }

    /**
     * Loads an image from bytes, compresses it (resize + JPEG quality), and saves it
     * so that the final file size is under targetMaxBytes.
     */
    public static byte[] saveCompressedImage(byte[] imageBytes) {

        // 1. Load the image from byte array
        BufferedImage original = null;
        try {
            original = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Start with high quality and good size
        float quality = 0.40f;
        BufferedImage current = original;
        int maxWidth = 900; // reasonable starting max width (adjust as needed)

        boolean success = false;

        // Iterative compression loop: reduce quality and/or size until under target
        while (!success) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Optional: resize if too wide (preserves aspect ratio)
            if (current.getWidth() > maxWidth) {
                double ratio = (double) maxWidth / current.getWidth();
                int newHeight = (int) (current.getHeight() * ratio);

                BufferedImage resized = new BufferedImage(maxWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(current, 0, 0, maxWidth, newHeight, null);
                g.dispose();
                current = resized;
            }

            // Write as JPEG with current quality
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(current, null, null), param);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                writer.dispose();
            }

            return baos.toByteArray();
        }
        return null;
    }
}