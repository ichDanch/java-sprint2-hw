package tracker.controller;

import tracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager<T> implements HistoryManager {

    private Map<Long, Node> history = new HashMap<>();

    private Node<T> head;
    private Node<T> tail;

    private void linkLast(Task task) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node node) {
        Node<T> nextNode = node.next;
        Node<T> prevNode = node.prev;

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }
    }

    class Node<E> {
        private Task data;
        private Node<E> next;
        private Node<E> prev;

        public Node(Node<E> prev, Task data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        linkLast(task);
        history.put(task.getId(), tail);
    }

    @Override
    public void remove(long id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<T> node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }
}





