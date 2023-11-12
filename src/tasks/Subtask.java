package tasks;

public class Subtask extends Task {
    public final TaskType TASK_TYPE = TaskType.SUBTASK;
    private long epicId;

    public Subtask(long id) {
        super(id);
    }

    public Subtask(long id, Status status) {
        super(id, status);
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + getEpicId() +
                ", status=" + getStatus() +
                '}';
    }
}
