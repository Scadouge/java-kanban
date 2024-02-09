package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
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
        assertEquals(epic.getId(), historyManager.getHistory().get(0));
        assertEquals(task.getId(), historyManager.getHistory().get(1));
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
        assertEquals(epic.getId(), historyManager.getHistory().get(0));
        assertEquals(task.getId(), historyManager.getHistory().get(1));
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
        assertEquals(task.getId(), historyManager.getHistory().get(0));
        assertEquals(epic.getId(), historyManager.getHistory().get(1));
        assertEquals(subtask.getId(), historyManager.getHistory().get(2));

        historyManager.remove(task.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic.getId(), historyManager.getHistory().get(0));
        assertEquals(subtask.getId(), historyManager.getHistory().get(1));
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
        assertEquals(task.getId(), historyManager.getHistory().get(0));
        assertEquals(epic.getId(), historyManager.getHistory().get(1));
        assertEquals(subtask.getId(), historyManager.getHistory().get(2));

        historyManager.remove(epic.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0));
        assertEquals(subtask.getId(), historyManager.getHistory().get(1));
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
        assertEquals(task.getId(), historyManager.getHistory().get(0));
        assertEquals(epic.getId(), historyManager.getHistory().get(1));
        assertEquals(subtask.getId(), historyManager.getHistory().get(2));

        historyManager.remove(subtask.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task.getId(), historyManager.getHistory().get(0));
        assertEquals(epic.getId(), historyManager.getHistory().get(1));
    }
}