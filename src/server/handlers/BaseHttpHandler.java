package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.Managers;
import controllers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    protected TaskManager taskManager;
    protected Gson gson = Managers.getDefaultGson();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET" -> processGet(exchange);
                case "POST" -> processPost(exchange);
                case "DELETE" -> processDelete(exchange);
                default -> sendText(exchange, "Такого метода нет", 405);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGet(HttpExchange exchange) throws IOException {
        new ProcessGetHandler(taskManager).processGet(exchange);
    }

    private void processPost(HttpExchange exchange) throws IOException {
        new ProcessPostHandler(taskManager).processPost(exchange);
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        new ProcessDeleteHandler(taskManager).processDelete(exchange);
    }

    protected void sendText(HttpExchange exchange, String resposeString, int responseCode) throws IOException {
        byte[] resp = resposeString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }
}
