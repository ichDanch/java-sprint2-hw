import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    ArrayList<Subtask> subtasksList = new ArrayList<>();

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = Manager.ID++;
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

    public void recalculateStatus() {
        int countNew = 0;
        int countDone = 0;

        for (Subtask element : subtasksList) {
            if (element.status.equals("NEW")) {
                countNew++;
            } else if (element.status.equals("DONE")) {
                countDone++;
            }
        }

        if (subtasksList.size() == 0) {
            status = "NEW";
        } else if (countNew == subtasksList.size()) {
            status = "NEW";
        } else if (countDone == subtasksList.size()) {
            status = "DONE";
        } else {
            status = "IN_PROGRESS";
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
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

