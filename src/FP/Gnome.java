package FP;

// class for basic info about a Gnome.
// we keep track of what village it's in, or what road it's on.
// a new Gnome is neither in a village nor on a road when created, but
// When traveling, will be either in a village OR on a road. If the Gnome
// is currently in a Village, onRoad will be null, and vice-versa.
public class Gnome {
    private int id;
    private String name;
    private String favColor;
    private int vipLevel; // 0 for commoners, higher values for fancier people
    private Village inVillage; // village he's in, or null if not in a village
    private Road onRoad; // Road he's on, or null if not on a road
    private MyList<Village> villageHistory; // keeps track of villages visited
    

    // normal case constructor
    public Gnome(String theName, String theFavColor, int theVIPLevel, int theID) {
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
    
    public String getFavColor() {
        return this.favColor;
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
    
    // this is set to a village when you arrive at a village. or null
    // if you are on a road. collect a list of all villages visited, but
    // don't include the nulls.
    public void setCurrentVillage(Village newVillage) {
        this.inVillage = newVillage;
        if (newVillage != null) this.villageHistory.addIfNew(newVillage);
    }
    
    // return current Road if on a Road, null if not on a Road now.
    public Road getCurrentRoad() {
        return this.onRoad;
    }
    
    public void setCurrentRoad(Road newRoad) {
        this.onRoad = newRoad;
    }
    
    public MyList<Village> getVillageHistory() {
        return this.villageHistory;
    }
    
}

