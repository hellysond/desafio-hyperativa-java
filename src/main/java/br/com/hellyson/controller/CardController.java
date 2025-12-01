package br.com.hellyson.controller;

import br.com.hellyson.model.dto.CardRequest;
import br.com.hellyson.model.dto.CardResponse;
import br.com.hellyson.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Card API", description = "API for creating, retrieving, and uploading cards")
public class CardController {

    private final CardService service;

    public CardController(CardService service) {
        this.service = service;
    }

    @Operation(
            summary = "Insert a new card",
            description = "Receives a card number and registers it in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card successfully inserted",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CardResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping
    public ResponseEntity<CardResponse> insert(
            @Parameter(description = "Card data to be inserted", required = true)
            @RequestBody CardRequest req) {
        return ResponseEntity.ok(service.insertCard(req.cardNumber()));
    }

    @Operation(
            summary = "Retrieve a card",
            description = "Searches for a card by its number and returns its details if found.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CardResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @GetMapping("/{cardNumber}")
    public ResponseEntity<?> find(
            @Parameter(description = "Card number to be retrieved", required = true, example = "1234567812345678")
            @PathVariable String cardNumber) {
        return service.findCard(cardNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Upload a file with cards",
            description = "Allows uploading a file (TXT) containing card numbers for batch processing.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "File uploaded for processing"),
                    @ApiResponse(responseCode = "400", description = "Empty or invalid file")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "File containing card numbers", required = true)
            @RequestParam("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("empty file");
        }

        service.processCardFile(file);
        return ResponseEntity.ok("File uploaded to be processed");
    }
}
