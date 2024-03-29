package FP;

import java.util.Random;

// This represents a trip for a gnome on a given map.
// It runs as a thread that advances the gnome around the map
// to get from a given start Village to a given destination Village.
// Progress is determined by the travel mode (lazy vs efficient)
// Lazy mode trips just go from town to town at random until destination.
// Efficient mode follows lowest cost path to destination.
// There is a maximum allowable trip cost. if you don't get to your
// destination within the max allowable cost, you are out of gas!
// 
// 
public class RoadTrip extends Thread {
	static MapGUI mapGUI;

	public static void setMapGUI(MapGUI mapGUI){
		RoadTrip.mapGUI=mapGUI;
	}

	// use these to specify the travel mode
	public static final int SIMULATION_TIME_STEP = 125; // one simulation step in ms
	public static final int LAZY_MODE = 0;
	public static final int EFFICIENT_MODE = 1;
	public static final int WANDER_MODE = 2; // wander with no destination
	public static final int MAX_TRIP_COST = 10000; // if you don't get to dest
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
		if (newMode >= LAZY_MODE && newMode <= WANDER_MODE) {
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
						" to " + destVillage.getName() + ", VIP: " + 
						traveler.getVIPLevel() +
						". Should take " + bestPath.getSize() + " roads.");
			} else if (mode == LAZY_MODE) {
				System.out.println("**Starting LAZY Road Trip: " + 
						traveler.getName() + 
						" is going from " + startVillage.getName() + 
						" to " + destVillage.getName() + ", VIP: " + 
						traveler.getVIPLevel() +
						". Good luck!");
			} else {
				System.out.println("**Starting WANDER Road Trip: " + 
						traveler.getName() + 
						" is wandering from " + startVillage.getName() +
						" with no destination in mind, VIP: " + 
						traveler.getVIPLevel() +
						". Happpy Trails!");
				
			}

			Random rn = new Random(); // used for lazy/wander modes
			Road roadToTake; // this will be the next road to take
			Village nextVillage; // this will be the next village we head to
			startTime = System.currentTimeMillis();

			// traveler starts out in the specified village; is not on a road yet
			startVillage.addOccupant(traveler);

			// the trip continues until we have arrived at destination, 
			// or exceeded max cost, or hit a dead end.
			// max cost and destination are irrelevant to wandering.
			// dead end is handled by a break from the loop when found.
			while (mode == WANDER_MODE ||
					(traveler.getCurrentVillage() != destVillage && 
					totalCost < MAX_TRIP_COST)) {
				
				// next road to take depends on what travel mode we're in.
				// LAZY gets a random road out. EFFICIENT gets next road
				// in the pre-calculated shortest path.
				if (mode == EFFICIENT_MODE) {
					roadToTake = bestPath.get(roadsUsedSoFar);
				} else {
					MyList<Road> roadsOut = traveler.getCurrentVillage().getRoadsOut();
					if (roadsOut.getSize() > 0) {
						int randomIndex = rn.nextInt(roadsOut.getSize());
						roadToTake = roadsOut.get(randomIndex);
					} else { // dead end
						break;
					}
				}

				nextVillage = map.getVillage(roadToTake.getToID());

				System.out.println(traveler.getName() + " going from " + 
						traveler.getCurrentVillage().getName() + " to " + 
						nextVillage.getName() + " on road " +
						roadToTake.getID() + ", cost is " +
						roadToTake.getWeight());

				// try to get on the next Road. if there's room, you get right on,
				// but if it's full, you'll be put in that Road's waiting list.
				// in either case, tryRoad returns after you have entered the road
				// and completed your travel time on it.
				tryRoad(traveler, roadToTake);
				roadsUsedSoFar++;
				totalCost += roadToTake.getWeight();
				
				// now try to get into next Village. Again, you may have to wait
				// to get in.
				tryVillage(traveler, map.getVillage(roadToTake.getToID()));
			}

			// when we get here, the trip is complete or out of gas. 
			// print out some trip info.
			System.out.println("\n");
			if (traveler.getCurrentVillage() == this.destVillage) {
				if (mode == EFFICIENT_MODE) {
					System.out.println("**EFFICIENT Road Trip Complete. " + traveler.getName() + 
							" went from " + startVillage.getName() + " to " +
							destVillage.getName() + " on " + roadsUsedSoFar + 
							" roads with cost " + totalCost + " in " + 
							((System.currentTimeMillis() - startTime)/SIMULATION_TIME_STEP) 
							+ " sim time.");
				} else {
					System.out.println("**LAZY Road Trip Complete. " + traveler.getName() + 
							" went from " + startVillage.getName() + " to " +
							destVillage.getName() + " on " + roadsUsedSoFar + 
							" roads with cost " + totalCost + " in " + 
							((System.currentTimeMillis() - startTime)/SIMULATION_TIME_STEP) 
							+ " sim time.");
				}
			} else if (totalCost >= MAX_TRIP_COST) {
				// you hit max cost before arriving at destination
				System.out.println("**Road Trip Out of Gas at Village " +
						traveler.getCurrentVillage().getName() + "! " + 
						traveler.getName() + 
						" wanted to go from " + startVillage.getName() + " to " +
						destVillage.getName() + " but exceeded max trip cost of " + 
						+ MAX_TRIP_COST + " using " + 
						((System.currentTimeMillis() - startTime)/SIMULATION_TIME_STEP) 
						+ " sim time.");
			}
			else {
				// you came to a dead end village
				System.out.println("**Road Trip Dead End! " + traveler.getName() + 
						" wanted to go from " + startVillage.getName() + " to " +
						destVillage.getName() + " but hit a dead end at " + 
						"village " + traveler.getCurrentVillage().getName() +
						".  Used " + 
						((System.currentTimeMillis() - startTime)/SIMULATION_TIME_STEP) 
						+ " sim time.");

			}
			// @modified: you can't just stick gnomes back into different
			// villages without breaking the queueing, etc. We need to 
			// just leave them where they finished, but this can result
			// in permanent backups if the map is crowded.
			//
			// remove the traveler from any road or village it is on to
			// avoid infinite traffic jams.
			//
			// @modified: gnomes don't just die when they fail a roadtrip.
			// Gnome is placed in most recently reached village
			//if (traveler.getCurrentRoad() != null) {
			//	traveler.setInVillage(map.getVillage(traveler.getCurrentRoad().getFromID()));
			//}
			
/*			if (traveler.getCurrentVillage() != null) {
				traveler.getCurrentVillage().removeOccupant(traveler);
			 }
			if (traveler.getCurrentRoad() != null) {
				traveler.getCurrentRoad().removeOccupant(traveler);
			 }
*/			
			// print out this gnome's travel history
			System.out.print(traveler.getName() + " has visited: ");
			for (int j = 0; j < traveler.getVillageHistory().getSize(); j++) {
				System.out.print(traveler.getVillageHistory().get(j).getName() + " ");
			}
			System.out.println(".\n");
		
		} catch (InterruptedException e) {
			System.out.println(traveler.getName() + "'s travel thread interrupted.");
			return;
		}

	} // end of run method

	// this tries to put a Gnome on a Road. If the Road
	// is already full, Gnome is put in a q to wait.
	// When the Gnome is allowed onto the Road, we wait
	// a number of seconds proportional to the Road weight
	// to simulate travel time.
	void tryRoad(Gnome g, Road r) throws InterruptedException {
		r.requestEntry(g);
		
		// notify if you didnt get in right away and are in q
		if (g.getCurrentRoad() != r) {
			System.out.println("!!!Traffic Jam!!! " + g.getName() + 
					" waiting to get onto Road " + r.getID() +
					", position in queue: " + r.getPositionInQueue(g));
		}
		
		// we may have to wait in a queue before actually getting
		// on road. wait here until we see that the Road has
		// allowed the Gnome to enter.
		//
		while (g.getCurrentRoad() != r) {
				if (mapGUI != null) mapGUI.refresh();
				//Thread.sleep(SIMULATION_TIME_STEP);
				synchronized(g) {g.wait();}
			}
		System.out.println(g.getName() + " now on road " + r.getID());

		// by the time you get here, you are now on the road. Sleep
		// an amount of time proportional to road weight to simulate
		// travel time.
		if (mapGUI != null) mapGUI.refresh(); // allow testing w/o gui
		Thread.sleep(r.getWeight() * SIMULATION_TIME_STEP);
	}

	// this tries to put a Gnome in a Village. If Village
	// is already full, Gnome is put in a q to wait.
	// When the Gnome is allowed into Village, we wait
	// one second to simulate passing through Village time.
	//
	void tryVillage(Gnome g, Village v) throws InterruptedException {
		v.requestEntry(g);
		
		// notify if you didn't get into v and are waiting in q
		if (g.getCurrentVillage() != v) {
		System.out.println("!!!Village Jam!!! " + g.getName() + 
				" waiting to get into Village " + v.getName() +
				", position in queue: " + v.getPositionInQueue(g));
		};

		// we may have to wait in a queue before actually getting
		// in. wait here until we see that the Village has
		// allowed the Gnome to enter.
		//
		while (g.getCurrentVillage() != v) {
			if(mapGUI != null) mapGUI.refresh();
			//Thread.sleep(SIMULATION_TIME_STEP);
			synchronized(g) {g.wait();}
			}
		System.out.println(g.getName() + " now in Village " + 
			map.getVillage(v.getID()).getName());

		// by the time you get here, you are now in Village. Sleep
		// one second to simulate
		// travel time through village.
		if (mapGUI != null) mapGUI.refresh(); // for testing w/o gui
		
		// time to pass through a village is one sim time step
		Thread.sleep(SIMULATION_TIME_STEP);
	}

}
