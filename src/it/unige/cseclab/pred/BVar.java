package it.unige.cseclab.pred;

import java.util.Map;

public class BVar implements Exp<Boolean> {
	
	String name;
	
	public BVar(String x) {
		name = x;
	}

	@Override
	public Boolean eval(Map<String, Object> env) throws ClassCastException {
		
		return (Boolean) env.get(name);
	}

}
