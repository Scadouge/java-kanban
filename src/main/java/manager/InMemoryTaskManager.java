package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final TreeSet<Task> sortedTasks;
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Subtask> subtasks;
    protected final HashMap<Long, Epic> epics;
    protected final HistoryManager historyManager;
    private long sequenceId;

    public InMemoryTaskManager() {
        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        sequenceId = 0;
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
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
        sortedTasks.add(task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            sortedTasks.add(task);
        } else {
            System.out.println("Обновляемая задача не найдена " + task);
        }
    }

    @Override
    public void removeTask(long id) {
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            sortedTasks.add(removedTask);
            historyManager.remove(id);
        }
    }

    // Subtask
    @Override
    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    @Override
    public void clearSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(sortedTasks::remove);
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
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            sortedTasks.add(subtask);
            updateStatus(epic);
            updateTime(epic);
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
            sortedTasks.add(subtask);
            updateStatus(epic);
            updateTime(epic);
        } else {
            System.out.println("Обновляемая подзадача не найдена " + subtask);
        }
    }

    @Override
    public void removeSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            subtasks.remove(id);
            sortedTasks.remove(subtask);
            historyManager.remove(id);
            updateStatus(epic);
            updateTime(epic);
        }
    }

    // Epic
    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public void clearEpics() {
        epics.keySet().forEach(historyManager::remove);
        epics.values().forEach(sortedTasks::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(sortedTasks::remove);
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
        sortedTasks.add(epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            sortedTasks.add(epic);
        } else {
            System.out.println("Обновляемый эпик не найден " + epic);
        }
    }

    @Override
    public void removeEpic(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtasks::remove);
            epics.remove(id);
            sortedTasks.remove(epic);
            epic.getSubtaskIds().forEach(historyManager::remove);
            historyManager.remove(id);
        }
    }

    @Override
    public Collection<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream().map(subtasks::get).collect(Collectors.toList());
    }

    private void updateTime(Epic epic) {
        Optional<LocalDateTime> min = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .map(Subtask::getStartTime)
                .filter(startTime -> !startTime.equals(LocalDateTime.MAX))
                .min(LocalDateTime::compareTo);
        Optional<LocalDateTime> max = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(t -> !t.getStartTime().equals(LocalDateTime.MAX))
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo);
        if (min.isPresent() && max.isPresent()) {
            int duration = epic.getSubtaskIds().stream()
                    .map(subtasks::get)
                    .reduce(0, (sum, t) -> sum + t.getDuration(), Integer::sum);
            epic.setDuration(duration);
            epic.setStartTime(min.get());
            epic.setEndTime(max.get());
        }
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
