package it.unige.cseclab.pred;

import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.smtlib.IExpr;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.SMT;

public class TestPred {
	
	@Test
	public void checkPred() {
		
		IExpr.IFactory efact = (new SMT()).smtConfig.exprFactory;
		
		ISymbol p = efact.symbol("p");
		ISymbol q = efact.symbol("q");
		
		IExpr sum = efact.fcn(
				efact.symbol("="), 
				efact.fcn(
						efact.symbol("+"), 
						p,
						q),
				efact.symbol("5"));
		
		IExpr mul = efact.fcn(
				efact.symbol("="), 
				efact.fcn(
						efact.symbol("*"), 
						p,
						q),
				efact.symbol("6"));
		
		Vector<String> symbs = new Vector<>();
		Vector<IExpr> exprs = new Vector<>();	
		
		symbs.add("p");
		symbs.add("q");
		
		exprs.add(sum);
		exprs.add(mul);
		
		Predicate pred = new Predicate(exprs, symbs);
		
		Map<String,Object> env = pred.solve();
		
		for(String k : env.keySet()) {
			System.out.println(k + " := " + env.get(k));
		}
	}

}
