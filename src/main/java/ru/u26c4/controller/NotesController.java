package ru.u26c4.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@Controller
public class NotesController {

    @RequestMapping(value = "notes", method = RequestMethod.POST)
    public ResponseEntity<List<Note>> notes(@AuthenticationPrincipal User user) {
        log.debug("/notes; user={}", user.getUsername());

        return new ResponseEntity<>(FakeData.list(user), HttpStatus.OK);
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Note> create(@AuthenticationPrincipal User user) {
        log.debug("/create; user={}", user.getUsername());

        return new ResponseEntity<>(FakeData.empty(user), HttpStatus.OK);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity save(@AuthenticationPrincipal User user, @RequestBody Note note) {
        log.debug("/save; user={}, note={}", user.getUsername(), note);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "del/{id}", method = RequestMethod.POST)
    public ResponseEntity del(@AuthenticationPrincipal User user, @PathVariable("id") String id) {
        log.debug("/del; user={}, note={}", user.getUsername(), id);

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
                    .createDate(generateDate())
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