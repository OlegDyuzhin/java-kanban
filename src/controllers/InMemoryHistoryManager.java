package controllers;

import model.tasks.Task;
import model.util.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final customLinkedHashMap<Task> historyList = new customLinkedHashMap<>();


    @Override
    public void add(Task task) {
        historyList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(historyList.getNodeById(id));
    }

    @Override
    public List<Task> getHistoryTask() {
        return historyList.getTasks();
    }


    private static class customLinkedHashMap<E extends Task> {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private final Map<Integer, Node<E>> historyMapTask = new HashMap<>();
        private Node<E> head;
        private Node<E> tail;

        private Node<E> getNodeById(int id) {
            if (!historyMapTask.containsKey(id)) return null;
            return historyMapTask.get(id);
        }

        private void linkLast(E task) {
            if (historyMapTask.containsKey(task.getId())) removeNode(historyMapTask.get(task.getId()));

            final Node<E> l = tail;
            final Node<E> newNode = new Node<>(tail, task, null);
            tail = newNode;
            if (l == null) {
                head = newNode;
            } else {
                l.setNext(newNode);
            }
            historyMapTask.put(task.getId(), newNode);
        }

        private List<Task> getTasks() {
            List<Task> returnTask = new ArrayList<>();
            for (Node<E> x = head; x != null; x = x.getNext()) {
                returnTask.add(x.getItem());
            }
            return returnTask;
        }

        private void removeNode(Node<E> node) {
            if (head != null && node != null) {
                historyMapTask.remove(node.getItem().getId());
                if (tail != node && head != node) {
                    node.getNext().setPrev(node.getPrev());
                    node.getPrev().setNext(node.getNext());
                }
                if (node.getNext() == node.getPrev()) {
                    head = null;
                    tail = null;
                }
                if (head == node) {
                    head = node.getNext();
                    node.getNext().setPrev(null);
                }
                if (tail == node) {
                    tail = node.getPrev();
                    node.getPrev().setNext(null);
                }
            }

        }
    }
}