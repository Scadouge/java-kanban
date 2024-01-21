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