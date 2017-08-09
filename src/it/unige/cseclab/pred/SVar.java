package it.unige.cseclab.pred;

import java.util.Map;

public class SVar implements Exp<String> {
	
	String name;
	
	public SVar(String x) {
		name = x;
	}

	@Override
	public String eval(Map<String, Object> env) throws ClassCastException {
		
		return (String) env.get(name);
	}

}
