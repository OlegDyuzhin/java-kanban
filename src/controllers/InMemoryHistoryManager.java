package controllers;

import model.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedHashMap<Task> historyList = new CustomLinkedHashMap<>();


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


    private static class CustomLinkedHashMap<E extends Task> {
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

        private static class Node<E> {
            private final E item;
            private Node<E> next;
            private Node<E> prev;

            public Node(Node<E> prev, E element, Node<E> next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }

            public Node<E> getNext() {
                return next;
            }

            public void setNext(Node<E> next) {
                this.next = next;
            }

            public Node<E> getPrev() {
                return prev;
            }

            public void setPrev(Node<E> prev) {
                this.prev = prev;
            }

            public E getItem() {
                return item;
            }
        }

    }
}