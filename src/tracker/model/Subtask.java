package model;


import manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Subtask extends Task {

    private int idParentEpic;

    public Subtask() {
    }

    public Subtask(String name, String description, Status status, int idParentEpic, int duration, String startTime) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);
        this.status = status;
        this.idParentEpic = idParentEpic;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = Optional.of(LocalDateTime.parse(startTime,formatterStartTime));
    }

    public Subtask(int id, String name, String description, Status status, int idParentEpic, int duration, String startTime) {
        this.id = id;
        InMemoryTaskManager.setID(InMemoryTaskManager.getID() + 1);
        this.name = name;
        this.description = description;
        this.status = status;
        this.idParentEpic = idParentEpic;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = Optional.of(LocalDateTime.parse(startTime,formatterStartTime));
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
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getIdParentEpic() {
        return idParentEpic;
    }

    public void setIdParentEpic(int idParentEpic) {
        this.idParentEpic = idParentEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", idParentEpic=" + idParentEpic +
                ", duration=" + duration +
                ", startTime=" + startTime +
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
