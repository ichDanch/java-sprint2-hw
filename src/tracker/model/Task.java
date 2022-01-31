package tracker.model;

import tracker.controller.Manager;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected String status;

    public Task() {
    }

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.id = Manager.getID() + 1;
        Manager.setID(this.id);
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


