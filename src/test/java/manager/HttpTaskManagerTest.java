package manager;

import api.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private final static URI defaultKVServerUrl = URI.create("http://localhost:8078");

    private KVServer kvServer;

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(defaultKVServerUrl);
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void should_saveAndLoad_TasksAndHistory() {
        final LocalDateTime task1StartTime = LocalDateTime.of(2024, 1, 5, 19, 13, 0);
        final Task task1 = new Task().setName("TASK NAME").setStartTime(task1StartTime).setDuration(50);
        final long task1Id = taskManager.createTask(task1);
        final Task task2 = new Task();
        final long task2Id = taskManager.createTask(task2);
        final Epic epic1 = new Epic();
        final long epic1Id = taskManager.createEpic(epic1);
        final LocalDateTime subtask1StartTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        final Subtask subtask1 = (Subtask) new Subtask(epic1Id).setStartTime(subtask1StartTime).setDuration(50).setStatus(Status.IN_PROGRESS);
        final long subtask1Id = taskManager.createSubtask(subtask1);
        taskManager.getTask(task2Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task2Id);
        taskManager.getSubtask(subtask1Id);
        taskManager.getEpic(epic1Id);

        HttpTaskManager newManager = new HttpTaskManager(defaultKVServerUrl);

        assertEquals(List.of(task1Id, task2Id, subtask1Id, epic1Id), newManager.getHistory());

        assertEquals(2, newManager.getTasks().size());
        assertTrue(newManager.getTasks().contains(task1));
        assertTrue(newManager.getTasks().contains(task2));

        assertEquals(task1StartTime, newManager.getTask(task1Id).getStartTime());
        assertEquals(50, newManager.getTask(task1Id).getDuration());
        assertEquals(Task.DEFAULT_START_TIME, newManager.getTask(task2Id).getStartTime());
        assertEquals(Task.DEFAULT_DURATION, newManager.getTask(task2Id).getDuration());

        assertEquals(1, newManager.getEpicSubtasks().size());
        assertTrue(newManager.getEpicSubtasks().contains(subtask1));
        assertEquals(subtask1StartTime, newManager.getSubtask(subtask1Id).getStartTime());
        assertEquals(50, newManager.getTask(task1Id).getDuration());

        assertEquals(1, newManager.getEpics().size());
        assertTrue(newManager.getEpics().contains(epic1));

        assertEquals(subtask1StartTime, newManager.getEpic(epic1Id).getStartTime());
        assertEquals(subtask1StartTime.plusMinutes(50), newManager.getEpic(epic1Id).getEndTime());
    }

    @Test
    void should_load_EmptyTasksAndHistory() {
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getEpicSubtasks().size());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getHistory().size());
    }
}