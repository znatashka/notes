package ru.u26c4.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class EmbeddedRedis {

    private RedisServer redisServer;

    @Autowired
    public EmbeddedRedis(RedisServer redisServer) {
        this.redisServer = redisServer;
    }

    @PostConstruct
    public void startRedis() {
        log.info("starting redis...");
        if (!redisServer.isActive()) {
            redisServer.start();
        }
        log.info("redis listen ports: {}", redisServer.ports());
    }

    @PreDestroy
    public void stop() {
        log.info("shutting down redis...");
        redisServer.stop();
        log.info("bye!");
    }
}
