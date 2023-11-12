import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(taskManager.generateId());
        taskManager.createTask(task1);

        Epic epic1 = new Epic(taskManager.generateId());
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask(taskManager.generateId(), Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1, epic1);
        Subtask subtask2 = new Subtask(taskManager.generateId(), Status.DONE);
        taskManager.createSubtask(subtask2, epic1);

//        taskManager.removeSubtask(subtask1.getId());

        Epic epic2 = new Epic(epic1.getId());
        epic2.setName("Epic 2 new name");
        taskManager.updateEpic(epic2);

        Subtask subtask3 = new Subtask(subtask1.getId(), Status.DONE);
        subtask3.setName("Subtask 3 new name");
        taskManager.updateSubtask(subtask3);

//        taskManager.clearEpics();
//        taskManager.clearSubtasks();

        taskManager.getTasks().forEach(System.out::println);
        taskManager.getEpics().forEach(System.out::println);
        taskManager.getSubtasks().forEach(System.out::println);
    }
}
