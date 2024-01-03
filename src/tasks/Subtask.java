package tasks;

public class Subtask extends Task {
    private final long epicId;

    public Subtask(long epicId) {
        super();
        setType(TaskType.SUBTASK);
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
