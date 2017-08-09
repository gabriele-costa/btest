package it.unige.cseclab.pred;

import java.util.Map;

public class OrExp implements Exp<Boolean> {
	
	Exp<Boolean> b1, b2;

	public OrExp(Exp<Boolean> b1, Exp<Boolean> b2) {
		super();
		this.b1 = b1;
		this.b2 = b2;
	}

	@Override
	public Boolean eval(Map<String, Object> env) throws ClassCastException {
		return b1.eval(env) | b2.eval(env);
	}

}
