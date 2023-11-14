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
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId()))
            tasks.put(task.getId(), task);
        else
            System.out.println("Обновляемая задача не найдена " + task);
    }

    public void removeTask(long id) {
        tasks.remove(id);
    }

    // Subtask
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public void clearSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        });
    }

    public Subtask getSubtask(long id) {
        return subtasks.get(id);
    }

    public void createSubtask(Subtask subtask) {
        if(epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            Epic epic = getEpic(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(epic);
        } else
            System.out.println("Не найден епик, при создании подзадачи " + subtask);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask existingSubtask = getSubtask(subtask.getId());
            Epic epic = getEpic(existingSubtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(epic);
        } else
            System.out.println("Обновляемая подзадача не найдена " + subtask);
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
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpic(long id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId()))
            epics.put(epic.getId(), epic);
        else
            System.out.println("Обновляемый эпик не найден " + epic);
    }

    public void removeEpic(long id) {
        Epic epic = getEpic(id);
        epic.getSubtaskIds().forEach(subtasks::remove);
        epics.remove(id);
    }

    public Collection<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream().map(this::getSubtask).collect(Collectors.toList());
    }

    private void updateStatus(Epic epic) {
        Status newStatus = null;
        for (Subtask subtask : getSubtasks(epic)) {
            if (newStatus != null) {
                if (subtask.getStatus() != newStatus) {
                    newStatus = Status.IN_PROGRESS;
                    break;
                }
            } else
                newStatus = subtask.getStatus();
        }
        if (newStatus == null || subtasks.isEmpty())
            newStatus = Status.NEW;
        epic.setStatus(newStatus);
    }

    private long generateId() {
        return sequenceId++;
    }
}
