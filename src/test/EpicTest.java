import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

import org.apiguardian.api.API;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    InMemoryTaskManager taskManager;

    @BeforeEach
    public void newInMemoryTaskManager() {
       taskManager = new InMemoryTaskManager();
    }

    @Test
    public void epicStatusNewWhenSubtaskListIsEmpty() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        Status status = epic.getStatus();
        Status exceptedStatus = Status.NEW;

        Assertions.assertEquals(exceptedStatus,status);
    }

    @Test
    public void epicStatusNewWhenAllSubtasksStatusNew() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        int subtask1Id = taskManager.addSubtask(new Subtask("ca", "desc", Status.NEW, epicId, 240,
                "09:15 13.11.2022"));
        int subtask2Id = taskManager.addSubtask(new Subtask("cb", "", Status.NEW, epicId, 240,
                "09:15 13.11.2022"));
        Status status = epic.getStatus();
        Status expectedStatus = Status.NEW;

        Assertions.assertEquals(expectedStatus,status);
    }

    @Test
    public void epicStatusDoneWhenAllSubtasksStatusDone() {
       Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        int subtask1Id = taskManager.addSubtask(new Subtask("ca", "desc", Status.DONE, epicId, 240,
                "09:15 13.11.2022"));
        int subtask2Id = taskManager.addSubtask(new Subtask("cb", "", Status.DONE, epicId, 240,
                "09:15 13.11.2022"));
        Status status = epic.getStatus();
        Status expectedStatus = Status.DONE;

        Assertions.assertEquals(expectedStatus,status);
    }

    @Test
    public void epicStatusInProgressWhenSubtaskStatusNewAndSubtaskStatusDone() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        int subtask1Id = taskManager.addSubtask(new Subtask("ca", "desc", Status.NEW, epicId, 240,
                "09:15 13.11.2022"));
        int subtask2Id = taskManager.addSubtask(new Subtask("cb", "", Status.DONE, epicId, 240,
                "09:15 13.11.2022"));
        Status status = epic.getStatus();
        Status expectedStatus = Status.IN_PROGRESS;

        Assertions.assertEquals(expectedStatus,status);
    }
    @Test
    public void epicStatusInProgressWhenAllSubtasksStatusInProgress() {
        Epic epic = new Epic("d", "desc D");
        int epicId = taskManager.addEpic(epic);
        int subtask1Id = taskManager.addSubtask(new Subtask("ca", "desc", Status.IN_PROGRESS,
                epicId, 240, "09:15 13.11.2022"));
        int subtask2Id = taskManager.addSubtask(new Subtask("cb", "", Status.IN_PROGRESS, epicId,
                240, "09:15 13.11.2022"));
        Status status = epic.getStatus();
        Status expectedStatus = Status.IN_PROGRESS;

        Assertions.assertEquals(expectedStatus,status);
    }



}