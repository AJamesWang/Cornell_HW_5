package FP;

import java.util.Random;

// This represents a trip for a gnome on a given map.
// It runs as a thread that advances the gnome around the map
// to get from a given start Village to a given destination Village.
// Progress is determined by the travel mode (lazy vs efficient)
// Lazy mode trips just go from town to town at random until destination.
// Efficient mode follows lowest cost path to destination. (not done yet).
// There is a maximum allowable trip cost. if you don't get to your
// destination within the max allowable cost, you are out of gas!
// 
// 
public class RoadTrip extends Thread {
	// use these to specify the travel mode
	public static final int LAZY_MODE = 0;
	public static final int EFFICIENT_MODE = 1;
	public static final int MAX_TRIP_COST = 100; // if you don't get to dest
	// in <= this cost, give up.

	// a RoadTrip object's attributes
	private Map map; // the map we're traveling on
	private Gnome traveler; 
	private int mode; // lazy or efficient
	private Village startVillage;
	private Village destVillage;
	private MyList<Road> bestPath; // this will be calculated shortest path

	private int roadsUsedSoFar;	// keep a running total while traveling
	private int totalCost; // keep a running total while traveling
	private long startTime; // use this to calculate total travel time at end

	// create a new RoadTrip. 
	public RoadTrip(Map theMap, Gnome theTraveler, 
			Village start, Village destination, int theMode) {
		this.map = theMap;
		this.traveler = theTraveler;
		this.mode = theMode;
		this.startVillage = start;
		this.destVillage = destination;
		roadsUsedSoFar = 0;
		totalCost = 0;
		if (this.mode == EFFICIENT_MODE) {
			bestPath = map.shortestPath(startVillage, destVillage);
		} else {
			bestPath = null;
		}
	}
	
	//@modified
	public Village getDestination(){
		return this.destVillage;
	}

	public long getTravelTime(){
		return (System.currentTimeMillis() - startTime)/1000;
	}

	public int getMode() {
		return this.mode;
	}

	public double getProgress(){
		if(bestPath==null){
			return -1;
		} else{
			double travelled=0;
			for(int i=0;i<roadsUsedSoFar; i++){
				travelled+=bestPath.get(i).getWeight();
			}
			double total=0;
			for(int i=0;i<bestPath.getSize(); i++){
				total+=bestPath.get(i).getWeight();
			}
			return travelled/total;
		}
	}
	
	// will only set the mode if the input is a legitimate value.
	// returns the mode at the end, whether it was changed or not.
	// don't use this once a RoadTrip has started!
	public int setMode(int newMode) {
		if (newMode == LAZY_MODE || newMode == EFFICIENT_MODE) {
			this.mode = newMode;
		}
		if (newMode == EFFICIENT_MODE) {
			bestPath = map.shortestPath(startVillage, destVillage);
		}
		
		return this.mode;
	}

	@Override
	public void run() {
		try {
			// print out a starting message, but just quit if we already know
			// there's no path to the destination from our shortestpath search
			if (mode == EFFICIENT_MODE && bestPath == null) {
				System.out.println("**EFFICIENT Road Trip Fail: " + 
						traveler.getName() + 
						" wanted to go from " + startVillage.getName() + 
						" to " + destVillage.getName() +
						" but there is no path there!");
				return;
			} else if (mode == EFFICIENT_MODE) {
				System.out.println("**Starting EFFICIENT Road Trip: " 
						+ traveler.getName() + 
						" is going from " + startVillage.getName() + 
						" to " + destVillage.getName() +
						". Should take " + bestPath.getSize() + " steps.");
			} else {
				System.out.println("**Starting LAZY Road Trip: " + 
						traveler.getName() + 
						" is going from " + startVillage.getName() + 
						" to " + destVillage.getName() +
						". Good luck!");
			}

			Random rn = new Random(); // used for lazy mode
			Road roadToTake; // this will be the next road to take
			Village nextVillage; // this will be the next village we head to
			startTime = System.currentTimeMillis();

			// traveler starts out in the specified village; is not on a road yet
			putGnomeInVillage(traveler, startVillage);

			// the trip continues until we have arrived at destination
			while (traveler.getCurrentVillage() != destVillage && 
					totalCost < MAX_TRIP_COST) {
				
				// next road to take depends on what travel mode we're in.
				// LAZY gets a random road out. EFFICIENT gets next road
				// in the pre-calculated shortest path.
				if (mode == EFFICIENT_MODE) {
					roadToTake = bestPath.get(roadsUsedSoFar);
				} else {
					MyList<Road> roadsOut = traveler.getCurrentVillage().getRoadsOut();
					int randomIndex = rn.nextInt(roadsOut.getSize());
					roadToTake = roadsOut.get(randomIndex);
				}

				nextVillage = map.getVillage(roadToTake.getToID());

				System.out.println(traveler.getName() + " going from " + 
						traveler.getCurrentVillage().getName() + " to " + 
						nextVillage.getName() + ", cost is " +
						roadToTake.getWeight());

				// now we know the road to get on. update gnome's location status
				// based on this. he's on this road, no longer in a village.
				putGnomeOnRoad(traveler, roadToTake);

				// travel on road is represented by sleeping
				// for a number of seconds equal to Road's weight
				Thread.sleep(1000 * roadToTake.getWeight());
				roadsUsedSoFar++;
				totalCost += roadToTake.getWeight();

				// traveler has arrived at next Village. time spent
				// to pass through a village is fixed at 1 second
				putGnomeInVillage(traveler, nextVillage);
				Thread.sleep(1000);

			}

			// when we get here, the trip is complete or out of gas. 
			// print out some trip info.
			// remove the traveler from any road or village it is on to
			// avoid infinite traffic jams.
			if (traveler.getCurrentVillage() == this.destVillage) {
				if (mode == EFFICIENT_MODE) {
					System.out.println("**EFFICIENT Road Trip Complete. " + traveler.getName() + 
							" went from " + startVillage.getName() + " to " +
							destVillage.getName() + " in " + roadsUsedSoFar + 
							" steps with cost " + totalCost + " in " + 
							((System.currentTimeMillis() - startTime)/1000) + " seconds.");
				} else {
					System.out.println("**LAZY Road Trip Complete. " + traveler.getName() + 
							" went from " + startVillage.getName() + " to " +
							destVillage.getName() + " in " + roadsUsedSoFar + 
							" steps with cost " + totalCost + " in " + 
							((System.currentTimeMillis() - startTime)/1000) + " seconds.");
				}
			} else {
				// if you get here, you never made it to the destination. sad.
				System.out.println("**Road Trip Out of Gas! " + traveler.getName() + 
						" wanted to go from " + startVillage.getName() + " to " +
						destVillage.getName() + " but exceeded max trip cost of " + 
						+ MAX_TRIP_COST + " using " + 
						((System.currentTimeMillis() - startTime)/1000) + " seconds.");
			}
			if (traveler.getCurrentRoad() != null) {
				traveler.getCurrentRoad().removeOccupant();
			}
			if (traveler.getCurrentVillage() != null) {
				traveler.getCurrentVillage().removeOccupant();
			}
			
		} catch (InterruptedException e) {
			System.out.println(traveler.getName() + "'s travel thread interrupted.");
			return;
		}

	} // end of run method

	// this tries to put a Gnome in a Village. If the village is full,
	// it waits a second and tries again. Repeat until success.
	// Remember to remove g from his current road as well, if there is one.
	void putGnomeInVillage(Gnome g, Village v) throws InterruptedException {
		while (v.isFull()) {
			System.out.println("!!!Traffic Jam!!! " + g.getName() + 
					" wating to get into village " + v.getName());
			Thread.sleep(1000);
		}
		v.addOccupant();
		g.setCurrentVillage(v);
		if (g.getCurrentRoad() != null) {
			g.getCurrentRoad().removeOccupant();
			g.setCurrentRoad(null);
		}
	}

	// this tries to put a Gnome on a Road. If the Road is full,
	// it waits a second and tries again. Repeat until success.
	// Remember to remove g from his current Village too, if there is one.
	void putGnomeOnRoad(Gnome g, Road r) throws InterruptedException {
		while (r.isFull()) {
			System.out.println("!!!Traffic Jam!!! " + g.getName() + 
					" wating to get onto Road " + r.getID());
			Thread.sleep(1000);
		}
		r.addOccupant();
		g.setCurrentRoad(r);
		if (g.getCurrentVillage() != null) {
			g.getCurrentVillage().removeOccupant();
			g.setCurrentVillage(null);
		}
	}

}
