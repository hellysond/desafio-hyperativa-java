package br.com.hellyson.controller;

import br.com.hellyson.model.dto.CardRequest;
import br.com.hellyson.model.dto.CardResponse;
import br.com.hellyson.service.CardService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService service;

    public CardController(CardService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CardResponse> insert(@RequestBody CardRequest req) {
        return ResponseEntity.ok(service.insertCard(req.cardNumber()));
    }

    @GetMapping("/{cardNumber}")
    public ResponseEntity<?> find(@PathVariable String cardNumber) {
        return service.findCard(cardNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("empty file");
        }

        service.processCardFile(file);

        return ResponseEntity.ok("File uploaded to be processed");
    }
}
