package it.unige.cseclab.pred;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.SMT;
import org.smtlib.command.C_assert;
import org.smtlib.impl.Factory;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Vector;

/**
 * Predicate Class
 */
public class Predicate {

    Vector<IExpr> expressions;
    Vector<IExpr.ISymbol> symbols; // Variable names in the code

    public Predicate(Vector<IExpr> expressions, Vector<String> sym) {
        this.expressions = expressions;
        this.symbols = new Vector<IExpr.ISymbol>();
        
        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;
        
        for(String s : sym) {
        	symbols.add(efactory.symbol(s));
        }
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

    public Vector<ICommand> prepare(Map<String,Object> env) {
        Vector<ICommand> commands = this.toSmt();

        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;

        if (env.size() != symbols.size()) throw new InvalidParameterException("Wrong number of values.");

        for (IExpr.ISymbol is : symbols) {
            commands.add(
                    new C_assert(
                            efactory.fcn(
                                    efactory.symbol("="),
                                    is,
                                    efactory.symbol(env.get(is.toString()).toString()))
                    )
            );
        }

        return commands;
    }
    
    public Vector<ICommand> exclude(Vector<Map<String,Object>> envs) {
        Vector<ICommand> commands = this.toSmt();

        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;

        for(Map<String,Object> env : envs) {
        	commands.addAll(exclude(env));
        }
        return commands;
    }
    
    private Vector<ICommand> exclude(Map<String,Object> env) {
        Vector<ICommand> commands = new Vector<>();

        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;

        if (env.size() != symbols.size()) throw new InvalidParameterException("Wrong number of values.");

        for (IExpr.ISymbol is : symbols) {
            commands.add(
                    new C_assert(
                    		efactory.fcn(
                    			efactory.symbol("not"),
	                            efactory.fcn(
	                                    efactory.symbol("="),
	                                    is,
	                                    efactory.symbol(env.get(is.toString()).toString()))
	                            )
                    )
            );
        }

        return commands;
    }
}