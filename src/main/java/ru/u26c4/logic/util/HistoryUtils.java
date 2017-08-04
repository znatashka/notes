package ru.u26c4.logic.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.u26c4.model.Note;

import java.util.Date;

/**
 * Утилитный класс для журнала изменений заметок
 */
@Component
public class HistoryUtils {

    private RedisTemplate<String, Note> redisTemplate;

    @Autowired
    public HistoryUtils(@Qualifier("redisNoteTemplate") RedisTemplate<String, Note> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isModified(Note note) {
        if (redisTemplate.hasKey(note.getId())) {
            Note oldNote = redisTemplate.opsForValue().get(RedisKey.NOTE.prefix() + note.getId());
            if (note.getText().equals(oldNote.getText())) {
                return false;
            }
        }
        return true;
    }

    public String modifier(Note note) {
        return note.getModifyUser() == null ? note.getCreateUser() : note.getModifyUser();
    }

    public Date modifyDate(Note note) {
        return note.getModifyDate() == null ? note.getCreateDate() : note.getModifyDate();
    }
}
