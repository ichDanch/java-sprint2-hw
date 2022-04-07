package tracker.manager;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public interface TaskManager {

    TreeSet<Task> getPrioritizedTasks();

    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    // добавить subtask по id в epic по id
    void addSubtaskToEpic(int idEpic, int idSubtask);

    void getAllTasks();

    ArrayList<Subtask> getAllSubtasksParentEpic(int idParentEpic);

    void removeAllTasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

}
