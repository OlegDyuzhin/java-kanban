package controllers;

import model.tasks.Task;


import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTask = new ArrayList<>();
    private final int CAPACITY = 10;
    private int counterHistoryTask = 0;

    @Override
    public void add(Task task) {
        final int CAPACITY = 10;
        historyTask.add(task);
        if (counterHistoryTask == CAPACITY) {
            historyTask.removeFirst();
            counterHistoryTask--;
        }
        counterHistoryTask++;
    }

    @Override
    public List<Task> getHistoryTask() {
        return historyTask;
    }
}
