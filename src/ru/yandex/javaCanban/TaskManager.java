package ru.yandex.javaCanban;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int counterId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, TypeTask> typeToId = new HashMap<>();

    public int getCounterId(TypeTask typeTask) {
        typeToId.put(counterId, typeTask);
        return counterId++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        for (Integer id : subtasks.keySet()) {
            epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        Task task = null;
        switch (typeToId.get(id)) {
            case TASK -> task = tasks.get(id);
            case SUBTASK -> task = subtasks.get(id);
            case EPIC -> task = epics.get(id);
        }
        return task;
    }

    public void setTask(Task task) {
        task.setId(getCounterId(task.getTypeTask()));
        switch (task.getTypeTask()) {
            case TASK -> tasks.put(task.getId(), task);
            case SUBTASK -> subtasks.put(task.getId(), (Subtask) task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
        }
    }

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
                if (task.getStatus() == Status.DONE) {
                    for (Integer id : ((Epic) task).getSubtasksId()) {
                        subtasks.get(id).setStatus(Status.DONE);
                    }
                }

            }
        }
    }

    public void removeTaskById(int id) {
        switch (typeToId.get(id)) {
            case TASK -> tasks.remove(id);
            case SUBTASK -> {
                epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
                subtasks.remove(id);
            }
            case EPIC -> {
                for (Integer i : epics.get(id).getSubtasksId()) {
                    subtasks.remove(i);
                }
                epics.get(id).getSubtasksId().clear();
                epics.remove(id);
            }
        }
    }

    public ArrayList<Subtask> getSubtaskEpic(Epic epic) {
        ArrayList<Subtask> subtasksEpic = new ArrayList<>();
        for (Integer id : epic.getSubtasksId()) {
            subtasksEpic.add(subtasks.get(id));
        }
        return subtasksEpic;
    }

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
