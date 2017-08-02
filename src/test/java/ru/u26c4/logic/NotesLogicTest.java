package ru.u26c4.logic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.u26c4.model.Note;
import ru.u26c4.model.NoteBuilder;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@SuppressWarnings("unchecked")
public class NotesLogicTest {

    private static final String USERNAME = "test";
    private static final int OK_CODE = 200;

    @Autowired
    private NotesLogic notesLogic;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ValueOperations valueOperations;

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
        ResponseEntity<List<Note>> response = notesLogic.notes();

        // ASSERT
        verify(redisTemplate).keys("*");
        verify(valueOperations).multiGet(anyCollectionOf(String.class));

        responseIsOk(response);
    }

    @Test
    public void create() {
        // ACT
        ResponseEntity<Note> response = notesLogic.create(user);

        // ASSERT
        responseIsOk(response);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(response.getBody().getCreateUser(), USERNAME);
    }

    @Test
    public void insert() {
        // PREPARE
        when(redisTemplate.hasKey(note.getId())).thenReturn(false);

        // ACT
        ResponseEntity response = notesLogic.save(user, note);

        // ASSERT
        responseIsOk(response);

        verify(redisTemplate, atLeastOnce()).hasKey(note.getId());
        verify(valueOperations, atLeastOnce()).set(note.getId(), note);
    }

    @Test
    public void update() {
        // PREPARE
        when(redisTemplate.hasKey(note.getId())).thenReturn(true);

        // ACT
        ResponseEntity response = notesLogic.save(user, note);

        // ASSERT
        responseIsOk(response);

        verify(redisTemplate, atLeastOnce()).hasKey(note.getId());
        verify(valueOperations, atLeastOnce()).set(note.getId(), note);
    }

    @Test
    public void del() {
        // PREPARE
        when(redisTemplate.hasKey(note.getId())).thenReturn(true);

        // ACT
        ResponseEntity response = notesLogic.del(user, note.getId());

        // ASSERT
        responseIsOk(response);

        verify(redisTemplate, atLeastOnce()).hasKey(note.getId());
        verify(redisTemplate, atLeastOnce()).delete(note.getId());
    }

    private void responseIsOk(ResponseEntity response) {
        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), OK_CODE);
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
        public NotesLogic notesLogic() {
            return new NotesLogicImpl(redisTemplate());
        }
    }
}