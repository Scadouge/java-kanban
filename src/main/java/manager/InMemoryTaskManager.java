package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Subtask> subtasks;
    protected final HashMap<Long, Epic> epics;
    protected final HistoryManager historyManager;
    private long sequenceId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        sequenceId = 0;
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Long> getHistory() {
        return historyManager.getHistory();
    }

    // Task
    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public void clearTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public long createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Обновляемая задача не найдена " + task);
        }
    }

    @Override
    public void removeTask(long id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    // Subtask
    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    public void clearSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public long createSubtask(Subtask subtask) {
        if(epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(epic);
        } else {
            System.out.println("Не найден епик, при создании подзадачи " + subtask);
        }
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask existingSubtask = subtasks.get(subtask.getId());
            Epic epic = epics.get(existingSubtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(epic);
        } else {
            System.out.println("Обновляемая подзадача не найдена " + subtask);
        }
    }

    @Override
    public void removeSubtask(long id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.removeSubtaskId(id);
        subtasks.remove(id);
        historyManager.remove(id);
        updateStatus(epic);
    }

    // Epic
    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public void clearEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(long id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public long createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Обновляемый эпик не найден " + epic);
        }
    }

    @Override
    public void removeEpic(long id) {
        Epic epic = epics.get(id);
        epic.getSubtaskIds().forEach(subtasks::remove);
        epics.remove(id);
        epic.getSubtaskIds().forEach(historyManager::remove);
        historyManager.remove(id);
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
            } else {
                newStatus = subtask.getStatus();
            }
        }
        if (newStatus == null || subtasks.isEmpty()) {
            newStatus = Status.NEW;
        }
        epic.setStatus(newStatus);
    }

    private long generateId() {
        return sequenceId++;
    }
}
