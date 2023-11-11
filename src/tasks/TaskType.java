package tasks;

import java.util.Arrays;
import java.util.Optional;

public enum TaskType {
    TASK(Task.class),
    EPIC(Epic.class),
    SUBTASK(Subtask.class),
    UNKNOWN(null);

    private final Object taskClass;
    TaskType(Object taskClass) {
        this.taskClass = taskClass;
    }

    public static TaskType getTaskType(Object object) {
        Optional<TaskType> taskType = Arrays.stream(TaskType.values())
                .filter(t -> t.getTaskClass() == object.getClass())
                .findFirst();
        return taskType.orElse(UNKNOWN);
    }

    private Object getTaskClass() {
        return taskClass;
    }
}
