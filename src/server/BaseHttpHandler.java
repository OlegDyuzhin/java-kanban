package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.TypeTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();
            String[] splitPath = exchange.getRequestURI().getPath().split("/");
            if (splitPath.length == 0 || splitPath.length > 4) {
                sendNotFound(exchange);
            } else {
                switch (requestMethod) {
                    case "GET" -> readResource(exchange, splitPath);
                    case "POST" -> createResource(exchange, splitPath);
                    case "DELETE" -> deleteResource(exchange, splitPath);
                    default -> sendText(exchange, "Такого метода нет", 405);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readResource(HttpExchange exchange, String[] path) throws IOException {
        switch (path[1]) {
            case "tasks" -> {
                switch (path.length) {
                    case 2 -> sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                    case 3 -> {
                        if (taskManager.getTaskById(Integer.parseInt(path[2])) == null) {
                            sendNotFound(exchange);
                        } else {
                            if (taskManager.getTaskById(Integer.parseInt(path[2])).getTypeTask() != TypeTask.TASK) {
                                sendNotFound(exchange);
                            } else {
                                sendText(exchange, gson.toJson(taskManager.getTaskById(Integer.parseInt(path[2]))),
                                        200);
                            }
                        }
                    }
                    default -> sendNotFound(exchange);
                }
            }
            case "subtasks" -> {
                switch (path.length) {
                    case 2 -> sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                    case 3 -> {
                        if (taskManager.getTaskById(Integer.parseInt(path[2])) == null) {
                            sendNotFound(exchange);
                        } else {
                            if (taskManager.getTaskById(Integer.parseInt(path[2])).getTypeTask() != TypeTask.SUBTASK) {
                                sendNotFound(exchange);
                            } else {
                                sendText(exchange, gson.toJson(taskManager.getTaskById(Integer.parseInt(path[2]))),
                                        200);
                            }
                        }
                    }
                    default -> sendNotFound(exchange);
                }
            }
            case "epics" -> {
                switch (path.length) {
                    case 2 -> sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                    case 3 -> {
                        if (taskManager.getTaskById(Integer.parseInt(path[2])) == null) {
                            sendNotFound(exchange);
                        } else {
                            if (taskManager.getTaskById(Integer.parseInt(path[2])).getTypeTask() != TypeTask.EPIC) {
                                sendNotFound(exchange);
                            } else {
                                sendText(exchange, gson.toJson(taskManager.getTaskById(Integer.parseInt(path[2]))),
                                        200);
                            }
                        }
                    }
                    case 4 -> {
                        int id = Integer.parseInt(path[2]);
                        if (TypeTask.EPIC == (taskManager.getTaskById(id).getTypeTask())) {
                            sendText(exchange,
                                    gson.toJson(taskManager.getSubtaskEpic((Epic) taskManager.getTaskById(id))),
                                    200);
                        } else sendNotFound(exchange);
                    }
                    default -> sendNotFound(exchange);
                }
            }
            case "history" -> sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
            case "prioritized" -> sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
            default -> {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        }
    }

    private void createResource(HttpExchange exchange, String[] path) throws IOException {
        switch (path.length) {
            case 3 -> {
                try {
                    String stringRequest = exchange.getRequestBody().readAllBytes().toString();
                    int id = Integer.parseInt(path[2]);
                    if (taskManager.getTaskById(id) == null) sendHasInteractions(exchange);
                    else {
                        switch (path[1]) {
                            case "tasks" -> taskManager.updateTask(gson.fromJson(stringRequest, Task.class));
                            case "subtasks" -> taskManager.updateTask(gson.fromJson(stringRequest, Subtask.class));
                            case "epics" -> taskManager.updateTask(gson.fromJson(stringRequest, Epic.class));
                            default -> sendNotFound(exchange);
                        }
                    }
                    exchange.sendResponseHeaders(200, 0);
                    exchange.close();
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                }
            }
            case 2 -> {
                try {
                    String stringRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                    switch (path[1]) {
                        case "tasks" -> taskManager.setTask(gson.fromJson(stringRequest, Task.class));
                        case "subtasks" -> taskManager.setTask(gson.fromJson(stringRequest, Subtask.class));
                        case "epics" -> taskManager.setTask(gson.fromJson(stringRequest, Epic.class));
                    }
                    exchange.sendResponseHeaders(200, 0);
                    exchange.close();
                } catch (IllegalArgumentException e) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                }
            }
            default -> {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        }
    }

    private void deleteResource(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 3) {
            try {
                int id = Integer.parseInt(path[2]);
                taskManager.removeTaskById(id);
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void sendText(HttpExchange exchange, String resposeString, int responseCode) throws IOException {
        byte[] resp = resposeString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    private void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }
}
