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

    @Override
    public String toString() {
        return this.name+" ("+this.id+")";
    }
    
    private String name;
    private MyList<Road> roadsOut;
    private MyList<Road> roadsIn;
    private int id;
    private int capacity;
    private int currentPop;
    
    // waiting list is a queue of Gnomes waiting to get into Village.
    // it will be prioritized by VIP level. It will be empty until the
    // Village reaches capacity, at which point any new Gnomes wanting to
    // get in will be put in the q to wait until there's room.
    private RankedQueue<Gnome> waitingList;

    public Village(String name, int theID) {
        this.name = name;
        this.roadsOut = new MyList<Road>();
        this.roadsIn = new MyList<Road>();
        this.id = theID;
        this.currentPop = 0;
        this.capacity = 2;
        waitingList = new RankedQueue<Gnome>();
    }
    
    //constructor that allows setting capacity
    public Village(String name, int theCapacity, int theID) {
    	this(name, theID);
    	this.capacity = theCapacity;
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

	public void removeRoadOut(Road removeMe) {
		for (int i=0; i<roadsOut.getSize(); i++) {
			if (roadsOut.get(i).equals(removeMe) ){
				roadsOut.remove(i);
			}
		}
	}

	public void removeRoadIn(Road removeMe) {
		for (int i=0; i<roadsIn.getSize(); i++) {
			if (roadsIn.get(i).equals(removeMe) ){
				roadsIn.remove(i);
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

    // add g to our village
    public synchronized void addOccupant(Gnome g) {
        this.currentPop++;
        g.setInVillage(this);
    }

    // currently ignores g and just decreases population
    public synchronized void removeOccupant(Gnome g) {
    	currentPop--;
    	if (waitingList.getSize() > 0) {
    		Gnome firstInLine = waitingList.remove();
    		addOccupant(firstInLine);
    	}
    }
    
    // this is how a Gnome requests entry to the Village.
    // if the Village has available capacity, Gnome
    // is immediately put in Vilalge. If the Village
    // is full, Gnome is added to the VIP-priority
    // waiting list. 
    // Returns the waiting list size at the end of
    // the operation. 0 would mean the Gnome got in
    // the Village immediately; >0 tells what position
    // the Gnome is in the waiting list.
    public int requestEntry(Gnome g) throws InterruptedException {
    	int positionInQueue = 0;
    	
    	// easy case is when there is room
    	if (currentPop < capacity) {
    		addOccupant(g);
    	} else {
    		// if Village is full, put g in waiting list.
    		// when g gets to front of q, we put it in Vilalge.
    		// That happens in 
    		positionInQueue = waitingList.add(g, g.getVIPLevel() + 1);
    	}
    	
    	return positionInQueue;
    }

    // tell what position the current Gnome is in the waiting
    // list. first in line is 1. 0 means not in waiting list.
    public int getPositionInQueue(Gnome g) {
    	return waitingList.getPosition(g) + 1;
    }
    
    
}
