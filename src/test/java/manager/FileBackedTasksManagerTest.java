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

    @Test
    @Override
    void should_createTask_updateTask_ThrowException_WhenIntervalsAlreadyClaimed() throws ManagerTaskException {
        super.should_createTask_updateTask_ThrowException_WhenIntervalsAlreadyClaimed();
    }

    @Test
    @Override
    void should_createSubtask_updateSubtask_ThrowException_WhenIntervalsAlreadyClaimed() throws ManagerTaskException {
        super.should_createSubtask_updateSubtask_ThrowException_WhenIntervalsAlreadyClaimed();
    }

    @Test
    @Override
    void should_getPrioritizedTasks_ReturnPrioritizedTasks() throws ManagerTaskException {
        super.should_getPrioritizedTasks_ReturnPrioritizedTasks();
    }

    @Test
    @Override
    void should_getStartTime_getEndTime_getDuration_ReturnCorrectData() throws ManagerTaskException {
        super.should_getStartTime_getEndTime_getDuration_ReturnCorrectData();
    }

    @Test
    @Override
    void should_getTasks_ReturnTasks() throws ManagerTaskException {
        super.should_getTasks_ReturnTasks();
    }

    @Test
    @Override
    void should_clearTasks_RemoveAllTasks() throws ManagerTaskException {
        super.should_clearTasks_RemoveAllTasks();
    }

    @Test
    @Override
    void should_getTask_ReturnExistingTask() throws ManagerTaskException {
        super.should_getTask_ReturnExistingTask();
    }

    @Test
    @Override
    void should_createTask_CreateNewTask() throws ManagerTaskException {
        super.should_createTask_CreateNewTask();
    }

    @Test
    @Override
    void should_updateTask_UpdateExistingTask() throws ManagerTaskException {
        super.should_updateTask_UpdateExistingTask();
    }

    @Test
    @Override
    void should_removeTask_RemoveExistingTask() throws ManagerTaskException {
        super.should_removeTask_RemoveExistingTask();
    }

    @Test
    @Override
    void should_getSubtasks_ReturnSubtasks() throws ManagerTaskException {
        super.should_getSubtasks_ReturnSubtasks();
    }

    @Test
    @Override
    void should_clearSubtasks_RemoveAllSubtasks() throws ManagerTaskException {
        super.should_clearSubtasks_RemoveAllSubtasks();
    }

    @Test
    @Override
    void should_getSubtask_ReturnExistingSubtask() throws ManagerTaskException {
        super.should_getSubtask_ReturnExistingSubtask();
    }

    @Test
    @Override
    void should_createSubtask_CreateNewSubtask() throws ManagerTaskException {
        super.should_createSubtask_CreateNewSubtask();
    }

    @Test
    @Override
    void should_updateSubtask_UpdateExistingSubtask() throws ManagerTaskException {
        super.should_updateSubtask_UpdateExistingSubtask();
    }

    @Test
    @Override
    void should_removeSubtask_RemoveExistingSubtask() throws ManagerTaskException {
        super.should_removeSubtask_RemoveExistingSubtask();
    }

    @Test
    @Override
    void should_getEpicId_ReturnEpicId() throws ManagerTaskException {
        super.should_getEpicId_ReturnEpicId();
    }

    @Test
    @Override
    void should_getEpics_ReturnEpics() throws ManagerTaskException {
        super.should_getEpics_ReturnEpics();
    }

    @Test
    @Override
    void should_clearEpics_RemoveAllEpics() throws ManagerTaskException {
        super.should_clearEpics_RemoveAllEpics();
    }

    @Test
    @Override
    void should_getEpic_ReturnExistingEpic() throws ManagerTaskException {
        super.should_getEpic_ReturnExistingEpic();
    }

    @Test
    @Override
    void should_createEpic_CreateNewEpic() throws ManagerTaskException {
        super.should_createEpic_CreateNewEpic();
    }

    @Test
    @Override
    void should_updateEpic_UpdateExistingEpic() throws ManagerTaskException {
        super.should_updateEpic_UpdateExistingEpic();
    }

    @Test
    @Override
    void should_removeEpic_RemoveExistingEpic() throws ManagerTaskException {
        super.should_removeEpic_RemoveExistingEpic();
    }

    @Test
    @Override
    void should_getSubtaskIds_ReturnSubtasksIds() throws ManagerTaskException {
        super.should_getSubtaskIds_ReturnSubtasksIds();
    }

    @Test
    @Override
    void should_getStatus_NEW_WhenSubtasksEmpty() throws ManagerTaskException {
        super.should_getStatus_NEW_WhenSubtasksEmpty();
    }

    @Test
    @Override
    void should_getStatus_NEW_WhenSubtasks_NEW() throws ManagerTaskException {
        super.should_getStatus_NEW_WhenSubtasks_NEW();
    }

    @Test
    @Override
    void should_getStatus_DONE_WhenSubtasks_DONE() throws ManagerTaskException {
        super.should_getStatus_DONE_WhenSubtasks_DONE();
    }

    @Test
    @Override
    void should_getStatus_IN_PROGRESS_WhenSubtasks_DONE_And_NEW() throws ManagerTaskException {
        super.should_getStatus_IN_PROGRESS_WhenSubtasks_DONE_And_NEW();
    }

    @Test
    @Override
    void should_getStatus_IN_PROGRESS_WhenSubtasks_IN_PROGRESS() throws ManagerTaskException {
        super.should_getStatus_IN_PROGRESS_WhenSubtasks_IN_PROGRESS();
    }
}