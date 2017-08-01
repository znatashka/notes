package ru.u26c4.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Note {

    private String id;
    private String createUser;
    private String modifyUser;
    private Date createDate;
    private Date modifyDate;
    private String text;

    public Note(String id, String createUser, Date createDate) {
        this.id = id;
        this.createUser = createUser;
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                '}';
    }
}
