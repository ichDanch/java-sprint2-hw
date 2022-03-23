package tracker.FileBackedTasksManager;

import tracker.controller.HistoryManager;
import tracker.controller.InMemoryTaskManager;
import tracker.controller.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    List<String> words = new ArrayList<>();

    public FileBackedTasksManager(String text) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(text))) {
            while (fileReader.ready()) {
                words.add(fileReader.readLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File myFile = new File("save.csv");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, true))) {
            for (String s : words) {
                bw.write(s);
                bw.write(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }

    }

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public Task getTask(long id) {
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(long id) {
        return super.getEpic(id);
    }

    @Override
    public Subtask getSubtask(long id) {
        return super.getSubtask(id);
    }

    @Override
    public long createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public long createEpic(Epic epic) {
        return super.createEpic(epic);
    }

    @Override
    public long createSubtask(Subtask subtask) {
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
    public void removeTask(long id) {
        super.removeTask(id);
    }

    @Override
    public void removeEpic(long id) {
        super.removeEpic(id);
    }

    @Override
    public void removeSubtask(long id) {
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
