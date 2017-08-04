package ru.u26c4.logic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import ru.u26c4.logic.util.RedisKey;
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
    private HistoryLogic historyLogic;

    @Autowired
    public NotesLogicImpl(HistoryLogic historyLogic,
                          @Qualifier("redisNoteTemplate") RedisTemplate<String, Note> redisTemplate) {
        this.historyLogic = historyLogic;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initData() {
        for (Note note : FakeData.list()) {
            redisTemplate.opsForValue().set(RedisKey.NOTE.prefix() + note.getId(), note);
            historyLogic.save(note);
        }
    }

    @Override
    public List<Note> notes() {
        Set<String> keys = redisTemplate.keys(RedisKey.NOTE.prefix() + "*");
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
        String key = RedisKey.NOTE.prefix() + note.getId();
        if (redisTemplate.hasKey(key)) {
            note.setModifyDate(new Date());
            note.setModifyUser(user.getUsername());
        }

        redisTemplate.opsForValue().set(key, note);
        historyLogic.save(note);
    }

    @Override
    public void del(User user, String id) {
        String key = RedisKey.NOTE.prefix() + id;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
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
