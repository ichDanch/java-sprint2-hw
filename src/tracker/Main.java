package tracker;

import tracker.controller.*;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {
        /*HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        TaskManager inMemoryTaskManager =  Managers.getDefaultTask(inMemoryHistoryManager);*/

        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(inMemoryHistoryManager);

        int one = inMemoryTaskManager.createTask(new Task("One", "", Status.DONE));
        int two = inMemoryTaskManager.createEpic(new Epic("Two", ""));
        inMemoryTaskManager.updateSubtask(new Subtask("Three", "update", Status.IN_PROGRESS,two));
        int four = inMemoryTaskManager.createTask(new Task("Four", "", Status.NEW));
        int five = inMemoryTaskManager.createTask(new Task("Five", "", Status.NEW));
        inMemoryTaskManager.updateTask(new Task("Six", "UPDATE", Status.NEW));
        int seven = inMemoryTaskManager.createTask(new Task("Seven", "", Status.NEW));
        int eight = inMemoryTaskManager.createTask(new Task("Eight", "", Status.NEW));
        int nine = inMemoryTaskManager.createEpic(new Epic("Nine", ""));
        int ten = inMemoryTaskManager.createSubtask(new Subtask("Ten", "", Status.NEW,nine));
        int eleven = inMemoryTaskManager.createTask(new Task("Eleven", "", Status.NEW));
        int twelve = inMemoryTaskManager.createTask(new Task("Twelve", "", Status.NEW));
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.getTask(4);
        inMemoryTaskManager.getTask(5);
        inMemoryTaskManager.getTask(6);
        inMemoryTaskManager.getTask(7);
        inMemoryTaskManager.getTask(8);
        inMemoryTaskManager.getEpic(9);
        inMemoryTaskManager.getSubtask(10);
        inMemoryTaskManager.getTask(11);
        inMemoryTaskManager.getTask(12);

        System.out.println(inMemoryHistoryManager.getHistory());


//        inMemoryTaskManager.updateEpic(new Epic("update", "up"));

        /*int one = taskManager.createTask(new Task("One", "to", Status.NEW));
        int two = taskManager.createTask(new Task("Two", "tt", Status.NEW));
        int three = taskManager.createEpic(new Epic("three", "fe"));
        int four = taskManager.createSubtask(new Subtask("four", "fs", Status.NEW, three));
        int five = taskManager.createSubtask(new Subtask("five", "ss", Status.DONE, three));
        int six = taskManager.createEpic(new Epic("six", "se"));
        int seven = taskManager.createSubtask(new Subtask("seven", "ts", Status.IN_PROGRESS, six));
        System.out.println(taskManager.getAllSubtasksParentEpic(three));
        taskManager.getAllTasks();
        taskManager.updateEpic(new Epic("update", "up", six));
        taskManager.updateSubtask(new Subtask("update", "up", Status.IN_PROGRESS, six));
        taskManager.getAllTasks();

        taskManager.updateEpic(new Epic("Epic", "ep", three));
        taskManager.getAllTasks();*/


    }
}
