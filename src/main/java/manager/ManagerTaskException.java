package manager;

import tasks.Task;

public class ManagerTaskException extends Exception {
    private final Task task;

    public ManagerTaskException(String message, Task task) {
        super(message);
        this.task = task;
    }

    public String getDetailedMessage() {
        return String.format("%s [%s]", this.getMessage(), task);
    }
}

