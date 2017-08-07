package FP;

public class Road implements Comparable<Road>{
    private int         fromID; // the ID# of the origination node
    private int         toID;   // the ID# of the destination node
    private int         weight; // weight assigned to edge
    private int         id;// unique id for this road
    private int 		currentPop;
    private int 		capacity;
    
    // waiting list is a queue of Gnomes waiting to get into this Road.
    // it will be prioritized by VIP level. It will be empty until the
    // Road reaches capacity, at which point any new Gnomes wanting to
    // get on this road will be put in the q to wait until there's room
    // on the Road.
    private RankedQueue<Gnome> waitingList;
    
    public Road(int fromThisNodeID, int toThisNodeID, int withThisWeight, int theID){
        this.fromID = fromThisNodeID;
        this.toID = toThisNodeID;
        this.weight = withThisWeight;
        this.id = theID;
        this.currentPop = 0;
        this.capacity=2;
        waitingList = new RankedQueue<Gnome>();
    }
    
    // constructor that allows specifying capacity
    public Road(int fromThisNodeID, int toThisNodeID, int withThisWeight, 
    		int withThisCapacity, int theID) {
    	this(fromThisNodeID, toThisNodeID, withThisWeight, theID);
    	this.capacity = withThisCapacity;
    }
    
    public int getFromID() {
        return fromID;
    }
    
    public int getToID() {
        return toID;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public int getID() {
        return this.id;
    }
    
    public boolean isFull() {
        return (currentPop >= capacity);
    }
    
    public int getCurrentPop() {
    	return this.currentPop;
    }
    
    public int getCapacity() {
    	return this.capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    // put g into our road's population if not 
    // already here. adding same Gnome twice will
    // muck up population count.
    public synchronized void addOccupant(Gnome g) {
    	if (g.getCurrentRoad() != this) {
    		this.currentPop++;
    		g.setOnRoad(this);
    	}
    }

    // someone is leaving road. if there's a queue,
    // put the first person in q on the road. otherwise
    // just decreases population.
    public synchronized void removeOccupant(Gnome g) {
    	currentPop--;
    	if (waitingList.getSize() > 0) {
    		Gnome firstInLine = waitingList.remove();
    		addOccupant(firstInLine);
    	}
    }
    
    // this is how a Gnome requests entry to the Road.
    // if the Road has available capacity, Gnome
    // is immediately put on the Road. If the Road
    // is full, Gnome is added to the VIP-priority
    // waiting list. 
    // Returns the waiting list size at the end of
    // the operation. 0 would mean the Gnome got on
    // the road immediately; >0 tells what position
    // the Gnome is in the waiting list.
    public int requestEntry(Gnome g) throws InterruptedException {
    	int positionInQueue = 0;
    	
    	// easy case is when there is room on Road
    	if (currentPop < capacity) {
    		addOccupant(g);
 
    	} else {
    		// if Road is full, put g in waiting list.
    		// when g gets to front of q, we put it on this Road.
    		positionInQueue = waitingList.add(g, g.getVIPLevel()) + 1;
    	}
    	
    	return positionInQueue;
    }

    // tell what position the current Gnome is in the waiting
    // list. first in line is 1. 0 means not in waiting list.
    public int getPositionInQueue(Gnome g) {
    	return waitingList.getPosition(g) + 1;
    }
    
    @Override
    public int compareTo(Road road){
        return this.getWeight()-road.getWeight();
    }

    public String toString(){
        return "Vil "+this.getFromID()+" (Toll "+this.weight+")";
    }

}
