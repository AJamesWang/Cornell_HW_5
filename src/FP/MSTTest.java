package FP;
import java.util.HashSet;

public class MSTTest{
	public static void main(String[] args){
		testMap1();
	}

	//triangle map
	private static void testMap1(){
		Map map=new Map();
		for(int i=0;i<3; i++){
			map.addVillage("Village "+i);
		}

		Road[] newRoads=new Road[6];
		newRoads[0]=new Road(0, 1, 1, 0);
		newRoads[1]=new Road(1, 2, 1, 1);
		newRoads[2]=new Road(2, 0, 1, 2);
		newRoads[3]=new Road(0, 2, 2, 3);
		newRoads[4]=new Road(2, 1, 2, 4);
		newRoads[5]=new Road(1, 0, 2, 5);

		HashSet<Road> chosenRoads=map.chooseNewRoads(newRoads);
		assert(chosenRoads.contains(newRoads[0]));
		assert(chosenRoads.contains(newRoads[1]));
		assert(chosenRoads.contains(newRoads[2]));
		assert(!chosenRoads.contains(newRoads[3]));
		assert(!chosenRoads.contains(newRoads[4]));
		assert(!chosenRoads.contains(newRoads[5]));


		newRoads[3]=new Road(0, 1, 1, 0);
		newRoads[4]=new Road(1, 2, 1, 1);
		newRoads[5]=new Road(2, 0, 1, 2);
		newRoads[0]=new Road(0, 2, 2, 3);
		newRoads[1]=new Road(2, 1, 2, 4);
		newRoads[2]=new Road(1, 0, 2, 5);

		chosenRoads=map.chooseNewRoads(newRoads);
		assert(chosenRoads.contains(newRoads[3]));
		assert(chosenRoads.contains(newRoads[4]));
		assert(chosenRoads.contains(newRoads[5]));
		assert(!chosenRoads.contains(newRoads[0]));
		assert(!chosenRoads.contains(newRoads[1]));
		assert(!chosenRoads.contains(newRoads[2]));

		System.out.println("Test success!");
	}
}