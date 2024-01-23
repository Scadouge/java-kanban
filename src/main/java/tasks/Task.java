package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private long id;
    protected TaskType type;
    private String name;
    private String description;
    private Status status;

    protected int duration;
    protected LocalDateTime startTime;

    public Task() {
        type = TaskType.TASK;
        status = Status.NEW;
        duration = 0;
        startTime = LocalDateTime.MAX;
    }

    public long getId() {
        return id;
    }

    public Task setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Task setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Task setDescription(String description) {
        this.description = description;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Task setStatus(Status status) {
        this.status = status;
        return this;
    }

    public TaskType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public Task setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Task setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
