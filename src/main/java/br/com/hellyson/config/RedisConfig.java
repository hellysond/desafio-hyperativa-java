package br.com.hellyson.config;

import br.com.hellyson.model.dto.CardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, CardResponse> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CardResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<CardResponse> serializer =
                new Jackson2JsonRedisSerializer<>(CardResponse.class);
        ObjectMapper mapper = new ObjectMapper();
        serializer.setObjectMapper(mapper);
        template.setDefaultSerializer(serializer);
        template.setKeySerializer(template.getStringSerializer());

        return template;
    }
}
