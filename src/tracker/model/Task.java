package tracker.model;

import tracker.manager.InMemoryTaskManager;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public Task() {
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "tracker.model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                '}';
    }

}


