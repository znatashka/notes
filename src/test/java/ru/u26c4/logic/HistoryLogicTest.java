package ru.u26c4.logic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.u26c4.logic.util.HistoryUtils;
import ru.u26c4.logic.util.RedisKey;
import ru.u26c4.model.History;
import ru.u26c4.model.Note;
import ru.u26c4.model.NoteBuilder;

import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@SuppressWarnings("unchecked")
public class HistoryLogicTest {

    @Autowired
    private HistoryLogic historyLogic;
    @Autowired
    private HistoryUtils historyUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ValueOperations valueOperations;

    private Note note;

    @Before
    public void setUp() {
        note = new NoteBuilder()
                .id("id")
                .text("text")
                .createUser("bender")
                .createDate(new Date())
                .build();
    }

    @Test
    public void insert() {
        // PREPARE
        String key = RedisKey.HISTORY.prefix() + note.getId();

        when(historyUtils.isModified(note)).thenReturn(true);
        when(redisTemplate.hasKey(key)).thenReturn(false);
        when(historyUtils.modifier(note)).thenReturn(note.getCreateUser());
        when(historyUtils.modifyDate(note)).thenReturn(note.getCreateDate());

        // ACT
        historyLogic.save(note);

        // ASSERT
        verify(historyUtils, atLeastOnce()).isModified(note);
        verify(redisTemplate, atLeastOnce()).hasKey(key);
        verify(historyUtils, atLeastOnce()).modifier(note);
        verify(historyUtils, atLeastOnce()).modifyDate(note);
        verify(valueOperations, atLeastOnce()).set(eq(key), argThat(new ArgumentMatcher<History>() {
            @Override
            public boolean matches(Object o) {
                History history = (History) o;
                History.Item item = history.getItems().get(0);

                return history.getNoteId().equals(note.getId()) &&
                        item.getText().equals(note.getText()) &&
                        item.getModifier().equals(note.getCreateUser()) &&
                        item.getModifyDate().equals(note.getCreateDate());
            }
        }));
    }

    @Test
    public void update() {
        // PREPARE
        String key = RedisKey.HISTORY.prefix() + note.getId();

        note.setModifyUser("modifier");
        note.setModifyDate(new Date());

        when(historyUtils.isModified(note)).thenReturn(true);
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(valueOperations.get(key)).thenReturn(new History(note.getId()));
        when(historyUtils.modifier(note)).thenReturn(note.getModifyUser());
        when(historyUtils.modifyDate(note)).thenReturn(note.getModifyDate());

        // ACT
        historyLogic.save(note);

        // ASSERT
        verify(historyUtils, atLeastOnce()).isModified(note);
        verify(redisTemplate, atLeastOnce()).hasKey(key);
        verify(valueOperations, atLeastOnce()).get(key);
        verify(historyUtils, atLeastOnce()).modifier(note);
        verify(historyUtils, atLeastOnce()).modifyDate(note);
        verify(valueOperations, atLeastOnce()).set(eq(key), argThat(new ArgumentMatcher<History>() {
            @Override
            public boolean matches(Object o) {
                History history = (History) o;
                History.Item item = history.getItems().get(0);

                return history.getNoteId().equals(note.getId()) &&
                        item.getText().equals(note.getText()) &&
                        item.getModifier().equals(note.getModifyUser()) &&
                        item.getModifyDate().equals(note.getModifyDate());
            }
        }));
    }

    @Test
    public void get() {
        // ACT
        historyLogic.get(note.getId());

        // ASSERT
        verify(valueOperations, atLeastOnce()).get(RedisKey.HISTORY.prefix() + note.getId());
    }

    @Configuration
    public static class ContextConfiguration {

        @Bean
        public ValueOperations valueOperations() {
            return mock(ValueOperations.class);
        }

        @Bean
        public RedisTemplate redisTemplate() {
            RedisTemplate redisTemplateMock = mock(RedisTemplate.class);

            when(redisTemplateMock.opsForValue()).thenReturn(valueOperations());

            return redisTemplateMock;
        }

        @Bean
        public HistoryUtils historyUtils() {
            return mock(HistoryUtils.class);
        }

        @Bean
        public HistoryLogic historyLogic() {
            return new HistoryLogicImpl(historyUtils(), redisTemplate());
        }
    }
}