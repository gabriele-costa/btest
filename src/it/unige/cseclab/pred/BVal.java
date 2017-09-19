package it.unige.cseclab.pred;

import java.util.Map;

public class BVal implements Exp<Boolean> {
	
	Boolean b;

	public BVal(Boolean b) {
		this.b = b;
	}

	@Override
	public Boolean eval(Map<String, Object> env) throws ClassCastException {
		return b;
	}

}
