package HttpServer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import controllers.TaskManager;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DurationAdapter;
import server.HttpTaskServer;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    HttpTaskServer taskServer = new HttpTaskServer();
    TaskManager manager = taskServer.getTaskManager();
    Gson gson;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        this.gson = gsonBuilder.create();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Тест добавления таски")
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Тест добавления эпика и сабтаски")
    @Test
    public void testAddSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 3", "Testing task 3");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtask = new Subtask("Test 1", "Testing task 1", LocalDateTime.now(),
                Duration.ofMinutes(5), epic.getId());
        String subtaskJson = gson.toJson(subtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> subtasksFromManager = manager.getAllSubtasks();
        List<Task> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", epicsFromManager.get(0).getName(), "Некорректное имя задачи");

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals(manager.getSubtaskEpic((Epic) epicsFromManager.get(0)).get(0).getId(),
                subtasksFromManager.get(0).getId(), "Сабтаск не привязался эпику через Http");
    }

    @DisplayName("Тест чтения тасок")
    @Test
    public void testGetTask() throws IOException, InterruptedException {
        testAddTask();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);

        Task task = gson.fromJson(jsonElement, Task.class);

        assertEquals(0, task.getId(), "Некорректный id");
        assertEquals("Test 2", task.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 2", task.getDescription(), "Некорректное имя задачи");
        assertEquals(Duration.ofMinutes(5), task.getDuration(), "Некорректное имя задачи");

    }

    @DisplayName("Тест чтения тасок по id")
    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        testAddTask();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonObject();

        Task task = gson.fromJson(jsonElement, Task.class);

        assertEquals(0, task.getId(), "Некорректный id");
        assertEquals("Test 2", task.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 2", task.getDescription(), "Некорректное имя задачи");
        assertEquals(Duration.ofMinutes(5), task.getDuration(), "Некорректное имя задачи");

        url = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("Тест чтения сабтасок и эпиков")
    @Test
    public void testGetSubtaskAndEpic() throws IOException, InterruptedException {
        testAddSubtaskAndEpic();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);
        Epic epic = gson.fromJson(jsonElement, Epic.class);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);
        Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(0, epic.getId(), "Некорректный id");
        assertEquals("Test 3", epic.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 3", epic.getDescription(), "Некорректное имя задачи");

        assertEquals(1, subtask.getId(), "Некорректный id");
        assertEquals("Test 1", subtask.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 1", subtask.getDescription(), "Некорректное имя задачи");
        assertEquals(Duration.ofMinutes(5), subtask.getDuration(), "Некорректное имя задачи");

    }

    @DisplayName("Тест чтения сабтасок и эпиков по id")
    @Test
    public void testGetSubtaskAndEpicById() throws IOException, InterruptedException {
        testAddSubtaskAndEpic();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonObject();
        Epic epic = gson.fromJson(jsonElement, Epic.class);

        url = URI.create("http://localhost:8080/subtasks/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body()).getAsJsonObject();
        Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(0, epic.getId(), "Некорректный id");
        assertEquals("Test 3", epic.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 3", epic.getDescription(), "Некорректное имя задачи");

        assertEquals(1, subtask.getId(), "Некорректный id");
        assertEquals("Test 1", subtask.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 1", subtask.getDescription(), "Некорректное имя задачи");
        assertEquals(Duration.ofMinutes(5), subtask.getDuration(), "Некорректное имя задачи");

        url = URI.create("http://localhost:8080/epics/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks/0");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("Тест удаления тасок по id")
    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        testAddTask();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("Тест удаления сабтасок и эпиков по id")
    @Test
    public void testDeleteSubtaskAndEpicById() throws IOException, InterruptedException {
        testAddSubtaskAndEpic();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> epicsFromManager = manager.getAllTasks();
        List<Task> subtasksFromManager = manager.getAllTasks();

        assertEquals(0, epicsFromManager.size(), "Задача не удалена");
        assertEquals(0, subtasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("Тест чтения сабтасок эпика по id эпика")
    @Test
    public void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        testAddSubtaskAndEpic();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body()).getAsJsonArray().get(0);
        Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(1, subtask.getId(), "Некорректный id");
        assertEquals("Test 1", subtask.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 1", subtask.getDescription(), "Некорректное имя задачи");
        assertEquals(Duration.ofMinutes(5), subtask.getDuration(), "Некорректное имя задачи");
    }

    @DisplayName("Тест чтения списка истории")
    @Test
    public void testGetHistoryManager() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);
        Epic epic = new Epic("Test 3", "Testing task 3");
        manager.setTask(epic);
        Subtask subtask = new Subtask("Test 1", "Testing task 1", LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(5), epic.getId());
        manager.setTask(subtask);
        manager.getTaskById(task.getId());
        manager.getTaskById(epic.getId());
        manager.getTaskById(subtask.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonArray asJsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        List<Task> taskList = gson.fromJson(asJsonArray, new TypeToken<List<Task>>() {
        }.getType());

        assertArrayEquals(manager.getHistory().toArray(), taskList.toArray(), "Списки не совпадают");

    }

    @DisplayName("Тест чтения prioritized списка")
    @Test
    public void testGetPrioritizedTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);
        Epic epic = new Epic("Test 3", "Testing task 3");
        manager.setTask(epic);
        Subtask subtask = new Subtask("Test 1", "Testing task 1", LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(5), epic.getId());
        manager.setTask(subtask);
        manager.getTaskById(task.getId());
        manager.getTaskById(epic.getId());
        manager.getTaskById(subtask.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonArray asJsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        List<Task> taskList = gson.fromJson(asJsonArray, new TypeToken<List<Task>>() {
        }.getType());
        assertArrayEquals(manager.getPrioritizedTasks().toArray(), taskList.toArray(), "Списки не совпадают");
    }
}