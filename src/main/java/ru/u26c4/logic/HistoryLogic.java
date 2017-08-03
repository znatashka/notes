package ru.u26c4.logic;

import ru.u26c4.model.History;
import ru.u26c4.model.Note;

/**
 * Логика работы с журналом изменений заметок
 */
public interface HistoryLogic {

    /**
     * Сохранеине истории
     *
     * @param note заметка
     */
    void save(Note note);

    /**
     * Получение истории
     *
     * @param noteId идентификатор заметки
     * @return история изменений заметки
     */
    History get(String noteId);
}
