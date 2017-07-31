package FP;

public class Map {
    // @modified new variables for GUI use
    public static int DEFAULT_WIDTH = 600;
    public static int DEFAULT_HEIGHT = 600;

    public MyList<Village> getVillages() {
        return this.villages;
    }

    public MyList<Road> getRoads() {
        return this.roads;
    }

    private MyList<Village> villages; // all the nodes in the graph
    private MyList<Road> roads;

    // construct an empty graph
    public Map() {
        villages = new MyList<Village>();
        roads = new MyList<Road>();
    }
 
	//remove a village from MyList villages
	//remove all the roads that went through the village
	public void removeVillage(int id){
		Village removeMe = villages.get(id);
	
		// remove roads out until you have no more. for each road out,
		// you will need to remove the RoadOut entry for this Village,
		// and the corresponding RoadIn entry for the Village on the
		// destination end of the Road.
		while (removeMe.getRoadsOut().getSize() > 0) {
			Road roadOut = removeMe.getRoadsOut().get(0);
			int destinationID = roadOut.getToID();
			Village destination = villages.get(destinationID);
			//remove road from destination village
			destination.removeRoadIn(roadOut);
			//remove from RemoveMe village
			removeMe.removeRoadOut(roadOut);
			roads.set_null(roadOut.getID());
		}
			
		//do the same with roadIn's
		while (removeMe.getRoadsIn().getSize() > 0) {
			Road roadIn = removeMe.getRoadsIn().get(0);
		    int fromID = roadIn.getFromID();
			Village from = villages.get(fromID);
			//remove road from fromVillage
			from.removeRoadOut(roadIn);
			//remove from RemoveMe Village
			removeMe.removeRoadIn(roadIn);
			roads.set_null(roadIn.getID());
		}

		villages.set_null(id);
			
	}




    // removes a village and any roads that went through the village
    // en route to other villages should be made direct
    public void removeVillage2(int id) {
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
                if (fromVillage != toVillage) {//@modified so doesn't road to self
                    System.out.println(fromVillage+""+toVillage);
                    addRoad(fromVillage, toVillage, newWeight);
                }
            }
        }
        // now delete all the old roads and removeMe
        removeVillage(id);
    }

    // add a road with the given weight and return its id
    // from one village to the other. villages are
    // specified by id#
    public int addRoad(int from, int to, int weight) throws ArrayIndexOutOfBoundsException {
        Road newRoad = new Road(from, to, weight);

        villages.get(from).addRoadOut(newRoad);
        villages.get(to).addRoadIn(newRoad);
        roads.add(newRoad);
        return newRoad.getID();
    }

    // @modified added this method
    public boolean removeRoad(int from, int to) {
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

    // tell how many outgoing edges the given node has.
    // node is specified by id# given when added to graph.
    public int nConnections(int id) throws ArrayIndexOutOfBoundsException {
        return villages.get(id).getRoadsOut().getSize();
    }
}
