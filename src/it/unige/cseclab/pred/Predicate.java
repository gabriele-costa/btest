package it.unige.cseclab.pred;

import org.smtlib.CharSequenceReader;
import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IParser;
import org.smtlib.IParser.ParserException;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.ISort;
import org.smtlib.ISource;
import org.smtlib.SMT;
import org.smtlib.command.C_assert;
import org.smtlib.command.C_check_sat;
import org.smtlib.command.C_declare_fun;
import org.smtlib.impl.Factory;
import org.smtlib.sexpr.ISexpr;
import org.smtlib.sexpr.Sexpr;
import org.smtlib.sexpr.Sexpr.Seq;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Predicate Class
 */
public class Predicate {

    Vector<IExpr> expressions;
    Vector<IExpr.ISymbol> symbols; // Variable names in the code
    
    @SuppressWarnings("unchecked")
	public Predicate(Predicate p) {
    	expressions = (Vector<IExpr>)p.expressions.clone();
    	symbols = (Vector<IExpr.ISymbol>)p.symbols.clone();
    }
    
    public Predicate(IExpr e, Vector<IExpr.ISymbol> sv) {
        this.expressions = new Vector<IExpr>();
        this.symbols = new Vector<IExpr.ISymbol>();
        
        expressions.add(e);
        symbols.addAll(sv);
    }

    public Predicate(Vector<IExpr> expressions, Vector<String> sym) {
        this.expressions = expressions;
        this.symbols = new Vector<IExpr.ISymbol>();
        
        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;
        
        for(String s : sym) {
        	symbols.add(efactory.symbol(s));
        }
    }
    
    public Predicate(boolean b) {
		expressions = new Vector<>();
		symbols = new Vector<>();
		
		IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;
        
		expressions.add(efactory.fcn(
				efactory.symbol("="), 
				efactory.symbol("0"),
				(b) ? efactory.symbol("0") : efactory.symbol("1")));
	}

	public void and(IExpr e, Vector<IExpr.ISymbol> sv) {
    	expressions.add(e);
    	symbols.addAll(sv);
    }

    /**
     *
     */
    public Vector<ICommand> toSmt() {

        Vector<ICommand> commands = new Vector<>();
        
        ISort.IFactory sortFactory = (new SMT()).smtConfig.sortFactory;
        IExpr.IFactory exprFactory = (new SMT()).smtConfig.exprFactory;
        
        for(IExpr.ISymbol s : symbols) {
        	commands.add(new C_declare_fun(s, new ArrayList<ISort>(), sortFactory.createSortParameter(exprFactory.symbol("Int"))));
        }

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
    
    public Predicate exclude(Map<String,Object> env) {
        Predicate pp = new Predicate(this);

        IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;

        if (env.size() != symbols.size()) {
        	throw new InvalidParameterException("Wrong number of values.");
        }

        for (IExpr.ISymbol is : symbols) {
            pp.expressions.add(
	            		efactory.fcn(
	            			efactory.symbol("not"),
	                        efactory.fcn(
	                                efactory.symbol("="),
	                                is,
	                                efactory.symbol(env.get(is.toString()).toString()))
	                        )
            );
        }

        return pp;
    }
    
    public Map<String,Object> solve() {
    	SMT smt = new SMT();
    	
    	ICommand.IScript script = new org.smtlib.impl.Script();
    	
    	script.commands().addAll(this.toSmt());
    	
    	System.out.println(script.commands());
    	
    	IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;
    	
    	ISolver solver = new org.smtlib.solvers.Solver_z3_4_3(smt.smtConfig,"z3");
		solver.start();
		solver.set_option(efactory.keyword(":produce-models"),efactory.symbol("true"));
		IResponse response = solver.set_logic("QF_NIA",null);
		response = script.execute(solver);
		response = solver.check_sat();
		if(response.toString().equalsIgnoreCase("sat")) {
			Map<String,Object> output = new HashMap<String,Object>();
			for(IExpr.ISymbol is : symbols) {
				response = solver.get_value(is); 
				try {
					Object val = parseValueResponse(response);
					output.put(is.toString(), val);
				}
				catch(IllegalArgumentException e) {
					System.out.println(e);
				}
			}
			return output;
			
		}
		
		return null;
    }

	private Object parseValueResponse(IResponse response) throws IllegalArgumentException {
		
		Sexpr.Seq seq = (Sexpr.Seq) response;
		seq = (Seq) seq.sexprs().get(0);
		ISexpr is = seq.sexprs().get(1);
		
		String s = is.toString();
		
		s = s.replace('(', ' ');
		s = s.replace(')', ' ');
		s = s.replace(" ", "");
		
		if(s.equalsIgnoreCase("true")) {
			return new Boolean(true);
		}
		else if(s.equalsIgnoreCase("false")) {
			return new Boolean(false);
		}
		
		try {
			return new Integer(Integer.parseInt(s));
		}
		catch(NumberFormatException e) {}
		
		try {
			return new Double(Double.parseDouble(s));
		}
		catch(NumberFormatException e) {}
		
		throw new IllegalArgumentException("Unable to recognize " + s);
	}

	public boolean isSolution(Map<String, Object> env) {
		SMT smt = new SMT();
    	
    	ICommand.IScript script = new org.smtlib.impl.Script();
    	
    	script.commands().addAll(this.prepare(env));
    	
    	IExpr.IFactory efactory = (new SMT()).smtConfig.exprFactory;
    	
    	ISolver solver = new org.smtlib.solvers.Solver_z3_4_3(smt.smtConfig,"z3");
		solver.start();
		// solver.set_option(efactory.keyword(":produce-models"),efactory.symbol("true"));
		IResponse response = solver.set_logic("QF_NIA",null);
		response = script.execute(solver);
		response = solver.check_sat();
		return response.toString().equalsIgnoreCase("sat");
	}
}