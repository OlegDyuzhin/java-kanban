package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ProcessPostHandler extends BaseHttpHandler {
    protected ProcessPostHandler(TaskManager taskManager) {
        super(taskManager);
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

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
}
