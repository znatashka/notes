package ru.u26c4.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class History implements Serializable {

    private final String noteId;
    private List<Item> items = new ArrayList<>();

    public History(String noteId) {
        this.noteId = noteId;
    }

    @Getter
    @AllArgsConstructor
    public class Item implements Serializable {

        private String modifier;
        private Date modifyDate;
        private String text;
    }
}
