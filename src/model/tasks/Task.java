package model.tasks;

import model.util.Status;
import model.util.TypeTask;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status status;
    protected TypeTask typeTask;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.typeTask = TypeTask.TASK;
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.typeTask = TypeTask.TASK;
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

    public int getId() {
        return this.id;
    }

    public TypeTask getTypeTask() {
        return typeTask;
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
        //  if (o == null || getClass() != o.getClass()) return false;
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

    public String toStringCSV() {
        return String.join(",", String.valueOf(id), typeTask.toString(), name, status.toString(), description);
    }
}
