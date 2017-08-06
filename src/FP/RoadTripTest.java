package FP;

import java.util.Random;
import java.awt.Color;

//class to try out our RoadTrip threading trickery on a sample Map
public class RoadTripTest {
	// public static MapMonitor locationMonitor;

	// public static void main(String[] args) {
	// 	// simple test on simple, fixed map
	// 	//doTest1();

	// 	// test2 generates a bigger, clustered map with the given parameters
	// 	// parameters for test2 are nClusters, nVillagesPerCluster, nGnomes
	// 	// last parameter is max capacitty for any road/village. range will
	// 	// be 1..max
	// 	doTest2(6, 5, 30, 4);
	// }

	// // simple test to set up relatively simple map and send a few
	// // Gnomes out traveling on it, printing out their progress, and
	// // some final results.
	// static void doTest1() {
	// 	System.out.println("Starting Test1\n");
	// 	Map theMap = createMap1();
	// 	MyList<Gnome> theGnomes = createGnomes1(theMap, 4);

	// 	// send gnome 0 on a trip from node 4 to node 3, lazy mode
	// 	RoadTrip trip1 = new RoadTrip(theMap, theGnomes.get(0), 
	// 			theMap.getVillage(4), theMap.getVillage(3), RoadTrip.LAZY_MODE);
	// 	trip1.start();

	// 	// send gnome 1 on a trip from node 2 to node 1, efficient mode
	// 	RoadTrip trip2 = new RoadTrip(theMap, theGnomes.get(1), 
	// 			theMap.getVillage(2), theMap.getVillage(1), RoadTrip.EFFICIENT_MODE);
	// 	trip2.start();

	// 	// send gnome 2 on a trip from node 1 to node 4, lazy mode. should be a short trip
	// 	RoadTrip trip3 = new RoadTrip(theMap, theGnomes.get(2), 
	// 			theMap.getVillage(1), theMap.getVillage(4), RoadTrip.LAZY_MODE);
	// 	trip3.start();

	// 	// send gnome 3 on a trip from node 4 to node 2, efficient mode
	// 	RoadTrip trip4 = new RoadTrip(theMap, theGnomes.get(3), 
	// 			theMap.getVillage(4), theMap.getVillage(2), RoadTrip.EFFICIENT_MODE);
	// 	trip4.start();

	// 	// hang out here until all threads are done. join() waits til thread done.
	// 	try {
	// 		trip1.join();
	// 		trip2.join();
	// 		trip3.join();
	// 		trip4.join();
	// 	} catch (InterruptedException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}

	// 	/// show the gnomes' travel history
	// 	for (int i = 0; i < theGnomes.getSize(); i++) {
	// 		System.out.print(theGnomes.get(i).getName() + " has visited: ");
	// 		for (int j = 0; j < theGnomes.get(i).getVillageHistory().getSize(); j++) {
	// 			System.out.print(theGnomes.get(i).getVillageHistory().get(j).getName() + " ");
	// 		}
	// 		System.out.println(".");
	// 	}
	// 	System.out.println("Test1 is done.");
	// }

	// static Map createMap1() {
	// 	Map m = new Map();

	// 	// add some villages to the map
	// 	for (int i = 0; i < 6; i++) {
	// 		m.addVillage("Village" + i);
	// 	}

	// 	// now add some Roads between the Villages
	// 	m.addRoad(0, 2, 2);
	// 	m.addRoad(0, 4,  4);

	// 	m.addRoad(1, 3, 2);
	// 	m.addRoad(1, 4, 3);

	// 	m.addRoad(2, 0, 2);
	// 	m.addRoad(2, 4, 2);
	// 	m.addRoad(2, 3, 1);

	// 	m.addRoad(3, 0, 3);
	// 	m.addRoad(3, 4, 1);

	// 	m.addRoad(4, 0, 4);
	// 	m.addRoad(4, 5, 10);

	// 	m.addRoad(5, 1, 8);


	// 	return m;
	// }

	// static MyList<Gnome> createGnomes1(Map m, int nGnomes) {

	// 	// don't worry about VIP yet...
	// 	for (int i = 0; i < nGnomes; i++) {
	// 		m.addGnome("Gnome" + i, Color.GREEN, 0);
	// 	}

	// 	return m.getGnomes();
	// }

	// // more complicated test with larger, clustered map.
	// // we set up a map that has the given number of clusters
	// // where each cluster has the given number of Villages in it.
	// // the clusters are well connected within themselves, but have
	// // only one 2-way road connecting them to one other cluster,
	// // so cluster to cluster travel will be limited. Then we send
	// // every Gnome out on a trip.
	// // maxCapacity is the maximum capacity any road or village will 
	// // have. min is 1.
	// static void doTest2 (int nClusters, int nVillagesPerCluster, int nGnomes,
	// 		int maxCapacity) {

	// 	System.out.println("\n\nTest2 start with " + nClusters + " clusters of "
	// 			+ nVillagesPerCluster + " villages each and " + nGnomes +
	// 			" Gnomes...");

	// 	// set up Map and Gnomes
	// 	Map theMap = createMap2(nClusters, nVillagesPerCluster, maxCapacity);
	// 	Gnome[] theGnomes = createGnomes2(theMap, nGnomes);

	// 	// set up and run trips
	// 	RoadTrip[] trips = runTrips2(theMap, theGnomes);

	// 	// run position monitor thread to update us with positions
	// 	locationMonitor = new MapMonitor(theMap);
	// 	locationMonitor.start();

	// 	// wait for all trips to end
	// 	for (int i = 0; i < trips.length; i++) {
	// 		try {
	// 			trips[i].join();
	// 		} catch (InterruptedException e) {
	// 			// complain and move on
	// 			System.out.println("Thread " + i + " was interrupted.");
	// 		}
	// 	}

	// 	/// show the gnomes' travel history
	// 	for (int i = 0; i < theGnomes.length; i++) {
	// 		System.out.print(theGnomes[i].getName() + " has visited: ");
	// 		for (int j = 0; j < theGnomes[i].getVillageHistory().getSize(); j++) {
	// 			System.out.print(theGnomes[i].getVillageHistory().get(j).getName() + " ");
	// 		}
	// 		System.out.println(".");
	// 	}
	// 	System.out.println("Test2 is done.");

	// }

	// // create the required number of Gnomes on the given map.
	// // use 3 VIP statuses, and 3 corresponding colors
	// static Gnome[] createGnomes2(Map m, int nGnomes) {
	// 	Color[] colors = {Color.RED, Color.YELLOW, Color.GREEN};

	// 	Gnome[] gnomes = new Gnome[nGnomes];
	// 	for (int i = 0; i < nGnomes; i++) {
	// 		m.addGnome("Gnome"+i, colors[i%3], i%3);
	// 		gnomes[i] = m.getGnome(i);
	// 	}

	// 	return gnomes;
	// }

	// static Map createMap2(int nClusters, int nVillagesPerCluster, 
	// 		int maxCapacity) {

	// 	// add the required number of villages to a new map. name them by
	// 	// cluster number and village number within that cluster, e.g., "C2V3"
	// 	Map m = new Map();
	// 	for (int i = 0; i < nClusters; i++) {
	// 		for (int j = 0; j < nVillagesPerCluster; j++) {
	// 			m.addVillage("C"+i+"V"+j, (j%maxCapacity + 1));
	// 		}
	// 	}

	// 	// now add roads. within each cluster, each village will be connected to 
	// 	// half of the villages in that cluster, chosen at random. what a mess.
	// 	Random r = new Random();
	// 	for (int i = 0; i < nClusters; i++) {
	// 		for (int j = 0; j < nVillagesPerCluster; j++) {
	// 			int villageID = i * nVillagesPerCluster + j;
	// 			Village v = m.getVillage(villageID);

	// 			// randomly choose a list of other villages in the cluster
	// 			// to connect to. keep choosing until we get the needed
	// 			// number of unique villages to connect to. we want to get
	// 			// nVillagesPerCluster/2 unique connections. also don't
	// 			// add a connection to yourself.
	// 			MyList<Integer> connections = new MyList<Integer>();
	// 			while (connections.getSize() < (nVillagesPerCluster + 1)/2) {
	// 				int randomChoice = r.nextInt(nVillagesPerCluster) + 
	// 						i * nVillagesPerCluster;
	// 				if (randomChoice != villageID ) {
	// 					connections.addIfNew(randomChoice);
	// 				}
	// 			}
	// 			// now we've found the village numbers we want to connect to. build roads.
	// 			// assign a weight value based on difference in id values of the two
	// 			// Villages.
	// 			for (int k = 0; k < connections.getSize(); k++) {
	// 				m.addRoad(v.getID(), connections.get(k), 
	// 						2 * Math.abs(v.getID() - connections.get(k)),
	// 						k%maxCapacity + 1);
	// 			}
	// 		}
	// 	}

	// 	// when we get here, all clusters are internally connected. now add
	// 	// connections from the last node of each cluster to the first node of
	// 	// the next cluster. weight for each is based on max capacity.
	// 	for (int i = 0; i < nClusters; i++) {
	// 		// first and last clusters are special cases since they don't 
	// 		// have a "previous" cluster or "next" cluster, respectively
	// 		int thisCluster = i;

	// 		int nextCluster = i + 1;
	// 		if (nextCluster > nClusters-1) nextCluster = 0;

	// 		int prevCluster = i - 1;
	// 		if (prevCluster < 0) prevCluster = nClusters - 1;

	// 		int firstInThisCluster = thisCluster * nVillagesPerCluster;
	// 		int lastInThisCluster = firstInThisCluster + nVillagesPerCluster - 1;
	// 		int firstInNextCluster = nextCluster * nVillagesPerCluster;
	// 		int lastInPrevCluster = prevCluster * nVillagesPerCluster + nVillagesPerCluster - 1;

	// 		// from this cluster's last to next cluster's first
	// 		m.addRoad(lastInThisCluster, firstInNextCluster, nVillagesPerCluster,
	// 				(maxCapacity + 1)/2);
	// 		m.addRoad(firstInThisCluster, lastInPrevCluster, nVillagesPerCluster,
	// 				(maxCapacity + 1)/2);

	// 	}
	// 	// so now we have the right number of clusters, well connected
	// 	// within each cluster, but with just one 2-way connection from
	// 	// one cluster to the next.
	// 	return m;
	// }

	// // set a bunch of Gnomes on trips.
	// // we will just pick start and destination villages at random
	// // some lazy, some efficient, some wander.
	// // make sure number of trips to any node is smaller than
	// // that node's capacity, or things will lock up.
	// static RoadTrip[] runTrips2(Map m, Gnome[] gnomes) {
	// 	RoadTrip[] theTrips = new RoadTrip[gnomes.length];
	// 	Random r = new Random();

	// 	// use this to count up the trips going to each village
	// 	// so we don't overload any.
	// 	int[] destCount = new int[m.getVillages().getSize()];
	// 	for (int i = 0; i < destCount.length; i++) destCount[i]=0;

	// 	// create a trip for each Gnome
	// 	for (int i = 0; i < theTrips.length; i++) {
	// 		int startID = r.nextInt(m.getMaxVillageID() + 1);

	// 		// pick a node at random, but try again if that node
	// 		// already has too many trips going to it. Also
	// 		// try again if random picked dest == start
	// 		int destID = r.nextInt(m.getMaxVillageID() + 1);
	// 		while (destID == startID || destCount[destID] + 1 >= 
	// 				m.getVillages().get(destID).getCapacity()) {
	// 			destID = r.nextInt(m.getMaxVillageID() + 1);
	// 		}
	// 		destCount[destID]++;

	// 		// pick a mode based on mod of total mode types.
	// 		int mode = i%3;
	// 		theTrips[i] = new RoadTrip(m, gnomes[i], m.getVillage(startID), 
	// 				m.getVillage(destID), mode);

	// 		theTrips[i].start();
	// 	}

	// 	return theTrips;
	// }


}

