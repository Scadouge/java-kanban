package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_ITEMS = 10;
    private final LinkedList<Task> taskHistory;

    public InMemoryHistoryManager() {
        this.taskHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if(task != null) {
            if (taskHistory.size() >= MAX_HISTORY_ITEMS) {
                taskHistory.removeFirst();
            }
            taskHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
