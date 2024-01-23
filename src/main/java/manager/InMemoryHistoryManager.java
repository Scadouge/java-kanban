package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
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
    public List<Long> getHistory() {
        return getTasks();
    }

    private void linkFirst(Task task) {
        Node<Task> oldHead = head;

        head = new Node<>(null, task, oldHead);
        if (oldHead == null) {
            tail = head;
        } else {
            oldHead.prev = head;
        }
    }

    private void linkLast(Task task) {
        Node<Task> oldTail = tail;

        tail = new Node<>(oldTail, task, null);
        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = tail;
        }
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.data = null;
    }

    private List<Long> getTasks() {
        List<Long> tasks = new ArrayList<>();

        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.data.getId());
            node = node.next;
        }
        return tasks;
    }

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}

