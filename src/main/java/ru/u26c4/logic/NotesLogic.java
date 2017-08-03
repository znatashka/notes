package ru.u26c4.logic;

import org.springframework.security.core.userdetails.User;
import ru.u26c4.model.Note;

import java.util.List;

/**
 * Основная логика приложения
 */
public interface NotesLogic {

    /**
     * Получение списка всех заметок
     *
     * @return список заметок
     */
    List<Note> notes();

    /**
     * Создание новой заметки
     *
     * @param user текущий пользователь
     * @return пустая заметка
     */
    Note create(User user);

    /**
     * Сохранение заметки
     *
     * @param user текущий пользователь
     * @param note заметка для сохранения
     */
    void save(User user, Note note);

    /**
     * Удаление заметки
     *
     * @param user текущий пользователь
     * @param id   идентификатор заметки
     */
    void del(User user, String id);
}