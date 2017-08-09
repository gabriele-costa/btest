package it.unige.cseclab.pred;

import java.util.Map;

public class IVar implements Exp<Integer> {
	
	String name;
	
	public IVar(String x) {
		name = x;
	}

	@Override
	public Integer eval(Map<String, Object> env) throws ClassCastException {
		
		return (Integer) env.get(name);
	}

}
