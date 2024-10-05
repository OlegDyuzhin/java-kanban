package controllers;

import model.tasks.Task;


import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTask = new ArrayList<>();
    private final int CAPACITY = 10;

    @Override
    public void add(Task task) {
        historyTask.add(task);
        if (historyTask.size() > CAPACITY) {
            historyTask.removeFirst();
        }
    }

    @Override
    public List<Task> getHistoryTask() {
        return List.copyOf(historyTask);
    }
}
