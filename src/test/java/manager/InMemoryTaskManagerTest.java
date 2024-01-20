package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void createTaskManager() {
        taskManager = new InMemoryTaskManager();
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
}