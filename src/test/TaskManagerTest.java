import manager.InMemoryHistoryManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    final PrintStream standardOut = System.out;
    final ByteArrayOutputStream outputStreamHand = new ByteArrayOutputStream();

    @BeforeEach
    public void OutPrintln() {
        System.setOut(new PrintStream(outputStreamHand));
    }

    @AfterEach
    public void closeOutPrintln() {
        System.setOut(standardOut);
    }

    @BeforeEach
    public abstract void createManager();

    @AfterEach
    public void resetId() {
        InMemoryTaskManager.setID(0);
    }

    @Test
    public void epicHaveStatusAndSubtaskHaveEpic() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("ca", "desc", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId = taskManager.addSubtask(subtask);
        Status status = epic.getStatus();
        Status expectedStatus = Status.DONE;

        Assertions.assertEquals(expectedStatus, status);

        int idParentEpic = subtask.getIdParentEpic();

        Assertions.assertEquals(epicId, idParentEpic);
    }


    @Test
    public void createTaskAddTaskGetTaskGetTasksCheckListTasks() {
        Task task = new Task("a", "desc A", Status.DONE, 300,
                "10:15 12.12.2022");
        int taskId = taskManager.addTask(task);
        Task expectedTask = taskManager.getTask(taskId);

        Assertions.assertNotNull(expectedTask, "задача не найдена");
        Assertions.assertEquals(expectedTask, task, "задачи не совпадают");

        List<Task> tasks = taskManager.getTasks();

        Assertions.assertNotNull(tasks, "задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "неверное количество задач");
        Assertions.assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    public void createEpicAddEpicGetEpicGetEpicsCheckListEpicsCheckSubtasksList() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        Task expectedEpic = taskManager.getEpic(epicId);

        Assertions.assertNotNull(expectedEpic, "задача не найдена");
        Assertions.assertEquals(expectedEpic, epic, "задачи не совпадают");

        List<Epic> epics = taskManager.getEpics();

        Assertions.assertNotNull(epics, "задачи не возвращаются");
        Assertions.assertEquals(1, epics.size(), "неверное количество задач");
        Assertions.assertEquals(epic, epics.get(0), "Задачи не совпадают");
        Assertions.assertTrue(epic.getSubtasksList().isEmpty());


        Subtask subtask = new Subtask("ca", "desc", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId = taskManager.addSubtask(subtask);
        ArrayList<Subtask> subtasksList = epic.getSubtasksList();

        Assertions.assertFalse(epic.getSubtasksList().isEmpty());
        Assertions.assertNotNull(subtasksList, "задачи не возвращаются");
        Assertions.assertEquals(1, subtasksList.size(), "неверное количество задач");
        Assertions.assertEquals(subtask, subtasksList.get(0), "Задачи не совпадают");
    }

    @Test
    public void createSubtaskAddSubtaskGetSubtaskGetSubtasksCheckListSubtasksGetIdParentEpic() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("ca", "desc", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId = taskManager.addSubtask(subtask);
        Task expectedSubtask = taskManager.getSubtask(subtaskId);

        Assertions.assertNotNull(expectedSubtask, "задача не найдена");
        Assertions.assertEquals(expectedSubtask, subtask, "задачи не совпадают");

        List<Subtask> subtasks = taskManager.getSubtasks();

        Assertions.assertNotNull(subtasks, "задачи не возвращаются");
        Assertions.assertEquals(1, subtasks.size(), "неверное количество задач");
        Assertions.assertEquals(subtask, subtasks.get(0), "Задачи не совпадают");

        int idParentEpic = subtask.getIdParentEpic();

        Assertions.assertEquals(epicId, idParentEpic);

        String string = "Указан ID несуществующего эпика при создании сабтаска " + subtask.getName() +
                " и попытке поместить сабтаск в лист эпика";
    }

    /*@Test
    public void wrongEpicIdWhenCreatingSubtask() {
        Subtask subtask = new Subtask("ca", "desc", Status.DONE, 5, 240,
                "09:15 13.11.2022");
        int subtaskId = taskManager.addSubtask(subtask);

        String string = "Указан ID несуществующего эпика при создании сабтаска " + subtask.getName() +
                " и попытке поместить сабтаск в лист эпика";
        Assertions.assertEquals(string,taskManager.getSubtask(subtaskId));
    }*/


    @Test
    public void getPrioritizedTasksAndComparatorWorks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        TreeSet<Task> tasks = new TreeSet<>(taskManager.getComparatorStartTime());
        tasks.add(new Task("a", "desc A", Status.DONE, 300,
                "10:15 12.12.2022"));
        tasks.add(new Task("b", "desc B", Status.NEW, 60,
                "09:15 12.12.2022"));

        TreeSet<Task> expectedTasks = new TreeSet<>(taskManager.getComparatorStartTime());
        tasks.add(new Task("b", "desc B", Status.NEW, 60,
                "09:15 12.12.2022"));
        tasks.add(new Task("a", "desc A", Status.DONE, 300,
                "10:15 12.12.2022"));

        TreeSet<Task> receivedTask = taskManager.getPrioritizedTasks();

        Assertions.assertEquals(expectedTasks, receivedTask);
    }

    /*@Test
    public void checkTimeCrossing() {
        // создать два таска которые пересекаются во времени и два которые не пересекаются и сравнить с сообщением
        Task task1 = new Task("a", "desc A", Status.DONE, 60,
                "10:15 12.12.2022");
        int taskId1 = taskManager.addTask(task1);
        Task task2 = new Task("a", "desc A", Status.DONE, 60,
                "09:15 12.12.2022");
        int taskId2 = taskManager.addTask(task2);
    }*/

    @Test
    public void getAllSubtasksParentEpic() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("ca", "desc", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("ca", "desc", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId2 = taskManager.addSubtask(subtask2);

        List<Subtask> allSubtasks = taskManager.getAllSubtasksParentEpic(epicId);

        Assertions.assertNotNull(allSubtasks);
        Assertions.assertEquals(2, allSubtasks.size());
        Assertions.assertEquals(subtask1, allSubtasks.get(0));
        Assertions.assertEquals(subtask2, allSubtasks.get(1));
    }

    @Test
    public void updateEpic() {
        Epic epic = new Epic("epic", "descEpic");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("subtask", "descSubtask", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId1 = taskManager.addSubtask(subtask1);
        Assertions.assertEquals(1, epicId);
        Assertions.assertFalse(taskManager.getSubtasks().isEmpty());
        Assertions.assertFalse(epic.getSubtasksList().isEmpty());

        Epic updateEpic = new Epic("epicUpdate", "descEpicUpdate", epicId);
        taskManager.updateEpic(updateEpic);

        Assertions.assertTrue(updateEpic.getSubtasksList().isEmpty());
        Assertions.assertTrue(taskManager.getSubtasks().isEmpty());
        Assertions.assertEquals(1, updateEpic.getId());
    }

    @Test
    public void updateSubtask() {
        Epic epic = new Epic("epic", "descEpic");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "descSubtask1", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId1 = taskManager.addSubtask(subtask1);

        Assertions.assertEquals(epic.getSubtasksList().get(0), taskManager.getSubtask(subtaskId1));

        taskManager.updateSubtask(new Subtask("updateSubtask", "updateSubtask", Status.NEW, epicId, 60,
                "10:15 13.11.2022"));

        Assertions.assertEquals(epic.getSubtasksList().get(0), taskManager.getSubtask(subtaskId1));
    }

    @Test
    public void deletingEpicRemovesSubtasksFromMap() {
        Epic epic = new Epic("epic", "descEpic");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "descSubtask1", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId1 = taskManager.addSubtask(subtask1);

        Assertions.assertFalse(taskManager.getSubtasks().isEmpty());

        taskManager.removeEpic(epicId);

        Assertions.assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void deletingSubtaskRemovedHimFromEpicSubtasksList() {

        Epic epic = new Epic("epic", "descEpic");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "descSubtask1", Status.DONE, epicId, 240,
                "09:15 13.11.2022");
        int subtaskId1 = taskManager.addSubtask(subtask1);

        Assertions.assertFalse(epic.getSubtasksList().isEmpty());
        Assertions.assertEquals(epic.getSubtasksList().get(0), subtask1);

        taskManager.removeSubtask(subtaskId1);

        Assertions.assertTrue(taskManager.getSubtasks().isEmpty());
        Assertions.assertTrue(epic.getSubtasksList().isEmpty());

    }

    @Test
    public void deleteWrongIdSubtask() {
        taskManager.removeSubtask(6);

        String actualOut = outputStreamHand.toString();

        Assertions.assertEquals("Сабтаска с таким ID не существует\r\n", actualOut);

        outputStreamHand.reset();
    }

    @Test
    public void addSubtaskToEpicWithEpicsWrongId() {
        Subtask subtask1 = new Subtask("subtask1", "descSubtask1", Status.DONE, 5, 240,
                "09:15 13.11.2022");
        int subtaskId1 = taskManager.addSubtask(subtask1);

        String actualOut = outputStreamHand.toString();

        Assertions.assertEquals("Указан неверный ID эпика при создании сабтаска " +
                subtask1.getName() + "\r\n", actualOut);

        outputStreamHand.reset();
    }

    @Test
    public void historyManagerAddTaskLinkLastGetHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("a", "desc A", Status.DONE, 300,
                "10:15 12.12.2022");
        Task task2 = new Task("b", "desc B", Status.DONE, 300,
                "13:15 12.12.2022");
        List<Task> tasks = List.of(task2,task1);
        taskManager.addTask(task2);
        taskManager.addTask(task1);
        taskManager.getTask(task2.getId());
        taskManager.getTask(task1.getId());

        List <Task> historyTasks = taskManager.getHistoryManager().getHistory();

        Assertions.assertEquals(tasks,historyTasks);
    }

    @Test
    public void emptyHistoryManagerGetHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        List<Task> tasks = new ArrayList<>();

        taskManager.getTask(1);
        taskManager.getTask(2);

        String actualOut = outputStreamHand.toString();

        Assertions.assertEquals("Такого ID нет\r\nТакого ID нет\r\n", actualOut);

        List <Task> historyTasks = taskManager.getHistoryManager().getHistory();

        Assertions.assertEquals(tasks,historyTasks);

        outputStreamHand.reset();
    }

    @Test
    public void duplicateTasksHistoryManagerAddTaskLinkLastGetHistory() {

        // обкатать этот метод по добавлению дубликатов
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task(2,"a", "desc A", Status.DONE, 300,
                "10:15 12.12.2022");
        Task task2 = new Task(2,"b", "desc B", Status.DONE, 300,
                "13:15 12.12.2022");
        List<Task> tasks = List.of(task2,task1);

        taskManager.getHistoryManager().add(task2);
        taskManager.getHistoryManager().add(task1);

        List <Task> historyTasks = taskManager.getHistoryManager().getHistory();

        Assertions.assertEquals(tasks,historyTasks);
    }


}
