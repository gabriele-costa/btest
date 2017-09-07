package it.unige.cseclab.wp;

import it.unige.cseclab.pred.Predicate;
import soot.SootMethod;

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
    public Predicate buildWp(Predicate vulnerabilitySpec, String targetApiSignature) {
        // TODO
        return null;
    }
}
