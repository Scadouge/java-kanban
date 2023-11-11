package tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Epic extends Task {
    private HashMap<Long, Subtask> subtasks;

    public Epic(long id) {
        super(id);
        subtasks = new HashMap<>();
    }

    private void updateStatus() {
        Status newStatus = null;
        for(Subtask subtask : subtasks.values()) {
            if(newStatus != null) {
                if (subtask.getStatus() != newStatus) {
                    newStatus = Status.IN_PROGRESS;
                    break;
                }
            } else
                newStatus = subtask.getStatus();
        }
        if(newStatus == null || subtasks.size() == 0)
            newStatus = Status.NEW;
        setStatus(newStatus);
    }

    public void addSubtask(Subtask subtask) {
        subtask.connectToEpic(this);
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
    }

    void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        updateStatus();
    }

    void setSubtasksMap(HashMap<Long, Subtask> newMap) {
        subtasks = newMap;
        updateStatus();
    }

    HashMap<Long, Subtask> getSubtasksMap() {
      return subtasks;
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    Collection<Task> onRemove() {
        return getSubtasks().stream().map(c -> (Task) c).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtasks.size()=" + subtasks.size() +
                ", status=" + getStatus() +
                '}';
    }
}
