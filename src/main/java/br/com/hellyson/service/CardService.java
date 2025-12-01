package br.com.hellyson.service;

import br.com.hellyson.kafka.CardFileProducer;
import br.com.hellyson.model.dto.CardResponse;
import br.com.hellyson.model.entity.Card;
import br.com.hellyson.repository.CardRepository;
import br.com.hellyson.util.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CardService {

    private final CardRepository repository;
    private final CryptoUtil crypto;
    private final RedisTemplate<String, CardResponse> redis;
    private final CardFileProducer producer;

    public CardService(CardRepository repository, CryptoUtil crypto, RedisTemplate<String, CardResponse> redis, CardFileProducer cardFileProducer, CardFileProducer producer) {
        this.repository = repository;
        this.crypto = crypto;
        this.redis = redis;
        this.producer = producer;
    }

    public CardResponse insertCard(String cardNumber) {
        String hash = crypto.hash(cardNumber);

        if (repository.findByHash(hash).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card already exists");

        Card card = new Card();
        card.setEncryptedCardNumber(crypto.encrypt(cardNumber));
        card.setHash(hash);
        card.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        var cardEntity = repository.save(card);

        var cardResponse = new CardResponse(cardEntity.getId(), cardEntity.getEncryptedCardNumber(), cardEntity.getHash(),cardEntity.getCreatedAt());

        redis.opsForValue().set(hash, cardResponse);

        return cardResponse;
    }

    public Optional<CardResponse> findCard(String cardNumber) {
        String hash = crypto.hash(cardNumber);

        CardResponse cached = redis.opsForValue().get(hash);

        if (cached != null) return Optional.of(cached);

        return repository.findByHash(hash).map(cardEntity -> {
           var cardReponse = new CardResponse(cardEntity.getId(), cardEntity.getEncryptedCardNumber(), cardEntity.getHash(),cardEntity.getCreatedAt());
            redis.opsForValue().set(hash,cardReponse);
            return cardReponse;
        });
    }

    public void processCardFile(MultipartFile file) throws IOException {
        List<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                .lines()
                .toList();

        for (String line : lines) {
            producer.sendLine(line);
        }
    }
}
