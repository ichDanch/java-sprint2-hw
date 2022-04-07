package tracker.manager;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    static int ID;

    protected final HistoryManager historyManager;

    protected ComparatorStartTime comparatorStartTime = new ComparatorStartTime();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(comparatorStartTime);
    protected HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    protected HashMap<Integer, Epic> epics = new HashMap<Integer, Epic>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<Integer, Subtask>();

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager();
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

    static class ComparatorStartTime implements Comparator<Task> {

        @Override
        public int compare(Task task1, Task task2) {
            if (task1.getStartTime().isPresent() && task2.getStartTime().isPresent()) {
                return task1.getStartTime().get().compareTo(task2.getStartTime().get());
            } else {
                return 1;
            }
        }
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));

        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }


    @Override
    public TreeSet<Task> getPrioritizedTasks() {
       /* prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(epics.values());
        prioritizedTasks.addAll(subtasks.values());*/

        return prioritizedTasks;
    }

    @Override
    public int addTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

        for (Task element : getPrioritizedTasks()) {
            if (task.getStartTime().isPresent()
                    && element.getStartTime().isPresent()
                    && task.getEndTime().isPresent()
                    && element.getEndTime().isPresent()) {
                if ((task.getStartTime().get().isAfter(element.getStartTime().get())
                        && task.getStartTime().get().isBefore(element.getEndTime().get()))
                        || (task.getEndTime().get().isAfter(element.getStartTime().get())
                        && task.getEndTime().get().isBefore(element.getEndTime().get()))) {
                    System.out.println(task.getName() + " "
                            + task.getStartTime().get()
                            + " " + task.getEndTime().get()
                            + " пересекается с " + element.getName()
                            + " " + element.getStartTime().get() + " " + element.getEndTime().get());
                }
            }
        }
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
        for (Task task : getPrioritizedTasks()) {
            if (epic.getStartTime().isPresent()
                    && task.getStartTime().isPresent()
                    && epic.getEndTime().isPresent()
                    && task.getEndTime().isPresent()) {
                if (epic.getStartTime().get().isAfter(task.getStartTime().get())
                        && epic.getStartTime().get().isBefore(task.getEndTime().get())) {
                    System.out.println(epic.getName() + " "
                            + epic.getStartTime().get()
                            + " " + epic.getEndTime().get()
                            + " пересекается с " + task.getName()
                            + " " + task.getStartTime().get() + " " + task.getEndTime().get());
                }
            }
        }

        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        //поместить subtask в список последнего epica
        try {
            epics.get(subtask.getIdParentEpic()).getSubtasksList().add(subtask);
        } catch (NullPointerException e) {
            System.out.println("Указан ID несуществующего эпика при создании сабтаска " + subtask.getName() +
                    " и попытке поместить сабтаск в лист эпика");
        }

        //пересчитать статус
        try {
            epics.get(subtask.getIdParentEpic()).recalculateStatus();
        } catch (NullPointerException e) {
            System.out.println("Указан ID несуществующего эпика при создании сабтаска " + subtask.getName() +
                    " и попытке пересчитать статус эпика");
        }

        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

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
        prioritizedTasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

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
        prioritizedTasks.add(epic);
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
        prioritizedTasks.add(subtask);
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
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
    public void removeSubtask(int id) {
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

