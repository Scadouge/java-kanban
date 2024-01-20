package manager;

import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected static TaskManager taskManager;

    void shouldReturnCollectionWithOneTaskWhenTaskCreated() {
        Task task = new Task();
        taskManager.createTask(task);
        assertTrue(taskManager.getTasks().contains(task));
    }

    void shouldReturnEmptyCollectionWhenNoTaskCreated() {
        assertTrue(taskManager.getTasks().isEmpty());
    }

    void shouldClearAllTasksWhenTaskCreated() {
        Task task = new Task();
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
        Task task = new Task();
        long id = task.getId();
        taskManager.createTask(task);
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
        Task task = new Task();
        task.setName("task_name");
        taskManager.createTask(task);
        long id = task.getId();
        assertEquals("task_name", taskManager.getTask(id).getName());

        Task newtask = new Task();
        newtask.setId(id);
        newtask.setName("new_task_name");
        taskManager.updateTask(newtask);

        assertEquals("new_task_name", taskManager.getTask(id).getName());
    }

    void shouldNotUpdateTaskWhenWrongIdGiven() {
        Task task = new Task();
        task.setName("task_name");
        taskManager.createTask(task);
        long id = task.getId();
        assertEquals("task_name", taskManager.getTask(id).getName());

        Task newtask = new Task();
        newtask.setId(-1);
        newtask.setName("new_task_name");
        taskManager.updateTask(newtask);

        assertEquals("task_name", taskManager.getTask(id).getName());
    }

    void shouldRemoveExistingTask() {
        Task task = new Task();
        taskManager.createTask(task);
        long id = task.getId();

        taskManager.removeTask(id);
    }

    // TODO сделать остальные тесты:
    /*

    void removeTask(long id);

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