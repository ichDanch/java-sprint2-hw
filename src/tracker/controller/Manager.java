package tracker.controller;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class Manager {

    static int ID;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Integer creatTask(Task task) {
        tasks.put(task.getId(), task);

        return task.getId();
    }

    public Integer creatEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        return epic.getId();
    }

    public Integer creatSubtask(Subtask subtask) {
        //поместить subtask в список последнего epica
        epics.get(subtask.getIdParentEpic()).getSubtasksList().add(subtask);
        //пересчитать статус
        epics.get(subtask.getIdParentEpic()).recalculateStatus();
        subtasks.put(subtask.getId(), subtask);

        return subtask.getId();
    }

    // добавить subtask по id в epic по id
    public void addSubtaskToEpic(int idEpic, int idSubtask) {
        epics.get(idEpic).getSubtasksList().add(subtasks.get(idSubtask));
    }

    public void getAllTasks() {
        System.out.println("tasks = " + tasks);
        System.out.println("epics = " + epics);
        System.out.println("subtasks = " + subtasks);
    }

    public ArrayList<Subtask> getAllSubtasksParentEpic(int idParentEpic) {
        return epics.get(idParentEpic).getSubtasksList();
    }

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getByIdTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getByIdEpic(Integer id) {
        return epics.get(id);
    }

    public Subtask getByIdSubtask(Integer id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        // удалить из subtasks объекты, находящиеся в ArrayList заменяемого епика
        ArrayList<Subtask> list = epics.get(epic.getId()).getSubtasksList();
        for (Subtask element : list) {
            if (subtasks.containsValue(element)) {
                subtasks.remove(element.getId());
            }
        }
        epic.recalculateStatus();
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        // присвоить ArrayList subtask-ов родительского EPICa
        ArrayList<Subtask> temporarySubtasksList = epics.get(subtask.getIdParentEpic()).getSubtasksList();
        // присвоить последний subtask из ArrayList EPICa
        Subtask lastSubtask = temporarySubtasksList.get(temporarySubtasksList.size() - 1);
        // присвоить ID последнего subtask новому
        subtask.setId(lastSubtask.getId());
        // заменить старый subtask на новый
        temporarySubtasksList.set(temporarySubtasksList.size() - 1, subtask);
        // пересчитать статус EPICa
        epics.get(subtask.getIdParentEpic()).recalculateStatus();
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeByIdTask(int id) {
        tasks.remove(id);
    }

    public void removeByIdEpic(int id) {
        // удалить из subtasks объекты, находящиеся в ArrayList удаляемого епика
        ArrayList<Subtask> list = epics.get(id).getSubtasksList();
        for (Subtask element : list) {
            if (subtasks.containsValue(element)) {
                subtasks.remove(element.getId());
            }
        }
        epics.remove(id);
    }

    public void removeByIdSubtask(int id) {
        // удалить subtask по переданному id из Arraylist epica
        // написал через for и if, idea предложила заменить на такую конструкцию
        for (Epic value : epics.values()) {
            value.getSubtasksList().removeIf(subtask -> subtask.getId() == id);
        }
        epics.get(subtasks.get(id).getIdParentEpic()).recalculateStatus();
        subtasks.remove(id);
    }

    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        Manager.ID = ID;
    }

    @Override
    public String toString() {
        return "tracker.controller.Manager{" +
                "tasks='" + tasks + '\'' +
                ", epics='" + epics + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}

