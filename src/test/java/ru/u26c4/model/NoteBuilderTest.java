package ru.u26c4.model;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoteBuilderTest {

    @Test
    public void build() {
        // PREPARE
        String id = "id";
        String text = "some text";
        String createUser = "author";
        Date createDate = new Date();
        String modifyUser = "modifier";
        Date modifyDate = new Date();

        // ACT
        Note note = new NoteBuilder()
                .id(id)
                .text(text)
                .createUser(createUser)
                .createDate(createDate)
                .modifyUser(modifyUser)
                .modifyDate(modifyDate)
                .build();

        // ASSERT
        assertNotNull(note);
        assertEquals(note.getId(), id);
        assertEquals(note.getText(), text);
        assertEquals(note.getCreateUser(), createUser);
        assertEquals(note.getCreateDate(), createDate);
        assertEquals(note.getModifyUser(), modifyUser);
        assertEquals(note.getModifyDate(), modifyDate);
    }
}