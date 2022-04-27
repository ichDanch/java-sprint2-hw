
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.FileBackedTasksManager;
import model.Status;
import model.Task;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;
import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        /*File filePath = new File("save.csv");
        FileBackedTasksManager file = new FileBackedTasksManager(filePath);
        file.printAllTasks();
        System.out.println(file.getHistoryManager().getHistory());
        System.out.println(file.getPrioritizedTasks());*/

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        KVServer kvServer = new KVServer();
        kvServer.start();
        FileBackedTasksManager taskManager = new HttpTaskManager();

        /*int taskId = taskManager.addTask(new Task(
                "Task",
                "desc Task",
                Status.DONE,
                300,
                "10:15 12.12.2022"));
        int epicId = taskManager.addEpic(new Epic(
                "Epic",
                "desc Epic"));
        int subtaskId = taskManager.addSubtask(new Subtask(
                "Subtask",
                "Desc Subtask",
                Status.DONE,
                epicId,
                240,
                "09:15 13.11.2022"));
        System.out.println("Task id = " + taskId);
        System.out.println("Epic id = " + epicId);
        System.out.println("Subtask id = " + subtaskId);*/
        Task task = new Task(
                "Task",
                "desc Task",
                Status.DONE,
                300,
                "10:15 12.12.2022");
        String jsonTask = gson.toJson(task);

        /*String jsonTask = "{\"name\":\"Task\",\"description\":\"desc Task\",\"id\":1,\"status\":\"DONE\"," +
                "\"duration\":300,\"startTime\":{\"value\":\"10:15 12.12.2022\"}}";
        String jsonEpic = "{\"endTime\":\"13:15 13.11.2022\",\"name\":\"Epic\",\"description\":\"desc Epic\",\"id\":2," +
                "\"status\":\"DONE\",\"duration\":240,\"startTime\":{\"value\":\"09:15 13.11.2022\"}}";
        String jsonSubtask = "{\"idParentEpic\":2,\"name\":\"Subtask\",\"description\":\"Desc Subtask\",\"id\":3," +
                "\"status\":\"DONE\",\"duration\":240,\"startTime\":{\"value\":\"09:15 13.11.2022\"}}";*/

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

        /*HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();*/
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        /*HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);*/

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .header("X-Add-Update-Task","add")
                .POST(bodyTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("КОД: " + response.statusCode());
        System.out.println("ТЕЛО: " + response.body());

        System.out.println(taskManager.getTask(1));

    }
}
