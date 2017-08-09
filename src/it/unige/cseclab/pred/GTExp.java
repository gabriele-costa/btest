package it.unige.cseclab.pred;

import java.util.Map;

public class GTExp implements Exp<Boolean> {
	
	Exp<Integer> i1, i2;

	public GTExp(Exp<Integer> i1, Exp<Integer> i2) {
		super();
		this.i1 = i1;
		this.i2 = i2;
	}

	@Override
	public Boolean eval(Map<String, Object> env) throws ClassCastException {
		return i1.eval(env).intValue() > i2.eval(env).intValue();
	}

}
