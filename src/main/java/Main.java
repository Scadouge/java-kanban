import java.io.IOException;
import java.net.URI;

public class Main {

    public static void main(String[] args) throws IOException {
        URI uri = URI.create("http://localhost:8078");

//        KVTaskClient kvTaskClient = new KVTaskClient(uri);
//        String value = "{\"TASKS\":[{\"id\":0,\"type\":\"TASK\",\"name\":\"Task 1\",\"status\":\"NEW\",\"duration\":0},{\"id\":1,\"type\":\"TASK\",\"name\":\"Task 2\",\"status\":\"NEW\",\"duration\":0}],\"SUBTASKS\":[{\"epicId\":2,\"id\":3,\"type\":\"SUBTASK\",\"name\":\"Subtask 1\",\"status\":\"NEW\",\"duration\":0}],\"EPICS\":[{\"subtaskIds\":[3],\"id\":2,\"type\":\"EPIC\",\"name\":\"Epic 1\",\"status\":\"NEW\",\"duration\":0}],\"HISTORY\":[3,2,0,1]}";
//        kvTaskClient.put("tasks", value);


    }
}
