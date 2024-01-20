package tasks;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

public class Epic extends Task {
    private final Collection<Long> subtaskIds;
    private LocalDateTime endTime;

    public Epic() {
        super();
        type = TaskType.EPIC;
        subtaskIds = new HashSet<>();
        endTime = LocalDateTime.MAX;
    }

    public Collection<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(long id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(long id) {
        subtaskIds.remove(id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtaskIds.size()=" + getSubtaskIds().size() +
                ", status=" + getStatus() +
                '}';
    }
}
