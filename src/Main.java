import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task();
        taskManager.createTask(task1);

        Epic epic1 = new Epic();
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask(epic1.getId());
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(epic1.getId());
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);

        printAllTasks(taskManager);

        Epic updateEpic1 = new Epic();
        updateEpic1.setId(epic1.getId());
        updateEpic1.setName("Epic 1 new name");
        taskManager.updateEpic(updateEpic1);

        printAllTasks(taskManager);

        Subtask updateSubtask1 = new Subtask(epic1.getId());
        updateSubtask1.setId(subtask1.getId());
        updateSubtask1.setName("Subtask 1 new name");
        updateSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(updateSubtask1);

        printAllTasks(taskManager);

        taskManager.clearEpics();

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        taskManager.getTasks().forEach(System.out::println);
        taskManager.getEpics().forEach(System.out::println);
        taskManager.getSubtasks().forEach(System.out::println);
        System.out.println("================================================");
    }
}
