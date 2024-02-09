package manager;

import exception.*;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

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
    private final static Comparator<Task> TASK_COMPARATOR = Comparator.comparing(Task::getStartTime).thenComparing(Task::getId);
    private long sequenceId;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

    public InMemoryTaskManager() {
        intervals = new HashSet<>();
        sortedTasks = new TreeSet<>(TASK_COMPARATOR);
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
        LocalDateTime startTime;
        try {
            startTime = task.getStartTime();
        } catch (TaskDataUndefinedException e) {
            return keys;
        }
        if (startTime.equals(Task.DEFAULT_START_TIME)) {
            return keys;
        }

        final int intervalInMinutes = 15;
        LocalDateTime coursor = LocalDateTime.of(startTime.toLocalDate(), LocalTime.of(startTime.getHour(),
                (startTime.getMinute() / intervalInMinutes) * intervalInMinutes));

        LocalDateTime endTime = task.getEndTime();
        LocalDateTime end = LocalDateTime.of(endTime.toLocalDate(), LocalTime.of(endTime.getHour(),
                (endTime.getMinute() / intervalInMinutes) * intervalInMinutes));
        while (coursor.isBefore(end.plusMinutes(intervalInMinutes))) {
            keys.add(coursor.format(formatter));
            coursor = coursor.plusMinutes(intervalInMinutes);
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
    public Task getTask(Long id) {
        if (id == null) {
            return null;
        }
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Long createTask(Task task) throws ManagerTaskException {
        if (task == null) {
            throw new ManagerTaskBadInputException("Задача не может быть создана: task = null");
        }
        if (tasks.get(task.getId()) != null) {
            throw new ManagerTaskAlreadyExistException("Задача не может быть создана: задача с таким id уже существует " + task.getId());
        }
        if (!claimIntervals(task)) {
            throw new ManagerTaskTimeIntersectionException("Задача не может быть создана: пересекаются интервалы");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
        return task.getId();

    }

    @Override
    public void updateTask(Task task) throws ManagerTaskException {
        if (task == null) {
            throw new ManagerTaskBadInputException("Задача не может быть обновлена: task = null");
        }
        if (!tasks.containsKey(task.getId())) {
            throw new ManagerTaskNotFoundException("Обновляемая задача не найдена: id = " + task.getId());
        }
        Task existingTask = tasks.get(task.getId());
        unclaimIntervals(existingTask);
        if (!claimIntervals(task)) {
            claimIntervals(existingTask);
            throw new ManagerTaskTimeIntersectionException("Задача не может быть обновлена: пересекаются интервалы");
        }
        tasks.put(task.getId(), task);
        sortedTasks.remove(existingTask);
        sortedTasks.add(task);
    }

    @Override
    public void removeTask(Long id) {
        if (id == null) {
            return;
        }
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            unclaimIntervals(removedTask);
            sortedTasks.remove(removedTask);
            historyManager.remove(id);
        }
    }

    // Subtask
    @Override
    public List<Subtask> getEpicSubtasks() {
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
    public Subtask getSubtask(Long id) {
        if (id == null) {
            return null;
        }
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Long createSubtask(Subtask subtask) throws ManagerTaskException {
        if (subtask == null) {
            throw new ManagerTaskBadInputException("Подзадача не может быть создана: subtask = null");
        }
        if (subtasks.get(subtask.getId()) != null) {
            throw new ManagerTaskAlreadyExistException("Подзадача не может быть создана: задача с таким id уже существует " + subtask.getId());
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new ManagerTaskException("Подзадача не может быть создана: не найден епик");
        }
        if (!claimIntervals(subtask)) {
            throw new ManagerTaskTimeIntersectionException("Подзадача не может быть создана: пересекаются интервалы");
        }
        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        sortedTasks.add(subtask);
        updateStatus(epic);
        updateTime(epic);
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerTaskException {
        if (subtask == null) {
            throw new ManagerTaskBadInputException("Подзадача не может быть обновлена: subtask = null");
        }
        if (!subtasks.containsKey(subtask.getId())) {
            throw new ManagerTaskNotFoundException("Обновляемая подзадача не найдена");
        }
        Subtask existingSubtask = subtasks.get(subtask.getId());
        unclaimIntervals(existingSubtask);
        if (!claimIntervals(subtask)) {
            claimIntervals(existingSubtask);
            throw new ManagerTaskException("Подзадача не может быть обновлена: пересекаются интервалы");
        }
        Epic epic = epics.get(existingSubtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        sortedTasks.remove(existingSubtask);
        sortedTasks.add(subtask);
        updateStatus(epic);
        updateTime(epic);
    }

    @Override
    public void removeSubtask(Long id) {
        if (id == null) {
            return;
        }
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
    public Epic getEpic(Long id) {
        if (id == null) {
            return null;
        }
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Long createEpic(Epic epic) throws ManagerTaskException {
        if (epic == null) {
            throw new ManagerTaskBadInputException("Эпик не может быть создан: epic = null");
        }
        if (epics.get(epic.getId()) != null) {
            throw new ManagerTaskAlreadyExistException("Эпик не может быть создан: задача с таким id уже существует " + epic.getId());
        }
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerTaskException {
        if (epic == null) {
            throw new ManagerTaskBadInputException("Эпик не может быть обновлен: epic = null");
        }
        if (!epics.containsKey(epic.getId())) {
            throw new ManagerTaskException("Обновляемый эпик не найден");
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeEpic(Long id) {
        if (id == null) {
            return;
        }
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtasks::remove);
            epics.remove(id);
            epic.getSubtaskIds().forEach(historyManager::remove);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(Long epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new ManagerTaskNotFoundException("Невозможно получить список подзадач: не найден эпик");
        }
        return epic.getSubtaskIds().stream().map(subtasks::get).collect(Collectors.toList());
    }

    private void updateTime(Epic epic) {
        if (epic.getSubtaskIds().size() > 0) {
            Optional<LocalDateTime> min = epic.getSubtaskIds().stream()
                    .map(subtasks::get)
                    .map(Subtask::getStartTime)
                    .filter(startTime -> !startTime.equals(Task.DEFAULT_START_TIME))
                    .min(LocalDateTime::compareTo);
            if (min.isPresent()) {
                Optional<LocalDateTime> max = epic.getSubtaskIds().stream()
                        .map(subtasks::get)
                        .filter(t -> !t.getStartTime().equals(Task.DEFAULT_START_TIME))
                        .map(Task::getEndTime)
                        .max(LocalDateTime::compareTo);
                if (max.isPresent()) {
                    int duration = epic.getSubtaskIds().stream()
                            .map(subtasks::get)
                            .reduce(0, (sum, t) -> sum + t.getDuration(), Integer::sum);
                    epic.setDuration(duration);
                    epic.setStartTime(min.get());
                    epic.setEndTime(max.get());
                    return;
                }
            }
        }
        epic.setDuration(0);
        epic.setStartTime(LocalDateTime.MAX);
        epic.setEndTime(LocalDateTime.MAX);
    }

    private void updateStatus(Epic epic) {
        Status newStatus = null;
        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
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
