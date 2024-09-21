package ru.yandex.javaCanban;

public class Main {

    public static void main(String[] args) {

        TaskManager tm = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        tm.setTask(task1); // id 0
        tm.setTask(task2); // id 1

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1");
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2");
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3");
        tm.setTask(subtask1); // id 2
        tm.setTask(subtask2); // id 3
        tm.setTask(subtask3); // id 4

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        tm.setTask(epic1); // id 5
        epic1.setSubtasks(subtask1);
        epic1.setSubtasks(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        tm.setTask(epic2); // id 6
        epic2.setSubtasks(subtask3);

        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllSubtasks());
        System.out.println(tm.getAllEpics());

        tm.updateTask(new Task("Новое имя Задачи 1", "Новое описание задачи 1",
                Status.DONE, task1.getId()));
        tm.updateTask(new Subtask("Новое имя подзадачи 1", "Новое описание", Status.IN_PROGRESS,
                subtask1.getId(), subtask1.getEpicId()));
        tm.updateTask(new Subtask(subtask3.getName(), subtask3.getDescription(), Status.DONE, subtask3.getId(),
                subtask3.getEpicId()));
        tm.updateTask(new Epic(epic1.getName(), epic1.getDescription(), Status.DONE, epic1.getId()));

        System.out.println("..............");
        System.out.println(tm.getAllEpics());
        System.out.println(tm.getSubtaskEpic(epic1));

        System.out.println(("............."));
        tm.removeTaskById(1);
        tm.removeTaskById(6);
        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllSubtasks());
        System.out.println(tm.getAllEpics());

    }


}
