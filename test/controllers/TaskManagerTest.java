package controllers;

import exceptions.TaskValidationTimeException;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.util.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    Task task;
    Subtask subtask;
    Subtask subtask2;
    Epic epic;


    @BeforeEach
    void createTasks() {
        this.task = new Task("Test addNewTask", "Test addNewTask description");
        this.epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        this.subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        this.subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description");
    }


    @DisplayName("Создание таска")
    @Test
    void addNewTask() {

        manager.setTask(task);
        final int taskId = task.getId();
        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(savedTask.getName(), task.getName(), "Имя при добавлении изменилось");
        assertEquals(savedTask.getDescription(), task.getDescription(), "Описание при добавлении изменилось");
        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @DisplayName("Создание Эпика с сабтаской")
    @Test
    void addNewEpicTest() {

        manager.setTask(epic);
        final int taskId = epic.getId();
        final Task savedTask = manager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
        assertEquals(savedTask.getName(), epic.getName(), "Имя при добавлении изменилось");
        assertEquals(savedTask.getDescription(), epic.getDescription(), "Описание при добавлении изменилось");
        manager.setTask(subtask);
        epic.setSubtasks(subtask);
        assertEquals(epic.getSubtasksId().getFirst(), subtask.getId(), "Эпик не сохраняет id subtask");

        final List<Task> tasks = manager.getAllEpics();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.getFirst(), "Задачи не совпадают.");
        assertNotEquals(epic.getId(), subtask.getId(), "id равны");
    }

    @DisplayName("Проверка ранее созданной сабтаски")
    @Test
    void addNewSubtaskTest() {
        addNewEpicTest();

        final int taskId = subtask.getId();
        final Task savedTask = manager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
        assertEquals(savedTask.getName(), subtask.getName(), "Имя при добавлении изменилось");
        assertEquals(savedTask.getDescription(), subtask.getDescription(), "Описание при добавлении изменилось");
        List<Task> tasks = manager.getAllSubtasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        manager.setTask(subtask2);
        tasks = manager.getAllSubtasks();
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(subtask, tasks.getFirst(), "Задачи не совпадают.");

        assertEquals(epic.getId(), subtask.getEpicId(), "Subtask не хранит id эпика");
        epic.setSubtasks(subtask2);
        assertEquals(epic.getId(), subtask2.getEpicId(), "Subtask2 не хранит id эпика");
    }

    @DisplayName("Равенство тасков при одинаковом id")
    @Test
    void equalsIdTuskTest() {
        task.setId(0);
        Task task2 = new Task("Другое название", "Другое описание");
        task2.setId(0);
        assertEquals(task, task2, "Таски с одинаковым id не равны");
        subtask.setId(1);
        epic.setId(1);
        assertEquals(subtask, epic, "Наследники при одинаковом id не равны");
    }

    @DisplayName("Проверка на входимость")
    @Test
    void entryTaskTest() {
        assertNotEquals(epic.getClass(), Subtask.class, "Метод добавления сабтаска сможет принять эпик"); //п.3 ТЗ нельзя передать эпик в качестве сабтаска
        // п. 4 ТЗ subtask не может сам себе присваивать EpicId это делает эпик

    }

    @DisplayName("Проверка на удаление")
    @Test
    void removeTaskTest() {
        manager.setTask(task);
        manager.setTask(epic);
        manager.setTask(subtask);
        epic.setSubtasks(subtask);

        assertEquals(1, manager.getAllTasks().size(), "Задача не добавлена");
        assertEquals(1, manager.getAllSubtasks().size(), "Задача не добавлена");
        assertEquals(1, manager.getAllEpics().size(), "Задача не добавлена");

        manager.setTask(subtask2);
        assertEquals(2, manager.getAllSubtasks().size(), "2 Задача не добавлена");
        manager.removeTaskById(subtask.getId());
        assertEquals(1, manager.getAllSubtasks().size(), "1 Задача не удалена");
        assertEquals(subtask2, manager.getAllSubtasks().getFirst(), "2 задача тоже была удалена");
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();
        assertEquals(0, manager.getAllTasks().size(), "Задачи не удалены");
        assertEquals(0, manager.getAllSubtasks().size(), "Задачи не удалены");
        assertEquals(0, manager.getAllEpics().size(), "Задачи не удалены");
    }

    @DisplayName("Проверка задач на обновление статуса")
    @Test
    void epicStatusTest() {
        manager.setTask(task);
        manager.setTask(epic);
        manager.setTask(subtask);
        epic.setSubtasks(subtask);
        manager.setTask(subtask2);
        epic.setSubtasks(subtask2);

        assertEquals(task.getStatus(), Status.NEW, "Статус при создании не NEW");
        assertEquals(subtask.getStatus(), Status.NEW, "Статус при создании не NEW");
        assertEquals(epic.getStatus(), Status.NEW, "Статус при создании не NEW");

        manager.updateTask(new Task(task.getName(), task.getDescription(), Status.DONE, task.getId()));
        manager.updateTask(new Subtask(subtask.getName(), subtask.getDescription(), Status.IN_PROGRESS, subtask.getId(),
                epic.getId()));
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getStatus(),
                "Статус после обновления Таска не соответствует");
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(subtask.getId()).getStatus(),
                "Статус после обновления Сабтаска не соответствует");
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(epic.getId()).getStatus(),
                "Статус Эпика после обновления Сабтаска не соответствует");

        manager.clearSubtasks();
        assertEquals(Status.NEW, manager.getTaskById(epic.getId()).getStatus(),
                "Статус Эпика после удаления Сабтаска не соответствует");
    }

    @DisplayName("Проверка на несуществующие id")
    @Test
    void getNonExistentTask() {
        assertNull(manager.getTaskById(0), "Ошибка при добавлении");

    }

    @DisplayName("Проверка на удаление id сабтасов у эпиков")
    @Test
    void getRemoveSubtaskId() {
        manager.setTask(epic);
        manager.setTask(subtask);
        manager.setTask(subtask2);
        epic.setSubtasks(subtask);
        epic.setSubtasks(subtask2);
        assertEquals(2, epic.getSubtasksId().size(), "Количество сабтасков не совпадает");
        manager.removeTaskById(subtask.getId());
        assertEquals(1, epic.getSubtasksId().size(), "Количество сабтасков не совпадает");
        manager.removeTaskById(subtask2.getId());
        assertEquals(0, epic.getSubtasksId().size(), "Количество сабтасков не совпадает");

    }

    @DisplayName("Создание задач с временем")
    @Test
    void createTaskThisTimeTest() {
        task = new Task("Test addNewTask", "Test addNewTask description", LocalDateTime.of(
                2024, 1, 1, 3, 15), Duration.ofMinutes(15));
        epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                LocalDateTime.of(2024, 1, 1, 3, 0), Duration.ofMinutes(15));
        subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description",
                LocalDateTime.of(2024, 1, 1, 3, 45), Duration.ofMinutes(15));
        manager.setTask(task);
        manager.setTask(epic);
        manager.setTask(subtask);
        manager.setTask(subtask2);
        manager.setSubtaskInEpic(epic, subtask);
        manager.setSubtaskInEpic(epic, subtask2);

        assertEquals(task, manager.getTaskById(task.getId()), "Задача в менеджере не совпадает");
        assertEquals(subtask, manager.getTaskById(subtask.getId()), "Подзадача в менеджере не совпадает");
        assertEquals(epic, manager.getTaskById(epic.getId()), "Подзадача в менеджере не совпадает");
    }

    @DisplayName("Проверка пересечения времени")
    @Test
    void validateTaskCrossTimeTest() {
        createTaskThisTimeTest();
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", LocalDateTime.of(
                2024, 1, 1, 3, 10), Duration.ofMinutes(15));
        assertThrows(TaskValidationTimeException.class, () -> manager.setTask(task2),
                "Пересечение не создает ошибки");
        Task task3 = new Task("Test addNewTask", "Test addNewTask description", LocalDateTime.of(
                2024, 1, 1, 3, 59, 59), Duration.ofMinutes(15));
        assertThrows(TaskValidationTimeException.class, () -> manager.setTask(task3),
                "Пересечение по нижней границе не создает ошибки");
        Task task4 = new Task("Test addNewTask", "Test addNewTask description", LocalDateTime.of(
                2024, 1, 1, 2, 45, 1), Duration.ofMinutes(15));
        assertThrows(TaskValidationTimeException.class, () -> manager.setTask(task4),
                "Пересечение по верхней границе не создает ошибки");
    }

    @DisplayName("Проверка сортировки тасков по времени")
    @Test
    void getPrioritizedTaskTest() {
        createTaskThisTimeTest();
        Task[] tasks = new Task[]{subtask, task, subtask2};
        assertArrayEquals(manager.getPrioritizedTask().stream().toArray(), tasks, "Задачи не расставлены по порядку");
    }
}