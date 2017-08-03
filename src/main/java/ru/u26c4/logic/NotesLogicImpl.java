package ru.u26c4.logic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import ru.u26c4.model.Note;
import ru.u26c4.model.NoteBuilder;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component
public class NotesLogicImpl implements NotesLogic {

    private RedisTemplate<String, Note> redisTemplate;

    @Autowired
    public NotesLogicImpl(RedisTemplate<String, Note> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initData() {
        for (Note note : FakeData.list()) {
            redisTemplate.opsForValue().set(note.getId(), note);
        }
    }

    @Override
    public List<Note> notes() {
        Set<String> keys = redisTemplate.keys("*");
        List<Note> notes = redisTemplate.opsForValue().multiGet(keys);

        notes.sort(Comparator.comparing(Note::getCreateDate));

        return notes;
    }

    @Override
    public Note create(User user) {
        return FakeData.empty(user);
    }

    @Override
    public void save(User user, Note note) {
        if (redisTemplate.hasKey(note.getId())) {
            note.setModifyDate(new Date());
            note.setModifyUser(user.getUsername());
        }

        redisTemplate.opsForValue().set(note.getId(), note);
    }

    @Override
    public void del(User user, String id) {
        if (redisTemplate.hasKey(id)) {
            redisTemplate.delete(id);
        }
    }

    static class FakeData {

        private static final String FAKE_USER = "bender";

        private static Random random = new Random();

        static List<Note> list() {
            List<Note> notes = new ArrayList<>();
            IntStream.range(0, 5).forEach(i -> notes.add(new NoteBuilder()
                    .id(generateId())
                    .createUser(FAKE_USER)
                    .createDate(generateDate())
                    .text(generateText())
                    .build())
            );
            return notes;
        }

        static Note empty(User user) {
            return new NoteBuilder()
                    .id(generateId())
                    .createUser(user.getUsername())
                    .createDate(new Date())
                    .build();
        }

        private static String generateId() {
            return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        }

        private static String generateText() {
            return RandomStringUtils.randomAlphabetic(random.nextInt(400));
        }

        private static Date generateDate() {
            return Date.from(
                    LocalDateTime.now()
                            .minusDays(random.nextInt(200))
                            .minusMinutes(random.nextInt(200))
                            .toInstant(ZoneOffset.UTC)
            );
        }
    }
}
