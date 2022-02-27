package tracker.model;

import tracker.controller.InMemoryTaskManager;

import java.util.Objects;

public class Subtask extends Task {

    private long idParentEpic;

    public Subtask() {
    }

    public Subtask(String name, String description, Status status, long idParentEpic) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);
        this.status = status;
        this.idParentEpic = idParentEpic;
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
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getIdParentEpic() {
        return idParentEpic;
    }

    public void setIdParentEpic(long idParentEpic) {
        this.idParentEpic = idParentEpic;
    }

    @Override
    public String toString() {
        return "tracker.model.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                ", idParentEpic=" + idParentEpic +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return this.getId() == subtask.getId();
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId());
        result = 31 * result;

        return result;
    }
}
