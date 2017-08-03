package ru.u26c4.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Protocol;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;
import ru.u26c4.model.History;
import ru.u26c4.model.Note;

@Configuration
public class RedisConfig {

    @Bean
    public RedisServer redisServer() {
        RedisServerBuilder builder = RedisServer.builder();
        builder.reset();
        return builder.port(Protocol.DEFAULT_PORT).build();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    @Qualifier("redisNoteTemplate")
    public RedisTemplate<String, Note> redisNoteTemplate() {
        RedisTemplate<String, Note> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    @Qualifier("redisHistoryTemplate")
    public RedisTemplate<String, History> redisHistoryTemplate() {
        RedisTemplate<String, History> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}