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
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkFirst(Task task) {
        Node<Task> oldHead = head;

        head = new Node<>(null, task, oldHead);
        if (oldHead == null) {
            tail = head;
        } else {
            oldHead.setPrev(head);
        }
    }

    private void linkLast(Task task) {
        Node<Task> oldTail = tail;

        tail = new Node<>(oldTail, task, null);
        if (oldTail == null) {
            head = tail;
        } else {
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
        } else if (head == node) {
            head = next;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.getData());
            node = node.getNext();
        }
        return tasks;
    }


    private static class Node<T> {
        private T data;
        private Node<T> prev;
        private Node<T> next;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }

        public T getData() {
            return data;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setData(T data) {
            this.data = data;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }
}

