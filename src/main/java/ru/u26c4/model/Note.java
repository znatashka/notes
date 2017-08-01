package ru.u26c4.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Note {

    private String id;
    private String createUser;
    private String modifyUser;
    private Date createDate;
    private Date modifyDate;
    private String text;

    Note() {
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                '}';
    }
}
