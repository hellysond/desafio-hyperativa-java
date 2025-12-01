package br.com.hellyson.model.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record CardResponse(UUID id, String encryptedCardNumber, String hash, Timestamp createdAt) {}
