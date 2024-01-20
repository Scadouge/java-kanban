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
    void shouldReturnPrioritizedTasks() {
        super.shouldReturnPrioritizedTasks();
    }

    @Test
    @Override
    void shouldEpicReturn_StartTime_EndTime_Duration() {
        super.shouldEpicReturn_StartTime_EndTime_Duration();
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