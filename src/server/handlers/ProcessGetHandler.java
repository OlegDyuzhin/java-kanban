package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.tasks.Epic;
import model.util.TypeTask;

import java.io.IOException;

public class ProcessGetHandler extends BaseHttpHandler {
    protected ProcessGetHandler(TaskManager taskManager) {
        super(taskManager);
    }

    protected void processGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
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

}
