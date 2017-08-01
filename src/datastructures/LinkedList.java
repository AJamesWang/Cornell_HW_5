package datastructures;

import java.util.Iterator;

//basically just copied over from HW_3
//changes: removed stack, changed private to protected, modified getNode so (index<0)=>head
public class LinkedList<T> /* implements */ /* Queue<T>, List<T>, Iterable<T> */ {
    protected ListNode<T> head;
    protected int length;

    public LinkedList() {
        head = new ListNode<T>();
        head.setNext(head);
        head.setPrev(head);
        length = 0;
    }

    public int getLength() {
        return this.length;
    }

    @Override
    public String toString() {
        String out = "";
        ListNode<T> cur = head.getNext();
        while (cur != head) {
            out = out + cur.getElem().toString() + ", ";
            cur = cur.getNext();
        }
        out = out.substring(0, out.length()==0?0:out.length()-2);
        return out;
    }

    /* Queue */

    public void qPush(T elem) {
        ListNode<T> newNode = new ListNode<T>();
        newNode.setElem(elem);
        newNode.setPrev(head.getPrev());
        newNode.setNext(head);

        head.getPrev().setNext(newNode);
        head.setPrev(newNode);
        length++;
    }

    public T qPop() /* throws ListEmptyException */ {
        if (length == 0) {
            throw new IndexOutOfBoundsException();
        }
        ListNode<T> out = head.getNext();
        head.setNext(out.getNext());
        head.getNext().setPrev(head);

        length--;
        return out.getElem();
    }

    public T qPeek() /* throws ListEmptyException */ {
        if (this.length == 0) {
            throw new IndexOutOfBoundsException();
        }
        return head.getNext().getElem();
    }

    /* List */

    public void add(T elem) {
        qPush(elem);
    }

    protected ListNode<T> getNode(int i) throws IndexOutOfBoundsException {
        if (i > this.length) {
            throw new IndexOutOfBoundsException();
        }
        int count = -1;
        ListNode<T> cur = head;
        while (count < i) {
            cur = cur.getNext();
            count++;
        }

        return cur;
    }

    public T get(int i) throws IndexOutOfBoundsException {
        return this.getNode(i).getElem();
    }

    public int getIndex(T elem) {
        ListNode<T> cur = head.getNext();
        int index = 0;
        while (cur != head) {
            if (cur.getElem().equals(elem)) {
                return index;
            }
            index++;
            cur = cur.getNext();
        }
        return -1;
    }

    public T set(int i, T elem) throws IndexOutOfBoundsException {
        ListNode<T> node = this.getNode(i);
        T out = node.getElem();
        node.setElem(elem);
        return out;
    }

    public T remove(int i) throws IndexOutOfBoundsException {
        ListNode<T> node = this.getNode(i);
        ListNode<T> prev = node.getPrev();
        ListNode<T> next = node.getNext();
        prev.setNext(next);
        next.setPrev(prev);
        this.length--;
        return node.getElem();
    }

    public Object[] toArray() {
        ListNode<T> cur = head.getNext();
        Object[] out = new Object[length];
        int index = 0;
        while (cur != head) {
            out[index] = cur.getElem();
            cur = cur.getNext();
            index++;
        }
        return out;
    }

    public ListIterator iterator() {
        return new ListIterator();
    }

    class ListIterator implements Iterator<T> {
        ListNode<T> cur;

        public ListIterator() {
            this.cur = head.getNext();
        }

        @Override
        public boolean hasNext() {
            return this.cur == head;
        }

        @Override
        public T next() {
            T out = cur.getElem();
            cur = cur.getNext();
            return out;
        }

    }
}