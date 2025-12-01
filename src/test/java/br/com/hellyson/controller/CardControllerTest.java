package java.br.com.hellyson.controller;

import br.com.hellyson.controller.CardController;
import br.com.hellyson.model.dto.CardResponse;
import br.com.hellyson.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @Test
    @DisplayName("POST /api/v1/cards")
    void insertCard() throws Exception {
        UUID generatedId = UUID.randomUUID();
        Timestamp createdAt = Timestamp.from(Instant.now());

        CardResponse cardResponse = new CardResponse(
                generatedId,
                "encryptedValue",
                "hashValue",
                createdAt
        );

        Mockito.when(cardService.insertCard("4456897999999999"))
                .thenReturn(cardResponse);

        String payload = """
                {
                  "cardNumber": "4456897999999999"
                }
                """;

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.encryptedCardNumber").value("encryptedValue"))
                .andExpect(jsonPath("$.hash").value("hashValue"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/cards/{cardNumber}")
    void findCardExists() throws Exception {
        UUID generatedId = UUID.randomUUID();
        Timestamp createdAt = Timestamp.from(Instant.now());

        CardResponse cardResponse = new CardResponse(
                generatedId,
                "encryptedValue",
                "hashValue",
                createdAt
        );

        Mockito.when(cardService.findCard("4456897999999999"))
                .thenReturn(Optional.of(cardResponse));

        mockMvc.perform(get("/api/v1/cards/4456897999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.encryptedCardNumber").value("encryptedValue"))
                .andExpect(jsonPath("$.hash").value("hashValue"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/cards/{cardNumber}")
    void findCardNotExists() throws Exception {
        Mockito.when(cardService.findCard("0000000000000000"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cards/0000000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/cards/upload")
    void uploadFile() throws Exception {
        String fileContent = """
                DESAFIO-HYPERATIVA 20180524LOTE0001000010
                C1     4456897922969999
                C2     4456897999999999
                LOTE0001000010
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cards.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileContent.getBytes(StandardCharsets.UTF_8)
        );

        Mockito.doNothing().when(cardService).processCardFile(file);

        mockMvc.perform(multipart("/api/v1/cards/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded to be processed"));
    }

    @Test
    @DisplayName("POST /api/v1/cards/upload")
    void uploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/cards/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("empty file"));
    }
}
