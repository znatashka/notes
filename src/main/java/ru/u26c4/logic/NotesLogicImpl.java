package ru.u26c4.logic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import ru.u26c4.model.Note;
import ru.u26c4.model.NoteBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
@Component
public class NotesLogicImpl implements NotesLogic {

    @Override
    public ResponseEntity<List<Note>> notes(User user) {

        return new ResponseEntity<>(FakeData.list(user), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Note> create(User user) {

        return new ResponseEntity<>(FakeData.empty(user), HttpStatus.OK);
    }

    @Override
    public ResponseEntity save(User user, Note note) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @Override
    public ResponseEntity del(User user, String id) {

        return new ResponseEntity(HttpStatus.OK);
    }

    static class FakeData {

        private static Random random = new Random();

        static List<Note> list(User user) {
            List<Note> notes = new ArrayList<>();
            IntStream.range(0, 5).forEach(i -> notes.add(new NoteBuilder()
                    .id(RandomStringUtils.randomAlphanumeric(6))
                    .createUser(user.getUsername())
                    .createDate(generateDate())
                    .text(generateText())
                    .build())
            );
            return notes;
        }

        static Note empty(User user) {
            return new NoteBuilder()
                    .id(RandomStringUtils.randomAlphanumeric(6))
                    .createUser(user.getUsername())
                    .createDate(new Date())
                    .build();
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
