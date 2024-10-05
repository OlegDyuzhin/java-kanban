package controllers;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class HistoryManagerTest {
    HistoryManager hm = Managers.getDefaultHistory();
    static Epic epic1, epic2;
    static Task task1, task2, task3;
    static Subtask subtask1, subtask2, subtask3, subtask4;

    @BeforeAll
    static void createTask() {
        epic1 = new Epic("Epic 1", "Epic1Description");
        epic2 = new Epic("Epic 2", "Epic2Description");
        task1 = new Task("Task 1", "Task1Description");
        task2 = new Task("Task 2", "Task2Description");
        task3 = new Task("Task 3", "Task3Description");
        subtask1 = new Subtask("SubTask 1", "Subtask1Description");
        subtask2 = new Subtask("SubTask 2", "Subtask2Description");
        subtask3 = new Subtask("SubTask 3", "Subtask3Description");
        subtask4 = new Subtask("SubTask 4", "Subtask4Description");
    }
    @DisplayName("Проверка на добавление Тасков")
    @Test
    void addTaskTest() {
        List<Task> history = hm.getHistoryTask();
        assertEquals(0, history.size(), "При создании список просмотренных задач не пуст");
        history.add(task1);
        assertEquals(1, history.size(), "Задача не добавляется");
        assertEquals(task1, hm.getHistoryTask().getFirst(), "Задачи не совпадают");
        history.add(epic1);
        history.add(subtask1);
        assertEquals(3, history.size(), "Задачи других типов не добавляются");
        assertEquals(subtask1, hm.getHistoryTask().getLast(), "Последняя задача не совпадает");

        List<Task> historyTest = Arrays.asList(task1, epic1, subtask1);
        assertArrayEquals(historyTest.toArray(), history.toArray(), "Добавленные задачи не совпадают");
    }

    @DisplayName("Проверка на максимальное количество задач")
    @Test
    void addMaxCapacityTest() {
        for (int i = 0; i < 10; i++) {
            hm.add(new Task("Задача " + i, "Описание"+ i));
        }
        assertEquals(10, hm.getHistoryTask().size(), "Максимальное количество задач не совпадает");
        hm.add(epic2);
        assertEquals(epic2, hm.getHistoryTask().getLast(), "Последний элемент не совпадает");
    }
}
