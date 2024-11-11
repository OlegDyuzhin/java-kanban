package controllers;

import exceptions.ManagerSaveException;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.Status;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path path = Path.of("src", "resources", "savedTasks.csv"); //файл по умолчанию
    private static final String HEADER_CSV = "id,type,name,status,description,epic";

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public FileBackedTaskManager() {
    }

    public void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write(HEADER_CSV + "\n");
            for (Task task : getAllTasks()) {
                bw.write(task.toStringCSV() + "\n");
            }
            for (Task epic : getAllEpics()) {
                bw.write(epic.toStringCSV() + "\n");
            }
            for (Task subtask : getAllSubtasks()) {
                bw.write(subtask.toStringCSV() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager tm = new FileBackedTaskManager(path);
        int maxId = 0;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                Task task = fromString(line);
                tm.setCounterId(task.getId());
                tm.setTask(task);
                if (task.getId() > maxId) maxId = task.getId();
            }
            for (Task subtask : tm.getAllSubtasks()) {
                Subtask sub = (Subtask) subtask;
                Epic ep = (Epic) tm.getTaskById(sub.getEpicId());
                ep.setSubtasks(sub);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось восстановить данные из файла или указанный файл не найден");
        } finally {
            tm.setCounterId(maxId + 1);
        }
        return tm;
    }

    public static Task fromString(String taskString) {
        String[] tasks = taskString.split(",");

        int id = Integer.parseInt(tasks[0]);
        String type = tasks[1];
        String name = tasks[2];
        Status status = Status.valueOf(tasks[3]);
        String description = tasks[4];
        Integer epicId = tasks.length > 5 ? Integer.valueOf(tasks[5]) : null;

        switch (type) {
            case "TASK" -> {
                return new Task(name, description, status, id);
            }
            case "EPIC" -> {
                Epic epic = new Epic(name, description);
                epic.setStatus(status);
                epic.setId(id);
                return epic;
            }
            case "SUBTASK" -> {
                return new Subtask(name, description, status, id, epicId);
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void setTask(Task task) {
        super.setTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }
}
