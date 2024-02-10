package task;

public class Subtask extends Task {
    private long epicId;

    private Subtask() {
    }

    public Subtask(long epicId) {
        super();
        type = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
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
