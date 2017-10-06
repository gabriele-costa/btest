package it.unige.cseclab.dist;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.junit.Test;
import org.smtlib.IExpr;
import org.smtlib.SMT;
import org.smtlib.IExpr.ISymbol;

import it.unige.cseclab.pred.Predicate;

public class TestPredicateDistance {
	
	@Test
	public void checkPred() {
		
		IExpr.IFactory efact = (new SMT()).smtConfig.exprFactory;
		
		String sp = "p";
		String sq = "q";
		String sr = "r";

		ISymbol p = efact.symbol(sp);
		ISymbol q = efact.symbol(sq);
		ISymbol r = efact.symbol(sr);
		
		IExpr pc = efact.fcn(
				efact.symbol("and"), 
				efact.fcn(
						efact.symbol(">"), 
						p,
						efact.symbol("2")),
				efact.fcn(
						efact.symbol("<"), 
						p,
						efact.symbol("5"))
				);
		IExpr qc = efact.fcn(
				efact.symbol("or"), 
				efact.fcn(
						efact.symbol(">"), 
						q,
						efact.symbol("3")),
				efact.fcn(
						efact.symbol("<"), 
						q,
						efact.symbol("1"))
				);
		IExpr rc = efact.fcn(
						efact.symbol(">"), 
						r,
						q);
		
		Vector<String> symbs = new Vector<>();
		Vector<IExpr> exprs = new Vector<>();	
		
		symbs.add(sp);
		symbs.add(sq);
		symbs.add(sr);
		
		exprs.add(pc);
		exprs.add(qc);
		exprs.add(rc);
		
		Predicate pred = new Predicate(exprs, symbs);
		
		PredicateDistance pd = new PredicateDistance(pred);
		
		Map<String,Object> x = new HashMap<>();
		x.put(sp, new Integer(7));
		x.put(sq, new Integer(7));
		x.put(sr, new Integer(1));
		
		double res = pd.d(x);
		
		System.out.println("Distance = " + res);
	}

}
