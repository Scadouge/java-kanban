package manager;

import task.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(long id);

    List<Long> getHistory();
}
