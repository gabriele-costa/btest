package it.unige.cseclab.pred;

import java.util.Map;

public interface Exp<T> {
	
	public T eval(Map<String,Object> env) throws ClassCastException;

}
