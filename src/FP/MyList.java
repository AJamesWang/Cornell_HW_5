package FP;

// simple dynamic size list. items are stored in an array.
// The array starts at a "standard" size. If we need more
// room when adding objects, we create a new array at
// double the current size, then move items over to
// that new array.

public class MyList<T> {
	// array will be initialized to this size
	private final static int STANDARD_START_SIZE = 32;
	
	// an array to hold all the items
	private Object[] theItems;
	
	// number of items currently stored
	private int size;
	
	// simple constructor; list is empty
	public MyList() {
		theItems = new Object[STANDARD_START_SIZE];
		size = 0;
	}
	
	// constructor that sets initial size
	public MyList(int initialSize) {
		theItems = new Object[initialSize];
		size = 0;
	}
	
	// add a new item at the end
	public synchronized void add(T newItem) {
		// if we have empty space in the array, it's easy. 
		// If we don't, we need to make a new, bigger array, and
		// copy all our items over to that new one before continuing
		if (size >= theItems.length) {
			Object[] newArray = new Object[theItems.length * 2];
			
			// transfer contents to new array
			for (int i = 0; i < theItems.length; i++) {
				newArray[i] = theItems[i];
			}
			theItems = newArray;
		}
		
		// by the time we get here, we know we have room to store the new object, and increase nItems
		theItems[size++] = newItem;
	}
	
	// add a new item at the end, only if it is not already in the MyList
	public synchronized void addIfNew(T newItem) {
		// add it if we didn't find it
		if (!this.has(newItem)) {
			add(newItem);
		}
	}
	
	// tell if the given item is already in the MyList
	public synchronized boolean has(T findMe) {
		boolean foundIt = false;
		
		// see if this thing is already in the MyList
		for (int i = 0; i < size && !foundIt; i++) {
			foundIt = (theItems[i].equals(findMe));
		}
		
		return foundIt;
	}
	
	// retrieve the item at a given position in the list
	public T get(int i) throws ArrayIndexOutOfBoundsException {
		if (i < 0 || i > size-1) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return (T)theItems[i];
	}
	
	// set the ith item to the given new value
	public void set(int i, T newValue) throws ArrayIndexOutOfBoundsException {
		if (i < 0 || i > size-1) {
			throw new ArrayIndexOutOfBoundsException();
		}
		theItems[i] = newValue;
		
	}
	
	// set a specific item to be null
	public void set_null(int i) {
		theItems[i]= null;
	}

	
	// tell how many items we have in the list
	public int getSize() {
		return size;
	}
	
	public synchronized void remove(int id) {
		 if(id < size){
	            Object obj = theItems[id];
	            theItems[id] = null;
	            int tmp = id;
	            while(tmp < size){
	                theItems[tmp] = theItems[tmp+1];
	                theItems[tmp+1] = null;
	                tmp++;
	            }
	            size--;
	        } else {
	            throw new ArrayIndexOutOfBoundsException();
	        }
	}
	
	
}
