package model;

import manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration = Duration.ofMinutes(0);
    protected Optional<LocalDateTime> startTime = Optional.empty();
    public Task() {
    }

    public Task(String name, String description, Status status, int duration, String startTime) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = Optional.of(LocalDateTime.parse(startTime,FormatterStartTime.getFormatterStartTime()));
    }
    // для десереализации и метода fromStringToTask
    public Task(int id, String name, String description, Status status, int duration, String startTime) {
        this.id = id;
        InMemoryTaskManager.setID(InMemoryTaskManager.getID() + 1);
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = Optional.of(LocalDateTime.parse(startTime,FormatterStartTime.getFormatterStartTime()));
    }
    // для десереализации и метода fromStringToTask, если отсутсвтует startTime
    public Task(int id, String name, String description, Status status, int duration) {
        this.id = id;
        InMemoryTaskManager.setID(InMemoryTaskManager.getID() + 1);
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
    }
    // для десереализации и метода fromStringToTask, если отсутсвтует duration
    public Task(int id, String name, String description, Status status, String startTime) {
        this.id = id;
        InMemoryTaskManager.setID(InMemoryTaskManager.getID() + 1);
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = Optional.of(LocalDateTime.parse(startTime,FormatterStartTime.getFormatterStartTime()));
    }

    public Optional<LocalDateTime> getEndTime() {
        Optional<Duration> durationOp = Optional.ofNullable(duration);
        LocalDateTime endTimeOp = null;
        if (startTime.isPresent()&& durationOp.isPresent()) {
            endTimeOp = startTime.get().plus(durationOp.get());
            return Optional.of(endTimeOp);
        } else {
            System.out.println("Не заданы startTime и duration задания");
            return Optional.ofNullable(endTimeOp);
        }
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
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
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getId() != task.getId()) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(task.getDescription()) : task.getDescription() != null)
            return false;
        if (getStatus() != task.getStatus()) return false;
        if (!getDuration().equals(task.getDuration())) return false;
        return getStartTime().equals(task.getStartTime());
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getId();
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + getDuration().hashCode();
        result = 31 * result + getStartTime().hashCode();
        return result;
    }
}


