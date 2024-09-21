package ru.yandex.javaCanban;

public class Subtask extends Task {
    private int epicId;
    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, Status status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
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
}
