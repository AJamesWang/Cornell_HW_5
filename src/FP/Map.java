package FP;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import datastructures.LinkedList;

public class Map {
    /* GUI */
    public static int DEFAULT_WIDTH = 600;
    public static int DEFAULT_HEIGHT = 600;

    public MyList<Village> getVillages() {
        return this.villages;
    }

    public MyList<Road> getRoads() {
        return this.roads;
    }

    public MyList<Gnome> getGnomes() {
        return this.gnomes;
    }

    public int getNextRoadID(){
        return this.nextRoadID++;
    }
    
    
    
    /* datastructure */
   //BSTs for efficient search of gnomes
    private BST<Gnome> nameTree = new BST<Gnome>();
    private BST<Gnome> vipTree = new BST<Gnome>();
    private BST<Gnome> colorTree = new BST<Gnome>();
    
    private MyList<Village> villages; // all the nodes in the graph
    private MyList<Road> roads;
    private MyList<Gnome> gnomes;

    // these are used to generate new unique IDs when
    // a Road or Village is added to the Map
    private int nextVillageID = 0;
    private int nextRoadID = 0;
    private int nextGnomeID = 0;

    // construct an empty graph
    // when you want to create a village, road, or a gnome, always do so by using
    // add methods
    // in this class
    public Map() {
        villages = new MyList<Village>();
        roads = new MyList<Road>();
        gnomes = new MyList<Gnome>();
    }

    // add a new village to villages. return an id
    // that can be used to quickly access this village
    public synchronized int addVillage(String name) {
        Village newVillage = new Village(name, nextVillageID++);
        villages.add(newVillage);
        return newVillage.getID();
    }
    
    // add a new village with the specified capacity. 
    // return its ID
    public synchronized int addVillage(String name, int theCapacity) {
        int theID = addVillage(name);
        villages.get(theID).setCapacity(theCapacity);
        return theID;
    }
   

    // remove a village from MyList villages
    // remove all the roads that went through the village
    public synchronized void removeVillage(int id) {
        Village removeMe = villages.get(id);

        // remove roads out until you have no more. for each road out,
        // you will need to remove the RoadOut entry for this Village,
        // and the corresponding RoadIn entry for the Village on the
        // destination end of the Road.
        while (removeMe.getRoadsOut().getSize() > 0) {
            Road roadOut = removeMe.getRoadsOut().get(0);
            int destinationID = roadOut.getToID();
            Village destination = villages.get(destinationID);
            // remove road from destination village
            destination.removeRoadIn(roadOut);
            // remove from RemoveMe village
            removeMe.removeRoadOut(roadOut);
            roads.set_null(roadOut.getID());
        }

        // do the same with roadIn's
        while (removeMe.getRoadsIn().getSize() > 0) {
            Road roadIn = removeMe.getRoadsIn().get(0);
            int fromID = roadIn.getFromID();
            Village from = villages.get(fromID);
            // remove road from fromVillage
            from.removeRoadOut(roadIn);
            // remove from RemoveMe Village
            removeMe.removeRoadIn(roadIn);
            roads.set_null(roadIn.getID());
        }

        villages.set_null(id);

    }

    // removes a village and any roads that went through the village
    // en route to other villages should be made direct
    public synchronized void removeVillage2(int id) {
        Village removeMe = villages.get(id);
        // outer loop iterates through the "fromVillage"s and connects
        // them to "toVIllage"s using the inner loop
        for (int i = 0; i < removeMe.getRoadsIn().getSize(); i++) {
            Road roadIn = removeMe.getRoadsIn().get(i);
            int fromVillage = roadIn.getFromID();

            for (int j = 0; j < removeMe.getRoadsOut().getSize(); j++) {
                Road roadOut = removeMe.getRoadsOut().get(j);
                int toVillage = roadOut.getToID();
                // now that we have roadIn and roadOut, connect Them!!
                // weight shall be the total of two previous roads
                int newWeight = roadIn.getWeight() + roadOut.getWeight();
                if (fromVillage != toVillage) {// @modified so doesn't road to self
                    addRoad(fromVillage, toVillage, newWeight);
                }
            }
        }
        // now delete all the old roads and removeMe
        removeVillage(id);
    }

    // adds a new Gnome and returns its ID
    public synchronized int addGnome(String theName, Color theFavColor, int theVIPLevel) {
        Gnome newGnome = new Gnome(theName, theFavColor, theVIPLevel, nextGnomeID++);
        gnomes.add(newGnome);
        nameTree.insert(newGnome, newGnome.getName());
        vipTree.insert(newGnome, newGnome.getVIPLevel());
        colorTree.insert(newGnome, newGnome.getFavColor());        
        return newGnome.getID();
    }

    // add a road with the given weight and return its id
    // from one village to the other. villages are
    // specified by id#
    public synchronized int addRoad(int from, int to, int weight) 
    		throws ArrayIndexOutOfBoundsException {
        Road newRoad = new Road(from, to, weight, nextRoadID++);

        villages.get(from).addRoadOut(newRoad);
        villages.get(to).addRoadIn(newRoad);
        roads.add(newRoad);
        return newRoad.getID();
    }
    
    // same as above but also allows specifying capacity
    public synchronized int addRoad(int from, int to, int weight, int capacity)
    		throws ArrayIndexOutOfBoundsException {
    	int theID = addRoad(from, to, weight);
    	roads.get(theID).setCapacity(capacity);
    	return theID;
    }

    public void addRoad(Road road){
        villages.get(road.getFromID()).addRoadOut(road);
        villages.get(road.getToID()).addRoadIn(road);
        roads.add(road);
    }

    // tries to remove road, returns true if succeeded
    public synchronized boolean removeRoad(int from, int to) {
        int roadID = -1;
        MyList<Road> roads = villages.get(from).getRoadsOut();
        for (int i = 0; i < roads.getSize(); i++) {
            Road road = roads.get(i);
            if (road != null && road.getToID() == to) {
                roadID = road.getID();
                break;
            }
        }

        if (roadID != -1) {
            villages.get(from).removeRoadOut(getRoad(roadID));
            villages.get(to).removeRoadIn(getRoad(roadID));
            this.roads.set_null(roadID);
            return true;
        } else {
            return false;
        }
    }

    // tell the max village ID created so far.
    public int getMaxVillageID() {
        return villages.getSize() - 1;
    }

    // get the Thing that has the given id
    public Village getVillage(int id) {
        return villages.get(id);
    }

    public Road getRoad(int id) {
        return roads.get(id);
    }

    public Gnome getGnome(int id) {
        return gnomes.get(id);
    }

    // tell how many outgoing edges the given node has.
    // node is specified by id# given when added to graph.
    public synchronized int nConnections(int id) throws ArrayIndexOutOfBoundsException {
        return villages.get(id).getRoadsOut().getSize();
    }

    public synchronized MyList<Road> shortestPath(Village from, Village to) {
        // just return null for stupid cases
        if (villages.getSize() < 2 || from == to) {
            return null;
        }

        // this will contain the roads that constitute the
        // shortest path. we will return this at the end.
        MyList<Road> theShortestPath = new MyList<Road>();

        // settled nodes. don't care about order here, so use MyList
        MyList<Village> settledNodes = new MyList<Village>();

        // unsettled nodes. we do care about order here. we want to get
        // the lowest distance one first.
        // We are using our RankedQueue class from a previous
        // homework, but it ranks things highest first, so there's some
        // goofiness to make that work. RankedQueue is used elsewhere in
        // normal "highest value first" setup, so didn't want to rewrite
        // it to be lowest value first.
        RankedQueue<Village> unsettledNodes = new RankedQueue<Village>();

        // we also need to keep track of the min known distance to every node
        // and a predecessor node for every node. Initialize them here with
        // infinity and null respectively. For our purposes, instead of
        // storing the predecessors as nodes, we are going to store the
        // edge/road that brought you there. This is because in the end,
        // we want to return a list of Roads that comprise the shortest
        // path.
        MyList<Integer> distances = new MyList<Integer>(villages.getSize());
        MyList<Road> predecessors = new MyList<Road>(villages.getSize());
        for (int i = 0; i < villages.getSize(); i++) {
            distances.add(Integer.MAX_VALUE);
            predecessors.add(null);
        }

        // before starting, set distance to the "from" Village to be 0 duh.
        distances.set(from.getID(), 0);

        // now put the starting point Village into our unsettled nodes.
        unsettledNodes.add(from, 0);

        // now we grind through this until we have no more unsettled nodes
        while (unsettledNodes.getSize() > 0) {
            Village evaluateThis = unsettledNodes.remove();
            int distanceToEvaluationNode = distances.get(evaluateThis.getID());

            // loop through the evaluation node's direct connections
            MyList<Road> roadsOut = evaluateThis.getRoadsOut();
            for (int i = 0; i < roadsOut.getSize(); i++) {
                Road thisRoad = roadsOut.get(i);
                int destID = thisRoad.getToID();
                int edgeDistance = thisRoad.getWeight();
                int minKnownDistance = distances.get(destID);

                // if we have found a new shortest path to this destination,
                // update the best distance for this destination, note the
                // predecessor node that corresponds to that distance,
                // and add this destination to the unsettled nodes queue.
                if (distanceToEvaluationNode + edgeDistance < minKnownDistance) {
                    distances.set(destID, distanceToEvaluationNode + edgeDistance);
                    predecessors.set(destID, thisRoad);
                    // here's the goofy math bit. since our prioritized queue gives highest
                    // ranked item first, we use minus distance as the rank, so lowest
                    // distance comes out first. confusing, but it works.
                    unsettledNodes.add(villages.get(destID), -distances.get(destID));
                }

            }
            // now we've evaluated all edges leading out of the evaluation node.
            // add the evaluation node to the list of settled nodes and repeat
            settledNodes.add(evaluateThis);

        }

        // now we have evaluated all the nodes we can get to from the start node.
        // it's possible that there is no path from the from node to the to node.
        // that would be indicated by the to node's distance being MAX_VALUE.
        // for now we'll return a null if that happened.
        if (distances.get(to.getID()) == Integer.MAX_VALUE) {
            return null;
        } else {
            // so if we did find a path to the "to" node, we need to
            // reconstruct the full path by working backwards through the
            // predecessor values. crazy talk!
            MyList<Road> backwardsPath = new MyList<Road>();
            Village currentNode = to;

            // starting from the destination node, we work backwards
            // through the previous values until we get to the from
            // node. This will accumulate the shortest path in reverse
            // order.
            while (currentNode != from) {
                Road predecessor = predecessors.get(currentNode.getID());
                backwardsPath.add(predecessor);
                currentNode = villages.get(predecessor.getFromID());
            }

            // now we have all the roads that make up the shortest
            // path, but in reverse order. return them in the correct
            // order.
            for (int i = backwardsPath.getSize() - 1; i >= 0; i--) {
                theShortestPath.add(backwardsPath.get(i));
            }
        }

        return theShortestPath;
    }

    //modified version of Kruskal's algorithm.
    //instead of checking for cycles, checks for reduntant connections.
    //doesn't lead to optimal solution, but strictly dominates plain old kruskal's algorithm (I think)
    private HashMap<Village, HashSet<Village>> generateConnections(){
        HashMap<Village, HashSet<Village>> connections = new HashMap<Village, HashSet<Village>>();
        for(int i=0;i<villages.getSize(); i++){
            if(villages.get(i)!=null){
                connections.put(villages.get(i), new HashSet<Village>());
            }
        }

        //gets connections for every village
        for(Village village:connections.keySet()){
            HashSet<Village> villagesReached=new HashSet<Village>();
            villagesReached.add(village);
            //Queue, but don't feel like creating Queue interface
            LinkedList<Road> toTravel=new LinkedList<Road> ();

            for(int i=0;i<village.getRoadsOut().getSize();i++){
                if(village.getRoadsOut().get(i)!=null){
                    toTravel.qPush(village.getRoadsOut().get(i));
                }
            }

            while(toTravel.getLength()>0){
                Road road=toTravel.qPop();
                Village curVillage=this.getVillage(road.getToID());

                //if curVillage has not been reached yet, add to villagesReached
                if(!villagesReached.contains(curVillage)){
                    //if curVillage can reach a village, then root village can reach that village
                    for(Object obj:connections.get(curVillage)){
                        Village reachableVillage=(Village)obj;
                        villagesReached.add(reachableVillage);
                    }
                    villagesReached.add(curVillage);

                    //adds all outgoing roads to queue
                    for(int i=0;i<curVillage.getRoadsOut().getSize();i++){
                        if(curVillage.getRoadsOut().get(i)!=null){
                            toTravel.qPush(curVillage.getRoadsOut().get(i));
                        }
                    }
                }
            }

            connections.put(village, villagesReached);
        }

        return connections;
    }

    //@todo: test
    public HashSet<Road> chooseNewRoads(Road[] newRoads){
        java.util.Arrays.sort(newRoads);
        HashSet<Road> addedRoads=new HashSet<Road>();
        HashMap<Village, HashSet<Village>> connections=generateConnections();
        for(Road road:newRoads){
            if(road==null) continue;

            Village from=this.getVillage(road.getFromID());
            Village to=this.getVillage(road.getToID());
            if(!connections.get(from).contains(to)){
                addedRoads.add(road);
                this.addRoad(road);
                connections=generateConnections();
            }
        }
        return addedRoads;
    }
   
    //searches for matching gnomes using the corresponding BST
   public MyList<Gnome> searchByName(String name){
	   return nameTree.findGnomes(name);
   }
   
   public MyList<Gnome> searchByVIP(int vip){
   		return vipTree.findGnomes(vip);
   }
   
   public MyList<Gnome> searchByColor(Color c){
	   return colorTree.findGnomes(c);
   }
}
