package manager;

import exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private Path path;  // поработать с файлом
    protected DateTimeFormatter formatterStartTime = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public static void main(String[] args) {


//        File file = new File("save.csv");
//        FileBackedTasksManager fb = new FileBackedTasksManager(file);
        FileBackedTasksManager fbtm = new FileBackedTasksManager("save.csv");
        int a = fbtm.addTask(new Task(
                "a",
                "desc A",
                Status.DONE,
                300,
                "10:15 12.12.2022"));
        int b = fbtm.addTask(new Task(
                "b",
                "desc B",
                Status.NEW,
                240,
                "09:05 11.11.2022"));
        int d = fbtm.addEpic(new Epic(
                "d",
                "desc D"));

        int ca = fbtm.addSubtask(new Subtask(
                "ca",
                "",
                Status.DONE,
                d,
                240,
                "09:15 13.11.2022"));
        int f = fbtm.addEpic(new Epic(
                "f",
                "desc F"));
        int cb = fbtm.addSubtask(new Subtask(
                "cb",
                "",
                Status.DONE,
                d,
                180,
                "09:15 11.11.2022"));
        int cd = fbtm.addSubtask(new Subtask(
                "cd",
                "",
                Status.DONE,
                d,
                60,
                "09:10 12.11.2022"));
        int e = fbtm.addTask(new Task(
                "e",
                "desc E",
                Status.NEW,
                60,
                "09:55 12.12.2022"));

        System.out.println(fbtm.getEpic(d).getEndTime());

        System.out.println(fbtm.getTask(a));
        System.out.println(fbtm.getTask(b));
        System.out.println(fbtm.getEpic(d));
        System.out.println(fbtm.getTask(e));
        System.out.println(fbtm.getEpic(f));
        System.out.println(fbtm.getSubtask(ca));
        System.out.println(fbtm.getSubtask(cb));
        System.out.println(fbtm.getSubtask(cd));
        System.out.println(fbtm.getPrioritizedTasks());
       /* int b = fbtm.addTask(new Task("task1", "descriptionTask1", Status.NEW));
        int m = fbtm.addTask(new Task("task2", "descriptionTask2", Status.NEW));
        int c = fbtm.addEpic(new Epic("epic1", "descriptionEpic1"));
        int ca = fbtm.addSubtask(new Subtask("subtask1", "descriptionSubtask1", Status.DONE, c));
        fbtm.getTask(b);
        fbtm.getEpic(c);
        fbtm.getAllTasks();
        System.out.println(fbtm.historyManager.getHistory());*/

        /*FileBackedTasksManager copy = new FileBackedTasksManager("save.csv");
        copy.getAllTasks();
        System.out.println(copy.historyManager.getHistory());*/
    }
    
    
    
    /*public FileBackedTasksManager(Path path) {
        this.path = path;
    }*/

    public FileBackedTasksManager(String string) { // читаем файл

        setID(0);
        try (BufferedReader fileReader = new BufferedReader(new FileReader("save.csv"))) {

            boolean isHistory = false;
            while (fileReader.ready()) {

                String[] line = fileReader.readLine().split("\n");

                for (String anyTask : line) {
                    if (anyTask.startsWith("id")) {
                        continue;
                    }

                    if (isHistory) {
                        List<Integer> list = FileBackedTasksManager.fromString(anyTask);
                        for (Integer element : list) {
                            if (tasks.containsKey(element)) {
                                historyManager.add(tasks.get(element));
                            } else if (epics.containsKey(element)) {
                                historyManager.add(epics.get(element));
                            } else if (subtasks.containsKey((element))) {
                                historyManager.add(subtasks.get(element));
                            }
                        }
                    } else if (!anyTask.isBlank()) {
                        Task task = fromStringToTask(anyTask);
                        Epic epic = fromStringToEpic(anyTask);
                        Subtask subtask = fromStringToSubtask(anyTask);
                        if (task != null && !tasks.containsKey(task.getId())) {
                            tasks.put(task.getId(), task);
                        } else if (epic != null && !epics.containsKey(epic.getId())) {
                            epics.put(epic.getId(), epic);
                        } else if (subtask != null && !subtasks.containsKey(subtask.getId())) {
                            subtasks.put(subtask.getId(), subtask);
                        }
                    }
                    if (anyTask.isBlank()) {
                        isHistory = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {                           // пишем в файл
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("save.csv", false))) {
            bw.write("id,type,name,status,description,epic,duration,startTime\n");
                     //  [0]  [1]  [2]   [3]      [4]      [5]   [6]        [7]
            for (Task task : tasks.values()) {
                bw.write(taskToString(task));
            }
            for (Epic epic : epics.values()) {
                bw.write(epicToString(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                bw.write(subtaskToString(subtask));
            }
            bw.write("                   " + "\n");
            bw.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    public Task fromStringToTask(String value) {

        String[] array = value.split(",");

        if (array[1].equals(TaskType.TASK.name())) {
            return new Task(
                    Integer.parseInt(array[0]),
                    array[2],
                    array[4],
                    Status.valueOf(array[3]),
                    Integer.parseInt(array[6]),
                    array[7]);
        }
        return null;
    }

    public Epic fromStringToEpic(String value) {

        String[] array = value.split(",");

       /* if (array[1].equals(TaskType.EPIC.name()) && array[3].equals("empty")) {
            return new Epic(Integer.parseInt(array[0]), array[2], array[4]);*/
        if (array[1].equals(TaskType.EPIC.name())) {
            return new Epic(Integer.parseInt(array[0]), array[2], Status.valueOf(array[3]),array[4]);
        }

        return null;
    }

    public Subtask fromStringToSubtask(String value) {

        String[] array = value.split(",");

        if (array[1].equals(TaskType.SUBTASK.name())) {
            return new Subtask(
                    Integer.parseInt(array[0]),
                    array[2],
                    array[4],
                    Status.valueOf(array[3]),
                    Integer.parseInt(array[5]),
                    Integer.parseInt(array[6]),
                    array[7]);
        }

        return null;
    }

    public static List<Integer> fromString(String value) {

        List<Integer> list = new ArrayList<>();
        String[] stringArray = value.split(",");
        Integer[] intArray = new Integer[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }
        Collections.addAll(list, intArray);

        return list;
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory();
        String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = String.valueOf(list.get(i).getId());
        }

        return String.join(",", array);

    }

    public String taskToString(Task task) {
        if (task.getStartTime().isPresent()) {
            return task.getId() + ","
                    + TaskType.TASK.name() + ","
                    + task.getName() + ","
                    + task.getStatus().name() + ","
                    + task.getDescription()+ ","
                    + "empty" + ","
                    + task.getDuration().toMinutes() + ","
                    + task.getStartTime().get().format(formatterStartTime) + ","
                    + "\n";
        } else {
            return task.getId() + ","
                    + TaskType.TASK.name() + ","
                    + task.getName() + ","
                    + task.getStatus().name() + ","
                    + task.getDescription()+ ","
                    + "empty" + ","
                    + task.getDuration().toMinutes() + ","
                    + "empty" + ","
                    + "\n";
        }

    }

    public String epicToString(Epic epic) {
        if (epic.getStatus() != null && epic.getStartTime().isPresent()) {
            return epic.getId() + ","
                    + TaskType.EPIC.name() + ","
                    + epic.getName() + ","
                    + epic.getStatus().name() + ","
                    + epic.getDescription() + ","
                    + "empty" + ","
                    + epic.getDuration().toMinutes() + ","
                    + epic.getStartTime().get().format(formatterStartTime) + ","
                    + "\n";
        } else if (epic.getStatus() != null){
            return epic.getId() + ","
                    + TaskType.EPIC.name() + ","
                    + epic.getName() + ","
                    + epic.getStatus().name() + ","
                    + epic.getDescription() +","
                    + "empty" + ","
                    + epic.getDuration().toMinutes() + ","
                    + "empty" + ","
                    + "\n";
        }
        else if (epic.getStartTime().isPresent()){
            return epic.getId() + ","
                    + TaskType.EPIC.name() + ","
                    + epic.getName() + ","
                    + "empty" + ","
                    + epic.getDescription() +","
                    + "empty" + ","
                    + epic.getDuration().toMinutes() + ","
                    + epic.getStartTime().get().format(formatterStartTime) + ","
                    + "\n";
        }

        return epic.getId() + ","
                + TaskType.EPIC.name() + ","
                + epic.getName() + ","
                + "empty" + ","
                + epic.getDescription() +","
                + "empty" + ","
                + epic.getDuration().toMinutes() + ","
                + "empty" + ","
                + "\n";
    }

    public String subtaskToString(Subtask subtask) {
        if (subtask.getStartTime().isPresent()) {
            return subtask.getId() + ","
                    + TaskType.SUBTASK.name() + ","
                    + subtask.getName() + ","
                    + subtask.getStatus().name() + ","
                    + subtask.getDescription() + ","
                    + subtask.getIdParentEpic()+ ","
                    + subtask.getDuration().toMinutes() + ","
                    + subtask.getStartTime().get().format(formatterStartTime) + ","
                    + "\n";
        } else {
            return subtask.getId() + ","
                    + TaskType.SUBTASK.name() + ","
                    + subtask.getName() + ","
                    + subtask.getStatus().name() + ","
                    + subtask.getDescription() + ","
                    + subtask.getIdParentEpic()+ ","
                    + subtask.getDuration().toMinutes() + ","
                    + "empty" + ","
                    + "\n";
        }

    }

    public static FileBackedTasksManager loadFromFile(String string) {
        return new FileBackedTasksManager(string);
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }
}
