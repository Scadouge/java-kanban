package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import task.Epic;
import task.Subtask;
import task.Task;

import java.net.URI;
import java.nio.file.Path;

public class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        URI defaultKVServerUrl = URI.create("http://localhost:8078");
        return new HttpTaskManager(defaultKVServerUrl);
    }

    public static TaskManager getFileBackedTasksManager() {
        Path defaultSavePath = Path.of("src/main/resources/save.CSV");
        return new FileBackedTasksManager(defaultSavePath);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getCustomGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Subtask.class, new TaskAdapter())
                .registerTypeAdapter(Epic.class, new TaskAdapter())
                .create();
    }
}
