package FP;

public class TreeNode<T> {
	private MyList<T> data;
	private TreeNode<T> left, right, parent;
	
	public TreeNode(T data, TreeNode<T> parent) {
		this.data = new MyList<T>();
		this.data.add(data);
		this.parent = parent;
	}
	public TreeNode(T data){
		this(data, null); 
		} //for the root node
	
	public void addData(T data) {//add data with repeated values to the list
		this.data.add(data);
		}
	
	public void removeData(int id) {
		this.data.remove(id);
	}
	public  MyList<T> getFullData() { 
		return this.data; 
		}
	
	public void setFullData(MyList<T> data) {
		this.data = data;
	}
	public T getData() {
		return this.data.get(0);
	}
	
	public void setData(T data, int a) { 
		this.data.set(a, data); 
	}
	
	public TreeNode<T> getLeft() { 
		return this.left; 
	}
	public TreeNode<T> getRight() {
		return this.right; 
	}
	public TreeNode<T> getParent() { 
		return this.parent; 
	}
	public void setLeft(TreeNode<T> L) { 
		this.left = L; 
	}
	public void setRight(TreeNode<T> R) { 
		this.right = R; 
	}
	public void setParent(TreeNode<T> P) { 
		this.parent = P; 
	}
	public boolean hasLeft() { 
		return this.left != null; 
	}
	public boolean hasRight() { 
		return this.right != null; 
	}
	
	public int getDataSize() {
		return this.data.getSize();
	}
	 
}
