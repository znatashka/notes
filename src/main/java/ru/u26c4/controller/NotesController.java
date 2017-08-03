package ru.u26c4.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.u26c4.logic.NotesLogic;
import ru.u26c4.model.Note;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class NotesController {

    private NotesLogic notesLogic;

    @Autowired
    public NotesController(NotesLogic notesLogic) {
        this.notesLogic = notesLogic;
    }

    @RequestMapping(value = "notes", method = RequestMethod.POST)
    public ResponseEntity<List<Note>> notes(@AuthenticationPrincipal User user) {
        log.debug("/notes; user={}", user.getUsername());

        return new ResponseEntity<>(notesLogic.notes(), HttpStatus.OK);
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Note> create(@AuthenticationPrincipal User user) {
        log.debug("/create; user={}", user.getUsername());

        return new ResponseEntity<>(notesLogic.create(user), HttpStatus.OK);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity save(@AuthenticationPrincipal User user, @RequestBody Note note, HttpServletRequest request) {
        log.debug("/save; user={}, note={}", user.getUsername(), note);

        notesLogic.save(user, note);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    public ResponseEntity del(@AuthenticationPrincipal User user, @RequestBody String id) {
        log.debug("/del; user={}, note={}", user.getUsername(), id);

        notesLogic.del(user, id);

        return new ResponseEntity(HttpStatus.OK);
    }
}