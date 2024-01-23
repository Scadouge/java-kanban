package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final static Path TEST_SAVE_FILE = Path.of("src/test/resources/save.CSV");
    private final static String FILE_HEADER = "id,type,name,status,description,epic,time,duration";

    @BeforeEach
    public void createTaskManager() throws IOException {
        if (Files.exists(TEST_SAVE_FILE)) {
            Files.delete(TEST_SAVE_FILE);
        }
        taskManager = new FileBackedTasksManager(TEST_SAVE_FILE);
    }

    @Test
    void should_saveAndLoad_TwoTasksAndHistory() throws IOException, ManagerTaskException {
        final LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        final Task task = new Task();
        task.setName("TASK NAME");
        final long task1Id = taskManager.createTask(task);
        final long task2Id = taskManager.createTask(new Task());
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask subtask = (Subtask) new Subtask(epicId).setStartTime(startTime).setDuration(50);
        subtask.setStatus(Status.IN_PROGRESS);
        final long subtaskId = taskManager.createSubtask(subtask);
        taskManager.getTask(task2Id);
        taskManager.getTask(task1Id);
        taskManager.getTask(task2Id);
        taskManager.getSubtask(subtaskId);
        taskManager.getEpic(epicId);

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(7, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertEquals(lines.get(1), task1Id + ",TASK,TASK NAME,NEW,null,," + LocalDateTime.MAX + ",0");
        assertEquals(lines.get(2), task2Id + ",TASK,null,NEW,null,," + LocalDateTime.MAX + ",0");
        assertEquals(lines.get(3), subtaskId + ",SUBTASK,null,IN_PROGRESS,null," + epicId + "," + startTime + ",50");
        assertEquals(lines.get(4), epicId + ",EPIC,null,IN_PROGRESS,null,," + startTime + ",50");
        assertTrue(lines.get(5).isEmpty());
        assertEquals(lines.get(6), String.join(",",
                String.valueOf(task1Id),
                String.valueOf(task2Id),
                String.valueOf(subtaskId),
                String.valueOf(epicId)));

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);

        assertEquals(List.of(task1Id, task2Id, subtaskId, epicId), manager.getHistory());
        assertEquals(4, manager.getHistory().size());
        assertEquals(2, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getSubtasks().size());
        assertEquals("TASK NAME", manager.getTask(task1Id).getName());
        assertEquals(epicId, manager.getSubtask(subtaskId).getEpicId());
        assertTrue(manager.getEpic(epicId).getSubtaskIds().contains(subtaskId));
        assertEquals(50, manager.getSubtasks().iterator().next().getDuration());
        assertEquals(startTime, manager.getSubtasks().iterator().next().getStartTime());
    }

    @Test
    void should_saveAndLoad_EmptyTasksAndHistory() throws IOException {
        taskManager.clearTasks();

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertTrue(lines.get(1).isEmpty());

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);

        assertEquals(0, taskManager.getHistory().size());
        assertEquals(0, manager.getTasks().size());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void should_saveAndLoad_OneEpic() throws IOException, ManagerTaskException {
        long epicId = taskManager.createEpic(new Epic());

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertEquals(lines.get(1), epicId + ",EPIC,null,NEW,null,," + LocalDateTime.MAX + ",0");
        assertTrue(lines.get(2).isEmpty());

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);

        assertEquals(0, taskManager.getHistory().size());
        assertEquals(0, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void should_saveAndLoad_TasksWithoutHistory() throws IOException, ManagerTaskException {
        final long task1Id = taskManager.createTask(new Task());
        final long task2Id = taskManager.createTask(new Task());

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(4, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertEquals(lines.get(1), task1Id + ",TASK,null,NEW,null,," + LocalDateTime.MAX + ",0");
        assertEquals(lines.get(2), task2Id + ",TASK,null,NEW,null,," + LocalDateTime.MAX + ",0");
        assertTrue(lines.get(3).isEmpty());

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);

        assertEquals(0, taskManager.getHistory().size());
        assertEquals(2, manager.getTasks().size());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }
}