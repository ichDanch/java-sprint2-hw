package tracker.model;

import com.sun.source.tree.IfTree;
import tracker.manager.InMemoryTaskManager;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {

    protected LocalDateTime endTime;
    private ArrayList<Subtask> subtasksList = new ArrayList<>();

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);

    }

    public Epic(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getID() + 1;
        InMemoryTaskManager.setID(this.id);
        this.status = status;
    }

    public Epic(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        duration = Duration.ofMinutes(0);
        for (Subtask element : subtasksList) {     // попробовать функц element.duration.ifPresent(value -> duration += value.);
            if (element.duration != null) {         //  element.duration.ifPresent(temporaryDuration::plus);
                duration = duration.plus(element.duration);
            }
        }

        if (subtasksList.size() == 0) {
            System.out.println("У эпика нету подзадач");
            return Optional.empty();
        }

        ComparatorStartTime comparatorStartTime = new ComparatorStartTime();
        subtasksList.sort(comparatorStartTime);

        this.startTime = subtasksList.get(0).startTime;
        if (startTime.isPresent()) {
            this.endTime = startTime.get().plus(duration);
        }
        // тоже самое startTime.ifPresent(localDateTime -> this.endTime = localDateTime.plus(duration));


        return Optional.ofNullable(this.endTime);
    }

    static class ComparatorStartTime implements Comparator<Subtask> {

        @Override
        public int compare(Subtask task1, Subtask task2) {
            if (task1.startTime.isPresent() && task2.startTime.isPresent()) {
                return task1.startTime.get().compareTo(task2.startTime.get());
            } else {
                return 1;
            }

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
        return "Epic{" +
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

