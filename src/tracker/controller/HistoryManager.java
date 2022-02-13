package tracker.controller;

import tracker.model.Task;

import java.util.List;


public interface HistoryManager {
    // метод добавляет просмотренную задачу в список
    void add(Task task);

    // метод возвращает список просмотренных задач
    List<Task> getHistory();
}
