package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;

import java.io.IOException;

public class ProcessDeleteHandler extends BaseHttpHandler {
    protected ProcessDeleteHandler(TaskManager taskManager) {
        super(taskManager);
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length == 3) {
            try {
                int id = Integer.parseInt(splitPath[2]);
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
}
