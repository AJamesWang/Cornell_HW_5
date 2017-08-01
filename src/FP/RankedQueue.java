package FP;

// this class implements a queue structure where items are ordered by
// some externally assigned integer ranking value. items are kept sorted
// in ranking value order from highest at the head to lowest at the tail.
// implementation is a specialized linked list structure.
public class RankedQueue<T> {
	private int 			nItems;
	private QueueItem<T> 	head;
	
	// new empty ranked queue
	public RankedQueue() {
		nItems = 0;
		head = null;
	}
	
	// create a ranked queue by 
	
	// add a new item to the queue. return its position in queue:
	// head is 0; tail is nItems-1. If the item is already in 
	// the queue, it simply returns its current position without adding
	// it again.
	//
	public int add(T data, int rankingValue) {
		// create new queueItem to insert into queue
		QueueItem<T> newItem = new QueueItem<T>(data, rankingValue);
		
		int position = 0; // this is the value we will return
		
		// easy if it's the first item into the queue
		if (nItems < 1) {
			nItems = 1;
			head = newItem;
			head.nextItem = null;
			return 0;
		} else {
			// handle the normal case where we walk down the list
			// until we find an item whose ranking value is lower
			// than this new item. we insert the new item there.
			QueueItem<T> checkThis = head;
			QueueItem<T> previousCheck = null;
			
			while (position < nItems && checkThis != null) {
				// oh and don't insert it if it's already here duh
				if (checkThis.data == newItem.data) {
					return position;
				} else if (checkThis.rank < rankingValue) {
					// found our spot -- put it before this guy,
					newItem.nextItem = checkThis;
					if (previousCheck == null) {
						// this new guy is now the head
						head = newItem;
					} else {
						previousCheck.nextItem = newItem;
					}
					nItems++;
					return position;
				} else { // keep moving down the list
					position++;
					previousCheck = checkThis;
					checkThis = checkThis.nextItem;
				}
			}
			// if we get here, we've come to the end of the list
			// without finding a lower ranked item. so put this 
			// one at the tail.
			previousCheck.nextItem = newItem;
			newItem.nextItem = null;
			nItems++;
			
			return position;
		}
	}
	
	// remove the item at the head of the queue and return it.
	// return null if the queue is empty.
	public T remove() {
		T returnValue = null;
	
		// we'll return null for empty queue. otherwise
		// return the head data, and set head's next item to be
		// the new head. and decrease nItems.
		if (nItems > 0) {
			returnValue = head.data;
			head = head.nextItem;
			nItems--;
		}
		
		return returnValue;
	}
	
	// simple get methods
	public int getSize() {
		return this.nItems;
	}
	
	// simple private class for holding one item's info
	private class QueueItem<T> {
		T				data;
		int				rank;
		QueueItem<T> 	nextItem;
		
		private QueueItem(T theData, int theRankingValue) {
			this.data = theData;
			this.rank = theRankingValue;
			this.nextItem = null;
		}
		
	}
}
