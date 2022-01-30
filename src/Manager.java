import java.util.HashMap;
import java.util.ArrayList;

public class Manager {

    static int ID;
    static int LAST_TASK_ID;
    static int LAST_EPIC_ID;
    static int LAST_SUBTASK_ID;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Integer creatTask(Task task) {
        LAST_TASK_ID = task.getId();

        tasks.put(task.getId(), task);

        return task.getId();
    }

    public Integer creatEpic(Epic epic) {
        LAST_EPIC_ID = epic.getId();

        epics.put(epic.getId(), epic);

        return epic.getId();
    }

    public Integer creatSubtask(Subtask subtask) {
        LAST_SUBTASK_ID = subtask.getId();
        // присвоить IdParentEpic id epica
        subtask.setIdParentEpic(LAST_EPIC_ID);
        //поместить subtask в список последнего epica
        epics.get(LAST_EPIC_ID).subtasksList.add(subtask);
        //пересчитать статус
        epics.get(LAST_EPIC_ID).recalculateStatus();
        subtasks.put(subtask.getId(), subtask);

        return subtask.getId();
    }

    // добавить subtask по id в epic по id
    public void addSubtaskToEpic(int idEpic, int idSubtask) {
        epics.get(idEpic).subtasksList.add(subtasks.get(idSubtask));
    }

    public void getAllTasks() {
        System.out.println("tasks = " + tasks);
        System.out.println("epics = " + epics);
        System.out.println("subtasks = " + subtasks);
    }

    public ArrayList<Subtask> getAllSubtasksParentEpic(int idParentEpic) {
        return epics.get(idParentEpic).subtasksList;
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
        task.setId(LAST_TASK_ID);
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        // удалить из subtasks объекты, находящиеся в ArrayList заменяемого епика
        ArrayList<Subtask> list = epics.get(LAST_EPIC_ID).subtasksList;
        for (Subtask element : list) {
            if (subtasks.containsValue(element)) {
                subtasks.remove(element.getId());
            }
        }
        epic.setId(LAST_EPIC_ID);
        epic.recalculateStatus();
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        // присвоить idParentEpic новому subtask от заменяемого(последнего)
        int idParentEpic = subtasks.get(LAST_SUBTASK_ID).getIdParentEpic();
        subtask.setIdParentEpic(idParentEpic);
        // присвоить ID новому subtask от заменяемого(последнего)
        subtask.setId(LAST_SUBTASK_ID);
        // заменить старый subtask на новый в ArrayList epica
        ArrayList<Subtask> list = (ArrayList<Subtask>) epics.get(LAST_EPIC_ID).subtasksList.clone();
        for (Subtask element : list) {
            if (element.getId() == subtask.getId()) {
                int index = epics.get(LAST_EPIC_ID).subtasksList.indexOf(element);
                epics.get(LAST_EPIC_ID).subtasksList.set(index, subtask);
            }
        }
        epics.get(idParentEpic).recalculateStatus();
        subtasks.put(subtask.getId(), subtask);

    }

    public void removeByIdTask(int id) {
        tasks.remove(id);
    }

    public void removeByIdEpic(int id) {
        // удалить из subtasks объекты, находящиеся в ArrayList удаляемого епика
        ArrayList<Subtask> list = epics.get(id).subtasksList;
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
            value.subtasksList.removeIf(subtask -> subtask.getId() == id);
        }
        epics.get(subtasks.get(id).getIdParentEpic()).recalculateStatus();
        subtasks.remove(id);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "tasks='" + tasks + '\'' +
                ", epics='" + epics + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}

