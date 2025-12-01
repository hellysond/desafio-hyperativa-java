package br.com.hellyson.repository;

import br.com.hellyson.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByHash(String hash);
}
