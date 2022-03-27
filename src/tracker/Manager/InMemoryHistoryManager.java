package tracker.Manager;

import tracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    protected Map<Integer, Node> history = new HashMap<Integer, Node>();

    private Node head;
    private Node tail;

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node node) {
        Node nextNode = node.next;
        Node prevNode = node.prev;

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

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        linkLast(task);
        history.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }
}

class Node {
    protected Task data;
    protected Node next;
    protected Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}




