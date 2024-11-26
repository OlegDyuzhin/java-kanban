package controllers;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.TypeTask;

import java.util.List;

public interface TaskManager {
    int getCounterId(TypeTask typeTask);

    List<Task> getAllTasks();

    List<Task> getAllEpics();

    List<Task> getAllSubtasks();

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    Task getTaskById(int id);

    void setTask(Task task);

    void updateTask(Task task);

    void removeTaskById(int id);

    List<Subtask> getSubtaskEpic(Epic epic);

    void checkEpicStatus(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
