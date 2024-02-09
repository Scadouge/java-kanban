package manager;

import exception.ManagerTaskException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    List<Long> getHistory();

    // Task
    Collection<Task> getTasks();

    void clearTasks();

    Task getTask(Long id);

    Long createTask(Task task) throws ManagerTaskException;

    void updateTask(Task task) throws ManagerTaskException;

    void removeTask(Long id);

    // Subtask
    List<Subtask> getEpicSubtasks();

    void clearSubtasks();

    Subtask getSubtask(Long id);

    Long createSubtask(Subtask subtask) throws ManagerTaskException;

    void updateSubtask(Subtask subtask) throws ManagerTaskException;

    void removeSubtask(Long id);

    // Epic
    List<Epic> getEpics();

    void clearEpics();

    Epic getEpic(Long id);

    Long createEpic(Epic epic) throws ManagerTaskException;

    void updateEpic(Epic epic) throws ManagerTaskException;

    void removeEpic(Long id);

    List<Subtask> getEpicSubtasks(Long epicId);
}
