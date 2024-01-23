package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager taskManager;

    @Test
    void should_createTask_updateTask_ThrowException_WhenIntervalsAlreadyClaimed() throws ManagerTaskException {
        final LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        final long taskId = taskManager.createTask(new Task().setStartTime(startTime).setDuration(50));

        assertDoesNotThrow(
                () -> taskManager.updateTask(new Task().setId(taskId).setStartTime(startTime).setDuration(45)));
        assertDoesNotThrow(
                () -> taskManager.createTask(new Task().setStartTime(startTime.minusMinutes(30)).setDuration(5)));

        assertThrows(ManagerTaskException.class,
                () -> taskManager.createTask(new Task().setStartTime(startTime).setDuration(55)));

        taskManager.removeTask(taskId);

        assertDoesNotThrow(
                () -> taskManager.createTask(new Task().setId(taskId).setStartTime(startTime).setDuration(55)));

        assertDoesNotThrow(
                () -> taskManager.createTask(new Task()));
        assertDoesNotThrow(
                () -> taskManager.createTask(new Task()));
    }

    @Test
    void should_createSubtask_updateSubtask_ThrowException_WhenIntervalsAlreadyClaimed() throws ManagerTaskException {
        final LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        final long epicId = taskManager.createEpic(new Epic());
        final long subtaskId = taskManager.createSubtask((Subtask) new Subtask(epicId).setStartTime(startTime).setDuration(50));

        assertDoesNotThrow(
                () -> taskManager.updateSubtask((Subtask) new Subtask(epicId).setId(subtaskId).setStartTime(startTime).setDuration(45)));
        assertDoesNotThrow(
                () -> taskManager.createSubtask((Subtask) new Subtask(epicId).setStartTime(startTime.minusMinutes(30)).setDuration(5)));

        assertThrows(ManagerTaskException.class,
                () -> taskManager.createSubtask((Subtask) new Subtask(epicId).setStartTime(startTime).setDuration(55)));

        taskManager.removeSubtask(subtaskId);

        assertDoesNotThrow(
                () -> taskManager.createSubtask((Subtask) new Subtask(epicId).setId(subtaskId).setStartTime(startTime).setDuration(55)));

        assertDoesNotThrow(
                () -> taskManager.createTask(new Subtask(epicId)));
        assertDoesNotThrow(
                () -> taskManager.createTask(new Subtask(epicId)));
    }

    @Test
    void should_getPrioritizedTasks_ReturnPrioritizedTasks() throws ManagerTaskException {
        final LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        final long epicId = taskManager.createEpic(new Epic());
        taskManager.createTask(new Task().setName("t1").setStartTime(startTime));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t2"));
        taskManager.createTask(new Task().setName("t3").setStartTime(startTime.plusHours(19)));
        taskManager.createTask(new Task().setName("t4").setStartTime(startTime.plusHours(8)));
        taskManager.createTask(new Task().setName("t5").setStartTime(startTime.plusHours(55)));

        final List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(5, prioritizedTasks.size());
        assertEquals(1, prioritizedTasks.get(0).getId());
        assertEquals(4, prioritizedTasks.get(1).getId());
        assertEquals(3, prioritizedTasks.get(2).getId());
        assertEquals(5, prioritizedTasks.get(3).getId());
        assertEquals(2, prioritizedTasks.get(4).getId());
    }

    @Test
    void should_getStartTime_getEndTime_getDuration_ReturnCorrectData() throws ManagerTaskException {
        final LocalDateTime startTime1 = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        Epic epic1 = (Epic) new Epic().setName("t0");
        final long epicId = taskManager.createEpic(epic1);

        final TaskDataUndefinedException eDurationEmpty = assertThrows(TaskDataUndefinedException.class,
                epic1::getDuration);
        assertEquals("Продолжительность не определена: список подзадач пуст", eDurationEmpty.getMessage());
        final TaskDataUndefinedException eStartTimeEmpty = assertThrows(TaskDataUndefinedException.class,
                epic1::getStartTime);
        assertEquals("Время начала не определено: список подзадач пуст", eStartTimeEmpty.getMessage());
        final TaskDataUndefinedException eEndTimeEmpty = assertThrows(TaskDataUndefinedException.class,
                epic1::getEndTime);
        assertEquals("Время окончания не определено: список подзадач пуст", eEndTimeEmpty.getMessage());

        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t2"));

        assertDoesNotThrow(epic1::getDuration);
        assertEquals(0, epic1.getDuration());
        final TaskDataUndefinedException eStartTimeOneEmptySubtask = assertThrows(TaskDataUndefinedException.class,
                epic1::getStartTime);
        assertEquals("Время начала не определено: подзадачи не имеют временных интервалов", eStartTimeOneEmptySubtask.getMessage());
        final TaskDataUndefinedException eEndTimeOneEmptySubtask = assertThrows(TaskDataUndefinedException.class,
                epic1::getEndTime);
        assertEquals("Время окончания не определено: подзадачи не имеют временных интервалов", eEndTimeOneEmptySubtask.getMessage());

        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t1").setStartTime(startTime1).setDuration(50));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t3").setStartTime(startTime1.plusHours(19)).setDuration(10));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t4").setStartTime(startTime1.plusHours(8)).setDuration(30));
        long subtask5Id = taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t5").setStartTime(startTime1.plusHours(48)).setDuration(30));

        final Epic epic2 = taskManager.getEpic(epicId);
        final LocalDateTime endTime1 = startTime1.plusHours(48).plusMinutes(30); // task t5

        assertEquals(startTime1, epic2.getStartTime());
        assertEquals(endTime1, epic2.getEndTime());
        assertEquals(120, epic2.getDuration());

        taskManager.removeSubtask(subtask5Id);

        final LocalDateTime endTime2 = startTime1.plusHours(19).plusMinutes(10); // task t3
        assertEquals(startTime1, epic2.getStartTime());
        assertEquals(endTime2, epic2.getEndTime());
        assertEquals(90, epic2.getDuration());

        final LocalDateTime startTime2 = startTime1.plusHours(100);
        Task task6 = new Task().setName("t6").setStartTime(startTime2);
        taskManager.createTask(task6);
        assertEquals(startTime2, task6.getStartTime());
        assertEquals(startTime2, task6.getEndTime());
        assertEquals(0, task6.getDuration());
    }

    // ================================ TASK ================================

    @Test
    void should_getTasks_ReturnTasks() throws ManagerTaskException {
        assertEquals(0, taskManager.getTasks().size());

        final Task task = new Task();
        taskManager.createTask(task);

        assertEquals(1, taskManager.getTasks().size());
        assertTrue(taskManager.getTasks().contains(task));
    }

    @Test
    void should_clearTasks_RemoveAllTasks() throws ManagerTaskException {
        taskManager.clearTasks();

        assertTrue(taskManager.getTasks().isEmpty());

        taskManager.createTask(new Task());
        assertEquals(1, taskManager.getTasks().size());

        taskManager.clearTasks();

        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void should_getTask_ReturnExistingTask() throws ManagerTaskException {
        assertNull(taskManager.getTask(-1));

        final Task task = new Task();
        final long id = taskManager.createTask(task);

        assertEquals(task, taskManager.getTask(id));
    }

    @Test
    void should_createTask_CreateNewTask() throws ManagerTaskException {
        assertEquals(0, taskManager.getTasks().size());

        taskManager.createTask(new Task());

        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void should_updateTask_UpdateExistingTask() throws ManagerTaskException {
        final long id = taskManager.createTask(new Task().setName("task_name"));

        assertEquals("task_name", taskManager.getTask(id).getName());

        final Task newTaskWrongId = new Task().setId(-1).setName("new_task_name");
        assertThrows(ManagerTaskException.class,
                () -> taskManager.updateTask(newTaskWrongId));
        assertEquals("task_name", taskManager.getTask(id).getName());

        taskManager.updateTask(new Task().setId(id).setName("new_task_name"));

        assertEquals("new_task_name", taskManager.getTask(id).getName());
    }

    @Test
    void should_removeTask_RemoveExistingTask() throws ManagerTaskException {
        assertEquals(0, taskManager.getTasks().size());
        assertDoesNotThrow(() -> taskManager.removeTask(-1));

        final long id = taskManager.createTask(new Task());

        assertEquals(1, taskManager.getTasks().size());

        taskManager.removeTask(id);

        assertEquals(0, taskManager.getTasks().size());
    }

    // ================================ SUBTASK ================================

    @Test
    void should_getSubtasks_ReturnSubtasks() throws ManagerTaskException {
        assertEquals(0, taskManager.getSubtasks().size());

        final Subtask subtask = new Subtask(taskManager.createEpic(new Epic()));
        taskManager.createSubtask(subtask);

        assertEquals(1, taskManager.getSubtasks().size());
        assertTrue(taskManager.getSubtasks().contains(subtask));
    }

    @Test
    void should_clearSubtasks_RemoveAllSubtasks() throws ManagerTaskException {
        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty());

        taskManager.createSubtask(new Subtask(taskManager.createEpic(new Epic())));
        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void should_getSubtask_ReturnExistingSubtask() throws ManagerTaskException {
        assertNull(taskManager.getSubtask(-1));

        final Subtask subtask = new Subtask(taskManager.createEpic(new Epic()));
        final long id = taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(id));
    }

    @Test
    void should_createSubtask_CreateNewSubtask() throws ManagerTaskException {
        assertEquals(0, taskManager.getTasks().size());

        taskManager.createSubtask(new Subtask(taskManager.createEpic(new Epic())));

        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    void should_updateSubtask_UpdateExistingSubtask() throws ManagerTaskException {
        final long epicId = taskManager.createEpic(new Epic());
        final long id = taskManager.createSubtask(
                (Subtask) new Subtask(epicId).setName("task_name"));

        assertEquals("task_name", taskManager.getSubtask(id).getName());

        final Subtask newSubtaskWrongId = (Subtask) new Subtask(epicId)
                .setId(-1).setName("new_task_name");
        assertThrows(ManagerTaskException.class,
                () -> taskManager.updateTask(newSubtaskWrongId));
        assertEquals("task_name", taskManager.getSubtask(id).getName());

        taskManager.updateSubtask((Subtask) new Subtask(epicId).setId(id).setName("new_task_name"));

        assertEquals("new_task_name", taskManager.getSubtask(id).getName());
    }

    @Test
    void should_removeSubtask_RemoveExistingSubtask() throws ManagerTaskException {
        assertEquals(0, taskManager.getSubtasks().size());
        assertDoesNotThrow(() -> taskManager.removeSubtask(-1));

        long id = taskManager.createSubtask(new Subtask(taskManager.createEpic(new Epic())));

        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.removeSubtask(id);

        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void should_getEpicId_ReturnEpicId() throws ManagerTaskException {
        final long epicId = taskManager.createEpic(new Epic());
        final long id = taskManager.createSubtask(new Subtask(epicId));
        final Subtask createdSubtask = taskManager.getSubtask(id);

        assertEquals(epicId, createdSubtask.getEpicId());
    }

    // ================================ EPIC ================================

    @Test
    void should_getEpics_ReturnEpics() throws ManagerTaskException {
        assertEquals(0, taskManager.getEpics().size());

        final Epic epic = new Epic();
        taskManager.createEpic(epic);

        assertEquals(1, taskManager.getEpics().size());
        assertTrue(taskManager.getEpics().contains(epic));
    }

    @Test
    void should_clearEpics_RemoveAllEpics() throws ManagerTaskException {
        taskManager.clearEpics();

        assertTrue(taskManager.getEpics().isEmpty());

        taskManager.createEpic(new Epic());
        assertEquals(1, taskManager.getEpics().size());

        taskManager.clearEpics();

        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void should_getEpic_ReturnExistingEpic() throws ManagerTaskException {
        assertNull(taskManager.getEpic(-1));

        final Epic epic = new Epic();
        final long id = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpic(id));
    }

    @Test
    void should_createEpic_CreateNewEpic() throws ManagerTaskException {
        assertEquals(0, taskManager.getEpics().size());

        taskManager.createEpic(new Epic());

        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void should_updateEpic_UpdateExistingEpic() throws ManagerTaskException {
        final long id = taskManager.createEpic((Epic) new Epic().setName("task_name"));

        assertEquals("task_name", taskManager.getEpic(id).getName());

        final Epic newEpicWrongId = (Epic) new Epic().setId(-1).setName("new_task_name");
        assertThrows(ManagerTaskException.class,
                () -> taskManager.updateEpic(newEpicWrongId));
        assertEquals("task_name", taskManager.getEpic(id).getName());

        taskManager.updateEpic((Epic) new Epic().setId(id).setName("new_task_name"));

        assertEquals("new_task_name", taskManager.getEpic(id).getName());
    }

    @Test
    void should_removeEpic_RemoveExistingEpic() throws ManagerTaskException {
        assertEquals(0, taskManager.getEpics().size());
        assertDoesNotThrow(() -> taskManager.removeEpic(-1));

        final long id = taskManager.createEpic(new Epic());

        assertEquals(1, taskManager.getEpics().size());

        taskManager.removeEpic(id);

        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void should_getSubtaskIds_ReturnSubtasksIds() throws ManagerTaskException {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);
        final long subtaskId = taskManager.createSubtask(new Subtask(epicId));

        assertTrue(epic.getSubtaskIds().contains(subtaskId));
    }

    @Test
    void should_getStatus_NEW_WhenSubtasksEmpty() throws ManagerTaskException {
        final Epic epic = new Epic();

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void should_getStatus_NEW_WhenSubtasks_NEW() throws ManagerTaskException {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.NEW));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.NEW));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void should_getStatus_DONE_WhenSubtasks_DONE() throws ManagerTaskException {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.DONE));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.DONE));

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void should_getStatus_IN_PROGRESS_WhenSubtasks_DONE_And_NEW() throws ManagerTaskException {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.NEW));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.DONE));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void should_getStatus_IN_PROGRESS_WhenSubtasks_IN_PROGRESS() throws ManagerTaskException {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.IN_PROGRESS));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setStatus(Status.IN_PROGRESS));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}