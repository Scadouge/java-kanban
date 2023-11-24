package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Subtask> subtasks;
    protected final HashMap<Long, Epic> epics;
    private long sequenceId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        sequenceId = 0;
    }

    // Task
    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        Managers.getDefaultHistory().add(task);
        return task;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId()))
            tasks.put(task.getId(), task);
        else
            System.out.println("Обновляемая задача не найдена " + task);
    }

    @Override
    public void removeTask(long id) {
        tasks.remove(id);
    }

    // Subtask
    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        Managers.getDefaultHistory().add(subtask);
        return subtask;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if(epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(epic);
        } else
            System.out.println("Не найден епик, при создании подзадачи " + subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask existingSubtask = subtasks.get(subtask.getId());
            Epic epic = epics.get(existingSubtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(epic);
        } else
            System.out.println("Обновляемая подзадача не найдена " + subtask);
    }

    @Override
    public void removeSubtask(long id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.removeSubtaskId(id);
        subtasks.remove(id);
        updateStatus(epic);
    }

    // Epic
    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(long id) {
        Epic epic = epics.get(id);
        Managers.getDefaultHistory().add(epic);
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId()))
            epics.put(epic.getId(), epic);
        else
            System.out.println("Обновляемый эпик не найден " + epic);
    }

    @Override
    public void removeEpic(long id) {
        Epic epic = epics.get(id);
        epic.getSubtaskIds().forEach(subtasks::remove);
        epics.remove(id);
    }

    @Override
    public Collection<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream().map(subtasks::get).collect(Collectors.toList());
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
