package FP;

import java.awt.Color;

// class for basic info about a Gnome.
// we keep track of what village it's in, or what road it's on.
// a new Gnome is neither in a village nor on a road when created, but
// When traveling, will be either in a village OR on a road. If the Gnome
// is currently in a Village, onRoad will be null, and vice-versa.
public class Gnome {
    /*GUI*/
    public static int MAX_SIZE=6;
    public static int MIN_SIZE=2;
    
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
    
    public void setCurrentVillage(Village currentVillage) {
    	this.inVillage = currentVillage;
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
    		if (inVillage != null) {
    			inVillage.removeOccupant(this);
    			inVillage = null;
    		}
        }
    }

    public RoadTrip getCurRoadTrip(){
        return this.roadTrip;
    }

    public void setNewRoadTrip(Map map, Village destination, int mode){
        if (this.inVillage!=null){
            this.roadTrip=new RoadTrip(map, this, this.inVillage, destination, mode);
        }
        else {
            //@todo: delete current roadTrip
            //teleports gnome to end of current road
            this.inVillage=map.getVillage(this.onRoad.getToID());
            this.roadTrip=new RoadTrip(map, this, this.inVillage, destination, mode);
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
    		if (onRoad != null) {
    			onRoad.removeOccupant(this);
    			onRoad = null;
    		}
        }
    }
    
    public MyList<Village> getVillageHistory() {
        return this.villageHistory;
    }
   
}

