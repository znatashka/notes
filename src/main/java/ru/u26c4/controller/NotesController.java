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

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
public class NotesController {

    @RequestMapping(value = "notes", method = RequestMethod.GET)
    public ResponseEntity<List<Note>> notes(@AuthenticationPrincipal User user) {
        log.debug("/notes; user={}", user.getUsername());

        return new ResponseEntity<>(Collections.<Note>emptyList(), HttpStatus.OK);
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Note> create(@AuthenticationPrincipal User user) {
        log.debug("/create; user={}", user.getUsername());

        return new ResponseEntity<>(
                new Note(RandomStringUtils.randomAlphanumeric(6), "user", new Date()),
                HttpStatus.OK);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity save(@AuthenticationPrincipal User user, @RequestBody Note note) {
        log.debug("/save; user={}, note={}", user.getUsername(), note);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "del/{id}", method = RequestMethod.DELETE)
    public ResponseEntity del(@AuthenticationPrincipal User user, @PathVariable("id") String id) {
        log.debug("/del; user={}, note={}", user.getUsername(), id);

        return new ResponseEntity(HttpStatus.OK);
    }
}
