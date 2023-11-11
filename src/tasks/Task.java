package tasks;

import java.util.ArrayList;
import java.util.Collection;

public class Task {
    private final long id;
    private String name;
    private String description;
    private Status status;

    public Task(long id) {
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
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

    void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    Collection<Task> onRemove() {
        return new ArrayList<>();
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
}
