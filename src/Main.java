import manager.FileBackedTasksManager;
import manager.TaskManager;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        test();
    }

    private static void test() throws IOException {
        TaskManager taskManager = FileBackedTasksManager.loadFromFile(new File("save.CSV"));

        taskManager.getEpics().forEach(System.out::println);
        taskManager.getSubtasks().forEach(System.out::println);
        taskManager.getTasks().forEach(System.out::println);
        System.out.println("---------------------------------");
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("---------------------------------");
    }
}
