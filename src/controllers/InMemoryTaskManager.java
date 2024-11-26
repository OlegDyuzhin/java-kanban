package controllers;

import exceptions.TaskValidationTimeException;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.Status;
import model.util.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, TypeTask> typeToId = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int counterId = 0;

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
        for (Task task : getPrioritizedTasks()) {
            if (task.getTypeTask() == TypeTask.TASK) prioritizedTask.remove(task);
        }
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
        for (Task task : getPrioritizedTasks()) {
            if (task.getTypeTask() == TypeTask.SUBTASK) prioritizedTask.remove(task);
        }
    }

    @Override
    public void clearSubtasks() {
        for (Integer id : subtasks.keySet()) {
            epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
        }
        subtasks.clear();
        for (Integer ids : epics.keySet()) {
            checkEpicStatus(ids);
            checkEpicStartTimeAndDuration((Epic) getTaskById(ids));
            for (Task task : getPrioritizedTasks()) {
                if (task.getTypeTask() == TypeTask.SUBTASK) prioritizedTask.remove(task);
            }
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
        if (task.getStartTime() != null) validateLocalDateTimeInterval(task);
        switch (task.getTypeTask()) {
            case TASK -> {
                tasks.put(task.getId(), task);
                if (task.getStartTime() != null) prioritizedTask.add(task);
            }
            case SUBTASK -> {
                subtasks.put(task.getId(), (Subtask) task);
                if (task.getStartTime() != null) prioritizedTask.add(task);
            }
            case EPIC -> epics.put(task.getId(), (Epic) task);
        }

    }

    @Override
    public void updateTask(Task task) {
        if (task.getTypeTask() != typeToId.get(task.getId())) removeTaskById(task.getId());
        validateLocalDateTimeInterval(task);
        switch (task.getTypeTask()) {
            case TASK -> tasks.put(task.getId(), task);
            case SUBTASK -> {
                subtasks.put(task.getId(), (Subtask) task);
                checkEpicStatus(((Subtask) task).getEpicId());
                checkEpicStartTimeAndDuration((Epic) getTaskById(((Subtask) task).getEpicId()));
            }
            case EPIC -> {
                ((Epic) task).setSubtasksId(epics.get(task.getId()).getSubtasksId());
                epics.put(task.getId(), (Epic) task);
                checkEpicStatus(task.getId());
                checkEpicStartTimeAndDuration((Epic) task);
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!(tasks.containsKey(id) || subtasks.containsKey(id) || epics.containsKey(id))) return;
        switch (typeToId.get(id)) {
            case TASK -> {
                prioritizedTask.remove(tasks.get(id));
                tasks.remove(id);
                historyManager.remove(id);
            }
            case SUBTASK -> {
                int epicId = subtasks.get(id).getEpicId();
                epics.get(epicId).removeSubtaskId(id);
                subtasks.remove(id);
                checkEpicStatus(epicId);
                checkEpicStartTimeAndDuration((Epic) getTaskById(epicId));
                historyManager.remove(id);
                prioritizedTask.remove(tasks.get(id));
            }
            case EPIC -> {
                for (Integer i : epics.get(id).getSubtasksId()) {
                    subtasks.remove(i);
                    prioritizedTask.remove(tasks.get(i));
                    historyManager.remove(i);
                }
                epics.get(id).getSubtasksId().clear();
                epics.remove(id);
                historyManager.remove(id);
            }
        }
    }

    @Override
    public List<Subtask> getSubtaskEpic(Epic epic) {
        return epic.getSubtasksId().stream().map(subtasks::get).toList();
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

    public void setSubtaskInEpic(Epic epic, Subtask subtask) {
        epic.setSubtasks(subtask);
        checkEpicStartTimeAndDuration(epic);
    }

    public void checkEpicStartTimeAndDuration(Epic epic) {
        LocalDateTime startTime = getSubtaskEpic(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(startTime);

        LocalDateTime endTime = getSubtaskEpic(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(endTime);

        Duration duration = getSubtaskEpic(epic).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(duration);
    }

    public void validateLocalDateTimeInterval(Task validateTask) {
        if (prioritizedTask.isEmpty()) return;
        for (Task task : prioritizedTask) {
            if (validateTask.getStartTime().isBefore(task.getEndTime())
                    && validateTask.getEndTime().isAfter(task.getStartTime())) {
                throw new TaskValidationTimeException("Временные интервалы для задач пересекаются");
            }
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTask);
    }
}
