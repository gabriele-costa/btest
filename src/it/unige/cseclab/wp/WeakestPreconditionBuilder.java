package it.unige.cseclab.wp;

import it.unige.cseclab.pred.Predicate;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.*;

/**
 *  WeakestPreconditionBuilder class
 *
 *  Computes the weakest precondition for a given method
 */
public class WeakestPreconditionBuilder {

    public WeakestPreconditionBuilder(SootMethod method) {
    }

    /**
     * Computes the weakest preconditions of <i>method</i> starting from <i>targetApiSignature</i>
     * and <i>vulnerabilitySpec</i>.
     * @param vulnerabilitySpec
     * @param targetApiSignature
     * @return
     */
    public Predicate generateMethodContract(Predicate vulnerabilitySpec, String targetApiSignature) {
        // TODO
        return null;
    }
    
    /*
     * backword wp computation requires the list of anchestors 
     */
    
    /**
     * Computes p such that {p} u {q}
     * 
     * @param u statement
     * @param q postcondition
     * @return p
     */
    public Predicate wp(Unit u, Predicate q) throws IllegalArgumentException {
    	Predicate p = null;
    	
    	if(u instanceof BreakpointStmt) {
    		// {q} breakpoint {q}
    		p = q;
    	}
    	else if(u instanceof AssignStmt) {
    		// {q[Exp/x]} x = Exp {q}
    		AssignStmt asgn = (AssignStmt) u;
    		
    	} 
    	else if(u instanceof EnterMonitorStmt) {
    		// {q} entermonitor {q}
    	}
    	else if(u instanceof GotoStmt) {
    		// {q} goto label {q}
    	}
    	else if(u instanceof IfStmt) {
    		// {q} if Exp goto label {q}
    	}
    	else if(u instanceof InvokeStmt) {
    		// {p} invoke m {q}
    	}
    	else if(u instanceof LookupSwitchStmt) {
    		// TODO
    	}
    	else if(u instanceof NopStmt) {
    		// {q} nop {q}
    		p = q;
    	}
    	else if(u instanceof RetStmt) {
    		// {false} ret x {q}
    		p = new Predicate(false);
    	}
    	else if(u instanceof ReturnStmt) {
    		// {false} return x {q}
    		p = new Predicate(false);
    	}
    	else if(u instanceof ReturnVoidStmt) {
    		// {false} return {q}
    		p = new Predicate(false);
    	}
    	else if(u instanceof TableSwitchStmt) {
    		// TODO
    	}
    	else if(u instanceof ThrowStmt) {
    		// TODO
    		
    	}
    	else {
    		throw new IllegalArgumentException("Unknown unit " + u);
    	}
    	
    	return p;
    }
    
}
