package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    // Task
    Collection<Task> getTasks();

    void clearTasks();

    Task getTask(long id);

    void createTask(Task task);

    void updateTask(Task task);

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
}
