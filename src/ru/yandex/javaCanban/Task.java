package ru.yandex.javaCanban;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status status;
    private TypeTask typeTask;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.typeTask = getTypeClass();
    }

    private TypeTask getTypeClass() {
        String typeClass = this.getClass().getSimpleName();
        switch (typeClass) {
            case "Task" -> typeTask = TypeTask.TASK;
            case "Subtask" -> typeTask = TypeTask.SUBTASK;
            case "Epic" -> typeTask = TypeTask.EPIC;
        }
        return typeTask;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.typeTask = getTypeClass();
    }

    public TypeTask getTypeTask() {
        return typeTask;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id=" + id + '\'' +
                ", статус=" + status +
                '}';
    }
}
