package tracker.model;

import tracker.controller.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Subtask> subtasksList = new ArrayList<>();

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);
    }

    public Epic(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void recalculateStatus() {
        int countNew = 0;
        int countDone = 0;

        for (Subtask element : subtasksList) {
            if (element.status == Status.NEW) {
                countNew++;
            } else if (element.status == Status.DONE) {
                countDone++;
            }
        }

        if (subtasksList.size() == 0) {
            status = Status.NEW;
        } else if (countNew == subtasksList.size()) {
            status = Status.NEW;
        } else if (countDone == subtasksList.size()) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return "tracker.model.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return this.getId() == epic.getId();
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId());
        result = 31 * result;

        return result;
    }
}

