package model.tasks;

import model.util.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.typeTask = TypeTask.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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



