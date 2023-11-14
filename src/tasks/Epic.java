package tasks;

import java.util.Collection;
import java.util.HashSet;

public class Epic extends Task {
    public final TaskType TASK_TYPE = TaskType.EPIC;
    private final Collection<Long> subtaskIds;

    public Epic() {
        super();
        subtaskIds = new HashSet<>();
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
