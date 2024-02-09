package task;

import exception.TaskDataUndefinedException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

public class Epic extends Task {
    public static final LocalDateTime DEFAULT_END_TIME = LocalDateTime.MAX;
    private final Collection<Long> subtaskIds;
    private LocalDateTime endTime;

    public Epic() {
        super();
        type = TaskType.EPIC;
        subtaskIds = new HashSet<>();
        duration = Task.DEFAULT_DURATION;
        startTime = Task.DEFAULT_START_TIME;
        endTime = DEFAULT_END_TIME;
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
        if (!hasDefaultTime()) {
            return super.getDuration();
        }
        if (getSubtaskIds().isEmpty()) {
            throw new TaskDataUndefinedException("Продолжительность не определена: список подзадач пуст", this);
        }
        return super.getDuration();
    }

    @Override
    public LocalDateTime getEndTime() throws TaskDataUndefinedException {
        if (!hasDefaultTime()) {
            return endTime;
        }
        if (getSubtaskIds().isEmpty()) {
            throw new TaskDataUndefinedException("Время окончания не определено: список подзадач пуст", this);
        }
        if (endTime == DEFAULT_END_TIME) {
            throw new TaskDataUndefinedException("Время окончания не определено: подзадачи не имеют временных интервалов", this);
        }
        return endTime;
    }

    @Override
    public LocalDateTime getStartTime() throws TaskDataUndefinedException {
        if (!hasDefaultTime()) {
            return super.getStartTime();
        }
        if (getSubtaskIds().isEmpty()) {
            throw new TaskDataUndefinedException("Время начала не определено: список подзадач пуст", this);
        }
        if (startTime == Task.DEFAULT_START_TIME) {
            throw new TaskDataUndefinedException("Время начала не определено: подзадачи не имеют временных интервалов", this);
        }
        return super.getStartTime();
    }

    private boolean hasDefaultTime() {
        return duration == Task.DEFAULT_DURATION || startTime == Task.DEFAULT_START_TIME;
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
