package br.com.hellyson.kafka;

import br.com.hellyson.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardFileListener {

    private final CardService cardService;

    @KafkaListener(topics = "card-file-lines", groupId = "hyperativa-group")
    public void consume(String line) {
        try {
            processLine(line);
        } catch (Exception e) {
            log.error("error line: {}", line, e);
        }
    }

    private void processLine(String line) {
        line = line.replace("\u0000", "").trim();

        if (line.startsWith("DESAFIO-HYPERATIVA")) {
            log.info("header must be skipped");
            return;
        }

        if (line.startsWith("LOTE")) {
            log.info("footer must be skipped: {}", line);
            return;
        }
        if (line.startsWith("C")) {

            Pattern pattern = Pattern.compile("^C\\d+\\s+(\\d{15,19})");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String cardNumber = matcher.group(1);
                log.info("saving card number - {}", cardNumber);
                cardService.insertCard(cardNumber);
            } else {
                log.warn("error invalid line - : {}", line);
            }
        }
    }

}
