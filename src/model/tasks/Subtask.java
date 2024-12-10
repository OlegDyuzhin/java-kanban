package model.tasks;

import model.util.Status;
import model.util.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, Integer epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration,
                   Integer epicId) {
        super(name, description, status, id, startTime, duration);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description) {
        super(name, description);
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int id, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(name, description, status, id, startTime, duration);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString() +
                " epicId=" + epicId +
                "}";
    }

    @Override
    public String toStringCSV() {
        return super.toStringCSV().concat(',' + String.valueOf(epicId));
    }
}
