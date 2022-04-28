import manager.FileBackedTasksManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file;

    @BeforeEach
    @Override
    public void createManager() {
        file = new File("save.csv");
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void clearFile() throws IOException {
            FileWriter fwOb = new FileWriter(file.getName(), false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
    }

    @Test
    public void shouldWriteTaskAndHistoryToFileAndReadTaskAndHistoryFromFile() {
        Task task1 = new Task("task1", "descTask1", Status.DONE, 60,
                "10:15 12.12.2022");
        int taskId = taskManager.addTask(task1);
        Task expectedTask = taskManager.getTask(taskId); //для создания истории
        List<Task> tasksHistory = taskManager.getHistoryManager().getHistory();

        FileBackedTasksManager taskManagerFromFile = new FileBackedTasksManager(file);
        Task taskFromFile = taskManagerFromFile.getTask(taskId);
        List<Task> tasksHistoryFromFile = taskManagerFromFile.getHistoryManager().getHistory();

        Assertions.assertEquals(expectedTask, taskFromFile);

        Assertions.assertEquals(tasksHistory, tasksHistoryFromFile);
    }

    @Test
    public void shouldWriteAndReadEmptyTasksList() {
        Task task1 = new Task("task1", "descTask1", Status.DONE, 60,
                "10:15 12.12.2022");
        int taskId = taskManager.addTask(task1);
        Task expectedTask = taskManager.getTask(taskId);
        taskManager.removeAllTasks();
        List<Task> emptyTasksList = taskManager.getTasks();
        List<Epic> emptyEpicsList = taskManager.getEpics();
        List<Subtask> emptySubtasksList = taskManager.getSubtasks();

        FileBackedTasksManager taskManagerFromFile = new FileBackedTasksManager(file);

        Assertions.assertEquals(0, emptyTasksList.size());
        Assertions.assertEquals(0, taskManagerFromFile.getTasks().size());
        Assertions.assertEquals(emptyTasksList, taskManagerFromFile.getTasks());
        Assertions.assertEquals(emptyEpicsList, taskManagerFromFile.getEpics());
        Assertions.assertEquals(emptySubtasksList, taskManagerFromFile.getSubtasks());
    }

    @Test
    public void shouldWriteAndReadEpicWithoutSubtasks() {
        Epic epic = new Epic("Epic", "descEpic");
        int epicId = taskManager.addEpic(epic);
        taskManager.getEpic(epicId);
        List<Task> tasksList = taskManager.getTasks();
        Assertions.assertEquals(1, taskManager.getHistoryManager().getHistory().size());

        FileBackedTasksManager taskManagerFromFile = new FileBackedTasksManager(file);

        Assertions.assertEquals(tasksList, taskManagerFromFile.getTasks());
        Assertions.assertEquals(taskManager.getHistoryManager().getHistory(),
                taskManagerFromFile.getHistoryManager().getHistory());
    }

    @Test
    public void shouldWriteAndReadTasksWithEmptyHistoryManager() {
        Epic epic = new Epic("Epic", "descEpic");
        int epicId = taskManager.addEpic(epic);
        List<Task> tasksList = taskManager.getTasks();
        taskManager.getHistoryManager().remove(epicId);

        FileBackedTasksManager taskManagerFromFile = new FileBackedTasksManager(file);
        Assertions.assertEquals(tasksList, taskManagerFromFile.getTasks());
    }
}
