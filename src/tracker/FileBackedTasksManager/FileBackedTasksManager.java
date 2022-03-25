package tracker.FileBackedTasksManager;

import tracker.controller.HistoryManager;
import tracker.controller.InMemoryTaskManager;
import tracker.controller.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    public static void main(String[] args) {

        FileBackedTasksManager fbtm = new FileBackedTasksManager("save.csv");

        int b = fbtm.createTask(new Task("task1", "descriptionTask1", Status.NEW));
        int m = fbtm.createTask(new Task("task2", "descriptionTask2", Status.NEW));
        int c = fbtm.createEpic(new Epic("epic1", "descriptionEpic1"));
        int ca = fbtm.createSubtask(new Subtask("subtask1", "descriptionSubtask1", Status.DONE, c));
        /*fbtm.getTask(b);
        fbtm.getEpic(c);*/
        fbtm.save();
        System.out.println();
    }

    public FileBackedTasksManager(String text) { // читаем файл
        try (BufferedReader fileReader = new BufferedReader(new FileReader(text, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {

                String[] line = fileReader.readLine().split("\n");

                for (String anyTask : line) {

                    boolean isHistory = false;

                    if (isHistory) {
                        List <Integer> list =  FileBackedTasksManager.fromString(anyTask); //на выходе получаем лист историй и его нужно куда-то сохранить
                            for (Integer element: list) {
                                historyManager.add();
                            }


                    }

                    if (anyTask.isEmpty()) {
                        isHistory = true;
                        continue;
                    }

                    if (!anyTask.isEmpty()) {
                        Task task = fromStringToTask(anyTask);
                        Epic epic = fromStringToEpic(anyTask);
                        Subtask subtask = fromStringToSubtask(anyTask);
                        if (task != null) {
                            tasks.put(task.getId(), task);
                        }
                        if (epic != null) {
                            epics.put(epic.getId(), epic);
                        }
                        if (subtask != null) {
                            tasks.put(subtask.getId(), subtask);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {                           // пишем в файл
        File myFile = new File("save.csv");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, StandardCharsets.UTF_8, true))) {

            for (Task task : tasks.values()) {
                bw.write(taskToString(task));
            }
            for (Epic epic : epics.values()) {
                bw.write(epicToString(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                bw.write(subtaskToString(subtask));
            }
            getHistoryManager();
            bw.write("   ");
            bw.write(historyToString(getHistoryManager()));

        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }

    }

    public Task fromStringToTask(String value) {

        String[] array = value.split(",");
        System.out.println(Arrays.toString(array));
        for (int i = 0; i < array.length; i++) {
            if (array[1].equals(taskType.TASK.name())) {
                return new Task(array[2], array[4], Status.valueOf(array[3]));
            }
        }
        return null;
    }

    public Epic fromStringToEpic(String value) {

        String[] array = value.split(",");

        for (int i = 0; i < array.length; i++) {
            if (array[1].equals(taskType.EPIC.name())) {
                return new Epic(array[2], array[4]);
            }
        }
        return null;
    }

    public Subtask fromStringToSubtask(String value) {

        String[] array = value.split(",");

        for (int i = 0; i < array.length; i++) {
            if (array[1].equals(taskType.SUBTASK.name())) {
                return new Subtask(array[2], array[3], Status.valueOf(array[3]), Integer.parseInt(array[5]));
            }
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

    public static String historyToString (HistoryManager manager)  {
        List <Task> list  = manager.getHistory();
        String line = "";
        for (Task task : list) {
            line = String.join(",",String.valueOf(task.getId()));
        }
        return line;

    }

    public String taskToString(Task task) {
        return task.getId() + ","
                + taskType.TASK.name() + ","
                + task.getName() + ","
                + task.getStatus().name() + ","
                + task.getDescription()
                + "\n";
    }

    public String epicToString(Epic epic) {
        return epic.getId() + ","
                + taskType.EPIC.name() + ","
                + epic.getName() + ","
                + epic.getStatus().name() + ","
                + epic.getDescription()
                + "\n";
    }

    public String subtaskToString(Subtask subtask) {
        return subtask.getId() + ","
                + taskType.SUBTASK.name() + ","
                + subtask.getName() + ","
                + subtask.getStatus().name() + ","
                + subtask.getDescription() + ","
                + subtask.getIdParentEpic()
                + "\n";
    }

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
    }

    @Override
    public int createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public int createEpic(Epic epic) {
        return super.createEpic(epic);
    }

    @Override
    public int createSubtask(Subtask subtask) {
        return super.createSubtask(subtask);
    }

    @Override
    public void addSubtaskToEpic(int idEpic, int idSubtask) {
        super.addSubtaskToEpic(idEpic, idSubtask);
    }

    @Override
    public void getAllTasks() {
        super.getAllTasks();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksParentEpic(long idParentEpic) {
        return super.getAllSubtasksParentEpic(idParentEpic);
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
