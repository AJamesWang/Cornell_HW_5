package FP;

import java.awt.Color;

// class for basic info about a Gnome.
// we keep track of what village it's in, or what road it's on.
// a new Gnome is neither in a village nor on a road when created, but
// When traveling, will be either in a village OR on a road. If the Gnome
// is currently in a Village, onRoad will be null, and vice-versa.
public class Gnome implements Comparable {
    /*GUI*/
    public static int MAX_SIZE=6;
    public static int MIN_SIZE=2;

    public String toString(){
        return this.name+" ("+this.id+")";
    }
    
    static class StringRange{
        String expression;
        public StringRange(String expression){
            this.expression=expression;
        }

        public boolean contains(String s){
            return s.contains(expression);
        }
    }

    static class ColorRange{
        Color color;
        public ColorRange(Color color){
            this.color=color;
        }

        public boolean contains(Color color){
            return this.color==null || this.color.equals(color);
        }
    }

    static class NumberRange{
        Integer low;
        Integer high;

        public NumberRange(Integer low, Integer high){
            this.low=low;
            this.high=high;
        }

        public boolean contains(int i){
            return (low==null||((int)low)<=i) && (high==null || ((int)high)>=i);
        }
    }

    /*datastructure*/
    private int id;
    private String name;
    private Color favColor; //@modified changed color from String to Color object
    private int vipLevel; // 0 for commoners, higher values for fancier people
    private Village inVillage; // village he's in, or null if not in a village
    private Road onRoad; // Road he's on, or null if not on a road
    private RoadTrip roadTrip;
    private MyList<Village> villageHistory; // keeps track of villages visited
    

    // normal case constructor
    public Gnome(String theName, Color theFavColor, int theVIPLevel, int theID) {
        this.id= theID;
        this.name = theName;
        this.favColor = theFavColor;
        this.vipLevel = theVIPLevel;
        this.onRoad = null; // not on any road yet
        this.inVillage = null;  // not in any village yet
        this.villageHistory = new MyList<Village>(); // none visited yet
    }
    
    public int getID() {
        return this.id;
    }
        
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public Color getFavColor() {
        return this.favColor;
    }
    
    public void setFavColor(Color c) {
    	this.favColor = c;
    }
    
    public int getVIPLevel() {
        return this.vipLevel;
    }
    public void setVIPLevel(int newVIPLevel) {
        this.vipLevel = newVIPLevel;
    }
    
    // return current Village. Will be null if Gnome is not in a Village now
    public Village getCurrentVillage() {
        return this.inVillage;
    }
    
        
    // return current Road if on a Road, null if not on a Road now.
    public Road getCurrentRoad() {
        return this.onRoad;
    }
    
    // set the onRoad value to be this Road.
    // set inVillage to be null.
    // do nothing if passed a null argument.
    // 
    public synchronized void setOnRoad(Road newRoad)  {
    	if (newRoad != null) {
    		onRoad = newRoad;
    		synchronized (this) { this.notifyAll(); }
    		if (inVillage != null) {
    			inVillage.removeOccupant(this);
    			inVillage = null;
    		}
    		synchronized (this) { this.notifyAll(); }
        }
    }

    public RoadTrip getCurRoadTrip(){
        return this.roadTrip;
    }

    // tried to make this work safely...
    // kills any current Road trip. If Gnome is in a village,
    // starts new RoadTrip from there to new destination.
    // If Gnome is on a road, we force the Gnome into 
    // the Village at the end of the Road, a and start
    // the new RoadTrip from there. 
    // if he's on neither a Road or Village... not supposed to
    // happen, but put an error message in just in case.
    //
    public void setNewRoadTrip(Map map, Village newDest, int newMode) {
    	
    	// stop current RoadTrip thread if we have one
    	if (roadTrip != null) {
    		roadTrip.interrupt();
    	}
    	
    	// now put us on a new trip. 
    	// force Gnome to end of current Road if it's on a Road.
    	// we don't care if others are waiting to get in, or even
    	// if we temporarily put the population over capacity--we just
    	// jump right into the Village.
    	//
    	if (onRoad != null) {
    		setInVillage(map.getVillage(onRoad.getToID()));
    	}
    	
    	// now we should be in a Village, but let's be careful
        if (inVillage != null) {
            roadTrip = new RoadTrip(map, this, this.inVillage, newDest, newMode);
            //roadTrip.start(); add it back in if u want it to start here
        } else {
            // this should never happen, but just in case...
        	System.out.println("Can't start new trip for " + name + 
        			". Cannot locate in Village or on Road.");
        }
    }

public MyList<Village> getVillageHistory() {
    return this.villageHistory;
}
    
    // set the inVillage value to be this Village.
    // set onRoad to be null.
    // do nothing if passed a null argument.
    // 
    public synchronized void setInVillage(Village newVillage) {
    	if (newVillage != null) {
    		inVillage = newVillage;
    		villageHistory.addIfNew(newVillage);
    		synchronized (this) { this.notifyAll(); }
    		if (onRoad != null) {
    			onRoad.removeOccupant(this);
    			onRoad = null;
    		}
    		synchronized (this) { this.notifyAll(); }
        }
    }
    
    public int compareTo(Object o) {
    	int returnThis = 0;
    	
    	if(o.getClass() == String.class) {
    		String s = (String) o;
    		returnThis = (this.getName().compareTo(s));
    	}
    	else if (o.getClass() == Color.class) {
    		Integer i = ((Color) o).getRGB();
    		returnThis = ((Integer)(this.getFavColor().getRGB())).compareTo(i);
    	}
    	else {
    		Integer i = (Integer) o;
    		returnThis = ((Integer)this.getVIPLevel()).compareTo(i);
    	}
    	
    	return returnThis;
    }

}

