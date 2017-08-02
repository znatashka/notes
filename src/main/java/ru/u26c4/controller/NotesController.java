package ru.u26c4.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.u26c4.logic.NotesLogic;
import ru.u26c4.model.Note;

import java.util.List;

@Slf4j
@Controller
public class NotesController {

    private NotesLogic notesLogic;

    @Autowired
    public NotesController(NotesLogic notesLogic) {
        this.notesLogic = notesLogic;
    }

    @RequestMapping(value = "notes", method = RequestMethod.POST)
    public ResponseEntity<List<Note>> notes(@AuthenticationPrincipal User user) {
        log.debug("/notes; user={}", user.getUsername());

        return notesLogic.notes(user);
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Note> create(@AuthenticationPrincipal User user) {
        log.debug("/create; user={}", user.getUsername());

        return notesLogic.create(user);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity save(@AuthenticationPrincipal User user, @RequestBody Note note) {
        log.debug("/save; user={}, note={}", user.getUsername(), note);

        return notesLogic.save(user, note);
    }

    @RequestMapping(value = "del/{id}", method = RequestMethod.POST)
    public ResponseEntity del(@AuthenticationPrincipal User user, @PathVariable("id") String id) {
        log.debug("/del; user={}, note={}", user.getUsername(), id);

        return notesLogic.del(user, id);
    }
}