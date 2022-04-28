package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import model.*;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    FileBackedTasksManager tasksManager;
    HttpServer httpServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer(FileBackedTasksManager tasksManager) {
        this.tasksManager = tasksManager;
        try {
           this.httpServer = HttpServer.create(new InetSocketAddress("localhost",PORT), 0);
            //httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks/", new AllTasksHandler());
            httpServer.createContext("/tasks/task", new TasksHandler());
            httpServer.createContext("/tasks/epic", new EpicsHandler());
            httpServer.createContext("/tasks/subtask", new SubtasksHandler());

             //httpServer.stop(1);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Что-то пошло не так");
        }
    }

    public void setTasksManager(FileBackedTasksManager tasksManager) {
        this.tasksManager = tasksManager;
    }

    public class AllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks/ запроса от клиента.");
            int responseCode = 0;
            String response = "";
            String method = httpExchange.getRequestMethod();

            if (method.equals("GET")) {
                response = gson.toJson(tasksManager.getTasks()) + "\n"
                        + gson.toJson(tasksManager.getEpics()) + "\n"
                        + gson.toJson(tasksManager.getSubtasks());
                responseCode = 200;
            } else {
                System.out.println("Некорректный метод для /tasks в классе AllTasksHandler");
                responseCode = 501;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class TasksHandler implements HttpHandler {                         // работает
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks/task запроса от клиента.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            List<String> header = httpExchange.getRequestHeaders().get("X-Add-Update-Task");
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Тело запроса:\n" + body);

            switch (method) {
                case "GET":
                    response = getTask(query);
                    responseCode = 200;
                    break;
                case "POST":
                    Task task = gson.fromJson(body, Task.class);
                    response = addOrUpdateTask(task, header);
                    responseCode = 201;
                    break;
                case "DELETE":                                                 // работает
                    responseCode = deleteTask(query);
                    break;
                default:
                    response = "Некорректный метод!";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class EpicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            System.out.println("Началась обработка /tasks/epic запроса от клиента.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            List<String> header = httpExchange.getRequestHeaders().get("X-Add-Update-Epic");

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Тело запроса:\n" + body);

            switch (method) {
                case "GET":
                    response = getEpic(query);
                    responseCode = 200;
                    break;
                case "POST":
                    Epic epic = gson.fromJson(body, Epic.class);
                    response = addOrUpdateEpic(epic, header);
                    responseCode = 201;
                    break;
                case "DELETE":
                    responseCode = deleteEpic(query);
                    break;
                default:
                    response = "Некорректный метод!";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class SubtasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            System.out.println("Началась обработка /tasks/subtask запроса от клиента.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            List<String> header = httpExchange.getRequestHeaders().get("X-Add-Update-Subtask");

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Тело запроса:\n" + body);

            System.out.println("SUBTASK: ");
            switch (method) {
                case "GET":
                    response = getSubtask(query);
                    responseCode = 200;
                    break;
                case "POST":
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    response = addOrUpdateSubtask(subtask, header);
                    responseCode = 201;
                    break;
                case "DELETE":
                    responseCode = deleteSubtask(query);
                    break;
                default:
                    response = "Некорректный метод!";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public String addOrUpdateTask(Task task, List<String> header) {     // работает
        if (header.isEmpty()) {
            throw new IllegalArgumentException("В запросе отсутсвует заголовок");
        }
        if (header.contains("add")) {
            return gson.toJson(tasksManager.addTask(task));
        } else if (header.contains("update")) {
            tasksManager.updateTask(task);
            return gson.toJson(task.getId());
        } else {
            throw new IllegalArgumentException("Заголовок" + " [" + header.get(0) + "] " + "некорректный");
        }
    }

    public String addOrUpdateEpic(Epic epic, List<String> header) {
        if (header.isEmpty()) {
            throw new IllegalArgumentException("В запросе отсутсвует заголовок");
        }
        if (header.contains("add")) {
            return gson.toJson(tasksManager.addEpic(epic));
        } else if (header.contains("update")) {
            tasksManager.updateEpic(epic);
            return gson.toJson(epic.getId());
        } else {
            throw new IllegalArgumentException("Заголовок" + " [" + header.get(0) + "] " + "некорректный");
        }
    }

    public String addOrUpdateSubtask(Subtask subtask, List<String> header) {

        if (header.isEmpty()) {
            throw new IllegalArgumentException("В запросе отсутсвует заголовок");
        }
        if (header.contains("add")) {
            return gson.toJson(tasksManager.addSubtask(subtask));
        } else if (header.contains("update")) {
            tasksManager.updateSubtask(subtask);
            return gson.toJson(subtask.getId());
        } else {
            throw new IllegalArgumentException("Заголовок" + " [" + header.get(0) + "] " + "некорректный");
        }
    }

    public String getTask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            return gson.toJson(tasksManager.getTask(id));

        }
        return gson.toJson(tasksManager.getTasks());
    }

    public String getEpic(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            return gson.toJson(tasksManager.getEpic(id));
        }
        return gson.toJson(tasksManager.getEpics());
    }

    public String getSubtask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            return gson.toJson(tasksManager.getSubtask(id));

        }
        return gson.toJson(tasksManager.getSubtasks());
    }

    public int deleteTask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            if (tasksManager.removeTask(id)) {
                return 204;
            }
        }
        return 404;
    }

    public int deleteEpic(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            if (tasksManager.removeEpic(id)) {
                return 204;
            }
        }
        return 404;
    }

    public int deleteSubtask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            if (tasksManager.removeSubtask(id)) {
                return 204;
            }
        }
        return 404;
    }


    private Integer extractIdFromQuery(String query) {
        String[] split = query.split("=");
        return Integer.parseInt(split[1]);
    }
    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
    public void stop() {
        httpServer.stop(0);
    }

 /*   public static void main(String[] args) throws IOException, InterruptedException {

        File file = new File("save.csv");
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);


        *//*int epicId = tasksManager.addEpic(new Epic(
                "Epic",
                "desc Epic"
        ));*//*
     *//*   int ca = tasksManager.addSubtask(new Subtask(
                "subtask",
                "descr Subtask",
                Status.DONE,
                10,
                240,
                "09:15 13.11.2022"));*//*
     *//* int a = tasksManager.addTask(new Task(
                "a",
                "desc A",
                Status.DONE,
                300,
                "10:15 12.12.2022"));*//*
        HttpTaskServer server = new HttpTaskServer(tasksManager);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                //       .version(HttpClient.Version.HTTP_1_1)
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }*/

}
