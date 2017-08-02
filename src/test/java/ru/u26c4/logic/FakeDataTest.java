package ru.u26c4.logic;

import org.junit.Test;
import org.springframework.security.core.userdetails.User;
import ru.u26c4.model.Note;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FakeDataTest {

    @Test
    public void list() {
        // ACT
        List<Note> notes = NotesLogicImpl.FakeData.list();

        // ASSERT
        assertNotNull(notes);
        assertEquals(notes.size(), 5);

        for (Note note : notes) {
            assertNotNull(note.getId());
            assertNotNull(note.getText());
            assertNotNull(note.getCreateDate());
            assertEquals(note.getCreateUser(), "bender");
        }
    }

    @Test
    public void empty() {
        // PREPARE
        String username = "usr";
        User user = (User) User
                .withUsername(username)
                .password("pwd")
                .roles("USER")
                .build();

        // ACT
        Note note = NotesLogicImpl.FakeData.empty(user);

        // ASSERT
        assertNotNull(note);
        assertNotNull(note.getCreateDate());
        assertEquals(note.getCreateUser(), username);
    }
}