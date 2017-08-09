package it.unige.cseclab.pred;

import java.util.Map;

public class NotExp implements Exp<Boolean> {
	
	Exp<Boolean> b;

	public NotExp(Exp<Boolean> b) {
		super();
		this.b = b;
	}

	@Override
	public Boolean eval(Map<String, Object> env) throws ClassCastException {
		return ! b.eval(env);
	}

}
