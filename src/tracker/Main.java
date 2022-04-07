package tracker;

import tracker.manager.*;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        int a = inMemoryTaskManager.addTask(new Task(
                "a",                                                                 //array[0]
                "desc A",                                                        //array[1]
                Status.DONE,                                                               //array[2]
                300,                                                               //array[3]
                "10:15 12.12.2022"));                                            //array[4]
        int b = inMemoryTaskManager.addTask(new Task(
                "b",
                "desc B",
                Status.NEW,
                240,
                "09:05 11.11.2022"));
        int d = inMemoryTaskManager.addEpic(new Epic(
                "d",
                "desc D"));

     /*   int ca = inMemoryTaskManager.addSubtask(new Subtask(
                "ca",
                "",
                Status.NEW,
                d,
                240,
                "09:15 13.11.2022"));
        int cb = inMemoryTaskManager.addSubtask(new Subtask(
                "ca",
                "",
                Status.NEW,
                d,
                180,
                "09:15 11.11.2022"));
        int cd = inMemoryTaskManager.addSubtask(new Subtask(
                "ca",
                "",
                Status.IN_PROGRESS,
                d,
                60,
                "09:10 12.11.2022"));*/
        int e = inMemoryTaskManager.addTask(new Task(
                "e",
                "desc E",
                Status.NEW,
                60,
                "09:55 12.12.2022"));
        int f = inMemoryTaskManager.addEpic(new Epic(
                "f",
                "desc F"));
        System.out.println(inMemoryTaskManager.getEpic(d).getEndTime());

        System.out.println(inMemoryTaskManager.getTask(a));
        System.out.println(inMemoryTaskManager.getTask(b));
        System.out.println(inMemoryTaskManager.getEpic(d));
        System.out.println(inMemoryTaskManager.getTask(e));
        System.out.println(inMemoryTaskManager.getEpic(f));
/*        System.out.println(inMemoryTaskManager.getSubtask(ca));
        System.out.println(inMemoryTaskManager.getSubtask(cb));
        System.out.println(inMemoryTaskManager.getSubtask(cd));*/
        System.out.println(inMemoryTaskManager.getPrioritizedTasks());


        /*FileBackedTasksManager file = new FileBackedTasksManager("save.csv");
        file.getAllTasks();
        System.out.println(file.getHistoryManager().getHistory());*/

        /*InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

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
*/
    }
}
