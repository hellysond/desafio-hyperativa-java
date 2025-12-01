package br.com.hellyson.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CryptoUtil {

    private static final String ALGO = "AES";
    private final SecretKeySpec key;

    public CryptoUtil(@Value("${app.aes.key}") String secret) {
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGO);
    }

    public String encrypt(String text) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(c.doFinal(text.getBytes()));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public String hash(String card) {
        return DigestUtils.sha256Hex(card);
    }

}
