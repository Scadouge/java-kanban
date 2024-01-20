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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final static Path TEST_SAVE_FILE = Path.of("src/test/resources/save.CSV");
    private final static String FILE_HEADER = "id,type,name,status,description,epic";

    @BeforeEach
    public void createTaskManager() throws IOException {
        if (Files.exists(TEST_SAVE_FILE)) {
            Files.delete(TEST_SAVE_FILE);
        }
        taskManager = new FileBackedTasksManager(TEST_SAVE_FILE);
    }

    @Test
    @Override
    void shouldReturnPrioritizedTasks() {
        super.shouldReturnPrioritizedTasks();
    }

    @Test
    @Override
    void shouldEpicReturn_StartTime_EndTime_Duration() {
        super.shouldEpicReturn_StartTime_EndTime_Duration();
    }

    @Test
    void shouldSaveTwoTasksAndHistory() {
        final Task task = new Task();
        task.setName("TASK NAME");
        final long task1Id = taskManager.createTask(task);
        final long task2Id = taskManager.createTask(new Task());
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask subtask = new Subtask(epicId);
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
        assertEquals(lines.get(1), task1Id + ",TASK,TASK NAME,NEW,null,");
        assertEquals(lines.get(2), task2Id + ",TASK,null,NEW,null,");
        assertEquals(lines.get(3), subtaskId + ",SUBTASK,null,IN_PROGRESS,null," + epicId);
        assertEquals(lines.get(4), epicId + ",EPIC,null,IN_PROGRESS,null,");
        assertTrue(lines.get(5).isEmpty());
        assertEquals(lines.get(6), String.join(",",
                String.valueOf(task1Id),
                String.valueOf(task2Id),
                String.valueOf(subtaskId),
                String.valueOf(epicId)));
    }

    @Test
    void shouldLoadTwoTasksAndHistory() throws IOException {
        final Task task = new Task();
        task.setName("TASK NAME");
        final long task1Id = taskManager.createTask(task);
        final long task2Id = taskManager.createTask(new Task());
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask subtask = new Subtask(epicId);
        subtask.setStatus(Status.IN_PROGRESS);
        final long subtaskId = taskManager.createSubtask(subtask);
        taskManager.getEpic(epicId); // 2
        taskManager.getTask(task1Id); // 0
        taskManager.getTask(task2Id); // 1
        taskManager.getSubtask(subtaskId); // 3

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);

        assertEquals(List.of(epicId, task1Id, task2Id, subtaskId), manager.getHistory());
        assertEquals(4, manager.getHistory().size());
        assertEquals(2, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getSubtasks().size());
        assertEquals("TASK NAME", manager.getTask(task1Id).getName());
        assertEquals(epicId, manager.getSubtask(subtaskId).getEpicId());
        assertTrue(manager.getEpic(epicId).getSubtaskIds().contains(subtaskId));
    }

    @Test
    void shouldSaveEmptyTasksAndHistory() {
        taskManager.clearTasks();

        List<String> lines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertTrue(lines.get(1).isEmpty());
    }

    @Test
    void shouldLoadEmptyTasksAndHistory() throws IOException {
        taskManager.clearTasks();

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(0, manager.getTasks().size());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldSaveOneEpic() {
        long epicId = taskManager.createEpic(new Epic());

        List<String> lines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertEquals(lines.get(1), epicId + ",EPIC,null,NEW,null,");
        assertTrue(lines.get(2).isEmpty());
    }

    @Test
    void shouldLoadOneEpic() throws IOException {
        taskManager.createEpic(new Epic());

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(0, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldSaveTasksWithoutHistory() {
        final long task1Id = taskManager.createTask(new Task());
        final long task2Id = taskManager.createTask(new Task());

        List<String> lines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(TEST_SAVE_FILE.toFile()))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(4, lines.size());
        assertEquals(lines.get(0), FILE_HEADER);
        assertEquals(lines.get(1), task1Id + ",TASK,null,NEW,null,");
        assertEquals(lines.get(2), task2Id + ",TASK,null,NEW,null,");
        assertTrue(lines.get(3).isEmpty());
    }

    @Test
    void shouldLoadTasksWithoutHistory() throws IOException {
        taskManager.createTask(new Task());
        taskManager.createTask(new Task());

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(TEST_SAVE_FILE);
        assertEquals(0, taskManager.getHistory().size());
        assertEquals(2, manager.getTasks().size());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    @Override
    void shouldReturnCollectionWithOneTaskWhenTaskCreated() {
        super.shouldReturnCollectionWithOneTaskWhenTaskCreated();
    }

    @Test
    @Override
    void shouldReturnEmptyCollectionWhenNoTaskCreated() {
        super.shouldReturnEmptyCollectionWhenNoTaskCreated();
    }

    @Test
    @Override
    void shouldClearAllTasksWhenTaskCreated() {
        super.shouldClearAllTasksWhenTaskCreated();
    }

    @Test
    @Override
    void shouldClearAllTasksWhenNoTaskCreated() {
        super.shouldClearAllTasksWhenNoTaskCreated();
    }

    @Test
    @Override
    void shouldReturnExistingTask() {
        super.shouldReturnExistingTask();
    }

    @Test
    @Override
    void shouldReturnNullWhenTasksEmpty() {
        super.shouldReturnNullWhenTasksEmpty();
    }

    @Test
    @Override
    void shouldCreateNewTask() {
        super.shouldCreateNewTask();
    }

    @Test
    @Override
    void shouldUpdateTask() {
        super.shouldUpdateTask();
    }

    @Test
    @Override
    void shouldNotUpdateTaskWhenWrongIdGiven() {
        super.shouldNotUpdateTaskWhenWrongIdGiven();
    }

    @Test
    @Override
    void shouldRemoveExistingTask() {
        super.shouldRemoveExistingTask();
    }

    @Test
    @Override
    void shouldNotCreateTaskWhenUpdatedTaskNonExistent() {
        super.shouldNotCreateTaskWhenUpdatedTaskNonExistent();
    }

    @Test
    @Override
    void shouldNotRemoveExistingTask() {
        super.shouldNotRemoveExistingTask();
    }

    @Test
    @Override
    void shouldReturnCollectionWithOneSubtaskWhenSubtaskCreated() {
        super.shouldReturnCollectionWithOneSubtaskWhenSubtaskCreated();
    }

    @Test
    @Override
    void shouldReturnEmptyCollectionWhenNoSubtaskCreated() {
        super.shouldReturnEmptyCollectionWhenNoSubtaskCreated();
    }

    @Test
    @Override
    void shouldClearAllSubtasksWhenSubtaskCreated() {
        super.shouldClearAllSubtasksWhenSubtaskCreated();
    }

    @Test
    @Override
    void shouldClearAllSubtasksWhenNoSubtaskCreated() {
        super.shouldClearAllSubtasksWhenNoSubtaskCreated();
    }

    @Test
    @Override
    void shouldReturnExistingSubtask() {
        super.shouldReturnExistingSubtask();
    }

    @Test
    @Override
    void shouldReturnNullWhenSubtasksEmpty() {
        super.shouldReturnNullWhenSubtasksEmpty();
    }

    @Test
    @Override
    void shouldCreateNewSubtask() {
        super.shouldCreateNewSubtask();
    }

    @Test
    @Override
    void shouldUpdateSubtask() {
        super.shouldUpdateSubtask();
    }

    @Test
    @Override
    void shouldNotUpdateSubtaskWhenWrongIdGiven() {
        super.shouldNotUpdateSubtaskWhenWrongIdGiven();
    }

    @Test
    @Override
    void shouldNotCreateSubtaskWhenUpdatedSubtaskNonExistent() {
        super.shouldNotCreateSubtaskWhenUpdatedSubtaskNonExistent();
    }

    @Test
    @Override
    void shouldRemoveExistingSubtask() {
        super.shouldRemoveExistingSubtask();
    }

    @Test
    @Override
    void shouldNotRemoveExistingSubtask() {
        super.shouldNotRemoveExistingSubtask();
    }

    @Test
    @Override
    void shouldSubtaskReturnEpicId() {
        super.shouldSubtaskReturnEpicId();
    }

    @Test
    @Override
    void shouldReturnCollectionWithOneEpicWhenEpicCreated() {
        super.shouldReturnCollectionWithOneEpicWhenEpicCreated();
    }

    @Test
    @Override
    void shouldReturnEmptyCollectionWhenNoEpicCreated() {
        super.shouldReturnEmptyCollectionWhenNoEpicCreated();
    }

    @Test
    @Override
    void shouldClearAllEpicsWhenEpicCreated() {
        super.shouldClearAllEpicsWhenEpicCreated();
    }

    @Test
    @Override
    void shouldClearAllEpicsWhenNoEpicCreated() {
        super.shouldClearAllEpicsWhenNoEpicCreated();
    }

    @Test
    @Override
    void shouldReturnExistingEpic() {
        super.shouldReturnExistingEpic();
    }

    @Test
    @Override
    void shouldReturnNullWhenEpicsEmpty() {
        super.shouldReturnNullWhenEpicsEmpty();
    }

    @Test
    @Override
    void shouldCreateNewEpic() {
        super.shouldCreateNewEpic();
    }

    @Test
    @Override
    void shouldUpdateEpic() {
        super.shouldUpdateEpic();
    }

    @Test
    @Override
    void shouldNotUpdateEpicWhenWrongIdGiven() {
        super.shouldNotUpdateEpicWhenWrongIdGiven();
    }

    @Test
    @Override
    void shouldNotCreateEpicWhenUpdatedEpicNonExistent() {
        super.shouldNotCreateEpicWhenUpdatedEpicNonExistent();
    }

    @Test
    @Override
    void shouldRemoveExistingEpic() {
        super.shouldRemoveExistingEpic();
    }

    @Test
    @Override
    void shouldNotRemoveExistingEpic() {
        super.shouldNotRemoveExistingEpic();
    }

    @Test
    @Override
    void shouldEpicReturnSubtask() {
        super.shouldEpicReturnSubtask();
    }

    @Test
    @Override
    void shouldEpicReturnStatus_NEW_WhenSubtasksEmpty() {
        super.shouldEpicReturnStatus_NEW_WhenSubtasksEmpty();
    }

    @Test
    @Override
    void shouldEpicReturnStatus_NEW_WhenSubtasks_NEW() {
        super.shouldEpicReturnStatus_NEW_WhenSubtasks_NEW();
    }

    @Test
    @Override
    void shouldEpicReturnStatus_DONE_WhenSubtasks_DONE() {
        super.shouldEpicReturnStatus_DONE_WhenSubtasks_DONE();
    }

    @Test
    @Override
    void shouldEpicReturnStatus_IN_PROGRESS_WhenSubtasks_DONE_And_NEW() {
        super.shouldEpicReturnStatus_IN_PROGRESS_WhenSubtasks_DONE_And_NEW();
    }

    @Test
    @Override
    void shouldEpicReturnStatus_IN_PROGRESS_WhenSubtasks_IN_PROGRESS() {
        super.shouldEpicReturnStatus_IN_PROGRESS_WhenSubtasks_IN_PROGRESS();
    }

    @Test
    @Override
    void shouldReturnSubtasksFromEpic() {
        super.shouldReturnSubtasksFromEpic();
    }
}