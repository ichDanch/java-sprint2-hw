package tracker;

import tracker.controller.Manager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();

        int one = manager.creatTask(new Task("One", "to", "NEW"));
        int two = manager.creatTask(new Task("Two", "tt", "NEW"));
        int three = manager.creatEpic(new Epic("three", "fe"));
        int four = manager.creatSubtask(new Subtask("four", "fs", "NEW", three));
        int five = manager.creatSubtask(new Subtask("five", "ss", "DONE", three));
        int six = manager.creatEpic(new Epic("six", "se"));
        int seven = manager.creatSubtask(new Subtask("seven", "ts", "IN_PROGRESS", six));
        System.out.println(manager.getAllSubtasksParentEpic(3));
        manager.getAllTasks();
        manager.updateEpic(new Epic("update", "up", six));
        manager.getAllTasks();
        manager.updateEpic(new Epic("Epic", "ep", three));
        manager.getAllTasks();

    }
}
