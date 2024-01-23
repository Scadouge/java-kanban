package tasks;

import manager.TaskDataUndefinedException;

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
        duration = 0;
        startTime = LocalDateTime.MAX;
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
    public int getDuration() throws TaskDataUndefinedException {
        if (getSubtaskIds().size() == 0) {
            throw new TaskDataUndefinedException("Продолжительность не определена: список подзадач пуст", this);
        } else {
            return super.getDuration();
        }
    }

    @Override
    public LocalDateTime getEndTime() throws TaskDataUndefinedException {
        if (getSubtaskIds().size() == 0) {
            throw new TaskDataUndefinedException("Время окончания не определено: список подзадач пуст", this);
        } else if (endTime == LocalDateTime.MAX) {
            throw new TaskDataUndefinedException("Время окончания не определено: подзадачи не имеют временных интервалов", this);
        } else {
            return endTime;
        }
    }

    @Override
    public LocalDateTime getStartTime() throws TaskDataUndefinedException {
        if (getSubtaskIds().size() == 0) {
            throw new TaskDataUndefinedException("Время начала не определено: список подзадач пуст", this);
        } else if (startTime == LocalDateTime.MAX) {
            throw new TaskDataUndefinedException("Время начала не определено: подзадачи не имеют временных интервалов", this);
        } else {
            return super.getStartTime();
        }
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
