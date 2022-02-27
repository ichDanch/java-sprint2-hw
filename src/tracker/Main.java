package tracker;

import tracker.controller.*;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        long a = inMemoryTaskManager.createTask(new Task("a", "", Status.DONE));
        long b = inMemoryTaskManager.createTask(new Task("b", "", Status.NEW));
        long c = inMemoryTaskManager.createEpic(new Epic("c", ""));
        long ca = inMemoryTaskManager.createSubtask(new Subtask("ca", "", Status.NEW, c));
        long cb = inMemoryTaskManager.createSubtask(new Subtask("cb", "", Status.NEW, c));
        long cd = inMemoryTaskManager.createSubtask(new Subtask("cd", "", Status.NEW, c));
        long d = inMemoryTaskManager.createEpic(new Epic("d", ""));
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
