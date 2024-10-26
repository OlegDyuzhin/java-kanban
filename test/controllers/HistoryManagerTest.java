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
    private static int id = 0;


    @BeforeAll
    static void createTask() {
        epic1 = new Epic("Epic 1", "Epic1Description");
        epic1.setId(id++);
        epic2 = new Epic("Epic 2", "Epic2Description");
        epic2.setId(id++);
        task1 = new Task("Task 1", "Task1Description");
        task1.setId(id++);
        task2 = new Task("Task 2", "Task2Description");
        task2.setId(id++);
        task3 = new Task("Task 3", "Task3Description");
        task3.setId(id++);
        subtask1 = new Subtask("SubTask 1", "Subtask1Description");
        subtask1.setId(id++);
        subtask1.setEpicId(epic1.getId());
        subtask2 = new Subtask("SubTask 2", "Subtask2Description");
        subtask2.setId(id++);
        subtask2.setEpicId(epic1.getId());
        subtask3 = new Subtask("SubTask 3", "Subtask3Description");
        subtask3.setId(id++);
        subtask3.setEpicId(epic1.getId());
        subtask4 = new Subtask("SubTask 4", "Subtask4Description");
        subtask4.setId(id++);
        subtask4.setEpicId(epic2.getId());
    }

    @DisplayName("Проверка на добавление Тасков")
    @Test
    void addTaskTest() {
        assertEquals(0, hm.getHistoryTask().size(), "При создании список просмотренных задач не пуст");
        hm.add(task1);
        assertEquals(1, hm.getHistoryTask().size(), "Задача не добавляется");
        assertEquals(task1, hm.getHistoryTask().getFirst(), "Задачи не совпадают");
        hm.add(epic1);
        hm.add(subtask1);
        assertEquals(3, hm.getHistoryTask().size(), "Задачи других типов не добавляются");
        assertEquals(subtask1, hm.getHistoryTask().getLast(), "Последняя задача не совпадает");

        List<Task> historyTest = Arrays.asList(task1, epic1, subtask1);
        assertArrayEquals(historyTest.toArray(), hm.getHistoryTask().toArray(), "Добавленные задачи не совпадают");
    }

    @DisplayName("Проверка на добавление 1 Таска / 2 раза")
    @Test
    void addSingleTaskTest() {
        hm.add(task1);
        assertEquals(1, hm.getHistoryTask().size(), "Задача не добавляется");
        hm.add(task1);
        assertEquals(1, hm.getHistoryTask().size(), "Задача не добавляется/Не очищает предыдущую");
    }

    @DisplayName("Проверка на порядок списка")
    @Test
    void procedureListTaskTest() {
        hm.add(task1);
        hm.add(subtask3);
        hm.add(epic2);
        List<Task> historyTest = Arrays.asList(task1, subtask3, epic2);
        assertArrayEquals(historyTest.toArray(), hm.getHistoryTask().toArray(), "Добавленные задачи не совпадают");
        hm.add(task1);
        hm.add(epic1);
        historyTest = Arrays.asList(subtask3, epic2, task1, epic1);
        assertArrayEquals(historyTest.toArray(), hm.getHistoryTask().toArray(), "Добавленные задачи не совпадают");
    }

    @DisplayName("Проверка на удаление задач")
    @Test
    void removeTaskTest() {
        hm.add(task1);
        hm.add(subtask3);
        hm.add(epic2);
        hm.add(epic1);
        hm.remove(task1.getId());
        List<Task> historyTest = Arrays.asList(subtask3, epic2, epic1);
        assertArrayEquals(historyTest.toArray(), hm.getHistoryTask().toArray(), "1 задача не удалена");
        hm.remove(epic2.getId());
        historyTest = Arrays.asList(subtask3, epic1);
        assertArrayEquals(historyTest.toArray(), hm.getHistoryTask().toArray(), "средняя задача не удалена");
        hm.remove(epic1.getId());
        historyTest = Arrays.asList(subtask3);
        assertArrayEquals(historyTest.toArray(), hm.getHistoryTask().toArray(), "последняя задача не удалена");
        hm.remove(subtask3.getId());
        assertEquals(0, hm.getHistoryTask().size(), "список не пуст");
    }
}
