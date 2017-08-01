package FP;

public class Road {
    private int         fromID; // the ID# of the origination node
    private int         toID;   // the ID# of the destination node
    private int         weight; // weight assigned to edge
    private int         id;// unique id for this road
    private int currentPop;
    private int capacity;
    
    public Road(int fromThisNodeID, int toThisNodeID, int withThisWeight, int theID) {
        this.fromID = fromThisNodeID;
        this.toID = toThisNodeID;
        this.weight = withThisWeight;
        this.id = theID;
        this.currentPop = 0;
        this.capacity=2;
        NextID++;
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
        return (currentPop>=capacity);
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

