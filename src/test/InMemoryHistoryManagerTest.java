import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    @Test
    public void shouldReturnTasksFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(2, "Task1", "descTask1", Status.DONE, 300,
                "10:15 12.12.2022");
        Task task2 = new Task(3, "Task2", "descTask2", Status.DONE, 300,
                "10:15 12.12.2022");
        historyManager.add(task2);
        historyManager.add(task1);
        List<Task> tasks = historyManager.getHistory();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(task2, tasks.get(0));
        Assertions.assertEquals(task1, tasks.get(1));
    }

    @Test
    public void shouldReturnEmptyHistoryAndNothingWillHappenIfRemovedFromEmptyList() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> tasks = historyManager.getHistory();
        historyManager.remove(5);

        Assertions.assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldReturnTheLastViewedOfTwoIdenticalId() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(3, "Task1", "descTask1", Status.DONE, 300,
                "10:15 12.12.2022");
        Task task2 = new Task(3, "Task2", "descTask2", Status.DONE, 300,
                "10:15 12.12.2022");
        historyManager.add(task2);
        historyManager.add(task1);
        List<Task> tasks = historyManager.getHistory();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(task1, tasks.get(0));
    }

    @Test
    public void shouldRemoveTasksFromHistoryFirstMiddleLast() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "descTask1", Status.DONE, 300,
                "10:15 12.12.2022");
        Task task2 = new Task(2, "Task2", "descTask2", Status.DONE, 300,
                "10:15 12.12.2022");
        Task subtask3 = new Subtask(3, "Subtask1", "descSubtask1", Status.DONE, 5, 300,
                "10:15 12.12.2022");
        Task subtask4 = new Subtask(4, "Subtask2", "descSubtask2", Status.DONE, 6, 300,
                "10:15 12.12.2022");
        List<Task> tasks4 = List.of(task2, subtask3, subtask4, task1);
        List<Task> tasks3 = List.of(task2, subtask4, task1);
        List<Task> tasks2 = List.of(task2, subtask4);
        List<Task> tasks1 = List.of(subtask4);

        historyManager.add(task2);
        historyManager.add(subtask3);
        historyManager.add(subtask4);
        historyManager.add(task1);
        // добвить удаление и сравнивать мапы
        Assertions.assertEquals(tasks4, historyManager.getHistory(), "список истории неверный");
        historyManager.remove(subtask3.getId());
        Assertions.assertEquals(tasks3, historyManager.getHistory(), "из середины удаляется некорректно");
        historyManager.remove(task1.getId());
        Assertions.assertEquals(tasks2, historyManager.getHistory(), "последний таск удаляется некорректно");
        historyManager.remove(task2.getId());
        Assertions.assertEquals(tasks1, historyManager.getHistory(), "первый таск удаляется некорректно");
    }
}
