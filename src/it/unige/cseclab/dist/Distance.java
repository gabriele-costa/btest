package it.unige.cseclab.dist;

public interface Distance<T> {

	//@ ensures \result >= 0
	public double d(T a, T b);
	
}
