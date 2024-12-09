package server;

import com.sun.net.httpserver.HttpServer;
import controllers.Managers;
import controllers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private HttpServer server;
    private TaskManager taskManager;

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", new BaseHttpHandler(taskManager));

    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
