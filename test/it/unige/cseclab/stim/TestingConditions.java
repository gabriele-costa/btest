package it.unige.cseclab.stim;

import it.unige.cseclab.pred.Predicate;
import org.smtlib.IExpr;
import org.smtlib.SMT;

import java.util.Vector;

public class TestingConditions {

    static IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;

    public static Predicate getRawQuery() {


        IExpr.ISymbol p4 = efactory.symbol("p4");

        IExpr e = efactory.fcn(
                efactory.symbol("="),
                efactory.symbol("0"),
                p4);

        Vector<IExpr.ISymbol> v = new Vector<>();

        v.add(p4);


        Predicate p = new Predicate(e, v);

        return p;
    }


}
