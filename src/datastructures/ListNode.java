package datastructures;

public class ListNode<T> {
    private T elem;
    private ListNode<T> prev;
    private ListNode<T> next;

    public ListNode() {
    }

    public ListNode(T elem) {
        this.elem = elem;
    }

    public void setElem(T elem) {
        this.elem = elem;
    }

    public T getElem() {
        return this.elem;
    }

    public void setPrev(ListNode<T> prev) {
        this.prev = prev;
    }

    public ListNode<T> getPrev() {
        return this.prev;
    }

    public void setNext(ListNode<T> next) {
        this.next = next;
    }

    public ListNode<T> getNext() {
        return this.next;
    }
}