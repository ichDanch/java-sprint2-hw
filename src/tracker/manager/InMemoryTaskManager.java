package manager;

import model.Epic;
import model.FormatterStartTime;
import model.Subtask;
import model.Task;

import javax.sound.midi.Soundbank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    static int ID;

    protected final HistoryManager historyManager;
    //protected DateTimeFormatter formatterStartTime = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
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
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Нет TASKa с таким ID");
            throw new IllegalArgumentException("Нет TASKa с таким ID");
        }

    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            throw new IllegalArgumentException("Нет EPICa с таким ID");
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new IllegalArgumentException("Нет SUBTASKa с таким ID");
        }
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

    public boolean checkTimeCrossing(Task task) {

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
                            + taskStartTime.format(FormatterStartTime.getFormatterStartTime())
                            + " - " + taskEndTime.format(FormatterStartTime.getFormatterStartTime())
                            + " пересекается с " + "[" + element.getName() + "] "
                            + " " + elementStartTime.format(FormatterStartTime.getFormatterStartTime()) + " - "
                            + elementEndTime.format(FormatterStartTime.getFormatterStartTime()));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int addTask(Task task) {
        if (checkTimeCrossing(task) && task != null) {
            tasks.put(task.getId(), task);
            return task.getId();
        } else {
            throw new IllegalArgumentException("TASK" + " не был добавлен.");
        }
    }

    @Override
    public int addEpic(Epic epic) {
        if (checkTimeCrossing(epic) && epic != null) {
            epics.put(epic.getId(), epic);
            return epic.getId();
        } else {
            System.out.println("EPIC не был добавлен.");
            throw new IllegalArgumentException("EPIC не был добавлен.");
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {

        if (checkTimeCrossing(subtask) && subtask != null) {
            //поместить subtask в список последнего epica
            try {
                int epicId = subtask.getIdParentEpic();
                Epic currentEpic = epics.get(epicId);
                List<Subtask> subtasksList = currentEpic.getSubtasksList();
                subtasksList.add(subtask);
                //пересчитать статус
                epics.get(subtask.getIdParentEpic()).recalculateStatus();
                epics.get(subtask.getIdParentEpic()).getEndTime();
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Указан неверный ID EPICa при создании сабтаска "
                        + "[" + subtask.getName() + "]");
            }
            subtasks.put(subtask.getId(), subtask);

            return subtask.getId();
        } else {
            throw new IllegalArgumentException("SUBTASK " + "не был добавлен.");
        }
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
        if (checkTimeCrossing(task)) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("TASK не был обновлен.");
            throw new IllegalArgumentException("TASK не был добавлен.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (checkTimeCrossing(epic)) {
            // удалить из subtasks объекты, находящиеся в ArrayList заменяемого епика
            ArrayList<Subtask> list = epics.get(epic.getId()).getSubtasksList();
            for (Subtask element : list) {
                if (subtasks.containsValue(element)) {
                    subtasks.remove(element.getId());
                }
            }
            epic.recalculateStatus();
            epic.getEndTime();
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("EPIC не был обновлен.");
            throw new IllegalArgumentException("EPIC не был добавлен.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (checkTimeCrossing(subtask)) {
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
            epics.get(subtask.getIdParentEpic()).getEndTime();
            subtasks.put(subtask.getId(), subtask);
        } else {
            System.out.println("SUBTASK не был обновлен.");
            throw new IllegalArgumentException("SUBTASK не был добавлен.");
        }
    }

    @Override
    public boolean removeTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
            return true;
        } else {
            System.out.println("ТАСКа с таким id не сущестует, введите существующий id");
            return false;
        }
    }

    @Override
    public boolean removeEpic(int id) {
        // удалить из subtasks объекты, находящиеся в ArrayList удаляемого епика
        if (epics.containsKey(id)) {
            ArrayList<Subtask> list = epics.get(id).getSubtasksList();
            System.out.println(list);
            if (list != null) {
                for (Subtask element : list) {
                    if (subtasks.containsValue(element)) {
                        subtasks.remove(element.getId());
                        historyManager.remove(element.getId());
                    }
                }
            }
            historyManager.remove(id);
            epics.remove(id);
            return true;
        } else {
            System.out.println("ЭПИКа с таким id не сущестует, введите существующий id");
            return false;
        }
    }

    @Override
    public boolean removeSubtask(int id) {
        // удалить subtask по переданному id из Arraylist epica
        if (subtasks.containsKey(id)) {
            for (Epic value : epics.values()) {
                value.getSubtasksList().removeIf(subtask -> subtask.getId() == id);
            }
            if (subtasks.containsKey(id)) {
                epics.get(subtasks.get(id).getIdParentEpic()).recalculateStatus();
                epics.get(subtasks.get(id).getIdParentEpic()).getEndTime();
            } else {
                System.out.println("Сабтаска с таким ID не существует");
            }

            historyManager.remove(id);
            subtasks.remove(id);
            return true;
        } else {
            System.out.println("САБТАСКа с таким id не сущестует, введите существующий id");
            return false;
        }

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

