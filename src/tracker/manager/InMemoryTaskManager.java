package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    static int ID;

    protected final HistoryManager historyManager;
    protected DateTimeFormatter formatterStartTime = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
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

    public ComparatorStartTime getComparatorStartTime() {
        return comparatorStartTime;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subtasks.values());

        return prioritizedTasks;
    }

    public void checkTimeCrossing(Task task) {

        for (Task element : getPrioritizedTasks()) {

            if (task.getStartTime().isPresent()
                    && element.getStartTime().isPresent()
                    && task.getEndTime().isPresent()
                    && element.getEndTime().isPresent()) {

                LocalDateTime taskStartTime = task.getStartTime().get();
                LocalDateTime taskEndTime = task.getEndTime().get();
                LocalDateTime elementStartTime = element.getStartTime().get();
                LocalDateTime elementEndTime = element.getEndTime().get();

                if ((taskStartTime.isAfter(elementStartTime)
                        && taskStartTime.isBefore(elementEndTime))
                        || (taskEndTime.isAfter(elementStartTime)
                        && taskEndTime.isBefore(elementEndTime))) {
                    System.out.println("[" + task.getName() + "] "
                            + taskStartTime.format(formatterStartTime)
                            + " - " + taskEndTime.format(formatterStartTime)
                            + " пересекается с " + "[" + element.getName() + "] "
                            + " " + elementStartTime.format(formatterStartTime) + " - "
                            + elementEndTime.format(formatterStartTime));
                }
            }
        }
    }

    @Override
    public int addTask(Task task) {
        tasks.put(task.getId(), task);
//        prioritizedTasks.add(task);
        checkTimeCrossing(task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
//        prioritizedTasks.add(epic);
        checkTimeCrossing(epic);
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        //поместить subtask в список последнего epica
        try {
            epics.get(subtask.getIdParentEpic()).getSubtasksList().add(subtask);
            //пересчитать статус
            epics.get(subtask.getIdParentEpic()).recalculateStatus();
        } catch (NullPointerException e) {
            System.out.println("Указан неверный ID эпика при создании сабтаска " + subtask.getName());
        }

        subtasks.put(subtask.getId(), subtask);
//        prioritizedTasks.add(subtask);
        checkTimeCrossing(subtask);
        return subtask.getId();
    }

    @Override
    public void printAllTasks() {
        System.out.println("tasks = " + tasks);
        System.out.println("epics = " + epics);
        System.out.println("subtasks = " + subtasks);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
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
//        prioritizedTasks.add(task);
        checkTimeCrossing(task);
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
//        prioritizedTasks.add(epic);
        checkTimeCrossing(epic);
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
//        prioritizedTasks.add(subtask);
        checkTimeCrossing(subtask);
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
        for (Epic value : epics.values()) {
            value.getSubtasksList().removeIf(subtask -> subtask.getId() == id);
        }
       if(subtasks.containsKey(id)) {
           epics.get(subtasks.get(id).getIdParentEpic()).recalculateStatus();
       } else {
           System.out.println("Сабтаска с таким ID не существует");
       }

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

