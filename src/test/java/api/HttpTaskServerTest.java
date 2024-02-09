package api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import exception.TaskDataUndefinedException;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static final String TASK_SERVER_URI = "http://localhost:8080";
    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Gson gson;
    private Task serverTask;
    private Subtask serverSubtask;
    private Epic serverEpic;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = Managers.getCustomGson();
        // create Task
        final URI createTaskUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_TASK.getPath());
        final HttpResponse<String> createTaskResponse = sendPost(createTaskUri, gson.toJson(new Task().setName("New task 1").setDescription("task 1 desc")));
        final long taskId = Long.parseLong(createTaskResponse.body());
        // get serverTask from server
        final URI getTaskUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASK.getPath(Map.of("id", String.valueOf(taskId))));
        final HttpResponse<String> getTaskResponse = sendGet(getTaskUri);
        serverTask = gson.fromJson(getTaskResponse.body(), Task.class);
        // create Epic
        final URI createEpicUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_EPIC.getPath());
        final HttpResponse<String> createEpicResponse = sendPost(createEpicUri, gson.toJson(new Epic().setName("New epic 1").setDescription("epic 1 desc")));
        final long epicId = Long.parseLong(createEpicResponse.body());
        // create Subtask
        final URI createSubtaskUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_SUBTASK.getPath());
        final HttpResponse<String> createSubtaskResponse = sendPost(createSubtaskUri, gson.toJson(
                new Subtask(epicId).setName("New subtask 1").setDescription("subtask 1 desc")
                        .setStartTime(LocalDateTime.of(2020, 1, 5, 8, 4))
                        .setDuration(30)));
        final long subtaskId = Long.parseLong(createSubtaskResponse.body());
        // get serverSubtask from server
        final URI getSubtaskUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASK.getPath(Map.of("id", String.valueOf(subtaskId))));
        final HttpResponse<String> getSubtaskResponse = sendGet(getSubtaskUri);
        serverSubtask = gson.fromJson(getSubtaskResponse.body(), Subtask.class);
        // get serverEpic from server
        final URI getEpicUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC.getPath(Map.of("id", String.valueOf(epicId))));
        final HttpResponse<String> getEpicResponse = sendGet(getEpicUri);
        serverEpic = gson.fromJson(getEpicResponse.body(), Epic.class);
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void should_getTasks() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASKS.getPath());
        final HttpResponse<String> response = sendGet(uri);

        final JsonArray element = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(1, element.size());

        final Task actualTask = gson.fromJson(element.get(0), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(serverTask.getName(), actualTask.getName());
        assertEquals(serverTask.getDescription(), actualTask.getDescription());
        assertEquals(serverTask.getStartTime(), actualTask.getStartTime());
        assertEquals(serverTask.getDuration(), actualTask.getDuration());
    }

    @Test
    void should_getTask() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASK.getPath(Map.of("id", String.valueOf(serverTask.getId()))));
        final HttpResponse<String> response = sendGet(uri);

        assertEquals(200, response.statusCode());

        final JsonObject element = JsonParser.parseString(response.body()).getAsJsonObject();
        final Task actualTask = gson.fromJson(element, Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(serverTask.getName(), actualTask.getName());
        assertEquals(serverTask.getDescription(), actualTask.getDescription());
        assertEquals(serverTask.getStartTime(), actualTask.getStartTime());
        assertEquals(serverTask.getDuration(), actualTask.getDuration());
    }

    @Test
    void should_createTask() throws IOException, InterruptedException {
        final URI postUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_TASK.getPath());
        final Task newTask = new Task().setName("New task 2");
        final HttpResponse<String> postResponse = sendPost(postUri, gson.toJson(newTask));
        final long createTaskId = Long.parseLong(postResponse.body());

        assertEquals(201, postResponse.statusCode());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASK.getPath(Map.of("id", String.valueOf(createTaskId))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        final JsonObject element = JsonParser.parseString(getResponse.body()).getAsJsonObject();
        final Task responseTask = gson.fromJson(element, Task.class);

        assertEquals(200, getResponse.statusCode());
        assertEquals(newTask.getName(), responseTask.getName());
        assertEquals(newTask.getDescription(), responseTask.getDescription());
        assertEquals(newTask.getStartTime(), responseTask.getStartTime());
        assertEquals(newTask.getDuration(), responseTask.getDuration());
    }

    @Test
    void should_updateTask() throws IOException, InterruptedException {
        final URI postUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_TASK.getPath());
        final Task updatedTask = new Task().setId(serverTask.getId()).setName("Updated task 1");
        final HttpResponse<String> postResponse = sendPost(postUri, gson.toJson(updatedTask));

        assertEquals(204, postResponse.statusCode());
        assertTrue(postResponse.body().isEmpty());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASK.getPath(Map.of("id", String.valueOf(serverTask.getId()))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        final JsonObject element = JsonParser.parseString(getResponse.body()).getAsJsonObject();
        final Task responseTask = gson.fromJson(element, Task.class);

        assertEquals(200, getResponse.statusCode());
        assertEquals(updatedTask.getName(), responseTask.getName());
        assertEquals(updatedTask.getDescription(), responseTask.getDescription());
        assertEquals(updatedTask.getStartTime(), responseTask.getStartTime());
        assertEquals(updatedTask.getDuration(), responseTask.getDuration());
    }

    @Test
    void should_deleteTask() throws IOException, InterruptedException {
        final URI deleteUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.DELETE_TASK.getPath(Map.of("id", String.valueOf(serverTask.getId()))));
        final HttpResponse<String> response = sendDelete(deleteUri);

        assertEquals(204, response.statusCode());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASK.getPath(Map.of("id", String.valueOf(serverTask.getId()))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void should_getSubtasks() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASKS.getPath());
        final HttpResponse<String> response = sendGet(uri);

        final JsonArray element = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(1, element.size());

        final Subtask actualTask = gson.fromJson(element.get(0), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(serverSubtask.getName(), actualTask.getName());
        assertEquals(serverSubtask.getDescription(), actualTask.getDescription());
        assertEquals(serverSubtask.getStartTime(), actualTask.getStartTime());
        assertEquals(serverSubtask.getDuration(), actualTask.getDuration());
    }

    @Test
    void should_getSubtask() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASK.getPath(Map.of("id", String.valueOf(serverSubtask.getId()))));
        final HttpResponse<String> response = sendGet(uri);

        assertEquals(200, response.statusCode());

        final JsonObject element = JsonParser.parseString(response.body()).getAsJsonObject();
        final Subtask actualTask = gson.fromJson(element, Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(serverSubtask.getName(), actualTask.getName());
        assertEquals(serverSubtask.getDescription(), actualTask.getDescription());
        assertEquals(serverSubtask.getStartTime(), actualTask.getStartTime());
        assertEquals(serverSubtask.getDuration(), actualTask.getDuration());
    }

    @Test
    void should_createSubtask() throws IOException, InterruptedException {
        final URI postUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_SUBTASK.getPath());
        final Subtask newTask = (Subtask) new Subtask(serverEpic.getId()).setName("New subtask 2");
        final HttpResponse<String> postResponse = sendPost(postUri, gson.toJson(newTask));
        final long createTaskId = Long.parseLong(postResponse.body());

        assertEquals(201, postResponse.statusCode());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASK.getPath(Map.of("id", String.valueOf(createTaskId))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        final JsonObject element = JsonParser.parseString(getResponse.body()).getAsJsonObject();
        final Subtask responseTask = gson.fromJson(element, Subtask.class);

        assertEquals(200, getResponse.statusCode());
        assertEquals(newTask.getName(), responseTask.getName());
        assertEquals(newTask.getDescription(), responseTask.getDescription());
        assertEquals(newTask.getStartTime(), responseTask.getStartTime());
        assertEquals(newTask.getDuration(), responseTask.getDuration());
    }

    @Test
    void should_updateSubtask() throws IOException, InterruptedException {
        final URI postUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_SUBTASK.getPath());
        final Subtask updatedTask = (Subtask) new Subtask(serverEpic.getId()).setId(serverSubtask.getId()).setName("Updated subtask 1");
        final HttpResponse<String> postResponse = sendPost(postUri, gson.toJson(updatedTask));

        assertEquals(204, postResponse.statusCode());
        assertTrue(postResponse.body().isEmpty());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASK.getPath(Map.of("id", String.valueOf(serverSubtask.getId()))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        final JsonObject element = JsonParser.parseString(getResponse.body()).getAsJsonObject();
        final Subtask responseTask = gson.fromJson(element, Subtask.class);

        assertEquals(200, getResponse.statusCode());
        assertEquals(updatedTask.getName(), responseTask.getName());
        assertEquals(updatedTask.getDescription(), responseTask.getDescription());
        assertEquals(updatedTask.getStartTime(), responseTask.getStartTime());
        assertEquals(updatedTask.getDuration(), responseTask.getDuration());
    }

    @Test
    void should_deleteSubtask() throws IOException, InterruptedException {
        final URI deleteUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.DELETE_SUBTASK.getPath(Map.of("id", String.valueOf(serverSubtask.getId()))));
        final HttpResponse<String> response = sendDelete(deleteUri);

        assertEquals(204, response.statusCode());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASK.getPath(Map.of("id", String.valueOf(serverSubtask.getId()))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void should_getEpics() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPICS.getPath());
        final HttpResponse<String> response = sendGet(uri);

        final JsonArray element = JsonParser.parseString(response.body()).getAsJsonArray();
        final Epic actualTask = gson.fromJson(element.get(0), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(serverEpic.getName(), actualTask.getName());
        assertEquals(serverEpic.getDescription(), actualTask.getDescription());
        assertEquals(serverEpic.getStartTime(), actualTask.getStartTime());
        assertEquals(serverEpic.getDuration(), actualTask.getDuration());
    }

    @Test
    void should_getEpic() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC.getPath(Map.of("id", String.valueOf(serverEpic.getId()))));
        final HttpResponse<String> response = sendGet(uri);

        assertEquals(200, response.statusCode());

        final JsonObject element = JsonParser.parseString(response.body()).getAsJsonObject();
        final Epic actualTask = gson.fromJson(element, Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(serverEpic.getName(), actualTask.getName());
        assertEquals(serverEpic.getDescription(), actualTask.getDescription());
        assertEquals(serverEpic.getStartTime(), actualTask.getStartTime());
        assertEquals(serverEpic.getDuration(), actualTask.getDuration());
    }

    @Test
    void should_createEpic() throws IOException, InterruptedException {
        final URI postUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_EPIC.getPath());
        final Epic newTask = (Epic) new Epic().setName("New epic 2");
        final HttpResponse<String> postResponse = sendPost(postUri, gson.toJson(newTask));
        final long createTaskId = Long.parseLong(postResponse.body());

        assertEquals(201, postResponse.statusCode());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC.getPath(Map.of("id", String.valueOf(createTaskId))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        final JsonObject element = JsonParser.parseString(getResponse.body()).getAsJsonObject();
        final Epic responseTask = gson.fromJson(element, Epic.class);

        assertEquals(200, getResponse.statusCode());
        assertEquals(newTask.getName(), responseTask.getName());
        assertEquals(newTask.getDescription(), responseTask.getDescription());

        assertThrows(TaskDataUndefinedException.class, responseTask::getStartTime);
        assertThrows(TaskDataUndefinedException.class, responseTask::getDuration);
    }

    @Test
    void should_updateEpic() throws IOException, InterruptedException {
        final URI postUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_EPIC.getPath());
        final Epic updatedTask = (Epic) new Epic().setId(serverEpic.getId()).setName("Updated epic 1");
        final HttpResponse<String> postResponse = sendPost(postUri, gson.toJson(updatedTask));

        assertEquals(204, postResponse.statusCode());
        assertTrue(postResponse.body().isEmpty());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC.getPath(Map.of("id", String.valueOf(serverEpic.getId()))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        final JsonObject element = JsonParser.parseString(getResponse.body()).getAsJsonObject();
        final Epic responseTask = gson.fromJson(element, Epic.class);

        assertEquals(200, getResponse.statusCode());
        assertEquals(updatedTask.getName(), responseTask.getName());
        assertEquals(updatedTask.getDescription(), responseTask.getDescription());

        assertThrows(TaskDataUndefinedException.class, responseTask::getStartTime);
        assertThrows(TaskDataUndefinedException.class, responseTask::getDuration);
    }

    @Test
    void should_deleteEpic() throws IOException, InterruptedException {
        final URI deleteUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.DELETE_EPIC.getPath(Map.of("id", String.valueOf(serverEpic.getId()))));
        final HttpResponse<String> response = sendDelete(deleteUri);

        assertEquals(204, response.statusCode());

        final URI getUri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC.getPath(Map.of("id", String.valueOf(serverEpic.getId()))));
        final HttpResponse<String> getResponse = sendGet(getUri);

        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void should_getEpicSubtasks() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC_SUBTASKS.getPath(Map.of("id", String.valueOf(serverEpic.getId()))));
        final HttpResponse<String> response = sendGet(uri);

        assertEquals(200, response.statusCode());

        final JsonArray element = JsonParser.parseString(response.body()).getAsJsonArray();
        List<Subtask> subtasks = gson.fromJson(element, new TypeToken<ArrayList<Subtask>>(){}.getType());

        assertEquals(1, subtasks.size());
        assertEquals(serverSubtask, subtasks.get(0));
    }

    @Test
    void should_getHistory() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_HISTORY.getPath());
        final HttpResponse<String> response = sendGet(uri);

        assertEquals(200, response.statusCode());

        final JsonArray element = JsonParser.parseString(response.body()).getAsJsonArray();
        List<Long> history = gson.fromJson(element, new TypeToken<ArrayList<Long>>(){}.getType());

        assertEquals(3, history.size());
        assertEquals(List.of(serverTask.getId(), serverSubtask.getId(), serverEpic.getId()), history);
    }

    @Test
    void should_getPrioritizedTasks() throws IOException, InterruptedException {
        final URI uri = URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_PRIORITIZED_TASKS.getPath());
        final HttpResponse<String> response = sendGet(uri);

        assertEquals(200, response.statusCode());

        final JsonArray elements = JsonParser.parseString(response.body()).getAsJsonArray();

        final List<Task> prioritizedTasks = new ArrayList<>();
        for(JsonElement element : elements) {
            JsonObject item = element.getAsJsonObject();
            TaskType type = TaskType.valueOf(item.get("type").getAsString());
            switch (type) {
                case TASK:
                    prioritizedTasks.add(gson.fromJson(element, Task.class));
                    break;
                case SUBTASK:
                    prioritizedTasks.add(gson.fromJson(element, Subtask.class));
                    break;
            }
        }

        assertEquals(2, prioritizedTasks.size());
        assertEquals(serverTask, prioritizedTasks.get(1));
        assertEquals(serverSubtask, prioritizedTasks.get(0));
    }

    @Test
    void should_getCorrectStatusCode() throws IOException, InterruptedException {
        assertEquals(404, sendGet(URI.create(TASK_SERVER_URI + "/wrong/path/")).statusCode());
        assertEquals(404, sendGet(
                URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_TASK
                        .getPath(Map.of("wrong-id", String.valueOf(serverTask.getId()))))).statusCode());
        assertEquals(404, sendGet(
                URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_EPIC
                        .getPath(Map.of("wrong-id", String.valueOf(serverTask.getId()))))).statusCode());
        assertEquals(404, sendGet(
                URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.GET_SUBTASK
                        .getPath(Map.of("wrong-id", String.valueOf(serverTask.getId()))))).statusCode());
        assertEquals(400, sendPost(
                URI.create(TASK_SERVER_URI + HttpTaskServer.Endpoint.POST_TASK.getPath()), "").statusCode());

    }

    private HttpResponse<String> sendPost(URI uri, String body) throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        final HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(bodyPublisher).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendGet(URI uri) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder().uri(uri).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDelete(URI uri) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder().uri(uri).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
    }
}