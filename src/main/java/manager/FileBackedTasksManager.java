package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String TABLE_HEADER = "id,type,name,status,description,epic" + System.lineSeparator();
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
        //0  1    2    3      4           5
        //id,type,name,status,description,epic
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
        //id,type,name,status,description,epic
        String str = String.join(",", String.valueOf(task.getId()), task.getType().toString(),
                task.getName(), task.getStatus().toString(), task.getDescription(), "");
        if (task.getType() == TaskType.SUBTASK) {
            str = str + ((Subtask) task).getEpicId();
        }
        return str;
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
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
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
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
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
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
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

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(Path.of("save.CSV"));

        Task task1 = new Task();
        task1.setName("task1");
        taskManager.createTask(task1);

        Task task2 = new Task();
        task2.setName("task2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic();
        epic1.setName("epic1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask(epic1.getId());
        subtask1.setName("subtask1");
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(epic1.getId());
        subtask2.setName("subtask2");
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic();
        epic2.setName("epic2");
        taskManager.createEpic(epic2);

        // History
        taskManager.getTask(task1.getId());

        taskManager.getSubtask(subtask2.getId());

        taskManager.getEpic(epic2.getId());

        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask1.getId());

        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask2.getId());

        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask1.getId());

        taskManager.getTasks().forEach(System.out::println);
        taskManager.getEpics().forEach(System.out::println);
        taskManager.getSubtasks().forEach(System.out::println);

        System.out.println("---------------------------------");
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("---------------------------------");

        System.out.println();
    }
}
