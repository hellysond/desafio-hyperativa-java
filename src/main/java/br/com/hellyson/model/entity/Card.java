package br.com.hellyson.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Card {

    @UuidGenerator
    @Id
    @JdbcTypeCode(Types.CHAR)
    @Column(name = "id", nullable = false, length = 36,columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String encryptedCardNumber;

    @Column(nullable = false)
    private String hash;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
