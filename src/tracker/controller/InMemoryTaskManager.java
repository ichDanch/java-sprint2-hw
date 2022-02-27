package tracker.controller;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    static long ID;

    private final HistoryManager historyManager;

    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Long, Epic> epics = new HashMap<>();
    HashMap<Long, Subtask> subtasks = new HashMap<>();

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public static long getID() {
        return ID;
    }

    public static void setID(long ID) {
        InMemoryTaskManager.ID = ID;
    }


    @Override
    public Task getTask(long id) {
        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Epic getEpic(long id) {
        historyManager.add(epics.get(id));

        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(long id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }


    @Override
    public long createTask(Task task) {
        tasks.put(task.getId(), task);

        return task.getId();
    }

    @Override
    public long createEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        return epic.getId();
    }

    @Override
    public long createSubtask(Subtask subtask) {
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
    public ArrayList<Subtask> getAllSubtasksParentEpic(long idParentEpic) {
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
    public void removeTask(long id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(long id) {
        // удалить из subtasks объекты, находящиеся в ArrayList удаляемого епика
        ArrayList<Subtask> list = epics.get(id).getSubtasksList();
        for (Subtask element : list) {
            if (subtasks.containsValue(element)) {
                subtasks.remove(element.getId());
                historyManager.remove(element.getId());
            }
        }
        historyManager.remove(id);
        epics.remove(id);

    }

    @Override
    public void removeSubtask(long id) {
        // удалить subtask по переданному id из Arraylist epica
        // написал через for и if, idea предложила заменить на такую конструкцию
        for (Epic value : epics.values()) {
            value.getSubtasksList().removeIf(subtask -> subtask.getId() == id);
        }
        epics.get(subtasks.get(id).getIdParentEpic()).recalculateStatus();
        historyManager.remove(id);
        subtasks.remove(id);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
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

