import controllers.FileBackedTaskManager;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        FileBackedTaskManager tm = new FileBackedTaskManager();

        /*
         * Дополнительное задание. Реализуем пользовательский сценарий
         */

        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        tm.setTask(task1);
        tm.setTask(task2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1");
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2");
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3");
        tm.setTask(subtask1);
        tm.setTask(subtask2);
        tm.setTask(subtask3);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        tm.setTask(epic1);
        epic1.setSubtasks(subtask1);
        epic1.setSubtasks(subtask2);
        epic1.setSubtasks(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Эпик без подзадач");
        tm.setTask(epic2);

        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllEpics());
        System.out.println(tm.getAllSubtasks());
        System.out.println("-------");
        tm.save();

        tm = FileBackedTaskManager.loadFromFile(Path.of("src/resources/savedTasks.csv"));
        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllEpics());
        System.out.println(tm.getAllSubtasks());

    }


}
