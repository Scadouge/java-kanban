import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task(taskManager.getNewId()));
        task1.setName("Task 1");

        Epic epic1 = (Epic) taskManager.createTask(new Epic(taskManager.getNewId()));
        epic1.setName("Epic 1");

        Subtask subtask1 = (Subtask) taskManager.createTask(new Subtask(taskManager.getNewId(), Status.DONE));
        subtask1.setName("Subtask 1");

        Subtask subtask2 = (Subtask) taskManager.createTask(new Subtask(taskManager.getNewId(), Status.IN_PROGRESS));
        subtask2.setName("Subtask 2");

        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);

        taskManager.getTasks().forEach(System.out::println);
        System.out.println();

        Subtask subtask3 = new Subtask(subtask2.getId(), Status.DONE); // обновление старого subtask новым объектом
        subtask3.setName("Subtask 3");
        taskManager.updateTask(subtask3);

        taskManager.getTasks().forEach(System.out::println);
        System.out.println();

        Epic epic2 = new Epic(epic1.getId()); // обновление старого epic новым объектом
        epic2.setName("Epic 1 new name");
        epic2.setDescription("new desc");
        taskManager.updateTask(epic2);

//        List<Epic> tasks = taskManager.getTasks(TaskType.EPIC).stream().map(t -> (Epic) t).collect(Collectors.toList());
//        taskManager.clearTasks(TaskType.EPIC);

        taskManager.getTasks().forEach(System.out::println);
        System.out.println();
        taskManager.clearTasks(TaskType.EPIC);
        taskManager.getTasks().forEach(System.out::println);
    }
}
