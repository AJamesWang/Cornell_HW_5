package FP;

import javax.swing.JButton;

public class DataButton<T> extends JButton{
	T data = null;

	public DataButton(){
		super();
	}

	public DataButton(String s){
		super(s);
	}

	public T getData(){
		return this.data;
	}

	public void setData(T data){
		this.data=data;
	}
}