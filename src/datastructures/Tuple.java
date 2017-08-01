package datastructures;

public class Tuple<A, B> {
    private A a;
    private B b;
    private boolean compareByA;

    public Tuple() {
        compareByA = true;
    }

    public Tuple(A a, B b) {
        this();
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public void setCompareByA(boolean compareByA) {
        this.compareByA = compareByA;
    }

    public boolean equals(Tuple<A, B> tuple) {
        if (compareByA) {
            return this.getA().equals(tuple.getA());
        } else {
            return this.getB().equals(tuple.getB());
        }
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass() == this.getClass() && this.equals((Tuple<A, B>) o);

    }

    @Override
    public String toString() {
        return this.a.toString() + ":" + this.b.toString();
    }
}
