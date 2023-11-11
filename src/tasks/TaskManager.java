package tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private final HashMap<Long, Task> tasks;
    private long sequenceId;

    public TaskManager() {
        tasks = new HashMap<>();
        sequenceId = 0;
    }

    public List<Task> getTasks(TaskType taskType) {
        return getTasks().stream()
                .filter(t -> TaskType.getTaskType(t) == taskType)
                .collect(Collectors.toList());
    }

    // Получение списка всех задач.
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public void clearTasks(TaskType taskType) {
        List<Task> taskToRemove = tasks.values().stream()
                .filter(t -> TaskType.getTaskType(t) == taskType)
                .collect(Collectors.toList());
        taskToRemove.forEach(t -> removeTask(t.getId()));
    }

    // Удаление всех задач.
    public void clearTasks() {
        tasks.clear();
    }

    // Получение по идентификатору.
    public Task getTask(long id) {
        return tasks.get(id);
    }

//    public void addSubtask(Epic newEpic, Subtask newSubtask) {
//        Epic epic = (Epic) createTask(newEpic);
//        epic.addSubtask((Subtask) createTask(newSubtask));
//    }
//
//    public void addSubtask(long epicId, Subtask newSubtask) {
//        Epic epic = (Epic) getTask(epicId);
//        epic.addSubtask((Subtask) createTask(newSubtask));
//    }

    // Создание. Сам объект должен передаваться в качестве параметра.
    public Task createTask(Task newTask) {
        Task existingTask = getTask(newTask.getId());
        if(existingTask != null) {
            if (TaskType.getTaskType(newTask) == TaskType.SUBTASK
                    && TaskType.getTaskType(existingTask) == TaskType.SUBTASK) {
                // замена старого Subtask'а новым с сохранением ссылки на Epic
                Subtask existingSubtask = (Subtask) existingTask;
                Epic epic = (Epic) getTask(existingSubtask.getEpicId());
                epic.removeSubtask(existingSubtask);
                epic.addSubtask((Subtask) newTask);
            } else if (TaskType.getTaskType(newTask) == TaskType.EPIC
                    && TaskType.getTaskType(existingTask) == TaskType.EPIC) {
                // замена старого Epic'а новым с сохранением привязанных Subtask
                Epic existingEpic = (Epic) existingTask;
                Epic newEpic = (Epic) newTask;
                newEpic.setSubtasksMap(existingEpic.getSubtasksMap());
            }
        }
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateTask(Task task) {
        return createTask(task);
    }

    // Удаление по идентификатору.
    public void removeTask(long id) {
        Task task = getTask(id);
        tasks.remove(id);
        Collection<Task> toRemove = task.onRemove();
        for (Task taskToRemove : toRemove)
            tasks.remove(taskToRemove.getId());
    }

    public long getNewId() {
        return sequenceId++;
    }
}
