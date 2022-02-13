package tracker.controller;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    static int ID;

    private HistoryManager historyManager;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public InMemoryTaskManager() {

    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        InMemoryTaskManager.ID = ID;
    }


    @Override
    public Task getTask(Integer id) {
        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Epic getEpic(Integer id) {
        historyManager.add(epics.get(id));

        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }


    @Override
    public Integer createTask(Task task) {
        tasks.put(task.getId(), task);

        return task.getId();
    }

    @Override
    public Integer createEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        return epic.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        //поместить subtask в список последнего epica
        epics.get(subtask.getIdParentEpic()).getSubtasksList().add(subtask);
        //пересчитать статус
        epics.get(subtask.getIdParentEpic()).recalculateStatus();
        subtasks.put(subtask.getId(), subtask);

        return subtask.getId();
    }

    // добавить subtask по id в epic по id
    @Override
    public void addSubtaskToEpic(int idEpic, int idSubtask) {
        epics.get(idEpic).getSubtasksList().add(subtasks.get(idSubtask));
    }

    @Override
    public void getAllTasks() {
        System.out.println("tasks = " + tasks);
        System.out.println("epics = " + epics);
        System.out.println("subtasks = " + subtasks);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksParentEpic(int idParentEpic) {
        return epics.get(idParentEpic).getSubtasksList();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
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

    @Override
    public void updateSubtask(Subtask subtask) {
        // присвоить ArrayList subtask-ов родительского EPICa
        ArrayList<Subtask> temporarySubtasksList = epics.get(subtask.getIdParentEpic()).getSubtasksList();
        if (temporarySubtasksList.size() == 0) {
            temporarySubtasksList.add(subtask);
        } else {
            // присвоить последний subtask из ArrayList EPICa
            Subtask lastSubtask = temporarySubtasksList.get(temporarySubtasksList.size() - 1);
            // присвоить ID последнего subtask новому
            subtask.setId(lastSubtask.getId());
            // заменить старый subtask на новый
            temporarySubtasksList.set(temporarySubtasksList.size() - 1, subtask);
        }
        // пересчитать статус EPICa
        epics.get(subtask.getIdParentEpic()).recalculateStatus();

        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void removeByIdTask(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void removeByIdSubtask(int id) {
        // удалить subtask по переданному id из Arraylist epica
        // написал через for и if, idea предложила заменить на такую конструкцию
        for (Epic value : epics.values()) {
            value.getSubtasksList().removeIf(subtask -> subtask.getId() == id);
        }
        epics.get(subtasks.get(id).getIdParentEpic()).recalculateStatus();
        subtasks.remove(id);
    }

    @Override
    public String toString() {
        return "tracker.controller.InMemoryTaskManager{" +
                "tasks='" + tasks + '\'' +
                ", epics='" + epics + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}

