package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected static TaskManager taskManager;

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

        assertNotNull(taskManager.getSubtask(id));
        assertThrows(NullPointerException.class,
            () -> taskManager.removeSubtask(id + 1));
        assertThrows(NullPointerException.class,
                () -> taskManager.removeSubtask(id - 1));
        assertTrue(taskManager.getSubtasks().contains(task));
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

        assertThrows(NullPointerException.class,
                ()-> taskManager.removeEpic(id + 1));
        assertThrows(NullPointerException.class,
                ()-> taskManager.removeEpic(id - 1));

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

    // TODO сделать остальные тесты:
    /*

    // Subtask
    Collection<Subtask> getSubtasks();

    void clearSubtasks();

    Subtask getSubtask(long id);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(long id);

    // Epic
    Collection<Epic> getEpics();

    void clearEpics();

    Epic getEpic(long id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpic(long id);

    Collection<Subtask> getSubtasks(Epic epic);
     */
}