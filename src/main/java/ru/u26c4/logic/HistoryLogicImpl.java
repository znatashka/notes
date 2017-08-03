package ru.u26c4.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.u26c4.logic.util.HistoryUtils;
import ru.u26c4.model.History;
import ru.u26c4.model.Note;

@Component
public class HistoryLogicImpl implements HistoryLogic {

    private RedisTemplate<String, History> redisTemplate;
    private HistoryUtils historyUtils;

    @Autowired
    public HistoryLogicImpl(HistoryUtils historyUtils,
                            @Qualifier("redisHistoryTemplate") RedisTemplate<String, History> redisTemplate) {
        this.historyUtils = historyUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Note note) {
        if (historyUtils.isModified(note)) {
            History history;
            if (redisTemplate.hasKey(note.getId())) {
                history = redisTemplate.opsForValue().get(note.getId());
            } else {
                history = new History(note.getId());
            }
            history.getItems().add(history.new Item(historyUtils.modifier(note), historyUtils.modifyDate(note), note.getText()));
            redisTemplate.opsForValue().set(note.getId(), history);
        }
    }

    @Override
    public History get(String noteId) {
        return redisTemplate.opsForValue().get(noteId);
    }
}
