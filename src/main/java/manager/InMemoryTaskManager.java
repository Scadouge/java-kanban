package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashSet<String> intervals;
    protected final TreeSet<Task> sortedTasks;
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, Subtask> subtasks;
    protected final HashMap<Long, Epic> epics;
    protected final HistoryManager historyManager;
    private long sequenceId;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

    public InMemoryTaskManager() {
        intervals = new HashSet<>();
        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        sequenceId = 0;
        historyManager = Managers.getDefaultHistory();
    }

    private boolean claimIntervals(Task task) {
        List<String> keys = getIntervalKeys(task);
        Optional<String> optional = keys.stream().filter(intervals::contains).findFirst();
        if (optional.isEmpty()) {
            intervals.addAll(getIntervalKeys(task));
            return true;
        } else {
            return false;
        }
    }

    private void unclaimIntervals(Task task) {
        List<String> keys = getIntervalKeys(task);
        keys.forEach(intervals::remove);
    }

    private static List<String> getIntervalKeys(Task task) {
        List<String> keys = new ArrayList<>();
        LocalDateTime startTime = task.getStartTime();
        if(startTime.equals(LocalDateTime.MAX))
            return keys;
        LocalDateTime coursor = LocalDateTime.of(startTime.toLocalDate(), LocalTime.of(startTime.getHour(),
                (startTime.getMinute() / 15) * 15));

        LocalDateTime endTime = task.getEndTime();
        LocalDateTime end = LocalDateTime.of(endTime.toLocalDate(), LocalTime.of(endTime.getHour(),
                (endTime.getMinute() / 15) * 15));
        while (coursor.isBefore(end.plusMinutes(15))) {
            keys.add(coursor.format(formatter));
            coursor = coursor.plusMinutes(15);
        }
        return keys;
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
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().forEach(this::unclaimIntervals);
        tasks.clear();
    }

    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public long createTask(Task task) throws ManagerTaskException {
        if (task != null) {
            if (!claimIntervals(task)) {
                throw new ManagerTaskException("Задача не может быть создана: пересекаются интервалы", task);
            }
            task.setId(generateId());
            tasks.put(task.getId(), task);
            sortedTasks.add(task);
            return task.getId();
        } else {
            throw new ManagerTaskException("Задача не может быть создана", task);
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerTaskException {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            unclaimIntervals(existingTask);
            if (!claimIntervals(task)) {
                claimIntervals(existingTask);
                throw new ManagerTaskException("Задача не может быть создана: пересекаются интервалы", task);
            }
            tasks.put(task.getId(), task);
            sortedTasks.add(task);
        } else {
            throw new ManagerTaskException("Обновляемая задача не найдена", task);
        }
    }

    @Override
    public void removeTask(long id) {
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            unclaimIntervals(removedTask);
            sortedTasks.add(removedTask);
            historyManager.remove(id);
        }
    }

    // Subtask
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(sortedTasks::remove);
        subtasks.values().forEach(this::unclaimIntervals);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public long createSubtask(Subtask subtask) throws ManagerTaskException {
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) {
                if (!claimIntervals(subtask)) {
                    throw new ManagerTaskException("Подзадача не может быть создана: пересекаются интервалы", subtask);
                }
                subtask.setId(generateId());
                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
                sortedTasks.add(subtask);
                updateStatus(epic);
                updateTime(epic);
            } else {
                throw new ManagerTaskException("Подзадача не может быть создана: не найден епик", subtask);
            }
            return subtask.getId();
        } else {
            throw new ManagerTaskException("Подзадача не может быть создана", subtask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerTaskException {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask existingSubtask = subtasks.get(subtask.getId());
            unclaimIntervals(existingSubtask);
            if (!claimIntervals(subtask)) {
                claimIntervals(existingSubtask);
                throw new ManagerTaskException("Подзадача не может быть создана: пересекаются интервалы", subtask);
            }
            Epic epic = epics.get(existingSubtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            sortedTasks.add(subtask);
            updateStatus(epic);
            updateTime(epic);
        } else {
            throw new ManagerTaskException("Обновляемая подзадача не найдена", subtask);
        }
    }

    @Override
    public void removeSubtask(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            unclaimIntervals(subtask);
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
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(this::unclaimIntervals);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(sortedTasks::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public long createEpic(Epic epic) throws ManagerTaskException {
        if (epic != null) {
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
            return epic.getId();
        } else {
            throw new ManagerTaskException("Эпик не может быть создан", epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerTaskException {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            throw new ManagerTaskException("Обновляемый эпик не найден", epic);
        }
    }

    @Override
    public void removeEpic(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtasks::remove);
            epics.remove(id);
            epic.getSubtaskIds().forEach(historyManager::remove);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream().map(subtasks::get).collect(Collectors.toList());
    }

    private void updateTime(Epic epic) {
        Optional<LocalDateTime> min = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .map(Subtask::getStartTime)
                .filter(startTime -> !startTime.equals(LocalDateTime.MAX))
                .min(LocalDateTime::compareTo);
        if (min.isPresent()) {
            Optional<LocalDateTime> max = epic.getSubtaskIds().stream()
                    .map(subtasks::get)
                    .filter(t -> !t.getStartTime().equals(LocalDateTime.MAX))
                    .map(Task::getEndTime)
                    .max(LocalDateTime::compareTo);
            if (max.isPresent()) {
                int duration = epic.getSubtaskIds().stream()
                        .map(subtasks::get)
                        .reduce(0, (sum, t) -> sum + t.getDuration(), Integer::sum);
                epic.setDuration(duration);
                epic.setStartTime(min.get());
                epic.setEndTime(max.get());
            }
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
