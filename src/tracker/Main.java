package tracker;

import tracker.controller.*;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        int a = inMemoryTaskManager.addTask(new Task("a", "", Status.DONE));
        int b = inMemoryTaskManager.addTask(new Task("b", "", Status.NEW));
        int c = inMemoryTaskManager.addEpic(new Epic("c", ""));
        int ca = inMemoryTaskManager.addSubtask(new Subtask("ca", "", Status.NEW, c));
        long cb = inMemoryTaskManager.addSubtask(new Subtask("cb", "", Status.NEW, c));
        long cd = inMemoryTaskManager.addSubtask(new Subtask("cd", "", Status.NEW, c));
        int d = inMemoryTaskManager.addEpic(new Epic("d", ""));
        System.out.println(inMemoryTaskManager.getTask(a));
        System.out.println(inMemoryTaskManager.getTask(b));
        System.out.println(inMemoryTaskManager.getEpic(c));
        System.out.println(inMemoryTaskManager.getTask(a));
        System.out.println(inMemoryTaskManager.getEpic(d));
        System.out.println(inMemoryTaskManager.getSubtask(ca));
        System.out.println(inMemoryTaskManager.getSubtask(ca));
        System.out.println(inMemoryTaskManager.getTask(a));
        System.out.println("");
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        inMemoryTaskManager.getHistoryManager().remove(a);
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        inMemoryTaskManager.removeEpic(c);
        inMemoryTaskManager.removeTask(b);
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());

    }
}
