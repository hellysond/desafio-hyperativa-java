package br.com.hellyson.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardFileProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "card-file-lines";

    public void sendLine(String line) {
        kafkaTemplate.send(TOPIC, line);
    }
}
