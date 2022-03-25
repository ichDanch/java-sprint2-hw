package tracker.controller;

import tracker.model.Task;

import java.util.List;


public interface HistoryManager {
    // метод добавляет просмотренную задачу в список
    void add(Task task);
    // метод удаляет просмотр из истории
    void remove(int id);
    // метод возвращает список просмотренных задач
    List<Task> getHistory();
}
