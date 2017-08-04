package ru.u26c4.logic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.u26c4.logic.util.RedisKey;
import ru.u26c4.model.Note;
import ru.u26c4.model.NoteBuilder;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@SuppressWarnings("unchecked")
public class NotesLogicTest {

    private static final String USERNAME = "test";

    @Autowired
    private NotesLogic notesLogic;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ValueOperations valueOperations;
    @Autowired
    private HistoryLogic historyLogic;

    private User user;
    private Note note;

    @Before
    public void setUp() {
        user = (User) User
                .withUsername(USERNAME)
                .password("secret")
                .roles("USER")
                .build();

        note = new NoteBuilder()
                .id("id")
                .text("text")
                .createUser(USERNAME)
                .createDate(new Date())
                .build();
    }

    @Test
    public void notes() {
        // ACT
        notesLogic.notes();

        // ASSERT
        verify(redisTemplate).keys(RedisKey.NOTE.prefix() + "*");
        verify(valueOperations).multiGet(anyCollectionOf(String.class));
    }

    @Test
    public void create() {
        // ACT
        Note note = notesLogic.create(user);

        // ASSERT
        assertNotNull(note);
        assertNotNull(note.getId());
        assertEquals(note.getCreateUser(), USERNAME);
    }

    @Test
    public void insert() {
        // PREPARE
        when(redisTemplate.hasKey(RedisKey.NOTE.prefix() + note.getId())).thenReturn(false);

        // ACT
        notesLogic.save(user, note);

        // ASSERT
        verify(redisTemplate, atLeastOnce()).hasKey(RedisKey.NOTE.prefix() + note.getId());
        verify(valueOperations, atLeastOnce()).set(RedisKey.NOTE.prefix() + note.getId(), note);
        verify(historyLogic, atLeastOnce()).save(note);
    }

    @Test
    public void update() {
        // PREPARE
        when(redisTemplate.hasKey(RedisKey.NOTE.prefix() + note.getId())).thenReturn(true);

        // ACT
        notesLogic.save(user, note);

        // ASSERT
        verify(redisTemplate, atLeastOnce()).hasKey(RedisKey.NOTE.prefix() + note.getId());
        verify(valueOperations, atLeastOnce()).set(RedisKey.NOTE.prefix() + note.getId(), note);
        verify(historyLogic, atLeastOnce()).save(note);
    }

    @Test
    public void del() {
        // PREPARE
        when(redisTemplate.hasKey(RedisKey.NOTE.prefix() + note.getId())).thenReturn(true);

        // ACT
        notesLogic.del(user, note.getId());

        // ASSERT
        verify(redisTemplate, atLeastOnce()).hasKey(RedisKey.NOTE.prefix() + note.getId());
        verify(redisTemplate, atLeastOnce()).delete(RedisKey.NOTE.prefix() + note.getId());
    }

    @Configuration
    @SuppressWarnings("unchecked")
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
        public HistoryLogic historyLogic() {
            return mock(HistoryLogic.class);
        }

        @Bean
        public NotesLogic notesLogic() {
            return new NotesLogicImpl(historyLogic(), redisTemplate());
        }
    }
}