package it.unige.cseclab.pred;

public class PredicateBuilder {
	
	/*
	 * BExpr
	 */
	public static Exp<Boolean> bval(Boolean b) {
		return new BVal(b);
	}
	
	public static Exp<Boolean> bvar(String s) {
		return new BVar(s);
	}
	
	public static Exp<Boolean> and(Exp<Boolean> l, Exp<Boolean> r) {
		return new AndExp(l, r);
	}
	
	public static Exp<Boolean> not(Exp<Boolean> e) {
		return new NotExp(e);
	}
	
	public static Exp<Boolean> or(Exp<Boolean> l, Exp<Boolean> r) {
		return new OrExp(l, r);
	}
	
	public static Exp<Boolean> eq(Exp<Integer> l, Exp<Integer> r) {
		return new EqExp(l, r);
	}
	
	public static Exp<Boolean> gt(Exp<Integer> l, Exp<Integer> r) {
		return new GTExp(l, r);
	}
	
	public static Exp<Boolean> st(Exp<Integer> l, Exp<Integer> r) {
		return new GTExp(r, l);
	}
	
	public static Exp<Boolean> gte(Exp<Integer> l, Exp<Integer> r) {
		return or(gt(l,r), eq(l,r));
	}
	
	public static Exp<Boolean> ste(Exp<Integer> l, Exp<Integer> r) {
		return or(st(l,r), eq(l,r));
	}
	
	/*
	 * IExpr
	 */
	
	public static Exp<Integer> ival(Integer i) {
		return new IVal(i);
	}
	
	public static Exp<Integer> ivar(String s) {
		return new IVar(s);
	}
	
	public static Exp<Integer> sum(Exp<Integer> i, Exp<Integer> j) {
		return new SumExp(i, j);
	}
	
	/*
	 * DExpr
	 */
	
	/*
	 * SExpr
	 */
	
	/*
	 * OExpr
	 */

}
