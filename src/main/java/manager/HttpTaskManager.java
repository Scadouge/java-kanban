package manager;

import api.KVTaskClient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import task.Epic;
import task.Subtask;
import task.Task;

import java.net.URI;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTasksManager {
    private final static String KV_ALL_TASKS_KEY = "all-tasks";
    private final static String JSON_TASKS_KEY = "TASKS";
    private final static String JSON_SUBTASKS_KEY = "SUBTASKS";
    private final static String JSON_EPICS_KEY = "EPICS";
    private final static String JSON_HISTORY_KEY = "HISTORY";
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(URI kvServerUri) {
        super(null);
        client = new KVTaskClient(kvServerUri);
        gson = Managers.getCustomGson();
        load();
    }

    @Override
    protected void save() {
        JsonArray taskArray = new JsonArray();
        tasks.forEach((k, t) -> taskArray.add(gson.toJsonTree(t)));
        JsonArray subtaskArray = new JsonArray();
        subtasks.forEach((k, t) -> subtaskArray.add(gson.toJsonTree(t)));
        JsonArray epicArray = new JsonArray();
        epics.forEach((k, t) -> epicArray.add(gson.toJsonTree(t)));
        JsonElement history = gson.toJsonTree(getHistory());

        JsonObject allTasks = new JsonObject();
        allTasks.add(JSON_TASKS_KEY, taskArray);
        allTasks.add(JSON_SUBTASKS_KEY, subtaskArray);
        allTasks.add(JSON_EPICS_KEY, epicArray);
        allTasks.add(JSON_HISTORY_KEY, history);

        client.put(KV_ALL_TASKS_KEY, allTasks.toString());
    }

    private void load() {
        String response = client.load(KV_ALL_TASKS_KEY);
        if (response != null) {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray taskArray = json.get(JSON_TASKS_KEY).getAsJsonArray();
            JsonArray subtaskArray = json.get(JSON_SUBTASKS_KEY).getAsJsonArray();
            JsonArray epicArray = json.get(JSON_EPICS_KEY).getAsJsonArray();
            JsonArray history = json.get(JSON_HISTORY_KEY).getAsJsonArray();

            taskArray.forEach(jt -> {
                Task task = gson.fromJson(jt, Task.class);
                tasks.put(task.getId(), task);
            });
            subtaskArray.forEach(jt -> {
                Subtask task = gson.fromJson(jt, Subtask.class);
                subtasks.put(task.getId(), task);
            });
            epicArray.forEach(jt -> {
                Epic task = gson.fromJson(jt, Epic.class);
                epics.put(task.getId(), task);
            });
            ArrayList<Long> arrayList = gson.fromJson(history, new TypeToken<ArrayList<Long>>() {}.getType());
            arrayList.forEach(id -> historyManager.add(getTaskUniversal((id))));
        }
    }
}
