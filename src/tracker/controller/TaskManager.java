package tracker.controller;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    Integer createTask(Task task);

    Integer createEpic(Epic epic);

    Integer createSubtask(Subtask subtask);

    // добавить subtask по id в epic по id
    void addSubtaskToEpic(int idEpic, int idSubtask);

    void getAllTasks();

    ArrayList<Subtask> getAllSubtasksParentEpic(int idParentEpic);

    void removeAllTasks();

    Task getTask(Integer id);

    Epic getEpic(Integer id);

    Subtask getSubtask(Integer id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeByIdTask(int id);

    void removeByIdEpic(int id);

    void removeByIdSubtask(int id);

}
