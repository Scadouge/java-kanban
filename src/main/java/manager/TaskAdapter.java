package manager;

import com.google.gson.*;
import exception.TaskDataUndefinedException;
import task.Epic;
import task.Task;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class TaskAdapter implements JsonSerializer<Task> {
    Gson gson = new Gson();
    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement taskJson = gson.toJsonTree(task);

        int duration = Task.DEFAULT_DURATION;
        LocalDateTime startTime = Task.DEFAULT_START_TIME;
        LocalDateTime endTime = Epic.DEFAULT_END_TIME;
        boolean isEpic = type.getTypeName().equals(Epic.class.getName());
        try {
            duration = task.getDuration();
            startTime = task.getStartTime();
            if (isEpic) {
                Epic epic = (Epic) task;
                endTime = epic.getEndTime();
            }
        } catch (TaskDataUndefinedException ignore) {
        }
        if (duration == Task.DEFAULT_DURATION) {
            taskJson.getAsJsonObject().remove("duration");
        }
        if (startTime == Task.DEFAULT_START_TIME) {
            taskJson.getAsJsonObject().remove("startTime");
        }
        if (isEpic) {
            if (endTime == Epic.DEFAULT_END_TIME) {
                taskJson.getAsJsonObject().remove("endTime");
            }
        }
        return taskJson;
    }
}
