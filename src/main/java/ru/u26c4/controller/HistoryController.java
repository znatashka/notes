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
import ru.u26c4.logic.HistoryLogic;
import ru.u26c4.model.History;

@Slf4j
@RestController
public class HistoryController {

    private HistoryLogic historyLogic;

    @Autowired
    public HistoryController(HistoryLogic historyLogic) {
        this.historyLogic = historyLogic;
    }

    @RequestMapping(value = "history", method = RequestMethod.POST)
    public ResponseEntity<History> history(@AuthenticationPrincipal User user, @RequestBody String noteId) {
        log.debug("/history; user={}", user.getUsername());

        return new ResponseEntity<>(historyLogic.get(noteId), HttpStatus.OK);
    }
}
