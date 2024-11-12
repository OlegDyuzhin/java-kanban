package controllers;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.TypeTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager manager = new FileBackedTaskManager();
    Task task;
    Subtask subtask;
    Subtask subtask2;
    Epic epic;
    Path path = Path.of("src", "resources", "savedTasks.csv");

    @BeforeEach
    void createTasks() {
        this.task = new Task("Test addNewTask", "Test addNewTask description");
        this.epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        this.subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        this.subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description");
        manager.setTask(task);
        manager.setTask(epic);
        manager.setTask(subtask);
        manager.setTask(subtask2);
        epic.setSubtasks(subtask);
        epic.setSubtasks(subtask2);
    }

    @DisplayName("Проверка на создание файла")
    @Test
    void createFileTaskManagerTest() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager.save();
        assertTrue(Files.exists(path));
        String HEADER_CSV = "id,type,name,status,description,epic";
        try {
            String[] stringsTaskCSV = Files.readString(path).split("\n");
            assertEquals(stringsTaskCSV[0], HEADER_CSV, "Header не соответствует");
            String firstTaskToFile = stringsTaskCSV[1];
            manager.save();
            assertEquals(Files.readString(path).split("\n")[1], firstTaskToFile, "Пересохранение не работает");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("Проверка загрузки файла")
    @Test
    void loadFromFile() {
        createFileTaskManagerTest();
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(path);
        assertArrayEquals(manager.getAllTasks().toArray(), taskManager2.getAllTasks().toArray(),
                "Таски созданные и восстановленные не равны");
        assertArrayEquals(manager.getAllSubtasks().toArray(), taskManager2.getAllSubtasks().toArray(),
                "Сабтаски созданные и восстановленные не равны");
        assertArrayEquals(manager.getAllEpics().toArray(), taskManager2.getAllEpics().toArray(),
                "Эпики созданные и восстановленные не равны");
        assertEquals(manager.getCounterId(TypeTask.EPIC), taskManager2.getCounterId(TypeTask.EPIC),
                "Cчетчик id не восстановлен из файла, возможны коллизии");
    }

}
