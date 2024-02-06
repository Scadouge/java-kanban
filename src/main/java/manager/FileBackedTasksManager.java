package manager;

import tasks.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String TABLE_HEADER = "id,type,name,status,description,epic,time,duration" + System.lineSeparator();
    private final Path file;

    public FileBackedTasksManager(Path file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file)) {
            FileBackedTasksManager manager = new FileBackedTasksManager(file);
            boolean nextLineIsHistory = false;
            if (br.ready()) { // skip 1 line
                br.readLine();
            }
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    nextLineIsHistory = true;
                    continue;
                }
                if (nextLineIsHistory) {
                    List<Long> history = historyFromString(line);
                    history.forEach(id -> manager.historyManager.add(manager.getTaskUniversal(id)));
                    break;
                } else {
                    manager.createTaskFromString(line);
                }
            }
            for (Subtask subtask : manager.getSubtasks()) {
                long epicId = subtask.getEpicId();
                Epic epic = manager.epics.get(epicId);
                epic.addSubtaskId(subtask.getId());
            }
            return manager;
        }
    }

    public static List<Long> historyFromString(String value) {
        String[] split = value.split(",");
        return Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
    }

    public static String historyToString(HistoryManager manager) {
        List<String> list = manager.getHistory().stream().map(String::valueOf).collect(Collectors.toList());
        return String.join(",", list);
    }

    private void createTaskFromString(String value) {
        //0  1    2    3      4           5    6    7
        //id,type,name,status,description,epic,time,duration
        Task task;
        String[] split = value.split(",");

        TaskType type = TaskType.valueOf(split[1]);
        long id = Long.parseLong(split[0]);
        switch (type) {
            case SUBTASK:
                task = new Subtask(Long.parseLong(split[5]));
                subtasks.put(id, (Subtask) task);
                break;
            case EPIC:
                task = new Epic();
                epics.put(id, (Epic) task);
                break;
            default:
                task = new Task();
                tasks.put(id, task);
        }
        task.setId(id);
        task.setName(split[2]);
        task.setStatus(Status.valueOf(split[3]));
        task.setDescription(split[4]);
        if (split.length <= 6 || split[6].isEmpty()) {
            task.setStartTime(Task.DEFAULT_START_TIME);
        } else {
            task.setStartTime(LocalDateTime.parse(split[6]));
        }
        if (split.length <= 7 || split[7].isEmpty()) {
            task.setDuration(0);
        } else {
            task.setDuration(Integer.parseInt(split[7]));
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(TABLE_HEADER);
            for (Task task : getTasks()) {
                writer.write(taskToString(task) + System.lineSeparator());
            }
            for (Task task : getSubtasks()) {
                writer.write(taskToString(task) + System.lineSeparator());
            }
            for (Task task : getEpics()) {
                writer.write(taskToString(task) + System.lineSeparator());
            }
            writer.write(System.lineSeparator());
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private String taskToString(Task task) {
        //id,type,name,status,description,epic,time,duration
        String epicId = "";
        String startTime;
        String duration;
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        try {
            if (task.getDuration() == Task.DEFAULT_DURATION) {
                duration = "";
            } else {
                duration = String.valueOf(task.getDuration());
            }
            if (task.getStartTime().equals(Task.DEFAULT_START_TIME)) {
                startTime = "";
            } else {
                startTime = task.getStartTime().toString();
            }
        } catch (TaskDataUndefinedException e) {
            duration = "";
            startTime = "";
        }

        return String.join(",", String.valueOf(task.getId()), task.getType().toString(),
                task.getName(), task.getStatus().toString(), task.getDescription(),
                epicId, startTime, duration);
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public Task getTask(long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public long createTask(Task task) throws ManagerTaskException {
        long id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) throws ManagerTaskException {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(long id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public long createSubtask(Subtask subtask) throws ManagerTaskException {
        long id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerTaskException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(long id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Epic getEpic(long id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public long createEpic(Epic epic) throws ManagerTaskException {
        long id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerTaskException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(long id) {
        super.removeEpic(id);
        save();
    }

    private Task getTaskUniversal(long id) {
        Task task = null;
        if (tasks.containsKey(id)) {
            task = tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            task = subtasks.get(id);
        } else if (epics.containsKey(id)) {
            task = epics.get(id);
        }
        return task;
    }
}
