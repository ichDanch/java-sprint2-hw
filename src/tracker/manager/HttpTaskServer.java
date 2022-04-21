package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.SerializedLambda;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HttpTaskServer {
    private static final int PORT = 8080;
    FileBackedTasksManager tasksManager;
    HttpServer httpServer;
    Gson gson = new GsonBuilder()
            //          .registerTypeAdapter(Task.class, new LocalDateTimeAdapter())
            //   .registerTypeAdapter(Task.class, new DurationAdapter())
//            .registerTypeAdapter(Epic.class, new LocalDateTimeAdapter)
//            .registerTypeAdapter(Subtask.class, new LocalDateTimeAdapter)
            .create();

    public static void main(String[] args) throws IOException, InterruptedException {


        File file = new File("save.csv");
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        int a = tasksManager.addTask(new Task(
                "a",
                "desc A",
                Status.DONE,
                300,
                "10:15 12.12.2022"));
        HttpTaskServer server = new HttpTaskServer(tasksManager);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpTaskServer(FileBackedTasksManager tasksManager) {
        this.tasksManager = tasksManager;
        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new AllTasksHandler());
            httpServer.createContext("/tasks/task", new TasksHandler());
            httpServer.createContext("/tasks/epic", new EpicsHandler());
            httpServer.createContext("/tasks/subtask", new SubtasksHandler());
            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
            // httpServer.stop(1);

        } catch (IOException e) {
            System.out.println("Что-то пошло не так");
        }

    }

    public class AllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            int responseCode = 0;
            String response = "";
            String method = httpExchange.getRequestMethod();

            if (method.equals("GET")) {
                response = gson.toJson(tasksManager.getTasks());
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

    public class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks/task запроса от клиента.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Тело запроса:\n" + body);
            Task task = gson.fromJson(body, Task.class);
            switch (method) {
                case "GET":
                    response = getTask(query);
                    responseCode = 200;
                    break;
                case "POST":
                    response = addTask(task,query);
                    responseCode = 201;
                    break;
                case "DELETE":
                    responseCode = 204;
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

            switch (method) {
                case "GET":
                    response = getEpic(query);
                    responseCode = 200;
                    break;
                case "POST":

                    break;
                case "DELETE":

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

            switch (method) {
                case "GET":
                    response = getSubtask(query);
                    responseCode = 200;
                    break;
                case "POST":

                    break;
                case "DELETE":

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

    public String addTask (Task task,String query) {

        if (query != null) {
            String id = query.split("=")[1];
            tasksManager.updateTask(task);
        } else {
            tasksManager.addTask(task);
        }
        return gson.toJson(task.getId());
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


    private Integer extractIdFromQuery(String query) {
        String[] split = query.split("=");
        return Integer.parseInt(split[1]);
    }

}
