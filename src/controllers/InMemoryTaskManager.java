package controllers;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.Status;
import model.util.TypeTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int counterId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, TypeTask> typeToId = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public void setCounterId(int counterId) {
        this.counterId = counterId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistoryTask();
    }

    @Override
    public int getCounterId(TypeTask typeTask) {
        typeToId.put(counterId, typeTask);
        return counterId++;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer id : subtasks.keySet()) {
            epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
        }
        subtasks.clear();
        for (Integer ids : epics.keySet()) {
            checkEpicStatus(ids);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = null;
        if (!(tasks.containsKey(id) || subtasks.containsKey(id) || epics.containsKey(id))) return task;
        switch (typeToId.get(id)) {
            case TASK -> task = tasks.get(id);
            case SUBTASK -> task = subtasks.get(id);
            case EPIC -> task = epics.get(id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void setTask(Task task) {
        task.setId(getCounterId(task.getTypeTask()));
        switch (task.getTypeTask()) {
            case TASK -> tasks.put(task.getId(), task);
            case SUBTASK -> subtasks.put(task.getId(), (Subtask) task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getTypeTask() != typeToId.get(task.getId())) removeTaskById(task.getId());
        switch (task.getTypeTask()) {
            case TASK -> tasks.put(task.getId(), task);
            case SUBTASK -> {
                subtasks.put(task.getId(), (Subtask) task);
                checkEpicStatus(((Subtask) task).getEpicId());
            }
            case EPIC -> {
                ((Epic) task).setSubtasksId(epics.get(task.getId()).getSubtasksId());
                epics.put(task.getId(), (Epic) task);
                checkEpicStatus(task.getId());
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!(tasks.containsKey(id) || subtasks.containsKey(id) || epics.containsKey(id))) return;
        switch (typeToId.get(id)) {
            case TASK -> {
                tasks.remove(id);
                historyManager.remove(id);
            }
            case SUBTASK -> {
                int epicId = subtasks.get(id).getEpicId();
                epics.get(epicId).removeSubtaskId(id);
                subtasks.remove(id);
                checkEpicStatus(epicId);
                historyManager.remove(id);
            }
            case EPIC -> {
                for (Integer i : epics.get(id).getSubtasksId()) {
                    subtasks.remove(i);
                    historyManager.remove(i);
                }
                epics.get(id).getSubtasksId().clear();
                epics.remove(id);
                historyManager.remove(id);
            }
        }
    }

    @Override
    public ArrayList<Subtask> getSubtaskEpic(Epic epic) {
        ArrayList<Subtask> subtasksEpic = new ArrayList<>();
        for (Integer id : epic.getSubtasksId()) {
            subtasksEpic.add(subtasks.get(id));
        }
        return subtasksEpic;
    }

    @Override
    public void checkEpicStatus(int epicId) {
        int counterNew = 0;
        int counterDone = 0;
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtasksId();

        for (Integer subtaskId : subtaskIds) {
            if (subtasks.get(subtaskId).getStatus() == Status.NEW) {
                counterNew++;
            } else if (subtasks.get(subtaskId).getStatus() == Status.DONE) {
                counterDone++;
            }
        }
        if (subtaskIds.size() == counterNew || subtaskIds.isEmpty()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (subtaskIds.size() == counterDone) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }
}
