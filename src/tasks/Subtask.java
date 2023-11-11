package tasks;

import java.util.ArrayList;
import java.util.Collection;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(long id) {
        super(id);
    }

    public Subtask(long id, Status status) {
        super(id, status);
    }

    public void connectToEpic(Epic epic) {
        this.epic = epic;
    }

    private Epic getEpic() {
        return epic;
    }

    long getEpicId() {
        return epic.getId();
    }

    @Override
    Collection<Task> onRemove() {
        getEpic().removeSubtask(this);
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", epic.getId()=" + epic.getId() +
                ", status=" + getStatus() +
                '}';
    }
}
