package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Subtask> subtasks;
    protected final HashMap<Long, Epic> epics;
    private long sequenceId;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        sequenceId = 0;
    }

    // Task
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task getTask(long id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void removeTask(long id) {
        tasks.remove(id);
    }

    // Subtask
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public void clearSubtasks() {
        Set<Long> keySet = new HashSet<>(subtasks.keySet());
        keySet.forEach(this::removeSubtask);
    }

    public Subtask getSubtask(long id) {
        return subtasks.get(id);
    }

    public void createSubtask(Subtask subtask, long epicId) {
        createSubtask(subtask, getEpic(epicId));
    }

    public void createSubtask(Subtask subtask, Epic epic) {
        subtask.setEpicId(epic.getId());
        epic.addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateStatus(epic);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask existingSubtask = getSubtask(subtask.getId());
        Epic epic = getEpic(existingSubtask.getEpicId());

        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(), subtask);
        updateStatus(epic);
    }

    public void removeSubtask(long id) {
        Epic epic = getEpic(getSubtask(id).getEpicId());
        epic.removeSubtaskId(id);
        subtasks.remove(id);
        updateStatus(epic);
    }

    // Epic
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    public void clearEpics() {
        Set<Long> keySet = new HashSet<>(epics.keySet());
        keySet.forEach(this::removeEpic);
    }

    public Epic getEpic(long id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        Epic existingEpic = getEpic(epic.getId());
        epic.setSubtaskIds(existingEpic.getSubtaskIds());
        updateStatus(epic);
        epics.put(epic.getId(), epic);
    }

    public void removeEpic(long id) {
        Epic epic = getEpic(id);
        HashSet<Long> keySet = new HashSet<>(epic.getSubtaskIds());
        keySet.forEach(this::removeSubtask);
        epics.remove(id);
    }

    public Collection<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream().map(this::getSubtask).collect(Collectors.toList());
    }

    private void updateStatus(Epic epic) {
        Status newStatus = null;
        for(Subtask subtask : getSubtasks(epic)) {
            if(newStatus != null) {
                if (subtask.getStatus() != newStatus) {
                    newStatus = Status.IN_PROGRESS;
                    break;
                }
            } else
                newStatus = subtask.getStatus();
        }
        if(newStatus == null || subtasks.size() == 0)
            newStatus = Status.NEW;
        epic.setStatus(newStatus);
    }

    public long generateId() {
        return sequenceId++;
    }
}
