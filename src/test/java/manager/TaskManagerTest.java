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

    protected static TaskManager taskManager;

    void shouldReturnPrioritizedTasks() {
        final LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        taskManager.createTask(new Task().setName("t1").setStartTime(startTime));
        taskManager.createTask(new Task().setName("t2"));
        taskManager.createTask(new Task().setName("t3").setStartTime(startTime.plusHours(19)));
        taskManager.createTask(new Task().setName("t4").setStartTime(startTime.plusHours(8)));
        taskManager.createTask(new Task().setName("t5").setStartTime(startTime.plusHours(55)));

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(0, prioritizedTasks.get(0).getId());
        assertEquals(3, prioritizedTasks.get(1).getId());
        assertEquals(2, prioritizedTasks.get(2).getId());
        assertEquals(4, prioritizedTasks.get(3).getId());
        assertEquals(1, prioritizedTasks.get(4).getId());
    }

    void shouldEpicReturn_StartTime_EndTime_Duration() {
        final LocalDateTime startTime = LocalDateTime.of(2024, 1, 20, 19, 13, 0);
        final long epicId = taskManager.createEpic((Epic) new Epic().setName("t0"));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t1").setStartTime(startTime).setDuration(50));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t2"));
        long subtask3Id = taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t3").setStartTime(startTime.plusHours(19)).setDuration(10));
        taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t4").setStartTime(startTime.plusHours(8)).setDuration(30));
        long subtask5Id = taskManager.createSubtask((Subtask) new Subtask(epicId).setName("t5").setStartTime(startTime.plusHours(48)).setDuration(30));

        final Epic epic = taskManager.getEpic(epicId);
        final LocalDateTime endTime1 = taskManager.getSubtask(subtask5Id).getEndTime();

        assertEquals(startTime, epic.getStartTime());
        assertEquals(endTime1, epic.getEndTime());
        assertEquals(120, epic.getDuration());

        taskManager.removeSubtask(subtask5Id);

        final LocalDateTime endTime2 = taskManager.getSubtask(subtask3Id).getEndTime();
        assertEquals(startTime, epic.getStartTime());
        assertEquals(endTime2, epic.getEndTime());
        assertEquals(90, epic.getDuration());
    }

    // ================================ TASK ================================

    void shouldReturnCollectionWithOneTaskWhenTaskCreated() {
        final Task task = new Task();
        taskManager.createTask(task);

        assertTrue(taskManager.getTasks().contains(task));
        assertEquals(1, taskManager.getTasks().size());
    }

    void shouldReturnEmptyCollectionWhenNoTaskCreated() {
        assertTrue(taskManager.getTasks().isEmpty());
    }

    void shouldClearAllTasksWhenTaskCreated() {
        final Task task = new Task();
        taskManager.createTask(task);

        assertEquals(1, taskManager.getTasks().size());

        taskManager.clearTasks();

        assertTrue(taskManager.getTasks().isEmpty());
    }

    void shouldClearAllTasksWhenNoTaskCreated() {
        taskManager.clearTasks();

        assertTrue(taskManager.getTasks().isEmpty());
    }

    void shouldReturnExistingTask() {
        final Task task = new Task();
        final long id = taskManager.createTask(task);

        assertEquals(task, taskManager.getTask(id));
    }

    void shouldReturnNullWhenTasksEmpty() {
        assertNull(taskManager.getTask(-1));
    }

    void shouldCreateNewTask() {
        assertEquals(0, taskManager.getTasks().size());

        taskManager.createTask(new Task());

        assertEquals(1, taskManager.getTasks().size());
    }

    void shouldUpdateTask() {
        final Task task = new Task();
        task.setName("task_name");
        final long id = taskManager.createTask(task);

        assertEquals("task_name", taskManager.getTask(id).getName());

        final Task newtask = new Task();
        newtask.setId(id);
        newtask.setName("new_task_name");
        taskManager.updateTask(newtask);

        assertEquals("new_task_name", taskManager.getTask(id).getName());
    }

    void shouldNotUpdateTaskWhenWrongIdGiven() {
        final Task task = new Task();
        task.setName("task_name");
        final long id = taskManager.createTask(task);

        assertEquals("task_name", taskManager.getTask(id).getName());

        final Task newTask = new Task();
        newTask.setId(-1);
        newTask.setName("new_task_name");
        taskManager.updateTask(newTask);

        assertEquals("task_name", taskManager.getTask(id).getName());
    }

    void shouldNotCreateTaskWhenUpdatedTaskNonExistent() {
        final Task task = new Task();

        assertEquals(0, taskManager.getTasks().size());

        taskManager.updateTask(task);

        assertEquals(0, taskManager.getTasks().size());
    }

    void shouldRemoveExistingTask() {
        final Task task = new Task();
        final long id = taskManager.createTask(task);

        assertEquals(1, taskManager.getTasks().size());

        taskManager.removeTask(id);

        assertEquals(0, taskManager.getTasks().size());
    }

    void shouldNotRemoveExistingTask() {
        final Task task = new Task();
        final long id = taskManager.createTask(task);

        assertNotNull(taskManager.getTask(id));

        taskManager.removeTask(id + 1);
        taskManager.removeTask(id - 1);

        assertTrue(taskManager.getTasks().contains(task));
    }

    // ================================ SUBTASK ================================

    void shouldReturnCollectionWithOneSubtaskWhenSubtaskCreated() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        taskManager.createSubtask(task);

        assertTrue(taskManager.getSubtasks().contains(task));
        assertEquals(1, taskManager.getSubtasks().size());
    }

    void shouldReturnEmptyCollectionWhenNoSubtaskCreated() {
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    void shouldClearAllSubtasksWhenSubtaskCreated() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        taskManager.createSubtask(task);

        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    void shouldClearAllSubtasksWhenNoSubtaskCreated() {
        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    void shouldReturnExistingSubtask() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        final long id = taskManager.createSubtask(task);

        assertEquals(task, taskManager.getSubtask(id));
    }

    void shouldReturnNullWhenSubtasksEmpty() {
        assertNull(taskManager.getSubtask(-1));
    }

    void shouldCreateNewSubtask() {
        assertEquals(0, taskManager.getSubtasks().size());
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        taskManager.createSubtask(task);

        assertEquals(1, taskManager.getSubtasks().size());
    }

    void shouldUpdateSubtask() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        task.setName("task_name");
        final long id = taskManager.createSubtask(task);
        assertEquals("task_name", taskManager.getSubtask(id).getName());

        final Subtask newTask = new Subtask(epicId);
        newTask.setId(id);
        newTask.setName("new_task_name");
        taskManager.updateSubtask(newTask);

        assertEquals("new_task_name", taskManager.getSubtask(id).getName());
    }

    void shouldNotUpdateSubtaskWhenWrongIdGiven() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        task.setName("task_name");
        final long id = taskManager.createSubtask(task);

        assertEquals("task_name", taskManager.getSubtask(id).getName());

        final Subtask newTask = new Subtask(epicId);
        newTask.setId(-1);
        newTask.setName("new_task_name");
        taskManager.updateSubtask(newTask);

        assertEquals("task_name", taskManager.getSubtask(id).getName());
    }

    void shouldNotCreateSubtaskWhenUpdatedSubtaskNonExistent() {
        final long epicId = 1;
        final Subtask task = new Subtask(epicId);
        assertEquals(0, taskManager.getSubtasks().size());

        taskManager.updateSubtask(task);

        assertEquals(0, taskManager.getSubtasks().size());
    }

    void shouldRemoveExistingSubtask() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        final long id = taskManager.createSubtask(task);

        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.removeSubtask(id);

        assertEquals(0, taskManager.getSubtasks().size());
    }

    void shouldNotRemoveExistingSubtask() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        final long id = taskManager.createSubtask(task);
        taskManager.removeSubtask(id + 1);
        taskManager.removeSubtask(id - 1);
        assertNotNull(taskManager.getSubtask(id));
    }

    void shouldSubtaskReturnEpicId() {
        final long epicId = taskManager.createEpic(new Epic());
        final Subtask task = new Subtask(epicId);
        final long id = taskManager.createSubtask(task);
        final Subtask createdSubtask = taskManager.getSubtask(id);

        assertEquals(epicId, createdSubtask.getEpicId());
    }

    // ================================ EPIC ================================

    void shouldReturnCollectionWithOneEpicWhenEpicCreated() {
        final Epic task = new Epic();
        taskManager.createEpic(task);

        assertTrue(taskManager.getEpics().contains(task));
        assertEquals(1, taskManager.getEpics().size());
    }

    void shouldReturnEmptyCollectionWhenNoEpicCreated() {
        assertTrue(taskManager.getEpics().isEmpty());
    }

    void shouldClearAllEpicsWhenEpicCreated() {
        final Epic task = new Epic();
        taskManager.createEpic(task);

        assertEquals(1, taskManager.getEpics().size());

        taskManager.clearEpics();

        assertTrue(taskManager.getEpics().isEmpty());
    }

    void shouldClearAllEpicsWhenNoEpicCreated() {
        taskManager.clearEpics();

        assertTrue(taskManager.getEpics().isEmpty());
    }

    void shouldReturnExistingEpic() {
        final Epic task = new Epic();
        final long id = taskManager.createEpic(task);

        assertEquals(task, taskManager.getEpic(id));
    }

    void shouldReturnNullWhenEpicsEmpty() {
        assertNull(taskManager.getEpic(-1));
    }

    void shouldCreateNewEpic() {
        assertEquals(0, taskManager.getEpics().size());

        taskManager.createEpic(new Epic());

        assertEquals(1, taskManager.getEpics().size());
    }

    void shouldUpdateEpic() {
        final Epic task = new Epic();
        task.setName("task_name");
        final long id = taskManager.createEpic(task);

        assertEquals("task_name", taskManager.getEpic(id).getName());

        final Epic newTask = new Epic();
        newTask.setId(id);
        newTask.setName("new_task_name");
        taskManager.updateEpic(newTask);

        assertEquals("new_task_name", taskManager.getEpic(id).getName());
    }

    void shouldNotUpdateEpicWhenWrongIdGiven() {
        final Epic task = new Epic();
        task.setName("task_name");
        final long id = taskManager.createEpic(task);

        assertEquals("task_name", taskManager.getEpic(id).getName());

        final Epic newTask = new Epic();
        newTask.setId(-1);
        newTask.setName("new_task_name");
        taskManager.updateEpic(newTask);

        assertEquals("task_name", taskManager.getEpic(id).getName());
    }

    void shouldNotCreateEpicWhenUpdatedEpicNonExistent() {
        final Epic task = new Epic();

        assertEquals(0, taskManager.getEpics().size());

        taskManager.updateEpic(task);

        assertEquals(0, taskManager.getEpics().size());
    }

    void shouldRemoveExistingEpic() {
        final Epic task = new Epic();
        final long id = taskManager.createEpic(task);

        assertEquals(1, taskManager.getEpics().size());

        taskManager.removeEpic(id);

        assertEquals(0, taskManager.getEpics().size());
    }

    void shouldNotRemoveExistingEpic() {
        final Epic task = new Epic();
        final long id = taskManager.createEpic(task);

        assertNotNull(taskManager.getEpic(id));

        taskManager.removeEpic(id + 1);
        taskManager.removeEpic(id - 1);

        assertTrue(taskManager.getEpics().contains(task));
    }

    void shouldEpicReturnSubtask() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);
        final Subtask subtask = new Subtask(epicId);
        final long subtaskId = taskManager.createSubtask(subtask);

        assertTrue(epic.getSubtaskIds().contains(subtaskId));
    }

    void shouldEpicReturnStatus_NEW_WhenSubtasksEmpty() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());
    }

    void shouldEpicReturnStatus_NEW_WhenSubtasks_NEW() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        final Subtask subtask1 = new Subtask(epicId);
        subtask1.setStatus(Status.NEW);
        taskManager.createSubtask(subtask1);
        final Subtask subtask2 = new Subtask(epicId);
        subtask2.setStatus(Status.NEW);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    void shouldEpicReturnStatus_DONE_WhenSubtasks_DONE() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        final Subtask subtask1 = new Subtask(epicId);
        subtask1.setStatus(Status.DONE);
        taskManager.createSubtask(subtask1);
        final Subtask subtask2 = new Subtask(epicId);
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    void shouldEpicReturnStatus_IN_PROGRESS_WhenSubtasks_DONE_And_NEW() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        final Subtask subtask1 = new Subtask(epicId);
        subtask1.setStatus(Status.NEW);
        taskManager.createSubtask(subtask1);
        final Subtask subtask2 = new Subtask(epicId);
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    void shouldEpicReturnStatus_IN_PROGRESS_WhenSubtasks_IN_PROGRESS() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());

        final Subtask subtask1 = new Subtask(epicId);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1);
        final Subtask subtask2 = new Subtask(epicId);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    void shouldReturnSubtasksFromEpic() {
        final Epic epic = new Epic();
        final long epicId = taskManager.createEpic(epic);

        assertEquals(0, taskManager.getSubtasks(epic).size());

        taskManager.createSubtask(new Subtask(epicId));
        taskManager.createSubtask(new Subtask(epicId));

        assertEquals(2, taskManager.getSubtasks(epic).size());
    }

}