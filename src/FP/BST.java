package FP;


public class BST <T extends Comparable>{ // there's almost no idiot-proofing -- done for relative clarity
	private TreeNode<T> root;
	
	
	public BST() { 
		this.root = null; 
		//has to be either VIP, Name, or Color
		}
	
	public void setRoot(TreeIterator i) { 
		this.root = i.getNode(); 
	}
	
	public boolean isEmpty() { 
		return root == null; 
	}
	
	public void insert(T data, Object compareThis) { // helper method
		if (isEmpty()) 
			root = new TreeNode<T>(data);
		else {
			insert(data, compareThis, new TreeIterator(this.root));
		}
	}
	private void insert(T data, Object compareThis, TreeIterator i) { 
		// recursive method; compareThis should be Color, VIPLevel, or name
		@SuppressWarnings("unchecked")
		int number = i.getData().compareTo(compareThis);
		//number = 0 means equal value, <0 means data>i.getData()
		//>0 means data<i.getData
		if (number == 0)
			i.addData(data);//to handle repeated values
		else if (number > 0) { // should put on left
			if (i.hasLeft()) 
				insert(data, compareThis, i.pointLeft());
			else i.setLeft(new TreeIterator(data, i));
		}
		else { // should put on right
			if (i.hasRight()) 
				insert(data, compareThis, i.pointRight());
			else i.setRight(new TreeIterator(data, i));
		}
	}
	
	public TreeIterator find(Object compareThis) {
		return find(compareThis, root); 
		// returns null if not found and a new TreeIterator if found
	}
	public TreeIterator find(Object compareThis, TreeNode<T> t) {
		int number = t.getData().compareTo(compareThis);
		//number = 0 means equal value, <0 means data>i.getData()
		//>0 means data<i.getData
		if (number == 0) 
			return new TreeIterator(t);
		if (number > 0) {
			if ( t.getLeft() == null) 
				return null;
			else 
				return find(compareThis, t.getLeft());
		}
		else
			if ( t.getRight() == null) 
				return null;
			else 
				return find(compareThis, t.getRight());
	}
	
	public void delete(T data, Object compareThis) { 
		TreeIterator found = find (compareThis); // null if not found

		if (found == null) return; // not there to delete !!
		
		MyList<T> dataset = found.getFullData();
		//delete the specific object from dataset
		for (int i =0; i<dataset.getSize(); i++) {
			if(dataset.get(i).equals(data))
				found.removeData(i);
				break;
			}
		if (found.getDataSize() == 0) {//we'll delete the whole leaf
			if (found.isLeaf()) {
				if (found.isRoot()) 
					found.nullifyNode(); // so was the only term in the tree
				else { 
					// i.e. it's a leaf and there's other stuff in the tree
					found.goUp();
					// moves found to point to found's parent
					int number = found.getData().compareTo(compareThis);
					//number = 0 means equal value, <0 means data>found.getData()
					//>0 means data<found.getData
					if (number > 0) 
						found.nullifyLeft();
					else
						found.nullifyRight();
					}
			
				System.out.println("\t deleted it since it was a leaf :) ");
				return;
			}
		} //not a leaf if we pass this line
		if (found.numChildren() == 1) { // case of having just one child
			TreeIterator child = found.point2onlyChild();
			if (found.isRoot()) 
				setRoot(child);
			else { // to skip a generation ...
				found.goUp(); // moves found to point to found's parent
				int number = found.getData().compareTo(compareThis);
				//number = 0 means equal value, <0 means data>found.getData()
				//>0 means data<found.getData
				if (number > 0) 
					found.setLeft(child); 
				else                    
					found.setRight(child);
			}
			child.setParent(found); // to ensure that the child points upwards to its new parent (ex-grandparent), possibly null
			System.out.println("\t deleted a single-child owning node :) ");
			return;
		} 
		//has two children if we pass this line 
		//-- we'll first find the biggest value to the left
		         System.out.println(">>\t about to delete a double-child node"); 
		TreeIterator lefty = new TreeIterator(found); 
		// cloning the found iterator
		lefty.goLeft(); // hopping one step to the left
		         
		if (!lefty.hasRight()) { // this is a special case where lefty's child must become found's left child
			found.copyData(lefty); // so copies the biggest value (and count) on the left to become the found node's value (and count)
			found.setLeft(lefty.pointLeft());    //new TreeIterator(lefty.getNode().getLeft()));
		}
		else { // here lefty can move to the right
			while (lefty.hasRight()) 
				lefty.goRight(); 
			// so moves lefty rightwards until no longer possible
			     
			// so copies the biggest value on the left to
			//become the found node's value
			    
			// make lefty's parent point to lefty's only child
		}
		System.out.println("successfully deleted a node having two children");
	} // end delete method
	
	//returns a list of gnomes that matches the search
	public MyList<T> findGnomes(Object compareThis) {
		TreeIterator i = find(compareThis);
		MyList<T> gnomes = null;
		if (i != null)
		gnomes = i.getFullData();
		return gnomes;
		
	}
	
	
	public class TreeIterator {
		private TreeNode<T> me;
		
		public TreeIterator (TreeNode<T> t) { 
			this.me = t;
		}
		public TreeIterator (TreeIterator i) { 
			this.me = i.getNode(); 
		}
		public TreeIterator (T data, TreeIterator parent) {
			this.me = new TreeNode<T>(data, parent.getNode()); 
			} // data = a, parent node = p
		
		public T getData() { 
			if (me != null) 
				return this.me.getData(); 
			return null; 
		}
		
		public MyList<T> getFullData() { 
			return this.me.getFullData(); 
		}
		
		public TreeNode<T> getNode() { 
			return this.me; 
		} 
		
		public void nullifyNode() { 
			this.me = null; 
		} // used in delete method
		
		public void nullifyLeft() { 
			this.me.setLeft(null); 
		} // nulls the left node of the node I'm pointing to
		
		public void nullifyRight() { 
			this.me.setRight(null);
		} // nulls the right node of the node I'm pointing to 
		
		public void goLeft() { 
			if (this.me != null) 
				this.me = this.me.getLeft(); 
		}
		
		public void goRight() {
			if (this.me != null) 
				this.me = this.me.getRight(); 
		}
		
		public void goUp() { 
			if (this.me != null && this.me != root) 
				this.me = this.me.getParent(); 
		}
		
		public TreeIterator pointLeft() { 
			return new TreeIterator(this.me.getLeft()); 
		} // note that this node might be null
		
		public TreeIterator pointRight() { 
			return new TreeIterator(this.me.getRight()); 
		} // note that this node might be null
		
		public TreeIterator pointUp() {
			return new TreeIterator(this.me.getParent()); 
		} // note that this node might be null
		
		public TreeIterator point2onlyChild() {
			if (numChildren() != 1) {
				// better to throw an appropriate exception
				System.out.println("trying to get an only child when not appropriate");
				return null;	
			}
			return hasLeft() ? new TreeIterator(this.me.getLeft()) : new TreeIterator(this.me.getRight());
		}
		
		public boolean hasLeft() { 
			return this.me.hasLeft(); 
		}
		
		public boolean hasRight() { 
			return this.me.hasRight(); 
		}
		
		public boolean isLeaf() { 
			return ! (hasLeft() || hasRight()); 
		}
		
		public boolean isRoot() { 
			return this.me == root; 
		}
		
		public int numChildren() {
			if ( isLeaf() ) 
				return 0;
			if ( hasLeft() && hasRight()) return 2;
			return 1; // since would otherwise have already returned
		}
		
		public void setData(T data, int n) { 
			this.me.setData(data, n); 
			} // sets the data of the node I'm pointing to
		
		public void copyData(TreeIterator i) { 
			this.me.setFullData(i.getFullData()); 
			}
		
		public void setLeft(TreeIterator i) { 
			this.me.setLeft(i.getNode()); 
			} // sets the left node of the node I'm pointing to
		
		public void setRight(TreeIterator i) { 
			this.me.setRight(i.getNode()); 
			} // sets the right node of the node I'm pointing to
		
		public void setParent(TreeIterator i) {
			this.me.setParent(i.getNode()); 
			} // sets the parent node of the node I'm pointing to
		
		public void addData(T data) {
			me.addData(data);
		}
		
		public void removeData(int id) {
			me.removeData(id);
		}
		public int getDataSize() {
			return me.getDataSize();
		}
	}
}
