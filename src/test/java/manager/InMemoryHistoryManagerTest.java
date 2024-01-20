package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void createHistoryManager() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldBeEmpty() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTasksInOrder() {
        Task task = new Task();
        task.setId(1);
        Task epic = new Epic();
        epic.setId(2);
        historyManager.add(epic);
        historyManager.add(task);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(task.getId(), historyManager.getHistory().get(1).longValue());
    }

    @Test
    void shouldIgnoreDuplicates() {
        Task task = new Task();
        task.setId(1);
        Task epic = new Epic();
        epic.setId(2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(task);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(task.getId(), historyManager.getHistory().get(1).longValue());
    }

    @Test
    void shouldRemoveTaskFromHead() {
        Task task = new Task();
        task.setId(1);
        Task epic = new Epic();
        epic.setId(2);
        Task subtask = new Subtask(epic.getId());
        subtask.setId(3);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(epic.getId(), historyManager.getHistory().get(1).longValue());
        assertEquals(subtask.getId(), historyManager.getHistory().get(2).longValue());

        historyManager.remove(task.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(subtask.getId(), historyManager.getHistory().get(1).longValue());
    }

    @Test
    void shouldRemoveTaskFromMiddle() {
        Task task = new Task();
        task.setId(1);
        Task epic = new Epic();
        epic.setId(2);
        Task subtask = new Subtask(epic.getId());
        subtask.setId(3);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(epic.getId(), historyManager.getHistory().get(1).longValue());
        assertEquals(subtask.getId(), historyManager.getHistory().get(2).longValue());

        historyManager.remove(epic.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(subtask.getId(), historyManager.getHistory().get(1).longValue());
    }

    @Test
    void shouldRemoveTaskFromTail() {
        Task task = new Task();
        task.setId(1);
        Task epic = new Epic();
        epic.setId(2);
        Task subtask = new Subtask(epic.getId());
        subtask.setId(3);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(epic.getId(), historyManager.getHistory().get(1).longValue());
        assertEquals(subtask.getId(), historyManager.getHistory().get(2).longValue());

        historyManager.remove(subtask.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0).longValue());
        assertEquals(epic.getId(), historyManager.getHistory().get(1).longValue());
    }
}