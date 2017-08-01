package FP;

import javax.swing.ImageIcon;

public class Village {
    // @modified added variables
    public static ImageIcon ICON = new ImageIcon("button.png");
    public static int DIAMETER = ICON.getIconWidth();
    private static int location = 0;
    private int x;
    private int y;

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return this.name;
    }
    
    private String name;
    private MyList<Road> roadsOut;
    private MyList<Road> roadsIn;
    private int id;
    private int capacity;
    private int currentPop;

    public Village(String name, int theID) {
        this.name = name;
        this.roadsOut = new MyList<Road>();
        this.roadsIn = new MyList<Road>();
        this.id = theID;
        this.currentPop = 0;
        this.capacity = 2;
    }

    public void addRoadOut(Road newRoad) {
        roadsOut.add(newRoad);
    }

    public void addRoadIn(Road newRoad) {
        roadsIn.add(newRoad);
    };

    public String getName() {
        return this.name;
    }

    public MyList<Road> getRoadsOut() {
        return roadsOut;
    }

    public MyList<Road> getRoadsIn() {
        return roadsIn;
    }

    // removes the road form roadsOut with the given id
    //@modified: replaced remove w/ set_null, was causing bugs when removing villages
    //@modified: now checks if roads.get(i) is null
    public void removeRoadOut(Road removeMe) {
        for (int i = 0; i < roadsOut.getSize(); i++) {
            if (roadsOut.get(i)!=null && roadsOut.get(i).equals(removeMe)) {
                roadsOut.set_null(i);
            }
        }
    }

    public void removeRoadIn(Road removeMe) {
        for (int i = 0; i < roadsIn.getSize(); i++) {
            if (roadsIn.get(i)!=null && roadsIn.get(i).equals(removeMe)) {
                roadsIn.set_null(i);
            }
        }

    }

    public int getID() {
        return id;

    }

    public boolean isFull() {
        return (currentPop >= capacity);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void addOccupant() {
        this.currentPop++;
    }

    public void removeOccupant() {
        this.currentPop--;
    }
}
