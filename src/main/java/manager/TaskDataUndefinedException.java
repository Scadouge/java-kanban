package manager;

import tasks.Task;

public class TaskDataUndefinedException extends RuntimeException {
    private final Task task;

    public TaskDataUndefinedException(String message, Task task) {
        super(message);
        this.task = task;
    }

    public String getDetailedMessage() {
        return String.format("%s [%s]", this.getMessage(), task);
    }
}
