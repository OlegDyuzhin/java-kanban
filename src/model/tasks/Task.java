package model.tasks;

import model.util.Status;
import model.util.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    protected TypeTask typeTask;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

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

    public Task(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.typeTask = TypeTask.TASK;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.typeTask = TypeTask.TASK;
        this.status = Status.NEW;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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
                ", время начала=" + startTime +
                ", продолжительность=" + duration +
                '}';
    }

    public String toStringCSV() {
        if (startTime != null) {
            return String.join(",", String.valueOf(id), typeTask.toString(), name, status.toString(),
                    description, startTime.toString(), duration.toString());
        } else {
            return String.join(",", String.valueOf(id), typeTask.toString(), name, status.toString(),
                    description);
        }
    }
}
