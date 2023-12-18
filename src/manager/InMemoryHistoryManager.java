package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> tail;
    private final HashMap<Long, Node<Task>> taskHistory;

    public InMemoryHistoryManager() {
        this.taskHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.containsKey(task.getId())) {
                removeNode(taskHistory.get(task.getId()));
            }
            linkLast(task);
            taskHistory.put(task.getId(), tail);
        }
    }

    @Override
    public void remove(long id) {
        if (taskHistory.containsKey(id)) {
            removeNode(taskHistory.get(id));
        }
        taskHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node<Task> oldTail = tail;

        tail = new Node<>(oldTail, task, null);
        if (oldTail != null) {
            oldTail.setNext(tail);
        }
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.getPrev();
        Node<Task> next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        }
        if (next != null) {
            next.setPrev(prev);
        }
        if (tail == node) {
            tail = prev;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        Node<Task> node = tail;
        while (node != null) {
            tasks.add(node.getData());
            node = node.getPrev();
        }
        return tasks;
    }
}
