package it.unige.cseclab.pred;

import java.util.Map;
import java.util.regex.Pattern;

public class SBelongExp implements Exp<Boolean> {
	
	Exp<String> val;
	String regex;
	
	public SBelongExp(Exp<String> val, String regex) {
		super();
		this.val = val;
		this.regex = regex;
	}

	@Override
	public Boolean eval(Map<String, Object> env) throws ClassCastException {
		return Pattern.matches(regex, val.eval(env));
	}

}
