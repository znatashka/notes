package ru.u26c4.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.u26c4.logic.util.HistoryUtils;
import ru.u26c4.logic.util.RedisKey;
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
        String key = RedisKey.HISTORY.prefix() + note.getId();
        if (historyUtils.isModified(note)) {
            History history;
            if (redisTemplate.hasKey(key)) {
                history = redisTemplate.opsForValue().get(key);
            } else {
                history = new History(note.getId());
            }
            history.getItems().add(history.new Item(historyUtils.modifier(note), historyUtils.modifyDate(note), note.getText()));
            redisTemplate.opsForValue().set(key, history);
        }
    }

    @Override
    public History get(String noteId) {
        return redisTemplate.opsForValue().get(RedisKey.HISTORY.prefix() + noteId);
    }
}
