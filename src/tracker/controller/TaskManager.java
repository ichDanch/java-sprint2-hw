package tracker.controller;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    long createTask(Task task);

    long createEpic(Epic epic);

    long createSubtask(Subtask subtask);

    // добавить subtask по id в epic по id
    void addSubtaskToEpic(int idEpic, int idSubtask);

    void getAllTasks();

    ArrayList<Subtask> getAllSubtasksParentEpic(long idParentEpic);

    void removeAllTasks();

    Task getTask(long id);

    Epic getEpic(long id);

    Subtask getSubtask(long id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(long id);

    void removeEpic(long id);

    void removeSubtask(long id);

}
