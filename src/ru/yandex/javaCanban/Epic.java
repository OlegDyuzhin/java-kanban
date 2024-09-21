package ru.yandex.javaCanban;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void removeSubtaskId(Integer id) {
        subtasksId.remove(id);
    }

    public void setSubtasks(Subtask subtask) {

        subtask.setEpicId(super.getId());
        subtasksId.add(subtask.getId());
    }

    @Override
    public String toString() {
        if (subtasksId.isEmpty()) {
            return super.toString();
        } else {
            return super.toString() +
                    " id подзадач=" + subtasksId +
                    "}";
        }
    }
}



