import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.FileBackedTasksManager;

import manager.InMemoryTaskManager;
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

import static org.junit.jupiter.api.Assertions.assertAll;

/**
 *      Вечер добрый, Евгений!)
 * С BindException разобрался, но не до конца понял логику работы. Кажется что за промежуток времени
 * после .stop() первого теста и .start() во втором тесте, порт не успевает освободиться и получается исключение.
 * Еще один момент непонятен, что перед каждым тестом я создаю новый менеджер и новый сервер, new HttpTaskManager()
 * и new HttpTaskServer(httpTaskManager), но ID тасков не сбрасывается. Хотя не в тестах счётчик ID отрабатывает
 * корректно. Наверное это неправильно, но вручную сбросив ID перед каждым тестом
 * методом InMemoryTaskManager.setID(0) всё работает.
 */

public class HttpTaskManagerTest {
    static KVServer kvServer;
    FileBackedTasksManager httpTaskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeAll
    public static void startKvServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    public static void stopServers() {
        kvServer.stop();
    }

    @BeforeEach
    public void startServers() {
        this.httpTaskManager = new HttpTaskManager();
        this.httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void stopHttpTaskServer() {
        httpTaskManager.removeAllTasks();
        InMemoryTaskManager.setID(0);
        this.httpTaskServer.stop();
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

        assertAll(
                () -> Assertions.assertEquals(task, httpTaskManager.getTask(1)),
                () -> Assertions.assertEquals(201, response.statusCode())
        );

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Task taskFromJson = gson.fromJson(responseGet.body(), Task.class);

        assertAll(
                () -> Assertions.assertEquals(task, taskFromJson),
                () -> Assertions.assertEquals(200, responseGet.statusCode())
        );

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseDelete.statusCode());
        assertAll(
                () -> Assertions.assertTrue(httpTaskManager.getTasks().isEmpty()),
                () -> Assertions.assertEquals(204, responseDelete.statusCode())
        );
    }

    @Test
    public void shouldAddEpicByPOSTAndGETEpicAndDELETEEpic() throws IOException, InterruptedException {
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

        assertAll(
                () -> Assertions.assertEquals(epic, httpTaskManager.getEpic(1)),
                () -> Assertions.assertEquals(201, response.statusCode())
        );

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Epic epicFromJson = gson.fromJson(responseGet.body(), Epic.class);
        assertAll(
                () -> Assertions.assertEquals(epic, epicFromJson),
                () -> Assertions.assertEquals(200, responseGet.statusCode())
        );
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> Assertions.assertTrue(httpTaskManager.getEpics().isEmpty()),
                () -> Assertions.assertEquals(204, responseDelete.statusCode())
        );
    }

    @Test
    public void shouldAddSubtaskByPOSTAndGETSubtaskAndDELETESubtask() throws IOException, InterruptedException {
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
        assertAll(
                () -> Assertions.assertEquals(subtask, httpTaskManager.getSubtask(2)),
                () -> Assertions.assertEquals(201, response.statusCode())
        );
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromJson = gson.fromJson(responseGet.body(), Subtask.class);

        assertAll(
                () -> Assertions.assertEquals(subtask, subtaskFromJson),
                () -> Assertions.assertEquals(200, responseGet.statusCode())
        );

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertAll(
                () -> Assertions.assertTrue(httpTaskManager.getSubtasks().isEmpty()),
                () -> Assertions.assertEquals(204, responseDelete.statusCode())
        );
    }


}
