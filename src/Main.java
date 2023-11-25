import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        test();
    }

    private static void test() {
        TaskManager taskManager = Managers.getDefault();

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

        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            if(random.nextBoolean())
                taskManager.getEpic(epic1.getId());
            else
                taskManager.getTask(task1.getId());
            taskManager.getHistory().forEach(System.out::println);
            System.out.println("---------------------------------");
        }
    }

    private static void printAllTasks(TaskManager taskManager) {
        taskManager.getTasks().forEach(System.out::println);
        taskManager.getEpics().forEach(System.out::println);
        taskManager.getSubtasks().forEach(System.out::println);
    }
}
