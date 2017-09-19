package it.unige.cseclab.pred;

import java.util.Map;

public class IVal implements Exp<Integer> {
	
	Integer i;

	public IVal(Integer i) {
		this.i = i;
	}

	@Override
	public Integer eval(Map<String, Object> env) throws ClassCastException {
		return i;
	}

}
