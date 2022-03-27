package tracker.manager;

import tracker.exception.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    public static void main(String[] args) {

        /*
        Вечер добрый, Игорь!
        Не знал где ответить на замечания, поэтому пишут тут.

        При первом запуске менеджер присваивает задачам id и добавляет их в мапы. И пишет в файл.
        При втором запуске он сначала восстанавливает задачи из файла.
        А потом продолжает работу и видит создание новых задач, присваивает id (ID присваивается автоматом
        в конуструкторе при выполнении new Task, new Epic и new Subtask) и добавляет
        их в мапы и пишет в файл. Т.е. если создать таски и не закоменнтировать, то при каждом запуске кода
        будут постоянно создаваться новые таски и записыватьс я в файл.

        Попробовал и подругому проверить. В методе main класса FileBackedTasksManager создаю объект, таски и
        добавляю просмотры в историю. В методе main класса Main создаю новый объект класса FileBackedTasksManager
        и подгружаю сохраненный файл и смотрю, корректно ли всё добавилось.

        file создавал для проверки метода loadFromFile(File file), нигде не использую его, удалил.
        В конструкторе его тоже не получается использовать, т.к. сначала нужно создать объект,
        а при создании объекта мы уже указываем путь. Получается только в методе save() его можно использовать.
        */
        File file = new File("save.csv");
        FileBackedTasksManager fb = new FileBackedTasksManager(file);
        FileBackedTasksManager fbtm = new FileBackedTasksManager("save.csv");

        int b = fbtm.addTask(new Task("task1", "descriptionTask1", Status.NEW));
        int m = fbtm.addTask(new Task("task2", "descriptionTask2", Status.NEW));
        int c = fbtm.addEpic(new Epic("epic1", "descriptionEpic1"));
        int ca = fbtm.addSubtask(new Subtask("subtask1", "descriptionSubtask1", Status.DONE, c));
        fbtm.getTask(b);
        fbtm.getEpic(c);
        fbtm.getAllTasks();
        System.out.println(fbtm.historyManager.getHistory());

        /*FileBackedTasksManager copy = new FileBackedTasksManager("save.csv");
        copy.getAllTasks();
        System.out.println(copy.historyManager.getHistory());*/
    }
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager(String text) { // читаем файл
        setID(0);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(text, StandardCharsets.UTF_8))) {

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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write("id,type,name,status,description,epic\n");

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
            return new Task(array[2], array[4], Status.valueOf(array[3]));
        }
        return null;
    }

    public Epic fromStringToEpic(String value) {

        String[] array = value.split(",");

        if (array[1].equals(TaskType.EPIC.name()) && array[3].equals("none")) {
            return new Epic(array[2], array[4]);
        } else if (array[1].equals(TaskType.EPIC.name())) {
            return new Epic(array[2], array[4], Status.valueOf(array[3]));
        }

        return null;
    }

    public Subtask fromStringToSubtask(String value) {

        String[] array = value.split(",");

        if (array[1].equals(TaskType.SUBTASK.name())) {
            return new Subtask(array[2], array[4], Status.valueOf(array[3]), Integer.parseInt(array[5]));
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
        return task.getId() + ","
                + TaskType.TASK.name() + ","
                + task.getName() + ","
                + task.getStatus().name() + ","
                + task.getDescription()
                + "\n";
    }

    public String epicToString(Epic epic) {
        if (epic.getStatus() != null) {
            return epic.getId() + ","
                    + TaskType.EPIC.name() + ","
                    + epic.getName() + ","
                    + epic.getStatus().name() + ","
                    + epic.getDescription()
                    + "\n";
        }

        return epic.getId() + ","
                + TaskType.EPIC.name() + ","
                + epic.getName() + ","
                + "none" + ","
                + epic.getDescription()
                + "\n";
    }

    public String subtaskToString(Subtask subtask) {
        return subtask.getId() + ","
                + TaskType.SUBTASK.name() + ","
                + subtask.getName() + ","
                + subtask.getStatus().name() + ","
                + subtask.getDescription() + ","
                + subtask.getIdParentEpic()
                + "\n";
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file.getName());
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

/*class ManagerSaveException extends RuntimeException {

    ManagerSaveException(String message) {
        super(message);
    }
}*/