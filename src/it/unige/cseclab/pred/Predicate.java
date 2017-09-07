package it.unige.cseclab.pred;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.SMT;
import org.smtlib.command.C_assert;

import java.security.InvalidParameterException;
import java.util.Vector;

/**
 * Predicate Class
 */
public class Predicate {

    Vector<IExpr> expressions;
    Vector<IExpr.ISymbol> symbols; // Variable names in the code

    public Predicate(Vector<IExpr> expressions, Vector<IExpr.ISymbol> symbols) {
        this.expressions = expressions;
        this.symbols = symbols;
    }

    /**
     *
     */
    public Vector<ICommand> toSmt() {

        Vector<ICommand> commands = new Vector<>();

        for (IExpr e: expressions) {
            commands.add(new C_assert(e));
        }

        return commands;
    }

    public Vector<ICommand> prepare(Vector<Object> values) {
        Vector<ICommand> commands = this.toSmt();

        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;

        if (values.size() != symbols.size()) throw new InvalidParameterException("Wrong number of values.");

        for (int i = 0; i < symbols.size(); i++) {
            commands.add(
                    new C_assert(
                            efactory.fcn(
                                    efactory.symbol("="),
                                    symbols.get(i),
                                    efactory.symbol(values.get(i).toString()))
                    )
            );
        }

        return commands;
    }
}