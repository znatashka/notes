package ru.u26c4.logic.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.u26c4.model.Note;
import ru.u26c4.model.NoteBuilder;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@SuppressWarnings("unchecked")
public class HistoryUtilsTest {

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
                .createUser("bender")
                .createDate(new Date())
                .text("some text")
                .build();
    }

    @Test
    public void isModified_New() {
        // PREPARE
        when(redisTemplate.hasKey(note.getId())).thenReturn(false);

        // ACT
        boolean modified = historyUtils.isModified(note);

        // ASSERT
        assertTrue(modified);

        verify(redisTemplate, atLeastOnce()).hasKey(note.getId());
    }

    @Test
    public void isModified_Yes() {
        // PREPARE
        Note old = new NoteBuilder()
                .id("id")
                .createUser("bender")
                .createDate(new Date())
                .text("other text")
                .build();

        when(redisTemplate.hasKey(note.getId())).thenReturn(true);
        when(valueOperations.get(RedisKey.NOTE.prefix() + note.getId())).thenReturn(old);

        // ACT
        boolean modified = historyUtils.isModified(note);

        // ASSERT
        assertTrue(modified);

        verify(redisTemplate, atLeastOnce()).hasKey(note.getId());
        verify(valueOperations, atLeastOnce()).get(RedisKey.NOTE.prefix() + note.getId());
    }

    @Test
    public void isModified_No() {
        // PREPARE
        when(redisTemplate.hasKey(note.getId())).thenReturn(true);
        when(valueOperations.get(RedisKey.NOTE.prefix() + note.getId())).thenReturn(note);

        // ACT
        boolean modified = historyUtils.isModified(note);

        // ASSERT
        assertFalse(modified);

        verify(redisTemplate, atLeastOnce()).hasKey(note.getId());
        verify(valueOperations, atLeastOnce()).get(RedisKey.NOTE.prefix() + note.getId());
    }

    @Test
    public void modifier_createUser() {
        // ACT
        String modifier = historyUtils.modifier(note);

        // ASSERT
        assertEquals(note.getCreateUser(), modifier);
    }

    @Test
    public void modifier_modifyUser() {
        // PREPARE
        note.setModifyUser("modifier");

        // ACT
        String modifier = historyUtils.modifier(note);

        // ASSERT
        assertEquals(note.getModifyUser(), modifier);
    }

    @Test
    public void modifyDate_createDate() {
        // ACT
        Date modifyDate = historyUtils.modifyDate(note);

        // ASSERT
        assertEquals(note.getCreateDate(), modifyDate);
    }

    @Test
    public void modifyDate_modifyDate() {
        // PREPARE
        note.setModifyDate(new Date());

        // ACT
        Date modifyDate = historyUtils.modifyDate(note);

        // ASSERT
        assertEquals(note.getModifyDate(), modifyDate);
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
        public HistoryUtils historyUtils() {
            return new HistoryUtils(redisTemplate());
        }
    }
}