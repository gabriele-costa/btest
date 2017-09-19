package it.unige.cseclab.pred;

import java.util.Map;

public class SumExp implements Exp<Integer> {
	
	Exp<Integer> i, j;

	public SumExp(Exp<Integer> i, Exp<Integer> j) {
		this.i = i;
		this.j = j;
	}

	@Override
	public Integer eval(Map<String, Object> env) throws ClassCastException {
		return i.eval(env) + j.eval(env);
	}

}
