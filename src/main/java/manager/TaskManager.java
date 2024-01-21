package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    List<Long> getHistory();

    // Task
    Collection<Task> getTasks();

    void clearTasks();

    Task getTask(long id);

    long createTask(Task task) throws ManagerTaskException;

    void updateTask(Task task) throws ManagerTaskException;

    void removeTask(long id);

    // Subtask
    List<Subtask> getSubtasks();

    void clearSubtasks();

    Subtask getSubtask(long id);

    long createSubtask(Subtask subtask) throws ManagerTaskException;

    void updateSubtask(Subtask subtask) throws ManagerTaskException;

    void removeSubtask(long id);

    // Epic
    List<Epic> getEpics();

    void clearEpics();

    Epic getEpic(long id);

    long createEpic(Epic epic) throws ManagerTaskException;

    void updateEpic(Epic epic) throws ManagerTaskException;

    void removeEpic(long id);

    List<Subtask> getSubtasks(Epic epic);
}
