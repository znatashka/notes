package ru.u26c4.model;

import java.util.Date;

public class NoteBuilder {

    private String id;
    private String createUser;
    private String modifyUser;
    private Date createDate;
    private Date modifyDate;
    private String text;

    public NoteBuilder id(String id) {
        this.id = id;
        return this;
    }

    public NoteBuilder createUser(String createUser) {
        this.createUser = createUser;
        return this;
    }

    public NoteBuilder modifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
        return this;
    }

    public NoteBuilder createDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public NoteBuilder modifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
        return this;
    }

    public NoteBuilder text(String text) {
        this.text = text;
        return this;
    }

    public Note build() {
        Note note = new Note();
        note.setId(this.id);
        note.setCreateUser(this.createUser);
        note.setModifyUser(this.modifyUser);
        note.setCreateDate(this.createDate);
        note.setModifyDate(this.modifyDate);
        note.setText(this.text);
        return note;
    }
}
