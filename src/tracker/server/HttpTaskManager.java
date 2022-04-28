package server;

import com.google.gson.*;
import manager.FileBackedTasksManager;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient kvTaskClient;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    public HttpTaskManager() {
        super();
        this.kvTaskClient = new KVTaskClient("http://localhost:8078/");
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(getTasks());
        kvTaskClient.put("task", jsonTasks);
        String jsonEpics = gson.toJson(getEpics());
        kvTaskClient.put("epic", jsonEpics);
        String jsonSubtasks = gson.toJson(getSubtasks());
        kvTaskClient.put("subtask", jsonSubtasks);
        String jsonHistory = gson.toJson(getHistoryManager().getHistory());
        kvTaskClient.put("history", jsonHistory);
    }
}