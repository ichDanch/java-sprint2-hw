import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.FileBackedTasksManager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;
import org.junit.jupiter.api.*;
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

/**
 *          Здравствуйте Игорь!
 * Очень тяжелое и объемное задание, уйма времени и сил ушло на то, чтобы разобраться с ТЗ, понять что вообще нужно,
 * как это должно работать, как связаны между собой все эти новые классы. Не судите строго, старался изо всех сил.
 * Некоторую логику методов переделал, а старые тесты не успел переделать, поэтому какая-то часть тестов валится.
 * Тесты в классе HttpTaskManagerTest по отдельности работают, а вот целиком класс мне запустить не удалось, получаю
 * ошибку BindException, так же при попытке остановить сервер HttpTaskServer получаю NullPointerException
 */

public class HttpTaskManagerTest {
    KVServer kvServer;
    FileBackedTasksManager httpTaskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = new HttpTaskManager();
        httpTaskServer = new HttpTaskServer(httpTaskManager);
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        //  httpTaskServer.stop();
    }

    @Test
    public void shouldAddTaskByPOSTAndGETTaskAndDELETETask() throws IOException, InterruptedException {
        Task task = new Task(
                "Task",
                "desc Task",
                Status.DONE,
                300,
                "10:15 12.12.2022");
        String jsonTask = gson.toJson(task);
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .header("X-Add-Update-Task", "add")
                .POST(bodyTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(task, httpTaskManager.getTask(1));
        Assertions.assertEquals(201, response.statusCode());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Task taskFromJson = gson.fromJson(responseGet.body(), Task.class);

        Assertions.assertEquals(task, taskFromJson);
        Assertions.assertEquals(200, responseGet.statusCode());

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseDelete.statusCode());

        Assertions.assertTrue(httpTaskManager.getTasks().isEmpty());
        Assertions.assertEquals(204, responseDelete.statusCode());
    }

    @Test
    public void shouldAddEpicByPOSTAndGETTaskAndDELETETask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic",
                "desc Epic");
        String jsonEpic = gson.toJson(epic);
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .header("X-Add-Update-Epic", "add")
                .POST(bodyEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(epic, httpTaskManager.getEpic(1));
        Assertions.assertEquals(201, response.statusCode());
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Epic epicFromJson = gson.fromJson(responseGet.body(), Epic.class);

        Assertions.assertEquals(epic, epicFromJson);
        Assertions.assertEquals(200, responseGet.statusCode());

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        Assertions.assertTrue(httpTaskManager.getEpics().isEmpty());
        Assertions.assertEquals(204, responseDelete.statusCode());
    }

    @Test
    public void shouldAddSubtaskByPOSTAndGETTaskAndDELETETask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic",
                "desc Epic");
        httpTaskManager.addEpic(epic);
        Subtask subtask = new Subtask(
                "Subtask",
                "Desc Subtask",
                Status.DONE,
                epic.getId(),
                240,
                "09:15 13.11.2022");

        String jsonSubtask = gson.toJson(subtask);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .header("X-Add-Update-Subtask", "add")
                .POST(bodySubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(subtask, httpTaskManager.getSubtask(2));
        Assertions.assertEquals(201, response.statusCode());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromJson = gson.fromJson(responseGet.body(), Subtask.class);

        Assertions.assertEquals(subtask, subtaskFromJson);
        Assertions.assertEquals(200, responseGet.statusCode());

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        Assertions.assertTrue(httpTaskManager.getSubtasks().isEmpty());
        Assertions.assertEquals(204, responseDelete.statusCode());
    }


}
